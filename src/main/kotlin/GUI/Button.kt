package GUI

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import org.newdawn.slick.Input

abstract class Button(gc: GameContainer, val imageCommon : Image ,val imageLighted : Image, val imageClicked : Image,
                      val sizeX : Float, val sizeY : Float, val xButton : Float, val yButton : Float ) {
    fun isButtonLighted(gc: GameContainer, x: Float, y: Float): Boolean{
        if (x >= xButton && x <= xButton + sizeX
                && y >= yButton && y <= yButton + sizeY) {
            return true
        }
        return false
    }
    fun draw(gc: GameContainer, x: Float, y: Float){
        val flag = gc.input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
        when (state) {
            State.COMMON -> imageCommon.draw(xButton, yButton, sizeX, sizeY)
            State.LIGHTED -> imageLighted.draw(xButton, yButton, sizeX, sizeY)
            State.CLICKED -> imageClicked.draw(xButton, yButton, sizeX, sizeY)

        }
        when {
            state == State.CLICKED && !flag -> state = State.USED
            !isButtonLighted(gc, x, y) -> state = State.COMMON
            flag && isButtonLighted(gc, x, y) -> state = State.CLICKED
            isButtonLighted(gc, x, y) && !flag -> state = State.LIGHTED
        }
    }
    open var state = State.COMMON
}