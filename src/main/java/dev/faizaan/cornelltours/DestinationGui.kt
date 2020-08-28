package dev.faizaan.cornelltours

import com.google.common.math.DoubleMath.roundToInt
import me.lucko.helper.item.ItemStackBuilder
import me.lucko.helper.menu.Gui
import me.lucko.helper.utils.Players
import org.bukkit.Material
import org.bukkit.entity.Player
import java.math.RoundingMode
import kotlin.math.max


class DestinationGui(player: Player)
    : Gui(player, max(1, roundToInt(TourManager.destinations.size / 9.0, RoundingMode.CEILING)), "Choose a destination") {

    private val colors = listOf('a', '9', '2', '1', '3', 'b', '4', 'c', '5', 'd', '6', 'e')

    override fun redraw() {
        if (isFirstDraw) {
            var ci = 0
            TourManager.destinations.forEach { d ->
                // cycle through colors
                val color = "&" + colors[ci++]
                if(ci >= colors.size) ci = 0
                addItem(ItemStackBuilder.of(Material.COMPASS)
                        .name(color + d.title)
                        .build {
                            val t = TourManager.activeTours[player.uniqueId] ?: return@build
                            t.target = d
                            close()
                            Players.msg(player, "&aFollow me to &l${d.title}&r&a.")
                        })
            }
        }
    }
}