package GUI

import org.newdawn.slick.*
import java.awt.MouseInfo

class SimpleSlickGame(gamename: String) : BasicGame(gamename) {
    lateinit var starter: Start
    lateinit var rules: Rules
    lateinit var exit: Exit
    lateinit var back: Back
    @Throws(SlickException::class)
    override fun init(gc: GameContainer) {
        starter = Start(gc)
        rules = Rules(gc)
        exit = Exit(gc)
        back = Back(gc)
    }

    @Throws(SlickException::class)
    override fun update(gc: GameContainer, i: Int) {

    }

    @Throws(SlickException::class)
    override fun render(gc: GameContainer, g: Graphics) {
        val coord = MouseInfo.getPointerInfo().location
        val x = coord.x.toFloat() //координаты мышки
        val y = coord.y.toFloat()//координаты мышки
        if ((starter.state != State.USED && rules.state != State.USED) || back.state == State.USED) {
            back.state = State.COMMON
            starter.draw(gc, x, y)
            rules.draw(gc, x, y)
            exit.draw(gc, x, y)
        }
        if (starter.state == State.USED || rules.state == State.USED) {
            back.draw(gc, x, y)
        }
    }
}