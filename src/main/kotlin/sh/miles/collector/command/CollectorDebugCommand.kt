package sh.miles.collector.command

import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.upgrade.CollectorUpgradeActionRegistry
import sh.miles.collector.util.CollectorDebugUtil
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineapple.tiles.api.Tiles

object CollectorDebugCommand : Command(CommandLabel("debug", "collector.command.debug")) {

    init {
        registerSubcommand(CollectorDebugGetCommand)
        registerSubcommand(CollectorDebugDeleteCommand)
        registerSubcommand(CollectorDebugModifyCommand)
    }
}

private object CollectorDebugModifyCommand : Command(CommandLabel("modify", "collector.command.debug.modify")) {
    init {
        registerSubcommand(CollectorModifyContainer)
        registerSubcommand(CollectorModifyUpgrade)
    }

    private object CollectorModifyContainer : Command(CommandLabel("container", "collector.command.debug.modify.container")) {
        init {
            registerSubcommand(ContainerInsert)
            registerSubcommand(ContainerClear)
        }

        private object ContainerClear :
            Command(CommandLabel("clear", "collector.command.debug.modify.container.clear")) {
            override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return true
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return true
                collector.stackContainer.clearContents()
                sender.spigot().sendMessage(
                    PineappleChat.parse(
                        "<green>Successfully cleared container"
                    )
                )
                return true
            }
        }

        private object ContainerInsert :
            Command(CommandLabel("insert", "collector.command.debug.modify.container.insert")) {
            override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return true
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return true
                val item = sender.inventory.itemInMainHand
                if (item.type.isAir) {
                    sender.sendMessage("Item in hand must not be air")
                    return true
                }
                collector.addItem(item)
                sender.spigot().sendMessage(
                    PineappleChat.parse(
                        "<green>Successfully added ${item.type} to container"
                    )
                )
                return true
            }
        }
    }

    private object CollectorModifyUpgrade : Command(CommandLabel("upgrade", "collector.command.debug.modify.upgrade")) {

        init {
            registerSubcommand(UpgradeList)
            registerSubcommand(UpgradeRemove)
            registerSubcommand(UpgradeAdd)
        }


        private object UpgradeAdd : Command(CommandLabel("add", "collector.command.debug.modify.upgrade.add")) {

            override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return true
                }

                if (args.size < 2) {
                    sender.spigot().sendMessage(PineappleChat.parse("<red>Invalid arg amount, requires 2"))
                    return true
                }

                val upgradeAction = when (val action = CollectorUpgradeActionRegistry.get(NamespacedKey.fromString("pineapple-collect:${args[0]}")!!)) {
                    is Some -> {
                        action.some()
                    }
                    is None -> {
                        sender.spigot().sendMessage(PineappleChat.parse("<red>Unknown upgrade key"))
                        return true
                    }
                }

                val upgradeLevel = args[1].toInt()

                if (upgradeAction.maxLevel < upgradeLevel) {
                    sender.spigot().sendMessage(PineappleChat.parse("<red>Invalid level, use one specified in tab complete"))
                    return true
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return true
                collector.upgrades[upgradeAction] = upgradeLevel
                sender.spigot().sendMessage(PineappleChat.parse("<green>Added upgrade <white>\'${upgradeAction.key}: $upgradeLevel\'"))
                return true
            }

            override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return super.complete(sender, args)
                }

                if (args.size == 1)
                    return StringUtil.copyPartialMatches(args[0], CollectorUpgradeActionRegistry.keys().map { it.key }, mutableListOf())
                else if (args.size == 2)
                    return StringUtil.copyPartialMatches(args[1], getIntToZero(CollectorUpgradeActionRegistry.get(NamespacedKey.fromString("pineapple-collect:${args[0]}")!!).orThrow().maxLevel).map { it.toString() }, mutableListOf())


                return super.complete(sender, args)
            }

            private fun getIntToZero(high: Int) : List<Int> {
                val toReturn = mutableListOf<Int>()
                for (i in high downTo 1) {
                    toReturn.add(i)
                }
                return toReturn
            }
        }
        private object UpgradeRemove : Command(CommandLabel("remove", "collector.command.debug.modify.upgrade.remove")) {

            override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return true
                }

                if (args.isEmpty()) {
                    sender.spigot().sendMessage(PineappleChat.parse("<red>Invalid amount of args, requires 1"))
                    return true
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return true
                val upgradeAction = when (val action = CollectorUpgradeActionRegistry.get(NamespacedKey.fromString("pineapple-collect:${args[0]}")!!)) {
                    is Some -> {
                        action.some()
                    }
                    is None -> {
                        sender.spigot().sendMessage(PineappleChat.parse("<red>Invalid upgrade key"))
                        return true
                    }
                }

                val level = collector.upgrades.remove(upgradeAction)
                if (level == null) {
                    sender.spigot().sendMessage(PineappleChat.parse("<red>This collector did not have that upgrade"))
                    return true
                }

                sender.spigot().sendMessage(PineappleChat.parse("<green>Successfully removed upgrade."))
                return true
            }

            override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return mutableListOf()
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return mutableListOf()

                return StringUtil.copyPartialMatches(args[0], collector.upgrades.map { it.key.key.key }, mutableListOf())
            }

        }
        private object UpgradeList : Command(CommandLabel("list", "collector.command.debug.modify.upgrade.list")) {


            override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return true
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return true
                sender.spigot().sendMessage(PineappleChat.parse(
                    "<green>Collector Upgrades: <white>${collector.upgrades}"
                ))
                return true
            }
        }
    }
}

private object CollectorDebugDeleteCommand : Command(CommandLabel("delete", "collector.command.debug.delete")) {
    init {
        registerSubcommand(CollectorDebugChunk)
        registerSubcommand(CollectorDebugTargeted)
    }

    private object CollectorDebugChunk : Command(CommandLabel("chunk", "collector.command.debug.delete.chunk")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return true
            }

            CollectorDebugUtil.printAllInChunk(sender, sender.location.chunk)
            Tiles.getInstance().deleteTiles(sender.location.chunk) { it is CollectorTile }
            return true
        }
    }

    private object CollectorDebugTargeted :
        Command(CommandLabel("targeted", "collector.command.debug.delete.targeted")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return true
            }

            val target = sender.getTargetBlockExact(10)
            if (target == null) {
                sender.sendMessage("You must target a block to use this command")
                return true
            }

            CollectorDebugUtil.printDebug(sender, target.location)
            Tiles.getInstance().deleteTile(target.location) { it is CollectorTile }
            return true
        }
    }
}

private object CollectorDebugGetCommand : Command(CommandLabel("get", "collector.command.debug.get")) {
    init {
        registerSubcommand(CollectorDebugChunk)
        registerSubcommand(CollectorDebugTargeted)
    }

    private object CollectorDebugChunk : Command(CommandLabel("chunk", "collector.command.debug.get.chunk")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return true
            }

            CollectorDebugUtil.printAllInChunk(sender, sender.location.chunk)
            return true
        }
    }

    private object CollectorDebugTargeted :
        Command(CommandLabel("targeted", "collector.command.debug.get.targeted")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return true
            }

            val target = sender.getTargetBlockExact(10)
            if (target == null) {
                sender.sendMessage("You must target a block to use this command")
                return true
            }

            CollectorDebugUtil.printDebug(sender, target.location)
            return true
        }
    }
}