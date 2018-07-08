package Game

import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import org.newdawn.slick.util.BufferedImageUtil
import java.util.*


class Minimap(val cells: Array<Array<Cell>>, val nick: String, var gc: GameContainer){
    val R = 16
    val tileSize = 32
    val minimapSize = gc.height / 4f
    val mapSize = 100
    var isMinimapCreated = false
    var minimapImage = Image(mapSize, mapSize)

    fun draw(players: HashMap<String, Player>, g: Graphics){
        if (!isMinimapCreated){
            for (i in 0..(cells.size - 1)){
                for (j in 0..(cells[i].size - 1)){
                    when{
                        (cells[i][j].type == layer.ROADS) -> g.color = Color.gray
                        (cells[i][j].type == layer.CRATES) -> g.color = Color.red
                        (cells[i][j].type == layer.GRASS) -> g.color = Color.green
                        (cells[i][j].type == layer.WATER) -> g.color = Color.blue
                        (cells[i][j].type == layer.HOUSES) -> g.color = Color.yellow
                    }
                    g.drawRect((cells[i][j].x / tileSize + players[nick]!!.x - (gc.width / 2) + R),
                            (cells[i][j].y / tileSize + players[nick]!!.y - (gc.height / 2) + R), 1f, 1f)
                }
            }
            isMinimapCreated = true
            g.copyArea(minimapImage, 0, 0)
        }else{
            minimapImage.draw((players[nick]!!.x + (gc.width / 2) + R - minimapSize),
                    (players[nick]!!.y - (gc.height / 2) + R), minimapSize, minimapSize)
        }
        for (i in players){
            g.color = Color.red
            g.fillRect((i.value.x / tileSize + players[nick]!!.x - (gc.width / 2) + R) * minimapSize / mapSize,
                    (i.value.y / tileSize + players[nick]!!.y - (gc.height / 2) + R) * minimapSize / mapSize,
                    minimapSize / mapSize,  minimapSize / mapSize)
        }
    }
}