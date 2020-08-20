package dev.faizaan.cornelltours

import org.bukkit.Location

data class Waypoint(
        val x: Double,
        val y: Double,
        val z: Double
) {
    /**
     * Converts this waypoint to a new Bukkit [Location] object.
     */
    fun toLocation(): Location {
        return Location(CornellTours.defaultWorld, x, y, z);
    }
}