package GUI

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image

abstract class Button(gc: GameContainer, val imageCommon : Image ,val imageLighted : Image, val imageClicked : Image,
                      val sizeX : Float, val sizeY : Float, val xButton : Float, val yButton : Float ) {
    abstract fun isButtonClicked(gc: GameContainer, x: Float, y: Float): Boolean
    abstract fun isButtonLighted(gc: GameContainer, x: Float, y: Float): Boolean
    abstract fun draw(gc: GameContainer, x: Float, y: Float)
    open var state = State.COMMON
    val xScreen = gc.screenWidth.toFloat()
    val yScreen = gc.screenHeight.toFloat()
}