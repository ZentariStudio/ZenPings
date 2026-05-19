package dev.luvtoxic.zenPings.listener

import dev.luvtoxic.zenPings.handler.PlayerStateService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener(private val playerStateService: PlayerStateService) : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        // We might want to keep toggles persistent across sessions if we had a database,
        // but for a lightweight plugin, clearing on quit prevents memory leaks.
        // If the user wants persistence, we should implement a small JSON/YAML storage.
        playerStateService.clearData(event.player)
    }
}
