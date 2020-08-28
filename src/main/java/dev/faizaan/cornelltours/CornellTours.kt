package dev.faizaan.cornelltours

import me.lucko.helper.plugin.ExtendedJavaPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class CornellTours : ExtendedJavaPlugin() {

    override fun enable() {
        saveDefaultConfig()
        TourManager.init()
        loadDestinations()
        CTCommands.init(this)
        log("Enabled CornellTours v%s by Faizaan Datoo. Licensed under MIT License.", description.version)
    }

    override fun disable() {
        TourManager.destroy()
        log("Successfully disabled. Goodbye!")
    }

    fun loadDestinations() {
        val dests = mutableListOf<Destination>()
        config.getKeys(false).forEach { k ->
            log(k)
            val x = config.getConfigurationSection(k) ?: return
            val loc = Waypoint(x.getDouble("location.x"), x.getDouble("location.y"), x.getDouble("location.z"))
            val adjacents = x.getStringList("adjacent")
            val d = Destination(k, loc, x.getString("title") ?: k, x.getString("subtitle")
                    ?: "", x.getString("description") ?: "&7&oNo description provided.", adjacents)
            dests.add(d)
        }
        TourManager.destinations = dests
    }

    fun log(m: String?, vararg format: Any?) {
        val sender = Bukkit.getConsoleSender()
        var formatted = String.format(m!!, *format)
        formatted = "&7[&9CornellTours&7]&r $formatted" // prefix
        formatted = ChatColor.translateAlternateColorCodes('&', formatted)
        sender.sendMessage(formatted)
    }
}