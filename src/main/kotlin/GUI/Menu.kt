package GUI

import Game.Game
import org.newdawn.slick.*
import org.newdawn.slick.gui.TextField
import java.awt.Font
import java.awt.MouseInfo

class SimpleSlickGame(gamename: String) : BasicGame(gamename) {
    lateinit var menu: Regulations
    lateinit var starter: Start
    lateinit var rules: Rules
    lateinit var exit: Exit
    lateinit var back: Back
    lateinit var sng: StartNewGame
    lateinit var sjg: StartJoinGame
    lateinit var text1: TextField
    lateinit var text2: TextField
    lateinit var maingame: Game

    var gameStarted = false
    @Throws(SlickException::class)
    override fun init(gc: GameContainer) {
        starter = Start(gc)
        rules = Rules(gc)
        exit = Exit(gc)
        back = Back(gc)
        menu = Regulations(gc)
        sng = StartNewGame(gc)
        sjg = StartJoinGame(gc)
        text1 = TextField(gc, TrueTypeFont(Font("Comic Sans MS", Font.BOLD, 35),
                false), gc.width / 3, (gc.height / 1.7f).toInt(), gc.width / 3, gc.width / 16)
        text2 = TextField(gc, TrueTypeFont(Font("Comic Sans MS", Font.BOLD, 35),
                false), gc.width / 3, (gc.height / 1.4f).toInt(), gc.width / 3, gc.width / 16)
        text1.text = "Nick"
        text2.text = "Lobby"
    }

    @Throws(SlickException::class)
    override fun update(gc: GameContainer, i: Int) {
        var nick: String
        var lobby: String
        if (sjg.state == State.USED || sng.state == State.USED) {
            nick = text1.text
            lobby = text2.text
            maingame = Game(gc, lobby, nick, sng.state == State.USED)
            sng.state = State.COMMON
            sjg.state = State.COMMON
            gameStarted = true
        }
        if (gameStarted) {
            maingame.update()
            gameStarted=!maingame.exited
        }
    }

    @Throws(SlickException::class)
    override fun render(gc: GameContainer, g: Graphics) {
        val coord = MouseInfo.getPointerInfo().location
        val x = coord.x.toFloat() //координаты мышки`
        val y = coord.y.toFloat()//координаты мышки
        if (!gameStarted) {
            if ((starter.state != State.USED && rules.state != State.USED) || back.state == State.USED) {
                back.state = State.COMMON
                starter.draw(gc, x, y)
                rules.draw(gc, x, y)
                exit.draw(gc, x, y)
            }
            if (rules.state == State.USED) {
                menu.draw(gc, x, y)
                menu.state = State.COMMON
                back.draw(gc, x, y)
            }
            if (starter.state == State.USED && !(sjg.state == State.USED || sng.state == State.USED)) {
                sjg.draw(gc, x, y)
                sng.draw(gc, x, y)
                back.draw(gc, x, y)
                text1.render(gc, g)
                text2.render(gc, g)
            }

            if (exit.state == State.USED) {
                gc.exit()
            }
        } else maingame.render(g)
    }
}