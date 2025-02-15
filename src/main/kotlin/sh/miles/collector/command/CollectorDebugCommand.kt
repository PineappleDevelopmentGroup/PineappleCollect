package sh.miles.collector.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import sh.miles.collector.Registries
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_DELETE
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_DELETE_CHUNK
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_DELETE_TARGETED
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_GET
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_GET_CHUNK
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_GET_TARGETED
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_MODIFY
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_MODIFY_CONTAINER
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_MODIFY_CONTAINER_CLEAR
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_MODIFY_CONTAINER_INSERT
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_MODIFY_UPGRADE
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_MODIFY_UPGRADE_ADD
import sh.miles.collector.util.COLLECTOR_COMMAND_DEBUG_MODIFY_UPGRADE_REMOVE
import sh.miles.collector.util.CollectorDebugUtil
import sh.miles.pineapple.api.tiles.api.Tiles
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

object CollectorDebugCommand : Command(CommandLabel("debug", COLLECTOR_COMMAND_DEBUG)) {

    init {
        registerSubcommand(CollectorDebugGetCommand)
        registerSubcommand(CollectorDebugDeleteCommand)
        registerSubcommand(CollectorDebugModifyCommand)
    }
}

private object CollectorDebugModifyCommand : Command(CommandLabel("modify", COLLECTOR_COMMAND_DEBUG_MODIFY)) {
    init {
        registerSubcommand(CollectorModifyContainer)
        registerSubcommand(CollectorModifyUpgrade)
    }

    private object CollectorModifyContainer : Command(CommandLabel("container", COLLECTOR_COMMAND_DEBUG_MODIFY_CONTAINER)) {
        init {
            registerSubcommand(ContainerInsert)
            registerSubcommand(ContainerClear)
        }

        private object ContainerClear :
            Command(CommandLabel("clear", COLLECTOR_COMMAND_DEBUG_MODIFY_CONTAINER_CLEAR)) {
            override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
                val sender = sourceStack.executor!!

                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return
                collector.stackContainer.clearContents()
                sender.sendMessage(
                    PineappleChat.parse(
                        "<green>Successfully cleared container"
                    )
                )
                return
            }
        }

        private object ContainerInsert :
            Command(CommandLabel("insert", COLLECTOR_COMMAND_DEBUG_MODIFY_CONTAINER_INSERT)) {
            override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
                val sender = sourceStack.executor!!

                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return
                val item = sender.inventory.itemInMainHand
                if (item.type.isAir) {
                    sender.sendMessage("Item in hand must not be air")
                    return
                }
                collector.addItem(item)
                sender.sendMessage(
                    PineappleChat.parse(
                        "<green>Successfully added ${item.type} to container"
                    )
                )
                return
            }
        }
    }

    private object CollectorModifyUpgrade : Command(CommandLabel("upgrade", COLLECTOR_COMMAND_DEBUG_MODIFY_UPGRADE)) {

        init {
            registerSubcommand(UpgradeRemove)
            registerSubcommand(UpgradeAdd)
        }


        private object UpgradeAdd : Command(CommandLabel("add", COLLECTOR_COMMAND_DEBUG_MODIFY_UPGRADE_ADD)) {

            override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
                val sender = sourceStack.executor!!

                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return
                }

                if (args.size < 2) {
                    sender.sendMessage(PineappleChat.parse("<red>Invalid arg amount, requires 2"))
                    return
                }

                val upgrade = when (val action = Registries.UPGRADE.get(NamespacedKey.fromString("pineapple-collect:${args[0]}")!!)) {
                    is Some -> {
                        action.some()
                    }
                    is None -> {
                        sender.sendMessage(PineappleChat.parse("<red>Unknown upgrade key"))
                        return
                    }
                }

                val upgradeLevel = args[1].toInt()

                if (upgrade.level.size < upgradeLevel) {
                    sender.sendMessage(PineappleChat.parse("<red>Invalid level, use one specified in tab complete"))
                    return
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return
                var enabled = 1
                if (collector.upgrades.containsKey(upgrade)) {
                    enabled = collector.upgrades[upgrade]!!.second
                }
                collector.upgrades[upgrade] = Pair(upgradeLevel, enabled)
                sender.sendMessage(PineappleChat.parse("<green>Added upgrade <white>\'${upgrade.key}: $upgradeLevel\'"))
                return
            }

            override fun suggest(sourceStack: CommandSourceStack, args: Array<out String>): Collection<String> {
                val sender = sourceStack.executor!!

                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return super.suggest(sourceStack, args)
                }

                if (args.size == 1)
                    return StringUtil.copyPartialMatches(args[0], Registries.UPGRADE.keys().map { it.key }, mutableListOf())
                else if (args.size == 2)
                    return StringUtil.copyPartialMatches(args[1], getIntToZero(Registries.UPGRADE.get(NamespacedKey.fromString("pineapple-collect:${args[0]}")!!).orThrow().level.size).map { it.toString() }, mutableListOf())


                return super.suggest(sourceStack, args)
            }

            private fun getIntToZero(high: Int) : List<Int> {
                val toReturn = mutableListOf<Int>()
                for (i in high downTo 1) {
                    toReturn.add(i)
                }
                return toReturn
            }
        }
        private object UpgradeRemove : Command(CommandLabel("remove", COLLECTOR_COMMAND_DEBUG_MODIFY_UPGRADE_REMOVE)) {

            override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
                val sender = sourceStack.executor!!

                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return
                }

                if (args.isEmpty()) {
                    sender.sendMessage(PineappleChat.parse("<red>Invalid amount of args, requires 1"))
                    return
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return
                val upgrade = when (val action = Registries.UPGRADE.get(NamespacedKey.fromString("pineapple-collect:${args[0]}")!!)) {
                    is Some -> {
                        action.some()
                    }
                    is None -> {
                        sender.sendMessage(PineappleChat.parse("<red>Invalid upgrade key"))
                        return
                    }
                }

                val level = collector.upgrades.remove(upgrade)
                if (level == null) {
                    sender.sendMessage(PineappleChat.parse("<red>This collector did not have that upgrade"))
                    return
                }

                sender.sendMessage(PineappleChat.parse("<green>Successfully removed upgrade."))
                return
            }

            override fun suggest(sourceStack: CommandSourceStack, args: Array<out String>): MutableList<String> {
                val sender = sourceStack.executor!!

                if (sender !is Player) {
                    sender.sendMessage("Only players can send this command")
                    return mutableListOf()
                }

                val collector = CollectorDebugUtil.getTargetedCollector(sender) ?: return mutableListOf()

                return StringUtil.copyPartialMatches(args[0], collector.upgrades.map { it.key.key.key }, mutableListOf())
            }

        }
    }
}

