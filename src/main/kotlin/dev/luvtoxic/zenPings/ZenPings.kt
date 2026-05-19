package dev.luvtoxic.zenPings

import dev.luvtoxic.zenPings.command.PingCommand
import dev.luvtoxic.zenPings.command.PingTabCompleter
import dev.luvtoxic.zenPings.config.ConfigManager
import dev.luvtoxic.zenPings.handler.PingHandler
import dev.luvtoxic.zenPings.handler.PlayerStateService
import dev.luvtoxic.zenPings.listener.ChatListener
import dev.luvtoxic.zenPings.listener.PlayerListener
import dev.luvtoxic.zenPings.service.NotificationService
import org.bukkit.plugin.java.JavaPlugin

class ZenPings : JavaPlugin() {
    val configManager: ConfigManager by lazy { ConfigManager(this) }
    val playerStateService: PlayerStateService by lazy { PlayerStateService() }
    val notificationService: NotificationService by lazy { NotificationService() }
    val pingHandler: PingHandler by lazy { 
        PingHandler(playerStateService, notificationService, configManager) 
    }

    override fun onEnable() {
        configManager.loadConfigValues()

        server.pluginManager.registerEvents(ChatListener(pingHandler), this)
        server.pluginManager.registerEvents(PlayerListener(playerStateService), this)
        
        getCommand("ping")?.apply {
            val cmd = PingCommand(this@ZenPings)
            setExecutor(cmd)
            setTabCompleter(PingTabCompleter())
        }

        logger.info("ZenPings v${description.version} enabled successfully!")
    }

    override fun onDisable() {
        logger.info("ZenPings disabled.")
    }
}
