package dev.luvtoxic.zenPings.command

import dev.luvtoxic.zenPings.ZenPings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PingCommand(private val plugin: ZenPings) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty() && args[0].lowercase() == "reload") {
            if (sender.hasPermission("zenpings.reload")) {
                plugin.configManager.loadConfigValues()
                sender.sendMessage(Component.text("ZenPings configuration reloaded!", NamedTextColor.GREEN))
            } else {
                sender.sendMessage(Component.text("You don't have permission to reload the configuration.", NamedTextColor.RED))
            }
            return true
        }

        if (sender !is Player) {
            sender.sendMessage(Component.text("This command (except /ping reload) can only be used by players.", NamedTextColor.RED))
            return true
        }

        if (!sender.hasPermission("zenpings.use")) {
            sender.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED))
            return true
        }

        val stateService = plugin.playerStateService

        if (args.isEmpty()) {
            val newState = stateService.togglePing(sender)
            sendToggleMessage(sender, newState)
            return true
        }

        when (args[0].lowercase()) {
            "on" -> {
                if (stateService.isPingEnabled(sender)) {
                    sender.sendMessage(Component.text("Pings are already enabled.", NamedTextColor.YELLOW))
                } else {
                    stateService.togglePing(sender)
                    sender.sendMessage(Component.text("Pings enabled!", NamedTextColor.GREEN))
                }
            }
            "off" -> {
                if (!stateService.isPingEnabled(sender)) {
                    sender.sendMessage(Component.text("Pings are already disabled.", NamedTextColor.YELLOW))
                } else {
                    stateService.togglePing(sender)
                    sender.sendMessage(Component.text("Pings disabled!", NamedTextColor.RED))
                }
            }
            "status" -> {
                val enabled = stateService.isPingEnabled(sender)
                sender.sendMessage(
                    Component.text("Your pings are currently ")
                        .append(Component.text(if (enabled) "ENABLED" else "DISABLED", if (enabled) NamedTextColor.GREEN else NamedTextColor.RED))
                        .append(Component.text("."))
                )
            }
            else -> {
                sender.sendMessage(Component.text("Usage: /ping [on|off|status|reload]", NamedTextColor.RED))
            }
        }

        return true
    }

    private fun sendToggleMessage(player: Player, state: Boolean) {
        val statusText = if (state) "enabled" else "disabled"
        val color = if (state) NamedTextColor.GREEN else NamedTextColor.RED
        player.sendMessage(
            Component.text("Pings have been ", NamedTextColor.GRAY)
                .append(Component.text(statusText, color))
                .append(Component.text(".", NamedTextColor.GRAY))
        )
    }
}
