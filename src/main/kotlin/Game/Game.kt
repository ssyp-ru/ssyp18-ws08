package Game

import netlib.Network
//import netlib.Players
import org.newdawn.slick.*
import org.newdawn.slick.geom.Rectangle
import java.awt.MouseInfo
import java.util.*
import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.tiled.TiledMap
import java.awt.Font
//import sun.nio.ch.Net
import java.util.Arrays.asList
import kotlin.collections.ArrayList
import kotlin.math.pow

class SimpleSlickGame(gamename: String) : BasicGame(gamename) {

    var gs = GameState()

    private lateinit var map: TiledMap
    private lateinit var comic: TrueTypeFont
    private lateinit var color: Color
    private var cells = Array<Array<Cell>>(100) {Array<Cell>(100, {i -> Cell(0, 0, 0)})}
    //private lateinit var minimap: Minimap
    private lateinit var minimapImage: Image
    private var tileID: Int = 0
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
    private var UI : UserInterface

    init {
        print("Host?")
        isHost = readLine()!!.toBoolean()
        print("Game name?")
        gameName = readLine()!!
        print("Nick?")
        nick = readLine()!!
        net = Network("10.0.0.88:9092", gameName, isHost, nick, gs)
        playersCreated = false
        UI = UserInterface(nick)
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
        comic = TrueTypeFont(Font("Comic Sans MS", Font.BOLD, 20), false)
        color = Color(Random().nextFloat(), Random().nextFloat(), Random().nextFloat())
        for (i in 0..(cells.size - 1)) {
            for (j in 0..(cells[i].size - 1)) {
                cells[i][j] = Cell(i * tileWidth, j * tileHeight, 0)
                when{
                    (map.getTileId(i, j, 0) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            1)
                    (map.getTileId(i, j, 1) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            2)
                    (map.getTileId(i, j, 3) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            4)
                    (map.getTileId(i, j, 4) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            5)

                }
            }
        }
        //minimap = Minimap(cells, nick)
        minimapImage = Image("res/map/Minimap.png")
        camera = Camera(map, mapWidth, mapHeight)
    }

    override fun update(gc: GameContainer, i: Int) {
        if(!net.getGameStarted() and gc.input.isKeyDown(Input.KEY_ENTER))net.startGame()
        if (net.getGameStarted() and (gs.players.isEmpty())) {
            val plrs = net.getPlayersAsHashMap()
            for(p in plrs){
                gs.players[p.key] = Player(1800f, 1800f, 5, p.key, mouseVec = Vector2f(1f, 1f),
                        numMeeleeWeapon = 0, numRangedWeapon = 0)
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
                when (a.name) {
                    /**/
                    "move" -> gs.players[a.sender]!!.velocity.add(Vector2f(a.params[0].toFloat(),
                            a.params[1].toFloat()))
                    "shot" -> gs.players[a.sender]!!.shot = true
                    "punch" -> gs.players[a.sender]!!.punch = true
                    "direction" -> gs.players[a.sender]!!.mouseVec = Vector2f(a.params[0].toFloat(),
                            a.params[1].toFloat())
                    "ressurection" -> {gs.players[a.sender]!!.x = a.params[0].toFloat()
                        gs.players[a.sender]!!.y = a.params[1].toFloat()
                        gs.players[a.sender]!!.HP = gs.players[a.sender]!!.maxHP
                        ++gs.players[a.sender]!!.deaths
                    }
                    "meeleeWeapon" -> gs.players[a.sender]!!.numMeeleeWeapon = a.params[0].toInt()
                    "rangedWeapon" -> gs.players[a.sender]!!.numRangedWeapon = a.params[0].toInt()
                }
            }
            if(gs.players.containsKey(nick))myControls(gc)
            allMove(gc)
            var meeleeGun: Weapon
            var rangedGun: Weapon
            for (i in gs.players) {
                if (i.value.numMeeleeWeapon <= i.value.arrayMeeleeWeapon.size - 1) {
                    meeleeGun = i.value.arrayMeeleeWeapon[i.value.numMeeleeWeapon]
                    meeleeGun.cooldownCounter += if (meeleeGun.cooldownCounter <
                            meeleeGun.cooldown) 1 else 0
                }
                if (i.value.numRangedWeapon <= i.value.arrayRangedWeapon.size - 1) {
                rangedGun = i.value.arrayRangedWeapon[i.value.numRangedWeapon]
                rangedGun.cooldownCounter += if (rangedGun.cooldownCounter <
                        rangedGun.cooldown) 1 else 0
                }
            }
            net.gameState = gs
        }
    }

//    private fun deathCheck() {
//        //val toKill = ArrayList<String>()
//        for(p in gs.players){
//            if(p.value.HP <= 0) {
//                p.value.isDead = true
//                //if(p.value.nick == nick)isGameOver = true
//                //toKill.add(p.key)
//            }
//        }
//        //for(p in toKill)gs.players.remove(p)
//    }

