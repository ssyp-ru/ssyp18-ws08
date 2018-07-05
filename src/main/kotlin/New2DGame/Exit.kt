package New2DGame

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input

class Exit(): button()
{
    override fun isButtonClicked(gc : GameContainer, x : Float, y : Float) : Boolean
    {
        if (x >= windowSizeX(gc)/3f && x <= windowSizeX(gc)/1.5f
                && y >= windowSizeY(gc)/1.7f && y <= windowSizeY(gc)/1.7f + windowSizeX(gc)/8f
                && gc.input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
        {
            return true
        }
        return false
    }
    override fun isButtonLighted(gc: GameContainer, x: Float, y: Float): Boolean
    {
        if (x >= windowSizeX(gc)/3f && x <= windowSizeX(gc)/1.5f
                && y >= windowSizeY(gc)/1.7f && y <= windowSizeY(gc)/1.7f + windowSizeX(gc)/8f)
        {
            return true
        }
        return false
    }

}