private object CollectorDebugDeleteCommand : Command(CommandLabel("delete", COLLECTOR_COMMAND_DEBUG_DELETE)) {
    init {
        registerSubcommand(CollectorDebugChunk)
        registerSubcommand(CollectorDebugTargeted)
    }

    private object CollectorDebugChunk : Command(CommandLabel("chunk", COLLECTOR_COMMAND_DEBUG_DELETE_CHUNK)) {
        override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
            val sender = sourceStack.executor!!

            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return
            }

            CollectorDebugUtil.printAllInChunk(sender, sender.location.chunk)
            Tiles.getInstance().deleteTiles(sender.location.chunk) { it is CollectorTile }
            return
        }
    }

    private object CollectorDebugTargeted :
        Command(CommandLabel("targeted", COLLECTOR_COMMAND_DEBUG_DELETE_TARGETED)) {
        override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
            val sender = sourceStack.executor!!

            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return
            }

            val target = sender.getTargetBlockExact(10)
            if (target == null) {
                sender.sendMessage("You must target a block to use this command")
                return
            }

            CollectorDebugUtil.printDebug(sender, target.location)
            Tiles.getInstance().deleteTile(target.location) { it is CollectorTile }
            return
        }
    }
}

private object CollectorDebugGetCommand : Command(CommandLabel("get", COLLECTOR_COMMAND_DEBUG_GET)) {
    init {
        registerSubcommand(CollectorDebugChunk)
        registerSubcommand(CollectorDebugTargeted)
    }

    private object CollectorDebugChunk : Command(CommandLabel("chunk", COLLECTOR_COMMAND_DEBUG_GET_CHUNK)) {
        override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
            val sender = sourceStack.executor!!

            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return
            }

            CollectorDebugUtil.printAllInChunk(sender, sender.location.chunk)
            return
        }
    }

    private object CollectorDebugTargeted :
        Command(CommandLabel("targeted", COLLECTOR_COMMAND_DEBUG_GET_TARGETED)) {
        override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
            val sender = sourceStack.executor!!

            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return
            }

            val target = sender.getTargetBlockExact(10)
            if (target == null) {
                sender.sendMessage("You must target a block to use this command")
                return
            }

            CollectorDebugUtil.printDebug(sender, target.location)
            return
        }
    }
}