    private fun myControls(gc: GameContainer) {
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
            input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) -> {
                net.doAction("shot", asList(""))
                gs.players[nick]!!.shot = true
            }
            input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) -> {
                net.doAction("punch", asList(""))
                gs.players[nick]!!.punch = true
            }
            input.isKeyPressed(Input.KEY_1) -> {
                net.doAction("rangedWeapon", asList("0"))
                gs.players[nick]!!.numRangedWeapon = 0
            }
            input.isKeyPressed(Input.KEY_2) -> {
                net.doAction("rangedWeapon", asList("1"))
                gs.players[nick]!!.numRangedWeapon = 1
            }
            input.isKeyPressed(Input.KEY_3) -> {
                net.doAction("rangedWeapon", asList("2"))
                gs.players[nick]!!.numRangedWeapon = 2
            }
            input.isKeyPressed(Input.KEY_5) -> {
                net.doAction("meeleeWeapon", asList("0"))
                gs.players[nick]!!.numMeeleeWeapon = 0
            }
            input.isKeyPressed(Input.KEY_6) -> {
                net.doAction("meeleeWeapon", asList("1"))
                gs.players[nick]!!.numMeeleeWeapon = 1
            }
            input.isKeyPressed(Input.KEY_7) -> {
                net.doAction("meeleeWeapon", asList("2"))
                gs.players[nick]!!.numMeeleeWeapon = 2
            }
        }
        val gm = gs.players[nick]!!
        gm.mouseVec = Vector2f(input.mouseX.toFloat() - ((gc.width) / 2),
                input.mouseY.toFloat() - ((gc.height) / 2))
        net.doAction("direction", asList("${gm.mouseVec.x}", "${gm.mouseVec.y}"))
    }


    private fun checkHit(){
        val toRemove = ArrayList<Bullets>()
        for(i in gs.players) {
            for (j in gs.bullets) {
                if (distance(i.value.x + i.value.R, i.value.y + i.value.R, j.x + (j.r), j.y + (j.r))
                        <= i.value.R + (j.r)){
                    i.value.HP -= j.damage
                    if (i.value.HP <= 0) j.owner.kills += if (j.owner.nick != i.value.nick) 1 else -1
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
            for (k in gs.bullets){
                arrAllBullets.add(k)
                k.x += k.direct.x
                k.y += k.direct.y
            }
        }
        checkHit()
        val gmr = gs.players[nick]!!
        if (gmr.HP <= 0) {
            gmr.x = Random().nextInt(((map.height * map.tileHeight - gmr.R * 2).toInt())).toFloat()
            gmr.y = Random().nextInt(((map.width * map.tileWidth - gmr.R * 2).toInt())).toFloat()
            gmr.HP = gmr.maxHP
            ++gmr.deaths
            net.doAction("ressurection", asList("${gmr.x}", "${gmr.y}"))
        }

        //костыли
        val tmp = ArrayList<Player>()
        for(p in gs.players)tmp.add(p.value)
        for (i in 0..(tmp.size - 1)) {
            tmp[i].hit(tmp, i, cells)
        }
        for(p in tmp)gs.players[p.nick] = p
        //конец косытлей
    }



    override fun render(gc: GameContainer, g: Graphics) {
        val HPbarDislocationHeight = 52.5f
        val HPbarDislocationWidth =  27.5f
        if (!net.getGameStarted()) {
            var y = 0f
            for (p in net.getPlayers()) {
                g.drawString(p.nick, 2.88f, y)
                y += 20
            }
        } else if(playersCreated){
            if(gs.players.containsKey(nick))camera.translate(g, gs.players[nick]!!, gc)
            g.background = Color.blue
            map.render(0, 0)
            g.font = comic
            g.color = color
            g.drawString("SSYP 20!8", 10f, 10f)
            for (i in gs.players) {
                if (i.value.numMeeleeWeapon <= i.value.arrayMeeleeWeapon.size - 1) {
                    i.value.arrayMeeleeWeapon[i.value.numMeeleeWeapon].draw(g, gs.bullets)
                }
                if (i.value.numRangedWeapon <= i.value.arrayRangedWeapon.size - 1) {
                    i.value.arrayRangedWeapon[i.value.numRangedWeapon].draw(g, gs.bullets)
                }
                i.value.draw(g)
                if(i.key != nick){
                    i.value.drawHP(g, i.value.x - HPbarDislocationWidth, i.value.y - HPbarDislocationHeight)
                }
            }
            val cameraShift = 5
            if (gs.players[nick] == null) return
            gs.players[nick]!!.drawHP(g, gs.players[nick]!!.x - HPbarDislocationWidth,
                                        gs.players[nick]!!.y - HPbarDislocationHeight)
            gs.players[nick]!!.drawReload(g,gs.players[nick]!!.x - HPbarDislocationWidth,
                    gs.players[nick]!!.y - HPbarDislocationHeight + 7.5f)

            UI.drawScore(g, gs, -camera.x.toFloat() + cameraShift, -camera.y.toFloat())

            //minimap.update(gs.players, g, gc, minimapImage)
        }
    }
}