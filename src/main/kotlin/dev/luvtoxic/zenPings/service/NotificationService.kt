package dev.luvtoxic.zenPings.service

import dev.luvtoxic.zenPings.config.PingConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

class NotificationService {
    private val miniMessage = MiniMessage.miniMessage()

    fun notifyMention(target: Player, sender: Player, config: PingConfig) {
        target.playSound(target.location, config.sound, config.volume, config.pitch)
        val message = miniMessage.deserialize(
            config.actionBarMessage.replace("%player%", sender.name)
        )
        target.sendActionBar(message)
    }

    fun notifyCooldown(player: Player, config: PingConfig, remainingSeconds: Long) {
        val message = miniMessage.deserialize(
            config.cooldownMessage.replace("%seconds%", remainingSeconds.toString())
        )
        player.sendMessage(message)
    }
}
