package game

import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image

class UserInterface(val gc: GameContainer,
                    var gs: GameState,
                    val nick: String,
                    private val cells: Array<Array<Cell>>,
                    val weaponIcons: Array<Image>) {
    var isMinimapCreated = false
    private val mapSize = 100
    private var minimapImage = Image(mapSize, mapSize)
    private val tileSize = 32
    private val sizeInventoryCell = 66f


    class MiniMap(val g: Graphics, var gc: GameContainer,
                  var miniMapImage: Image) {
        val tileSize = 32
        val minimapSize = gc.height / 4f
        val mapSize = 100


        fun draw(x: Float, y: Float, gs: GameState) {
            miniMapImage.draw((x), y, minimapSize, minimapSize)

            for (i in gs.players) {
                g.drawOval((i.value.x / tileSize * (minimapSize / mapSize) + x),
                        (i.value.y / tileSize * (minimapSize / mapSize) + y),
                        (minimapSize / mapSize) * 2, (minimapSize / mapSize) * 2)
                g.fillRect((i.value.x / tileSize * (minimapSize / mapSize) + x),
                        (i.value.y / tileSize * (minimapSize / mapSize) + y),
                        (minimapSize / mapSize) * 2, (minimapSize / mapSize) * 2)
            }
        }
    }

    fun syncState(state: GameState) {
        this.gs = state
    }

    private fun drawInventory(g: Graphics, x: Float, y: Float) {
        val cellShift = 76f
        var displacement = 0f
        g.color = Color(1f, 0f, 1f)
        for (weapon in gs.players[nick]!!.arrayRangedWeapon) {
            g.drawRect(x + displacement, y, sizeInventoryCell, sizeInventoryCell)
            weaponIcons[weapon.ID].draw(x + displacement + 1, y + 1, 64f, 64f)
            displacement += cellShift
        }
        for (weapon in gs.players[nick]!!.arrayMeleeWeapon) {
            g.drawRect(x + displacement, y, sizeInventoryCell, sizeInventoryCell)
            weaponIcons[weapon.ID].draw(x + displacement + 1, y + 1, sizeInventoryCell - 2, sizeInventoryCell - 2)
            displacement += cellShift
        }
    }


    private fun drawScore(g: Graphics, x: Float, y: Float) {
        val stringShift = 20f
        var displacement = stringShift
        g.color = Color.lightGray
        g.drawString("NICK : K / D", x, y)
        for (currPlayer in gs.players) {
            if (currPlayer.value.nick == nick) g.color = Color.yellow
            else g.color = Color.white
            g.drawString("${currPlayer.value.nick} : ${currPlayer.value.kills} / ${currPlayer.value.deaths}", x,
                    y + displacement)
            displacement += stringShift
        }
    }

    fun drawUI(g: Graphics, x: Float, y: Float) {
        if (!isMinimapCreated) {
            for (i in 0..(cells.size - 1)) {
                for (j in 0..(cells[i].size - 1)) {
                    when {
                        (cells[i][j].type == LayerType.ROADS) -> g.color = Color.gray
                        (cells[i][j].type == LayerType.CRATES) -> g.color = Color.red
                        (cells[i][j].type == LayerType.GRASS) -> g.color = Color(15, 70, 8)
                        (cells[i][j].type == LayerType.WATER) -> g.color = Color.blue
                        (cells[i][j].type == LayerType.HOUSES) -> g.color = Color.yellow
                    }
                    g.drawRect((cells[i][j].x / tileSize + x),
                            (cells[i][j].y / tileSize + y), 1f, 1f)
                }
            }
            isMinimapCreated = true
            g.copyArea(minimapImage, 0, 0)
        }
        val minimap = MiniMap(g, gc, minimapImage)
        minimap.draw(x + gc.width - minimap.minimapSize, y, gs) //
        val tableSHift = 5
        drawScore(g, x + tableSHift, y)
        val inventoryShift = 5
        drawInventory(g, x + inventoryShift, y + gc.height - sizeInventoryCell - inventoryShift)
    }

}