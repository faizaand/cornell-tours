package dev.faizaan.cornelltours

import me.lucko.helper.Events
import me.lucko.helper.utils.Players
import net.minecraft.server.v1_16_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*


object TourManager {

    var activeTours: MutableMap<UUID, Tour> = mutableMapOf()
    var destinations: MutableList<Destination> = mutableListOf()

    fun init() {
        Events.subscribe(PlayerMoveEvent::class.java)
                .filter { x -> activeTours.containsKey(x.player.uniqueId) }
                .filter { x -> activeTours[x.player.uniqueId]!!.active }
                .filter { x -> activeTours[x.player.uniqueId]!!.target != null }
                .handler { event ->
                    val tour = activeTours[event.player.uniqueId]!!
                    val loc = tour.target!!.loc

                    // Bukkit.getLogger().info("X: ${event.player.location.x} vs ${loc.x}; Y: ${event.player.location.y} vs ${loc.y}; Z: ${event.player.location.z} vs ${loc.z}")

                    if (inRange(event.player.location, loc.toLocation(event.player.world), 5.0)) {
                        // reached destination
                        event.player.sendTitle(tour.target!!.title, tour.target!!.subtitle)
                        Players.msg(
                                event.player,
                                "&8.-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-.",
                                tour.target!!.title,
                                "&7&o" + tour.target!!.title,
                                "&r",
                                tour.target!!.description,
                                "&8.-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-.",
                                "&r",
                                "&aPunch me or type &o/tours&r&a to select your next destination!"
                        )
                        // TODO adjacency


                        tour.target = null // clear this waypoint
                    }
                }

        Events.subscribe(EntityDamageByEntityEvent::class.java)
                .filter { x -> x.entityType == EntityType.VILLAGER }
                .handler { e ->
                    e.isCancelled = true
                    if(activeTours.containsKey(e.damager.uniqueId)) {
                        DestinationGui(e.damager as Player).open()
                    }
                }
    }

    fun startTour(player: Player) {
        if (activeTours.containsKey(player.uniqueId)) return

        val entity: LivingEntity = player.world.spawnEntity(player.location, EntityType.VILLAGER) as LivingEntity

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

    fun moveToMe(player: Player) {
        val t = activeTours[player.uniqueId] ?: return
        // todo figure out how to get them to not have an AI but still pathfind
        // once you do that, have it navigate to a waypoint and wait for the player.
        // if it gets too far, have it teleport back to the player and ask to continue
        // if player wants to continue (/resume) then continue to waypoint
        // when it arrives at waypoint, stop
//        val path = t.nms.navigation.a(player.location.x, player.location.y, player.location.z, 0)
//        t.nms.navigation.a(path, 1.0)
        t.target = Destination("Foo", Waypoint(player.location.x + 20, player.location.y + 20, player.location.z + 20), "Test", "Test test", "Foobar")
    }

    private fun toNmsEntity(entity: LivingEntity): EntityInsentient {
        return (entity as CraftLivingEntity).handle as EntityInsentient;
    }

    fun endTour(player: Player) {
        val t = activeTours[player.uniqueId] ?: return
        t.active = false // stop guide
        t.npc.remove() // kill NPC
        activeTours.remove(player.uniqueId)
        player.sendMessage("Tour over wabooosh")
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