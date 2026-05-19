package dev.luvtoxic.zenPings.config

import org.bukkit.Sound

data class PingConfig(
    val prefix: String = "@",
    val replacementText: String = "<yellow>@%player%",
    val cooldownSeconds: Long = 5,
    val sound: Sound = Sound.BLOCK_NOTE_BLOCK_PLING,
    val volume: Float = 1.0f,
    val pitch: Float = 1.0f,
    val actionBarMessage: String = "<gold><bold>PING!</bold> <yellow>%player% mentioned you!",
    val mentionColors: List<MentionColor> = emptyList(),
    val cooldownMessage: String = "<red>Please wait %seconds%s before pinging again!"
)

data class MentionColor(
    val id: String,
    val format: String
)
