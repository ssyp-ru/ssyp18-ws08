package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle


class Camera() {
    var x: Int = 0
    var y: Int = 0
    private var viewPort: Rectangle
    private val radius: Int = 16
    private val WIDTH = 1366f
    private val HEIGHT = 768f

    init {
        viewPort = Rectangle(0f, 0f, WIDTH, HEIGHT)
    }

    fun translate(g: Graphics, hero: Player, gc: GameContainer) {
        var deltaX = gc.input.mouseX - gc.width / 2
        var deltaY = gc.input.mouseY - gc.height / 2
        x = (gc.width / 2) - hero.x.toInt() - radius
        y = (gc.height / 2) - hero.y.toInt() - radius
        x -= deltaX/5
        y -= deltaY/5

        g.translate(x.toFloat(), y.toFloat())
        viewPort.x = (-x).toFloat()
        viewPort.y = (-y).toFloat()
    }
}