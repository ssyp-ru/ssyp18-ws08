package game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Rectangle


class Camera(var gc: GameContainer) {
    var x: Int = 0
    var y: Int = 0
    private var viewPort = Rectangle(0f, 0f, gc.width.toFloat(), gc.height.toFloat())
    private val radius: Int = 16

    fun translate(g: Graphics, hero: Player) {
        val cameraShift = 5
        val deltaX = gc.input.mouseX - gc.width / 2
        val deltaY = gc.input.mouseY - gc.height / 2
        x = (gc.width / 2) - hero.x.toInt() - radius
        y = (gc.height / 2) - hero.y.toInt() - radius
        x -= deltaX / cameraShift
        y -= deltaY / cameraShift
        g.translate(x.toFloat(), y.toFloat())
        viewPort.x = (-x).toFloat()
        viewPort.y = (-y).toFloat()
    }
}