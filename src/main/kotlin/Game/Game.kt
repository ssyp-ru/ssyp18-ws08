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
import kotlin.math.pow

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
                gs.players[p.key] = Player(0f, 0f, 5, p.key, mouseVec = Vector2f(1f, 1f), IDWeapon = 101)
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
                        "move" -> gs.players[a.sender]!!.velocity.add(Vector2f(a.params[0].toFloat(),
                                a.params[1].toFloat()))
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
            var gun: Weapon
            for (i in gs.players) {
                if(i.value.isDead)continue
                gun = i.value.weapon
                gun.cooldownCounter += if (gun.cooldownCounter <
                        gun.cooldown) 1 else 0
            }
            net.gameState = gs
        }
    }

    private fun deathCheck() {
        //val toKill = ArrayList<String>()
        for(p in gs.players){
            if(p.value.HP <= 0) {
                p.value.isDead = true
                //if(p.value.nick == nick)isGameOver = true
                //toKill.add(p.key)
            }
        }
        //for(p in toKill)gs.players.remove(p)
    }

    private fun myControls(gc: GameContainer) {
        //println(playersCreated)
        if(gs.players[nick]!!.isDead)return
        val input = gc.input
        if (input.isKeyDown(Input.KEY_D)) {
            gs.players[nick]!!.velocity.x += 1f
        }
        if (input.isKeyDown(Input.KEY_A)) {
            gs.players[nick]!!.velocity.x -= 1f
        }
        if (input.isKeyDown(Input.KEY_W)) {
            gs.players[nick]!!.velocity.y -= 1f
        }
        if (input.isKeyDown(Input.KEY_S)) {
            gs.players[nick]!!.velocity.y += 1f
        }
        gs.players[nick]!!.velocity = gs.players[nick]!!.velocity.normalise()
        net.doAction("move", asList("${gs.players[nick]!!.velocity.x}", "${gs.players[nick]!!.velocity.y}"))

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


    private fun checkHit(){
        val toRemove = ArrayList<Bullets>()
        for(i in gs.players) {
            for (j in gs.bullets) {
                if (distance(i.value.x + i.value.R, i.value.y + i.value.R, j.x + (j.r), j.y + (j.r))
                        <= i.value.R + (j.r)){
                    i.value.HP -= j.damage
                    toRemove.add(j)
                }
                if (j.y > map.height * map.tileHeight || j.y < 0) toRemove.add(j)
                if (j.x > map.width * map.tileWidth || j.x < 0) toRemove.add(j)
            }
        }
        for(b in toRemove){
            gs.bullets.remove(b)
        }

    }

    private fun allMove(gc: GameContainer) {
        val arrAllBullets = ArrayList<Bullets>()
        for (i in gs.players) {
            i.value.controlPlayer(gc, gs.players, i.value, gs.bullets)
            if (i.value.isDead) continue
            for (k in gs.bullets){
                arrAllBullets.add(k)
                k.x += k.direct.x
                k.y += k.direct.y
            }
        }
        checkHit()

        deathCheck()

        //костыли
        val tmp = ArrayList<Player>()
        for(p in gs.players)tmp.add(p.value)
        for (i in 0..(tmp.size - 1)) {
            if(tmp[i].isDead)continue
            tmp[i].hit(tmp, i)
        }
        for(p in tmp)gs.players[p.nick] = p
        //конец косытлей
    }

    override fun render(gc: GameContainer, g: Graphics) {
        if (!net.getGameStarted()) {
            var y = 20f
            for (p in net.getPlayers()) {
                g.drawString(p.nick, 10f, y)
                y += 20
            }
        } else if(playersCreated){
            if(gs.players.containsKey(nick))camera.translate(g, gs.players[nick]!!, gc)
            g.background = Color.blue
            map.render(0, 0)
            for (i in gs.players) {
                if(i.value.isDead)continue
                i.value.weapon.draw(g, gs.bullets)
                i.value.draw(g)
                if(i.key != nick){
                    i.value.drawHP(g, i.value.x - 27.5f, i.value.y - 52.5f)
                }
            }
            if (gs.players[nick] == null) return
            gs.players[nick]!!.drawHP(g, gs.players[nick]!!.x - 27.5f, gs.players[nick]!!.y - 52.5f)
        }
    }
}