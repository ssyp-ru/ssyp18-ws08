package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.tiled.TiledMap
import java.awt.MouseInfo


class Camera(map: TiledMap, private val mapWidth: Int, private val mapHeight: Int) {
    private var x: Int = 0
    private var y: Int = 0
    private var viewPort = Rectangle(0f, 0f, 1920f, 1080f)
    private var coord = MouseInfo.getPointerInfo().location

    init {
    }

    fun translate(g: Graphics, hero: Player, gc: GameContainer) {
        coord = MouseInfo.getPointerInfo().location
        x = (-(9 * hero.x + coord.x) / 10).toInt() + gc.screenWidth / 2 - 20
        y = (-(9 * hero.y + coord.y) / 10).toInt() + gc.screenHeight / 2 - 20
        g.translate(x.toFloat(), y.toFloat())
        viewPort.x = (-x).toFloat()
        viewPort.y = (-y).toFloat()
    }
}