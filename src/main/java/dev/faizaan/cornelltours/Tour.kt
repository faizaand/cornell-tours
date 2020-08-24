package dev.faizaan.cornelltours

import net.minecraft.server.v1_16_R1.EntityInsentient
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

data class Tour(
        val player: Player,
        val npc: LivingEntity,
        val nms: EntityInsentient,
        var active: Boolean,
        var target: Destination?
)
