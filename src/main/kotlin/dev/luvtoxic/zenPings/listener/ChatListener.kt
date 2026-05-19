package dev.luvtoxic.zenPings.listener

import dev.luvtoxic.zenPings.handler.PingHandler
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ChatListener(private val pingHandler: PingHandler) : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerChat(event: AsyncChatEvent) {
        event.message(pingHandler.processMessage(event.player, event.message()))
    }
}
