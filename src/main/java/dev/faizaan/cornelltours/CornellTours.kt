package dev.faizaan.cornelltours

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class CornellTours : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        TourManager.init()
        CTCommands.init(this)
        log("Enabled CornellTours v%s by Faizaan Datoo. Licensed under MIT License.", description.version)
    }

    override fun onDisable() {
        log("Successfully disabled. Goodbye!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name == "foo") {
            TourManager.startTour(sender as Player)
            return true
        }
        if(command.name == "moveto") {
            TourManager.moveToMe(sender as Player)
            return true
        }
        if(command.name == "endtour") {
            TourManager.endTour(sender as Player)
            return true
        }
        return false;
    }

    fun log(m: String?, vararg format: Any?) {
        val sender = Bukkit.getConsoleSender()
        var formatted = String.format(m!!, *format)
        formatted = "&7[&9CornellTours&7]&r $formatted" // prefix
        formatted = ChatColor.translateAlternateColorCodes('&', formatted)
        sender.sendMessage(formatted)
    }
}