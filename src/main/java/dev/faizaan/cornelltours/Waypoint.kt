package dev.faizaan.cornelltours

import org.bukkit.Location
import org.bukkit.World

data class Waypoint(
        val x: Double,
        val y: Double,
        val z: Double
) {
    /**
     * Converts this waypoint to a new Bukkit [Location] object.
     */
    fun toLocation(world: World): Location {
        return Location(world, x, y, z);
    }
}