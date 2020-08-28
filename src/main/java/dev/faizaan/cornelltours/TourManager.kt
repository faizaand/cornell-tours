package dev.faizaan.cornelltours

import me.lucko.helper.Events
import me.lucko.helper.utils.Players
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import net.minecraft.server.v1_16_R1.EntityHuman
import net.minecraft.server.v1_16_R1.EntityInsentient
import net.minecraft.server.v1_16_R1.PathfinderGoalLookAtPlayer
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*


object TourManager {

    var activeTours: MutableMap<UUID, Tour> = mutableMapOf()
    var destinations: MutableList<Destination> = mutableListOf()
    var waitingFor: MutableMap<UUID, () -> Unit> = mutableMapOf()
    var alreadyShowedTo: MutableList<UUID> = mutableListOf()

    /**
     * Registers event listeners.
     */
    fun init() {
        Events.subscribe(PlayerMoveEvent::class.java)
                .filter { x -> activeTours.containsKey(x.player.uniqueId) }
                .filter { x -> activeTours[x.player.uniqueId]!!.active }
                .handler { event ->
                    if(activeTours[event.player.uniqueId]!!.target == null) return@handler

                    val tour = activeTours[event.player.uniqueId]!!
                    val loc = tour.target!!.loc

                    // Bukkit.getLogger().info("X: ${event.player.location.x} vs ${loc.x}; Y: ${event.player.location.y} vs ${loc.y}; Z: ${event.player.location.z} vs ${loc.z}")

                    if (inRange(event.player.location, loc.toLocation(event.player.world), 5.0) && !alreadyShowedTo.contains(event.player.uniqueId)) {
                        // reached destination
                        event.player.sendTitle(tour.target!!.title, tour.target!!.subtitle)
                        Players.msg(
                                event.player,
                                "&r",
                                tour.target!!.title,
                                "&7&o" + tour.target!!.title,
                                "&r",
                                tour.target!!.description,
                                "&r",
                                "&r",
                                "&aSelect a nearby place to go next from below, or punch me to view the whole menu!"
                        )

                        // print adjacents
                        for (adjacent in tour.target!!.adjacents) {
                            val a: Destination = getDestinationById(adjacent) ?: continue
//                            val next = TextComponent(ChatColor.translateAlternateColorCodes('&', "&7• Click here to go to &a&l${a.title}."))
//                            next.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tour ${a.id}")
//                            next.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("Let's go to ${a.title}!"))
//                            event.player.spigot().sendMessage(next)
                            Players.msg(event.player, "&7• Type &a/tour ${a.id} &7to go to &a&l${a.title}.")
                        }


                        alreadyShowedTo.add(event.player.uniqueId)

                        tour.target = null // clear this waypoint
                        activeTours[event.player.uniqueId]?.target = null
                    }
                }

        Events.subscribe(EntityDamageEvent::class.java)
                .filter { x -> x.entityType == EntityType.VILLAGER }
                .handler { e -> e.isCancelled = true }

        Events.subscribe(EntityDamageByEntityEvent::class.java)
                .filter { x -> x.entityType == EntityType.VILLAGER }
                .handler { e ->
                    e.isCancelled = true
                    if (activeTours.containsKey(e.damager.uniqueId)) {
                        if (waitingFor.contains(e.damager.uniqueId)) {
                            resumeFor(e.damager as Player)
                        } else {
                            DestinationGui(e.damager as Player).open()
                        }
                    }
                }

        Events.subscribe(PlayerInteractEntityEvent::class.java)
                .filter { x -> x.rightClicked.type == EntityType.VILLAGER }
                .handler { e ->
                    e.isCancelled = true
                    if (activeTours.containsKey(e.player.uniqueId)) {
                        if (waitingFor.contains(e.player.uniqueId)) {
                            resumeFor(e.player)
                        } else {
                            DestinationGui(e.player).open()
                        }
                    }
                }

        Events.subscribe(PlayerQuitEvent::class.java).filter { x -> activeTours.containsKey(x.player.uniqueId) }.handler { e -> endTour(e.player) }
    }

    fun destroy() {
        // kill the tour guides
        activeTours.values.forEach { x -> x.npc.remove() }
    }

    fun nextDestination(d: Destination, uid: UUID) {
        alreadyShowedTo.remove(uid)
        val t = activeTours[uid] ?: return
        t.target = d
    }

    fun getDestinationById(id: String): Destination? {
        return destinations.firstOrNull { x -> x.id.toLowerCase() == id }
    }

    fun startTour(player: Player) {
        if (activeTours.containsKey(player.uniqueId)) return

        // two blocks in front, at ground level
        val entityLoc = player.location.add(player.location.direction.multiply(2))
        entityLoc.y = player.world.getHighestBlockAt(player.location).y + 3.0

        val entity: LivingEntity = player.world.spawnEntity(entityLoc, EntityType.VILLAGER) as LivingEntity
        entity.customName = ChatColor.translateAlternateColorCodes('&', "&b${player.displayName}'s &9Tour Guide")
        entity.isCustomNameVisible = true
        (entity as Villager).profession = Villager.Profession.NITWIT

        val nms = toNmsEntity(entity)
        NmsUtils.clearGoals(nms)
        nms.goalSelector.a(8, PathfinderGoalLookAtPlayer(nms, EntityHuman::class.java, 8.0F))

        val tour = Tour(
                player,
                entity,
                nms,
                true,
                null
        )
        activeTours[player.uniqueId] = tour

        nms.goalSelector.a(8, PathfinderGoalGuidePlayer(nms, tour))
    }

    fun endTour(player: Player) {
        val t = activeTours[player.uniqueId] ?: return
        t.active = false // stop guide
        t.npc.remove() // kill NPC
        activeTours.remove(player.uniqueId)
    }

    /**
     * When the villager loses the player, it'll come back and
     * invoke this method to ask the player to resume.
     */
    fun waitFor(player: Player, callback: () -> Unit): Boolean {
        if (waitingFor.containsKey(player.uniqueId)) return false
        waitingFor[player.uniqueId] = callback
        return true
    }

    fun isWaiting(player: Player): Boolean {
        return waitingFor.containsKey(player.uniqueId)
    }

    fun resumeFor(player: Player): Boolean {
        if (!waitingFor.containsKey(player.uniqueId)) return false
        waitingFor[player.uniqueId]?.invoke()
        waitingFor.remove(player.uniqueId)
        return true
    }

    /**
     * Converts a Bukkit entity to an NMS entity.
     */
    private fun toNmsEntity(entity: LivingEntity): EntityInsentient {
        return (entity as CraftLivingEntity).handle as EntityInsentient;
    }

    /**
     * Checks if [cur] is within a [rad] block radius of [tar].
     * Ignores y for now
     */
    private fun inRange(cur: Location, tar: Location, rad: Double): Boolean {
        val minX = tar.x - rad
        val maxX = tar.x + rad
//        val minY = tar.y - rad
//        val maxY = tar.y + rad
        val minZ = tar.z - rad
        val maxZ = tar.z + rad
        return cur.x in minX..maxX /* && cur.y in minY..maxY */ && cur.z in minZ..maxZ
    }

}