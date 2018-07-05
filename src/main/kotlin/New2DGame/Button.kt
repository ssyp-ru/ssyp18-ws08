package New2DGame

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input

abstract class button()
{
    abstract fun isButtonClicked(gc: GameContainer, x : Float, y : Float): Boolean
    abstract fun isButtonLighted(gc: GameContainer, x: Float, y: Float) : Boolean
    var state = State.Common
}




