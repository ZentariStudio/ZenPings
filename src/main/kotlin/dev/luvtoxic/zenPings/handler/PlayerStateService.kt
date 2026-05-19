package dev.luvtoxic.zenPings.handler

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerStateService {
    private val toggles: ConcurrentHashMap<UUID, Boolean> = ConcurrentHashMap()
    private val lastPingTimes: ConcurrentHashMap<UUID, Long> = ConcurrentHashMap()

    fun isPingEnabled(player: Player): Boolean = toggles.getOrDefault(player.uniqueId, true)

    fun togglePing(player: Player): Boolean {
        return toggles.compute(player.uniqueId) { _, current -> !(current ?: true) } ?: true
    }

    fun getRemainingCooldown(player: Player, cooldownSeconds: Long): Long {
        val now = System.currentTimeMillis()
        val lastPing = lastPingTimes.getOrDefault(player.uniqueId, 0L)
        val elapsed = (now - lastPing) / 1000
        return (cooldownSeconds - elapsed).coerceAtLeast(0)
    }

    fun updateLastPingTime(player: Player) {
        lastPingTimes[player.uniqueId] = System.currentTimeMillis()
    }

    fun clearData(player: Player) {
        toggles.remove(player.uniqueId)
        lastPingTimes.remove(player.uniqueId)
    }
}
