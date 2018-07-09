package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.tiled.TiledMap
import java.awt.MouseInfo


class Camera() {
    var x: Int = 0
    var y: Int = 0
    private var viewPort: Rectangle
    private val radius: Int = 16
    private val WIDTH = 1920f
    private val HEIGHT = 1080f

    init {
        viewPort = Rectangle(0f, 0f, WIDTH, HEIGHT)
    }

    fun translate(g: Graphics, hero: Player, gc: GameContainer) {
        x = (gc.width / 2) - hero.x.toInt() - radius
        y = (gc.height / 2) - hero.y.toInt() - radius

        g.translate(x.toFloat(), y.toFloat())
        viewPort.x = (-x).toFloat()
        viewPort.y = (-y).toFloat()
    }
}