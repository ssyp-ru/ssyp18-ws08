package Game

import netlib.Network
//import netlib.Players
import org.newdawn.slick.*
import org.newdawn.slick.geom.Rectangle
import java.awt.MouseInfo
import java.util.*
import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.tiled.TiledMap
//import sun.nio.ch.Net
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class SimpleSlickGame(gamename: String) : BasicGame(gamename) {

    var gs = GameState()

    private lateinit var map: TiledMap
    private lateinit var blockedWalk: Array<Array<Boolean>>
    private lateinit var blockedFire: Array<Array<Boolean>>
    private lateinit var blocksWalk: ArrayList<Rectangle>
    private lateinit var blocksFire: ArrayList<Rectangle>
    private var tileID: Int = 0
    private val layerWalk: Int = 0
    private val layerFire: Int = 1
    private lateinit var value: String
    private var mapHeight: Int = 0
    private var mapWidth: Int = 0
    private var tileHeight: Int = 0
    private var tileWidth: Int = 0
    private lateinit var camera: Camera
    var isHost = false
    val net: Network
    val gameName: String
    var nick: String
    private var playersCreated = false
    private var isGameOver = false
    init {
        print("Host?")
        isHost = readLine()!!.toBoolean()
        print("Game name?")
        gameName = readLine()!!
        print("Nick?")
        nick = readLine()!!
        net = Network("10.0.0.88:9092", gameName, isHost, nick, gs)
        playersCreated = false
    }
    override fun init(gc: GameContainer) {
        gc.setVSync(true)
        gc.alwaysRender = true

        //получаем начальные данные

        map = TiledMap("res/map/FirstFowlMap.TMX")
        mapHeight = map.height * map.tileHeight
        mapWidth = map.width * map.tileWidth
        tileHeight = map.tileHeight
        tileWidth = map.tileWidth
        for (i in 0..99) {
            for (j in 0..99) {
                tileID = map.getTileId(i, j, layerWalk)
                value = map.getTileProperty(tileID, "blocked", "false")
                if (value.equals("true")) {
                    blockedWalk[i][j] = true
                    blocksWalk.add(Rectangle(i * tileWidth.toFloat(), j * tileHeight.toFloat(),
                            tileWidth.toFloat(), tileHeight.toFloat()))
                }
                tileID = map.getTileId(i, j, layerFire)
                value = map.getTileProperty(tileID, "blocked", "false")
                if (value.equals("true")) {
                    blockedFire[i][j] = true
                    blocksFire.add(Rectangle(i * tileWidth.toFloat(), j * tileHeight.toFloat(),
                            tileWidth.toFloat(), tileHeight.toFloat()))
                }
            }
        }
        camera = Camera(map, mapWidth, mapHeight)
    }

    override fun update(gc: GameContainer, i: Int) {
        if(!net.getGameStarted() and gc.input.isKeyDown(Input.KEY_ENTER))net.startGame()
        if (net.getGameStarted() and (gs.players.isEmpty())) {
            val plrs = net.getPlayersAsHashMap()
            for(p in plrs){
                gs.players[p.key] = Player(0f, 0f, 5, p.key, mouseVec = Vector2f(1f, 1f))
            }
            playersCreated = true
            for(p in gs.players){
                println("${p.key} - ${p.value}")
            }
        } else if (net.getGameStarted() and playersCreated) {
            //SYNC
            val tmp = net.gameState
            if (tmp is GameState) gs = tmp
            
            val acts = net.getActions()
            for(a in acts){
                try {
                    when (a.name) {
                    /**/
                        "move" -> {
                            when (a.params[0]) {
                                "right" -> gs.players[a.sender]!!.goRight = true
                                "left" -> gs.players[a.sender]!!.goLeft = true
                                "up" -> gs.players[a.sender]!!.goUp = true
                                "down" -> gs.players[a.sender]!!.goDown = true
                            }
                            allMove(gc)
                        }
                        "shot" -> gs.players[a.sender]!!.shot = true
                        "direction" -> gs.players[a.sender]!!.weapon.mouseVec = Vector2f(a.params[0].toFloat(),
                                a.params[1].toFloat())
                    }
                }catch(e: NullPointerException){
                    println("${a.sender} alredy dead, skipping...")
                }
            }
            if(gs.players.containsKey(nick))myControls(gc)
            allMove(gc)
            var gun: Meelee
            for (i in gs.players) {
                gun = i.value.weapon
                gun.cooldownCounter += if (gun.cooldownCounter <
                        gun.cooldown) 1 else 0
            }
            
            net.gameState = gs
        }
    }

    private fun deathCheck() {
        val toKill = ArrayList<String>()
        for(p in gs.players){
            if(p.value.HP <= 0) {
                if(p.value.nick == nick)isGameOver = true
                toKill.add(p.key)
            }
        }
        for(p in toKill)gs.players.remove(p)
    }

    private fun myControls(gc: GameContainer) {
        //println(playersCreated)
        val input = gc.input
        if (input.isKeyDown(Input.KEY_D)) {
            gs.players[nick]!!.goRight = true
            net.doAction("move", asList("right"))
        }
        if (input.isKeyDown(Input.KEY_A)) {
            gs.players[nick]!!.goLeft = true
            net.doAction("move", asList("left"))
        }
        if (input.isKeyDown(Input.KEY_W)) {
            net.doAction("move", asList("up"))
            gs.players[nick]!!.goUp = true
        }
        if (input.isKeyDown(Input.KEY_S)) {
            net.doAction("move", asList("down"))
            gs.players[nick]!!.goDown = true
        }

        when {
            input.isMousePressed(Input.MOUSE_LEFT_BUTTON) -> {
                net.doAction("shot", asList(""))
                gs.players[nick]!!.shot = true
            }
        }

        val gun = gs.players[nick]!!.weapon
        gun.mouseVec = Vector2f(input.mouseX.toFloat() - ((gc.width) / 2),
                input.mouseY.toFloat() - ((gc.height) / 2))
        net.doAction("direction", asList("${gun.mouseVec.x}", "${gun.mouseVec.y}"))
    }

    private fun allMove(gc: GameContainer) {
        for (i in gs.players)
            i.value.controlPlayer(gc, gs.players, i.value)

        deathCheck()

        //костыли
        val tmp = ArrayList<Player>()
        for(p in gs.players)tmp.add(p.value)
        for (i in 0..(tmp.size - 1)) {
            tmp[i].hit(tmp, i)
        }
        for(p in tmp)gs.players[p.nick] = p
        //конец косытлей
    }

    override fun render(gc: GameContainer, g: Graphics) {
        if (!net.getGameStarted()) {
            var y = 10f
            for (p in net.getPlayers()) {
                g.drawString(p.nick, 10f, y)
                y += 20
            }
        } else if(playersCreated){
            if(gs.players.containsKey(nick))camera.translate(g, gs.players[nick]!!, gc)
            g.background = Color.blue
            map.render(0, 0)
            for (i in gs.players) {
                i.value.weapon.draw(g)
                i.value.draw(g)
            }
        }
    }
}