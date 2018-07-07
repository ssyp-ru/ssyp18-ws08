package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.tiled.TiledMap
import java.awt.MouseInfo


class Camera(map: TiledMap, private val mapWidth: Int, private val mapHeight: Int) {
    private var x: Int = 0
    private var y: Int = 0
    private var viewPort = Rectangle(0f, 0f, 640f, 480f)
    private var coord = MouseInfo.getPointerInfo().location
    private val radius = 20
    init {
    }

    fun translate(g: Graphics, hero: Player, gc: GameContainer) {
        /*coord = MouseInfo.getPointerInfo().location
        x = (-(9 * hero.x + coord.x) / 10).toInt() + gc.screenWidth / 2 - 20
        y = (-(9 * hero.y + coord.y) / 10).toInt() + gc.screenHeight / 2 - 20*/
        /*x = gc.screenWidth / 2 - hero.x.toInt() - 60
        y = gc.screenHeight / 2 - hero.y.toInt() - 40*/
        //println(gc.screenWidth)
       //g.drawString("${gc.width}", 10f, 50f)
        x = (gc.width / 2) - hero.x.toInt() - radius
        y = (gc.height / 2) - hero.y.toInt() - radius

        //x = hero.x.toInt() - ((gc.width * 0.5) -radius).toInt()

        g.translate(x.toFloat(), y.toFloat())
        viewPort.x = (-x).toFloat()
        viewPort.y = (-y).toFloat()
    }
}