package dev.faizaan.cornelltours

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

object CornellTours : JavaPlugin() {

    var defaultWorld: World? = null

    override fun onEnable() {
        saveDefaultConfig()

        this.defaultWorld = Bukkit.getWorlds().first { world -> world.name == config.getString("defaultWorld") }

        log("Enabled CornellTours v%s by Faizaan Datoo. Licensed under MIT License.", description.version)
    }

    override fun onDisable() {
        log("Successfully disabled. Goodbye!")
    }

    fun log(m: String?, vararg format: Any?) {
        val sender = Bukkit.getConsoleSender()
        var formatted = String.format(m!!, *format)
        formatted = "&7[&9CornellTours&7]&r $formatted" // prefix
        formatted = ChatColor.translateAlternateColorCodes('&', formatted)
        sender.sendMessage(formatted)
    }
}