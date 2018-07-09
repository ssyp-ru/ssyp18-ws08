package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.tiled.TiledMap
import java.awt.MouseInfo


class Camera(map: TiledMap, private val mapWidth: Int, private val mapHeight: Int) {
    var x: Int = 0
    var y: Int = 0
    private var viewPort: Rectangle
    private val radius: Int = 16

    init {
        viewPort = Rectangle(0f, 0f, 1920f, 1080f)
    }

    fun translate(g: Graphics, hero: Player, gc: GameContainer) {
        x = (gc.width / 2) - hero.x.toInt() - radius
        y = (gc.height / 2) - hero.y.toInt() - radius

        g.translate(x.toFloat(), y.toFloat())
        viewPort.x = (-x).toFloat()
        viewPort.y = (-y).toFloat()
    }
}