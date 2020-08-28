package dev.faizaan.cornelltours

import me.lucko.helper.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType

object CTCommands {

    private const val defaultPermission = "cornelltours.default"
    private const val adminPermission = "cornelltours.admin"

    fun init(plugin: CornellTours) {
        // /tour
        Commands.create().assertPermission(defaultPermission)
                .assertPlayer()
                .handler { cmd ->
                    TourManager.startTour(cmd.sender())
                    if (cmd.arg(0).isPresent) {
                        val d: Destination? = TourManager.getDestinationById(cmd.arg(0).value().get().toLowerCase())
                        if (d == null) {
                            cmd.reply("&cI couldn't find a destination with that name. &7Type /tour to view the whole menu.")
                            return@handler
                        }
                        TourManager.nextDestination(d, cmd.sender().uniqueId)
                        cmd.reply("&aFollow me to &l${d.title}&r&a.")
                    } else {
                        cmd.reply("&aWelcome to Cornell University! Please select your destination.")
                        cmd.reply("&7You can end your tour at any time by typing &c/endtour&7.")
                        DestinationGui(cmd.sender()).open()
                    }
                }.registerAndBind(plugin, "tour")

        // /endtour
        Commands.create().assertPermission(defaultPermission)
                .assertPlayer()
                .handler { cmd ->
                    TourManager.endTour(cmd.sender())
                    cmd.reply("&aYour tour has been ended! &7You can always type &o/tour&r&7 to keep exploring!")
                }.registerAndBind(plugin, "endtour")

        Commands.create()
                .assertUsage("[subcommand]")
                .handler { cmd ->
                    val subcmd = cmd.arg(0).value().orElse("")
                    if(!cmd.sender().hasPermission(adminPermission) && subcmd.isNotEmpty()) {
                        cmd.reply("&9Cornell Tours &7v1.0. Written by &aFaizaan &7for &cCornellCraft&7.")
                        cmd.reply("&cOnly admins can perform that command.")
                    }
                    when (subcmd) {
                        "list" -> {
                            val response = StringBuilder("&9&lDestinations: \n")
                            TourManager.destinations.map { x ->
                                response.append("&r• ${x.id} &7${x.loc} &r\n \t${x.description} \n")
                            }
                            cmd.reply(response.toString())
                        }
                        "reload" -> {
                            plugin.reloadConfig()
                            plugin.loadDestinations()
                            cmd.reply("&aReloaded destinations. &7Type &o/destination list&r&7 to make sure it all looks good.")
                        }
                        else -> {
                            cmd.reply("&9Cornell Tours &7v1.0. Written by &aFaizaan &7for &cCornellCraft&7.")
                        }
                    }

                }.registerAndBind(plugin, "destination")

        Commands.create()
                .assertPermission(defaultPermission)
                .assertPlayer().handler { cmd ->
                    if (!TourManager.isWaiting(cmd.sender())) cmd.reply("&cYou don't have a tour guide waiting for you right now. &7Type &a/tour &7to start a tour. If you've &clost your tour guide&7, type &c/endtour &7and then &a/tour&7 to start a new one.")
                    else TourManager.resumeFor(cmd.sender()) // Pathfinder handles "Let's go!" message
                }.registerAndBind(plugin, "resume")

        Commands.create()
                .assertPlayer()
                .handler { cmd ->
                    // two blocks in front, at ground level
                    val loc = cmd.sender().location
                    val newLoc = loc.add(cmd.sender().location.direction.multiply(2))
                    newLoc.y = cmd.sender().world.getHighestBlockAt(loc).y + 1.0
                    Bukkit.getLogger().info(newLoc.toString())
                    cmd.reply("Bar")
                    cmd.sender().world.spawnEntity(newLoc, EntityType.CHICKEN)
                }.registerAndBind(plugin, "foo")
    }
}