package New2DGame

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input

class Back() : button()
{
    override fun isButtonClicked(gc : GameContainer, x : Float, y : Float) : Boolean
    {
        if (x >= 30f && x <= windowSizeX(gc)/6f + 30f
                && y >= 30f && y <= 30f + windowSizeX(gc)/16f
                && gc.input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
        {
            return true
        }
        return false
    }
    override fun isButtonLighted(gc: GameContainer, x: Float, y: Float): Boolean
    {
        if (x >= 30f && x <= windowSizeX(gc)/6f + 30f
                && y >= 30f && y <= 30f + windowSizeX(gc)/16f)
        {
            return true
        }
        return false
    }
}