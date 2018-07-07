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
    /*var gs = GameState(Player(300F, 360F, 5, readLine().toString(), false, false, false, false,
            false, Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat() - 668F,
            MouseInfo.getPointerInfo().getLocation().getY().toFloat() - 384F), 1), ArrayList<Player>(), 
            ArrayList<Player>())*/
    var gs = GameState()
//    var arrayEnemy = ArrayList<Player>()
//    var arrAllPlayers = ArrayList<Player>()
//
//    var gamer = Player(300F, 360F, 5, false, false, false, false,
//            false, Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat() - 668F,
//            MouseInfo.getPointerInfo().getLocation().getY().toFloat() - 384F), 1)
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
    //var players = Players() // Not ready
    var playersCreated = false
    var isGameOver = false
    init {
        print("Host?")
        isHost = readLine()!!.toBoolean()
        print("Game name?")
        gameName = readLine()!!
        print("Nick?")
        nick = readLine()!!
        net = Network("10.0.0.88:9092", gameName, isHost, nick, gs)
        val playersCreated = false
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
        /*for (i in 0..4) {
            gs.arrayEnemy.add(Player((15 + i * 60F), (15 + i * 60F), 5, false, false, false,
                    false, false, Vector2f(1F, 1F)))
            for (i in gs.arrayEnemy) gs.arrAllPlayers.add(i)
            gs.arrAllPlayers.add(gs.gamer)
        }*/
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
                        }
                        "shot" -> gs.players[a.sender]!!.shot = true
                        "direction" -> gs.players[a.sender]!!.weapon.mouseVec = Vector2f(a.params[0].toFloat(),
                                a.params[1].toFloat())
                    }
                }catch(e: NullPointerException){
                    println("${a.sender} alredy dead, skipping...")
                }
            }
           /* for (a in acts) {
                when (a.name) {
                    "move" -> movePlyer(players[a.sender]!!, a.params[0])
                    "stop" -> stopPlayer(players[a.sender]!!)
                    "color" -> players[a.sender]!!.color = Color(a.params[0].toInt(),
                            a.params[1].toInt(), a.params[2].toInt())
                    "background" -> players.backround = Color(a.params[0].toInt(),
                            a.params[1].toInt(), a.params[2].toInt())
                    else -> ""
                /*
                "pos" -> {
                    players[a.sender]!!.x = a.params[0].toFloat()
                    players[a.sender]!!.y = a.params[1].toFloat()
                }
                */
                }
            }*/
            if(!isGameOver and playersCreated)myControls(gc)
            allMove(gc)
            var gun: Meelee
            for (i in gs.players) {
                gun = i.value.weapon
                gun.cooldownCounter += if (gun.cooldownCounter <
                        gun.cooldown) 1 else 0
            }
            
            net.gameState = gs
        }
        
        /*for (i in gs.arrayEnemy) {
            i.goLeft = (Random().nextInt(2) == 1)
            i.goRight = (Random().nextInt(2) == 1)
            i.goUp = (Random().nextInt(2) == 1)
            i.goDown = (Random().nextInt(2) == 1)
            i.shot = (Random().nextInt(2) == 1)
        }*/
        //получаем экшины в больших количествах и начнаем с ними что-то делать

       
    }

    /*override fun keyPressed(key: Int, c: Char) {
        when (key) {
            Input.KEY_UP -> {
                net.doAction("move", Arrays.asList("u")); movePlyer(players[nick]!!, "u")
            }
            Input.KEY_DOWN -> {
                net.doAction("move", Arrays.asList("d")); movePlyer(players[nick]!!, "d")
            }
            Input.KEY_LEFT -> {
                net.doAction("move", Arrays.asList("l")); movePlyer(players[nick]!!, "l")
            }
            Input.KEY_RIGHT -> {
                net.doAction("move", Arrays.asList("r")); movePlyer(players[nick]!!, "r")
            }
            Input.KEY_C -> {
                players[nick]!!.color = Color(rnd.nextInt(256), rnd.nextInt(256),
                        rnd.nextInt(256))
                net.doAction("color", Arrays.asList("${players[nick]!!.color.red}",
                        "${players[nick]!!.color.green}",
                        "${players[nick]!!.color.blue}"))
            }
            Input.KEY_ENTER -> {
                if (isHost) net.startGame()
            }
            Input.KEY_B -> {
                players.backround = Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                net.doAction("background", Arrays.asList("${players.backround.red}",
                        "${players.backround.green}",
                        "${players.backround.blue}"))
            }
        }
    }

    override fun keyReleased(key: Int, c: Char) {
        if ((key != Input.KEY_UP) and (key != Input.KEY_DOWN) and (key != Input.KEY_RIGHT) and (key != Input.KEY_LEFT)){
            return
        }
        stopPlayer(players[nick]!!)
        net.doAction("stop", Arrays.asList(""))
    }*/

    private fun deathCheck() {
        //if (gs.players[nick]!!.HP <= 0) gs.players.remove(nick)
        //Если работает, то я буду орать
        val toKill = ArrayList<String>()
        for(p in gs.players){
            if(p.value.HP <= 0) {
                if(p.value.nick == nick)isGameOver = true
                toKill.add(p.key)
            }
        }
        for(p in toKill)gs.players.remove(p)

        /*var flag = true
        while (flag) {
            flag = false
            for (i in gs.arrAllPlayers)
                if (i.HP <= 0) {
                    gs.arrAllPlayers.remove(i)
                    gs.arrayEnemy.remove(i)
                    flag = true
                    break
                }
        }*/
    }

    private fun myControls(gc: GameContainer) {
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
        /*gun.mouseVec = Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat()
                - 640,
                MouseInfo.getPointerInfo().getLocation().getY().toFloat()
                        - 360)*/
        gun.mouseVec = Vector2f(input.mouseX.toFloat() - ((gc.width) / 2),
                input.mouseY.toFloat() - ((gc.height) / 2))
        net.doAction("direction", asList("${gun.mouseVec.x}", "${gun.mouseVec.y}"))
    }

    private fun allMove(gc: GameContainer) {
        for (i in gs.players)
            i.value.controlPlayer(gc, gs.players, i.value)

        deathCheck()

        /*for (i in 0..(gs.arrAllPlayers.size - 1)) {
            gs.arrAllPlayers[i].hit(gs.arrAllPlayers, i)
        }*/
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
            if(!isGameOver)camera.translate(g, gs.players[nick]!!, gc)
            g.background = Color.blue
            map.render(0, 0)
            for (i in gs.players) {
                i.value.weapon.draw(g)
                i.value.draw(g)
            }
        }
        //g.color = Color.green
        //g.fillOval(gc.width / 2f - 5f, gc.height / 2f - 5f, 10f, 10f)
    }
}