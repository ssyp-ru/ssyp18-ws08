package Game

import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import org.newdawn.slick.util.BufferedImageUtil
import java.util.*


/*class Minimap(val cells: Array<Array<Cell>>, val nick: String){
    fun update (players: HashMap<String, Player>, g: Graphics, gc: GameContainer, minimapImage: Image){
        /*g.color = Color.white
        g.drawRect(0f, 0f, 410f, 410f)
        for (i in 0..(cells.size - 1)){
            for (j in 0..(cells[i].size - 1)){
                when{
                    (cells[i][j].type == 1) -> g.color = Color.gray
                    (cells[i][j].type == 2) -> g.color = Color.red
                    (cells[i][j].type == 0) -> g.color = Color.green
                    (cells[i][j].type == 4) -> g.color = Color.blue
                    (cells[i][j].type == 5) -> g.color = Color.yellow
                }
                g.drawRect((cells[i][j].x / 32 + players[nick]!!.x - (gc.width / 2) + 20),
                        (cells[i][j].y / 32 + players[nick]!!.y - (gc.height / 2) + 20), 4f, 4f)
            }
        }*/
        minimapImage.draw((players[nick]!!.x - (gc.width / 2) + 20), (players[nick]!!.y - (gc.height / 2) + 20),
                gc.height / 3f, gc.height / 3f)
        for (i in players){
            g.color = Color.red
            g.fillRect((i.value.x / 32 + players[nick]!!.x - (gc.width / 2) + 20) * gc.height / 300f,
                    (i.value.y / 32 + players[nick]!!.y - (gc.height / 2) + 20) * gc.height / 300f,
                    gc.height / 300f, gc.height / 300f)
        }
    }
}*/