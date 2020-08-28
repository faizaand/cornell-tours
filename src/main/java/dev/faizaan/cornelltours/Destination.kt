package dev.faizaan.cornelltours

data class Destination(
        val id: String,
        val loc: Waypoint,
        val title: String,
        val subtitle: String,
        val description: String,
        val adjacents: List<String>
)