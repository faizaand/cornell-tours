package dev.faizaan.cornelltours

import net.minecraft.server.v1_16_R1.EntityInsentient
import net.minecraft.server.v1_16_R1.PathfinderGoal
import org.bukkit.entity.Player

/**
 * Pathfinding behavior for the villager tour guides.
 * Guides the player to a position,
 */
class PathfinderGoalGuidePlayer(val entity: EntityInsentient, val tour: Tour) : PathfinderGoal() {

    private var player: Player? = null
    private var target: Destination? = null

    // should I run?
    override fun a(): Boolean {
        if(!tour.active || tour.target == null) return false
        this.target = tour.target
        this.player = tour.player
        return true
    }

    override fun c() {
        if(target == null) return
        val dest = target!!.loc.toLocation(player!!.world)
        val path = entity.navigation.a(dest.x, dest.y, dest.z, 0)
        entity.navigation.a(path, 0.8)
    }

}