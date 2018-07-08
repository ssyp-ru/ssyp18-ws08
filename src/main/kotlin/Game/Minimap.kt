package Game

import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import org.newdawn.slick.util.BufferedImageUtil
import java.util.*


class Minimap(val cells: Array<Array<Cell>>, val nick: String) {
    val radius = 20
    val tile = 32
    fun update(players: HashMap<String, Player>, g: Graphics, gc: GameContainer, minimapImage: Image) {
        g.drawImage(minimapImage, (players[nick]!!.x - (gc.width / 2) + radius),
                (players[nick]!!.y - (gc.height / 2) + radius))
        for (i in players) {
            g.color = Color.red
            g.fillRect((i.value.x / tile + players[nick]!!.x - (gc.width / 2) + radius) * gc.height / 300f,
                    (i.value.y / tile + players[nick]!!.y - (gc.height / 2) + radius) * gc.height / 300f,
                    gc.height / 300f, gc.height / 300f)
        }
    }
}