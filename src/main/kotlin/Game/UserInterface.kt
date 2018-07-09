package Game

import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import java.util.HashMap
import kotlin.system.measureTimeMillis

class UserInterface(val gc : GameContainer,
                    var gs : GameState ,
                    val nick : String,
                    val cells: Array<Array<Cell>>,
                    val weaponIcons : Array<Image>) {
    var isMinimapCreated = false
    val mapSize = 100
    var minimapImage = Image(mapSize, mapSize)
    val tileSize = 32
    val sizeInventoryCell = 66f


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

    fun syncState(state : GameState){
        this.gs = state
    }

    fun drawInventory(g : Graphics, x : Float, y : Float){
        val cellShift = 76f
        var displacement = 0f
        g.color = Color(1f, 0f, 1f)
        for (weapon in gs.players[nick]!!.arrayRangedWeapon){
            if (weapon == null) continue
            g.drawRect(x + displacement, y, sizeInventoryCell,sizeInventoryCell)
            weaponIcons[weapon.ID].draw(x + displacement + 1, y + 1, 64f, 64f)
            displacement += cellShift
        }
        for (weapon in gs.players[nick]!!.arrayMeeleeWeapon){
            if (weapon == null) continue
            g.drawRect(x + displacement, y, sizeInventoryCell,sizeInventoryCell)
            weaponIcons[weapon.ID].draw(x + displacement + 1, y + 1, sizeInventoryCell - 2, sizeInventoryCell -2)
            displacement += cellShift
        }
    }


    fun drawScore(g : Graphics, x : Float, y : Float){
        val stringShift = 20f
        var displacement = stringShift
        g.color = Color.lightGray
        g.drawString("NICK : K / D", x, y)
        for (currPlayer in gs.players){
            if (currPlayer.value.nick == nick)g.color = Color.yellow
            else g.color = Color.white
            g.drawString("${currPlayer.value.nick} : ${currPlayer.value.kills} / ${currPlayer.value.deaths}", x,
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
                        (cells[i][j].type == layer.GRASS) -> g.color = Color(15, 70, 8)
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
        minimap.draw(x + gc.width - minimap.minimapSize, y, gs) //
        val tableSHift = 5
        drawScore(g,x + tableSHift, y)
        var inventoryShift = 5
        drawInventory(g, x + inventoryShift,y + gc.height - sizeInventoryCell - inventoryShift)
    }

}