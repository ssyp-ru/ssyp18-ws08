package gui

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image
import org.newdawn.slick.Input

abstract class Button(var imageCommon: Image,
                      var imageLighted: Image,
                      var imageClicked: Image,
                      var sizeX: Float,
                      var sizeY: Float,
                      var xButton: Float,
                      var yButton: Float) {
    private fun isButtonLighted(x: Float, y: Float): Boolean {
        if (x >= xButton && x <= xButton + sizeX
                && y >= yButton && y <= yButton + sizeY) {
            return true
        }
        return false
    }


    fun draw(gc: GameContainer, x: Float, y: Float) {
        val flag = gc.input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
        when (state) {
            State.COMMON -> imageCommon.draw(xButton, yButton, sizeX, sizeY)
            State.LIGHTED -> imageLighted.draw(xButton, yButton, sizeX, sizeY)
            State.CLICKED -> imageClicked.draw(xButton, yButton, sizeX, sizeY)
            State.USED -> {}
        }
        when {
            state == State.CLICKED && !flag -> state = State.USED
            !isButtonLighted(x, y) -> state = State.COMMON
            flag && isButtonLighted(x, y) -> state = State.CLICKED
            isButtonLighted(x, y) && !flag -> state = State.LIGHTED
        }
    }
    open var state = State.COMMON
}