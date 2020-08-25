# Cornell Tours
This plugin adds Cornell campus tours to Minecraft. Each player has its own personal
villager tour guide. The player can select where it wants to go, and the tour guide will
take them there. If the player strays too far from the tour guide, the guide will come
back to the player and then resume the tour.

## Player Commands
Permission node for the following commands is `cornelltours.default`.
* `/tour` initiates the tour. If a tour is already in progress, brings up the destination selector.
* `/endtour` ends the tour. Does nothing if no tour is in progress.
* `/resume` resumes the tour. Used when the player loses the tour guide and the guide
teleports back to the player. It asks the player if it wants to continue, and they can
either `/resume` or `/endtour`.

## Admin Commands
Permission node for the following commands is `cornelltours.admin`.
* `/destination list` lists all destinations.
* `/destination info <name>` gives information about a specific destination.
* `/destination reload` reloads the destinations from the configuration file.
Destinations are added or removed in config.yml.

