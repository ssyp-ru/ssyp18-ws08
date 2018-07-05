package ru.enginner

import org.newdawn.slick.*
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.util.*
import java.util.Arrays.asList

fun main(args: Array<String>) {

    val g = NetGame()
    val gc = AppGameContainer(g)
    gc.setDisplayMode(320, 240, false)
    gc.alwaysRender = true
    gc.setShowFPS(false)
    gc.start()
}

class NetGame() : BasicGame("Now with network!") {
    val net: Net
    val isHost: Boolean
    val gameName: String
    val nick: String
    var players = Players()
    var nicks: Array<String> = arrayOf()
    var me = 0
    val rnd = Random()

    init {
        print("Host?")
        isHost = readLine()!!.toBoolean()
        print("Game name?")
        gameName = readLine()!!
        print("Nick?")
        nick = readLine()!!
        net = Net("192.168.99.100:9092", gameName, isHost, nick, players)
    }

    override fun init(gc: GameContainer) {}
    override fun update(gc: GameContainer, millis: Int) {
        if (net.getGameStarted() and (players.isEmpty())) {
            for (p in net.getPlayersAsHashMap()) {
                players[p.key] = Player()
            }
            net.doAction("begin", asList(""))
            net.setGameState(players)
        } else if (net.getGameStarted()) {
            val tmp = net.getGameState()
            if (tmp is Players) players = tmp
            val acts = net.getActions()
            for (a in acts) {
                when (a.name) {
                    "move" -> movePlyer(players[a.sender]!!, a.params[0])
                    "stop" -> stopPlayer(players[a.sender]!!)
                    "color" -> players[a.sender]!!.color = Color(a.params[0].toInt(),
                            a.params[1].toInt(), a.params[2].toInt())
                }
            }
            for (p in players) {
                p.value.x += millis / 25f * p.value.velocity.x
                p.value.y += millis / 25f * p.value.velocity.y
            }
            net.setGameState(players)
        }
    }

    override fun keyPressed(key: Int, c: Char) {
        when (key) {
            Input.KEY_UP -> {
                net.doAction("move", asList("u")); movePlyer(players[nick]!!, "u")
            }
            Input.KEY_DOWN -> {
                net.doAction("move", asList("d")); movePlyer(players[nick]!!, "d")
            }
            Input.KEY_LEFT -> {
                net.doAction("move", asList("l")); movePlyer(players[nick]!!, "l")
            }
            Input.KEY_RIGHT -> {
                net.doAction("move", asList("r")); movePlyer(players[nick]!!, "r")
            }
            Input.KEY_C -> {
                players[nick]!!.color = Color(rnd.nextInt(256), rnd.nextInt(256),
                        rnd.nextInt(256))
                net.doAction("color", asList("${players[nick]!!.color.red}",
                        "${players[nick]!!.color.green}",
                        "${players[nick]!!.color.blue}"))
            }
            Input.KEY_ENTER -> {
                if (isHost) net.startGame()
            }
        }
    }

    override fun keyReleased(key: Int, c: Char) {
        if ((key != Input.KEY_UP) and (key != Input.KEY_DOWN) and (key != Input.KEY_RIGHT) and (key != Input.KEY_LEFT)){
            return
        }
        stopPlayer(players[nick]!!)
        net.doAction("stop", asList(""))
    }

    override fun render(gc: GameContainer, g: Graphics) {
        if (!net.getGameStarted()) {
            var y = 10f
            for (p in net.getPlayers()) {
                g.drawString(p.nick, 10f, y)
                y += 20
            }
        } else {
            for (p in players) {
                g.color = p.value.color
                g.fillOval(p.value.x, p.value.y, 10f, 10f)
            }
        }
    }

    fun movePlyer(player: Player, dir: String) {
        when (dir) {
            "l" -> player.velocity = Vector2f(-10f, 0f)
            "r" -> player.velocity = Vector2f(10f, 0f)
            "u" -> player.velocity = Vector2f(0f, -10f)
            "d" -> player.velocity = Vector2f(0f, 10f)
        }
    }

    fun stopPlayer(player: Player) {
        player.velocity = Vector2f(0f, 0f)
    }
}

class Player() : Serializable {
    var x = 0f
    var y = 0f

    class vect(val x: Float, val y: Float)

    var color = Color.yellow
    var velocity = Vector2f(0f, 0f)
}

class Players() : HashMap<String, Player>(), Serializable