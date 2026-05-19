package dev.luvtoxic.zenPings.config

import dev.luvtoxic.zenPings.ZenPings
import org.bukkit.Sound

class ConfigManager(private val plugin: ZenPings) {
    private var pingConfig: PingConfig = PingConfig()

    fun loadConfigValues() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        val config = plugin.config

        val mentionColors = mutableListOf<MentionColor>()
        config.getConfigurationSection("mention-colors")?.let { section ->
            for (key in section.getKeys(false)) {
                section.getString(key)?.let { format ->
                    mentionColors.add(MentionColor(key, format))
                }
            }
        }

        pingConfig = PingConfig(
            prefix = config.getString("prefix", "@")!!,
            replacementText = config.getString("replacement-text", "<yellow>@%player%")!!,
            cooldownSeconds = config.getLong("cooldown-seconds", 5),
            sound = try {
                Sound.valueOf(config.getString("notifications.sound", "BLOCK_NOTE_BLOCK_PLING")?.uppercase() ?: "BLOCK_NOTE_BLOCK_PLING")
            } catch (e: Exception) {
                plugin.logger.warning("Invalid sound in config, defaulting to BLOCK_NOTE_BLOCK_PLING")
                Sound.BLOCK_NOTE_BLOCK_PLING
            },
            volume = config.getDouble("notifications.volume", 1.0).toFloat(),
            pitch = config.getDouble("notifications.pitch", 1.0).toFloat(),
            actionBarMessage = config.getString("notifications.action-bar", "<gold><bold>PING!</bold> <yellow>%player% mentioned you!")!!,
            mentionColors = mentionColors,
            cooldownMessage = config.getString("cooldown-message", "<red>Please wait %seconds%s before pinging again!")!!
        )
    }

    fun getConfig(): PingConfig = pingConfig
}
