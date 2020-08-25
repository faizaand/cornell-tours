package dev.faizaan.cornelltours

import me.lucko.helper.plugin.ExtendedJavaPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.MemorySection
import org.bukkit.entity.Player

class CornellTours : ExtendedJavaPlugin() {

    override fun enable() {
        saveDefaultConfig()
        TourManager.init()
        loadDestinations()
        CTCommands.init(this)
        log("Enabled CornellTours v%s by Faizaan Datoo. Licensed under MIT License.", description.version)
    }

    override fun disable() {
        log("Successfully disabled. Goodbye!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name == "foo") {
            TourManager.startTour(sender as Player)
            return true
        }
        if (command.name == "moveto") {
            TourManager.moveToMe(sender as Player)
            return true
        }
        if (command.name == "endtour") {
            TourManager.endTour(sender as Player)
            return true
        }
        return false;
    }

    fun loadDestinations() {
        val dests = mutableListOf<Destination>()
        config.getKeys(false).forEach { k ->
            log(k)
            val x = config.getConfigurationSection(k) ?: return
            val loc = Waypoint(x.getDouble("location.x"), x.getDouble("location.y"), x.getDouble("location.z"))
            val d = Destination(k, loc, x.getString("title") ?: k, x.getString("subtitle") ?: "", x.getString("description") ?: "&7&oNo description provided.")
            // TODO adjacency
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