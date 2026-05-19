package dev.luvtoxic.zenPings.handler

import dev.luvtoxic.zenPings.config.ConfigManager
import dev.luvtoxic.zenPings.config.PingConfig
import dev.luvtoxic.zenPings.service.NotificationService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import java.util.regex.Pattern

class PingHandler(
    private val playerStateService: PlayerStateService,
    private val notificationService: NotificationService,
    private val configManager: ConfigManager
) {
    private val miniMessage = MiniMessage.miniMessage()
    private val plainSerializer = PlainTextComponentSerializer.plainText()

    fun processMessage(sender: Player, message: Component): Component {
        if (!sender.hasPermission("zenpings.ping")) return message

        val config = configManager.getConfig()
        val remainingCooldown = playerStateService.getRemainingCooldown(sender, config.cooldownSeconds)

        // Find ALL potential pings first to handle cooldown and notifications correctly
        val allOnlinePlayers = sender.server.onlinePlayers
        val mentionsInMessage = findMentions(message, config, allOnlinePlayers)

        if (mentionsInMessage.isEmpty()) return message

        // If on cooldown and tried to ping, notify and return original message
        if (remainingCooldown > 0) {
            notificationService.notifyCooldown(sender, config, remainingCooldown)
            return message
        }

        val actuallyMentioned = mutableSetOf<Player>()
        var processedMessage = message

        // Sort players by name length descending to prevent partial matching issues
        // e.g., if "@Player" and "@PlayerOne" are online, "@PlayerOne" should match first.
        val sortedPlayers = mentionsInMessage.sortedByDescending { it.name.length }

        for (target in sortedPlayers) {
            if (!playerStateService.isPingEnabled(target)) continue
            if (target == sender && !sender.hasPermission("zenpings.ping.self")) continue

            val pattern = Pattern.compile("(?i)${Pattern.quote(config.prefix)}${Pattern.quote(target.name)}(?![a-zA-Z0-9_])")
            
            val replacementConfig = TextReplacementConfig.builder()
                .match(pattern)
                .replacement { result, _ ->
                    actuallyMentioned.add(target)
                    // Preserve the exact casing used by the sender if preferred, 
                    // or use the target's real name. Let's use target's real name for consistency.
                    getReplacementComponent(config, target, sender)
                }
                .build()

            processedMessage = processedMessage.replaceText(replacementConfig)
        }

        if (actuallyMentioned.isNotEmpty()) {
            playerStateService.updateLastPingTime(sender)
            actuallyMentioned.forEach { target ->
                notificationService.notifyMention(target, sender, config)
            }
        }

        return processedMessage
    }

    private fun findMentions(message: Component, config: PingConfig, players: Collection<Player>): List<Player> {
        val plainText = plainSerializer.serialize(message)
        return players.filter { player -> 
            val pattern = Pattern.compile("(?i)${Pattern.quote(config.prefix)}${Pattern.quote(player.name)}(?![a-zA-Z0-9_])")
            pattern.matcher(plainText).find()
        }
    }

    private fun getReplacementComponent(config: PingConfig, target: Player, sender: Player): Component {
        var formatPrefix = ""
        for (mentionColor in config.mentionColors) {
            if (sender.hasPermission("zenpings.color.${mentionColor.id}")) {
                formatPrefix = mentionColor.format
                break
            }
        }

        val template = if (formatPrefix.isNotEmpty()) {
            "$formatPrefix${config.replacementText}"
        } else {
            config.replacementText
        }

        // Use MiniMessage to deserialize the template, then replace %player% with the target name
        // We use a placeholder to avoid MiniMessage trying to parse the player name as tags
        return miniMessage.deserialize(template.replace("%player%", "<player_name>"))
            .replaceText(TextReplacementConfig.builder()
                .matchLiteral("<player_name>")
                .replacement(target.name)
                .build()
            )
    }
}
