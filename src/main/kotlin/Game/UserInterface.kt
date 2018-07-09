package Game

import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import java.util.HashMap
import kotlin.system.measureTimeMillis

class UserInterface(val gc : GameContainer, val gs : GameState, val nick : String, val cells: Array<Array<Cell>>) {
    var isMinimapCreated = false
    val mapSize = 100
    var minimapImage = Image(mapSize, mapSize)
    val tileSize = 32

    class Minimap(val g: Graphics, var gc: GameContainer, val cells: Array<Array<Cell>>,
                  var minimapImage : Image) {
        val R = 16
        val tileSize = 32
        val minimapSize = gc.height / 4f
        val mapSize = 100

        fun draw(x: Float, y: Float, gs: GameState) {
            val minimapScale = 2f
            minimapImage.draw((x), y, minimapSize, minimapSize)

            for (i in gs.players){
                g.drawOval((i.value.x / tileSize * (minimapSize / mapSize) + x),
                        (i.value.y / tileSize * (minimapSize / mapSize) + y),
                        (minimapSize / mapSize) * 2,  (minimapSize / mapSize) * 2)
                g.fillRect((i.value.x / tileSize * (minimapSize / mapSize) + x),
                        (i.value.y / tileSize * (minimapSize / mapSize) + y),
                        (minimapSize / mapSize) * 2,  (minimapSize / mapSize) * 2)
            }
        }
    }


    fun drawScore(g : Graphics, x : Float, y : Float){
        val stringShift = 20f
        var displacement = stringShift
        g.color = Color.lightGray
        g.drawString("NICK : K / D", x, y)
        for (player in gs.players){
            if (player.value.nick == nick)g.color = Color.yellow
            else g.color = Color.white
            g.drawString("${player.value.nick} : ${player.value.kills} / ${player.value.deaths}", x,
                    y + displacement)
            displacement += stringShift
        }
    }

    fun drawUI(g : Graphics, x : Float, y : Float){
        if (!isMinimapCreated) {
            for (i in 0..(cells.size - 1)) {
                for (j in 0..(cells[i].size - 1)) {
                    when {
                        (cells[i][j].type == layer.ROADS) -> g.color = Color.gray
                        (cells[i][j].type == layer.CRATES) -> g.color = Color.red
                        (cells[i][j].type == layer.GRASS) -> g.color = Color.green
                        (cells[i][j].type == layer.WATER) -> g.color = Color.blue
                        (cells[i][j].type == layer.HOUSES) -> g.color = Color.yellow
                    }
                    g.drawRect((cells[i][j].x / tileSize + x),
                            (cells[i][j].y / tileSize + y), 1f, 1f)
                }
            }
            isMinimapCreated = true
            g.copyArea(minimapImage, 0, 0)
        }
        val minimap = Minimap(g, gc, cells, minimapImage)
        if(!isMinimapCreated)this.isMinimapCreated = true
        minimap.draw(x + gc.width - minimap.minimapSize, y, gs) //
        val tableSHift = 5
        drawScore(g,x + tableSHift, y)
    }

}