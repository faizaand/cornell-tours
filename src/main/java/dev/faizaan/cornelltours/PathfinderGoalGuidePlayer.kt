package dev.faizaan.cornelltours

import me.lucko.helper.utils.Players
import net.minecraft.server.v1_16_R1.EntityInsentient
import net.minecraft.server.v1_16_R1.PathfinderGoal
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Pathfinding behavior for the villager tour guides.
 * Guides the player to a position,
 */
class PathfinderGoalGuidePlayer(private val entity: EntityInsentient, private val tour: Tour) : PathfinderGoal() {

    private var player: Player? = null
    private var target: Destination? = null

    // should I run?
    override fun a(): Boolean {
        if (!tour.active || tour.target == null) return false
        this.target = tour.target
        this.player = tour.player
        return true
    }

    override fun c() {
        if (target == null) return
        val dest = target!!.loc.toLocation(player!!.world)
        val path = entity.navigation.a(dest.x, dest.y, dest.z, 0)
        entity.navigation.a(path, 0.70)
    }

    private var lastLoc: Location? = null

    override fun e() {
        if (player == null) return
        if (isFar(entity.locX(), entity.locY(), entity.locZ()) || TourManager.isWaiting(player!!)) {
            if (lastLoc == null) {
                lastLoc = player!!.location
            }

            val currLoc = player!!.location

            if(lastLoc!!.x != currLoc.x || lastLoc!!.z != currLoc.z) {
                val pLoc = currLoc.add(currLoc.direction.multiply(-3))
                pLoc.y = player!!.world.getHighestBlockAt(player!!.location).y + 1.0
            }

            val path = entity.navigation.a(lastLoc!!.x, lastLoc!!.y, lastLoc!!.z, 0)
            entity.navigation.a(path, 0.8)

            val waiting = TourManager.waitFor(player!!) {
                Players.msg(player!!, "&aLet's go!")
                lastLoc = null
            }

            if (waiting)
                Players.msg(player!!, "&cYou've fallen behind! &7I'll wait here. Type &a/resume &7when you're ready to continue (or punch me). Or, type &c/endtour to stop the tour.")
        } else {
            c()
        }
    }

    private fun isFar(x: Double, y: Double, z: Double): Boolean {
        if (player == null) return false
        val dist = sqrt(
                (x - player!!.location.x).pow(2.0)
                        + (y - player!!.location.y).pow(2.0)
                        + (z - player!!.location.z).pow(2.0)
        )
        return dist > 20.0
    }

}