package dev.faizaan.cornelltours

import kotlin.math.pow
import kotlin.math.sqrt

data class Edge(
        val start: Destination,
        val end: Destination,
        val distance: Double = calcDistance(start, end)
)

fun calcDistance(start: Destination, end: Destination): Double {
    // sqrt[(x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2]
    return sqrt(
            (end.loc.x - start.loc.x).pow(2) +
                    (end.loc.y - start.loc.y).pow(2) +
                    (end.loc.y - start.loc.y).pow(2)
    )
}