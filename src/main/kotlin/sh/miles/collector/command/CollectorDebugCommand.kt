package sh.miles.collector.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.util.CollectorDebugUtil
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.tiles.api.Tiles

object CollectorDebugCommand : Command(CommandLabel("debug", "collector.command.debug")) {

    init {
        registerSubcommand(CollectorDebugGetCommand)
        registerSubcommand(CollectorDebugDeleteCommand)
        registerSubcommand(CollectorDebugModifyCommand)
    }

    private object CollectorDebugModifyCommand : Command(CommandLabel("modify", "collector.command.debug.modify")) {
        init {
            registerSubcommand(CollectorModifyContainer)
        }

        private object CollectorModifyContainer :
            Command(CommandLabel("container", "collector.command.debug.modify.container")) {
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
                    collector.stackContainer.add(item)
                    sender.spigot().sendMessage(
                        PineappleChat.parse(
                            "<green>Successfully added ${item.type} to container"
                        )
                    )
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

}
