package New2DGame

import org.newdawn.slick.*
import org.newdawn.slick.Input.MOUSE_LEFT_BUTTON
import java.awt.MouseInfo

fun windowSizeX(gc: GameContainer) : Float
{
    val xScreen = gc.screenWidth.toFloat()
    return xScreen
}

fun windowSizeY(gc: GameContainer) : Float
{
    val yScreen = gc.screenHeight.toFloat()
    return yScreen
}



class SimpleSlickGame(gamename: String) : BasicGame(gamename)
{
    var starter = Starter()
    val rules = Rules()
    val exit = Exit()
    val back = Back()
    /*var flagForStart : Boolean = false
    var flagForRules : Boolean = false
    var flagForExit : Boolean = false
    var flagForBack : Boolean = false
    var checker:Boolean = false //проверяет нажата ли кнопка, чтобы не перерисовывать весь render полностью*/
    @Throws(SlickException::class)
    override fun init(gc: GameContainer)
    {

    }
    @Throws(SlickException::class)
    override fun update(gc: GameContainer, i: Int)
    {

    }
    @Throws(SlickException::class)
    override fun render(gc: GameContainer, g: Graphics) {
        val imageExit1 = Image("res/exit1.png")
        val coord = MouseInfo.getPointerInfo().location
        val x = coord.x.toFloat() //координаты мышки
        val y = coord.y.toFloat()//координаты мышки
        if (starter.state == State.Used)
        {
            //запусти игру
        }
        else if (rules.state == State.Used)
        {
            if (back.state == State.Clicked && !gc.input.isMouseButtonDown(MOUSE_LEFT_BUTTON))
            {
                rules.state = State.Common
                back.state = State.Common
            }
            else if (!back.isButtonLighted(gc, x, y))
            {
                val imageBack = Image("res/back1.png")
                back.state = State.Common//flagForBack = false
                imageBack.draw(30f, 30f, windowSizeX(gc) / 6f, windowSizeX(gc) / 16f)
            }
            else
            {
                if (gc.input.isMouseButtonDown(MOUSE_LEFT_BUTTON))
                {
                    val imageBack = Image("res/back3.png")
                    imageBack.draw(30f, 30f, windowSizeX(gc) / 6f, windowSizeX(gc) / 16f)
                    back.state = State.Clicked
                }
                else
                {
                    val imageBack = Image("res/back2.png")
                    imageBack.draw(30f, 30f, windowSizeX(gc) / 6f, windowSizeX(gc) / 16f)
                    back.state = State.Lighted
                }
            }
        }
        else if (exit.state == State.Used)
        {
            gc.exit()
        }
        else
        {
            if (starter.state == State.Clicked && !gc.input.isMouseButtonDown(MOUSE_LEFT_BUTTON))
            {
                starter.state = State.Used
            }
            else if (!starter.isButtonLighted(gc, x, y)) // все состояния кнопки START
            {
                val imageStart = Image("res/start1.png")
                starter.state = State.Common//flagForStart = false
                imageStart.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 12f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
            }
            else
            {
                if (gc.input.isMouseButtonDown(MOUSE_LEFT_BUTTON))
                {
                    val imageStart = Image("res/start3.png")
                    imageStart.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 12f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
                    starter.state = State.Clicked
                }
                else
                {
                    val imageStart = Image("res/start2.png")
                    imageStart.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 12f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
                    starter.state = State.Lighted
                }
            }
            if (rules.state == State.Clicked && !gc.input.isMouseButtonDown(MOUSE_LEFT_BUTTON))
            {
                rules.state = State.Used
            }
            else if (!rules.isButtonLighted(gc, x, y)) // се состояния кнопки RULES
            {
                rules.state = State.Common
                val imageRules = Image("res/rules1.png")
                imageRules.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 3f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
            }
            else
            {
                if (gc.input.isMouseButtonDown(MOUSE_LEFT_BUTTON))
                {
                    val imageRules = Image("res/rules3.png")
                    imageRules.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 3f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
                    rules.state = State.Clicked
                }
                else
                {
                    val imageRules = Image("res/rules2.png")
                    imageRules.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 3f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
                    rules.state = State.Lighted
                }
            }
            if (exit.state == State.Clicked && !gc.input.isMouseButtonDown(MOUSE_LEFT_BUTTON))
            {
                exit.state = State.Used
            }
            else if (!exit.isButtonLighted(gc, x, y))// все состояния кнопки EXIT
            {
                exit.state = State.Common
                imageExit1.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 1.7f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
            }
            else
            {
                if (gc.input.isMouseButtonDown(MOUSE_LEFT_BUTTON))
                {
                    val imageExit = Image("res/exit3.png")
                    imageExit.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 1.7f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
                    exit.state = State.Clicked
                }
                else
                {
                    val imageExit2 = Image("res/exit2.png")
                    imageExit2.draw(windowSizeX(gc) / 3f, windowSizeY(gc) / 1.7f, windowSizeX(gc) / 3f, windowSizeX(gc) / 8f)
                    exit.state = State.Lighted
                }

            }
        }
    }
}


