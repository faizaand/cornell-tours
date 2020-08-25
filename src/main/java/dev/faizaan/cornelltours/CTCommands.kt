package dev.faizaan.cornelltours

import me.lucko.helper.Commands

object CTCommands {

    private const val defaultPermission = "cornelltours.default"
    private const val adminPermission = "cornelltours.admin"

    fun init(plugin: CornellTours) {
        // /tour
        Commands.create().assertPermission(defaultPermission)
                .assertPlayer()
                .handler { cmd ->
                    TourManager.startTour(cmd.sender())
                    cmd.reply("&aWelcome to Cornell University! Please select your destination.")
                    DestinationGui(cmd.sender()).open()
                }.registerAndBind(plugin, "tour")

        // /endtour
        Commands.create().assertPermission(defaultPermission)
                .assertPlayer()
                .handler { cmd ->
                    TourManager.endTour(cmd.sender())
                    cmd.reply("&aYour tour has been ended! &7You can always type &o/tour&r&7 to keep exploring!")
                }.registerAndBind(plugin, "endtour")

        Commands.create().assertPermission(adminPermission)
                .assertUsage("[subcommand]")
                .assertArgument(0, { s -> s == "list" || s == "info" || s == "reload" }, "&cValid subcommands: list, info, reload")
                .handler { cmd ->
                    val subcmd = cmd.arg(0).value().orElse("")
                    if(subcmd == "list") {
                        val response = StringBuilder("&9&lDestinations: \n")
                        TourManager.destinations.map {
                            x -> response.append("&r• ${x.id} &7${x.loc} &r\n \t${x.description} \n")
                        }
                        cmd.reply(response.toString())
                    } else if(subcmd == "reload") {
                        plugin.reloadConfig()
                        plugin.loadDestinations()
                        cmd.reply("&aReloaded destinations. &7Type &o/destination list&r&7 to make sure it all looks good.")
                    }

                }.registerAndBind(plugin, "destination")
    }
}