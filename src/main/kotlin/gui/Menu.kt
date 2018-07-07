package gui

import game.Game
import org.newdawn.slick.*
import org.newdawn.slick.gui.TextField
import java.awt.Font

class Menu(gameName: String) : BasicGame(gameName) {
    private lateinit var menu: Regulations
    private lateinit var starter: Start
    private lateinit var rules: Rules
    private lateinit var exit: Exit
    private lateinit var back: Back
    private lateinit var sng: StartNewGame
    private lateinit var sjg: StartJoinGame
    private lateinit var text1: TextField
    private lateinit var text2: TextField
    private lateinit var maingame: Game
    private var exited = false
    private var gameStarted = false

    override fun init(gc: GameContainer) {
        starter = Start(gc)
        rules = Rules(gc)
        exit = Exit(gc)
        back = Back(gc)
        menu = Regulations(gc)
        sng = StartNewGame(gc)
        sjg = StartJoinGame(gc)
        text1 = TextField(gc, TrueTypeFont(Font("Comic Sans MS", Font.BOLD, 35),
                false), gc.width / 3,
                (gc.height / 1.7f).toInt(), gc.width / 3, gc.height / 8)
        text2 = TextField(gc, TrueTypeFont(Font("Comic Sans MS", Font.BOLD, 35),
                false), gc.width / 3, (gc.height / 1.4f).toInt(),
                gc.width / 3, gc.height / 8)
        text1.text = "Nick"
        text2.text = "Lobby"
    }

    override fun update(gc: GameContainer, i: Int) {
        val nick: String
        val lobby: String

        if (sjg.state == State.USED || sng.state == State.USED) {
            nick = text1.text
            lobby = text2.text
            maingame = Game(gc, lobby, nick, sng.state == State.USED)
            sng.state = State.COMMON
            sjg.state = State.COMMON
            exited = false
            gameStarted = true
        }
        if (gameStarted) {
            maingame.update()
            exited = maingame.exited
            gameStarted = !maingame.exited
        }
    }

    override fun render(gc: GameContainer, g: Graphics) {
        val x = gc.input.mouseX.toFloat()
        val y = gc.input.mouseY.toFloat()
        if (exited) {
            g.background = Color.black
            g.color = Color.white
        }
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