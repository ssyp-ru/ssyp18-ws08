package GUI

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image
import org.newdawn.slick.Input

class Start(gc: GameContainer) : Button(gc, imageCommon = Image("res/start1.png"), imageLighted = Image("res/start2.png"),
        imageClicked = Image("res/start3.png"), sizeX = gc.screenWidth.toFloat() / 3f, sizeY = gc.screenWidth.toFloat() / 8f,
        xButton = gc.screenWidth.toFloat() / 3f, yButton = gc.screenHeight.toFloat() / 12f) {
    override var state = State.COMMON
    override fun isButtonClicked(gc: GameContainer, x: Float, y: Float): Boolean {
        if (x >= xButton && x <= xButton + sizeX
                && y >= yButton && y <= yButton + sizeY
                && gc.input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
            return true
        }
        return false
    }

    override fun isButtonLighted(gc: GameContainer, x: Float, y: Float): Boolean {
        if (x >= xButton && x <= xButton + sizeX
                && y >= yButton && y <= yButton + sizeY) {
            return true
        }
        return false
    }

    override fun draw(gc: GameContainer, x: Float, y: Float) {
        val flag = gc.input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)
        when (state) {
            State.USED -> state = State.COMMON
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
}