package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.tiled.TiledMap
import java.awt.MouseInfo


class Camera(map: TiledMap, private val mapWidth: Int, private val mapHeight: Int) {
    private var x: Int = 0
    private var y: Int = 0
    private var viewPort: Rectangle

    init {
        viewPort = Rectangle(0f, 0f, 640f, 480f)
    }

    fun translate(g: Graphics, hero: Player, gc: GameContainer) {
        x = gc.screenWidth / 2 - hero.x.toInt() - 50
        y = gc.screenHeight / 2 - hero.y.toInt() - 40
        g.translate(x.toFloat(), y.toFloat())
        viewPort.x = (-x).toFloat()
        viewPort.y = (-y).toFloat()
    }
}