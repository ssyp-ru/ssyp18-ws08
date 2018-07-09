package Game

//import netlib.Players
//import sun.nio.ch.Net
import GUILobby.Lobby
import netlib.Network
import org.newdawn.slick.*
import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.imageout.ImageIOWriter
import org.newdawn.slick.tiled.TiledMap
import java.awt.Font
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStream
//import sun.nio.ch.Net
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.exitProcess

class Game(var gc: GameContainer, val gameName: String,
           var nick: String, var isHost: Boolean) {

    var gs = GameState()

    private lateinit var map: TiledMap
    private lateinit var comic: TrueTypeFont
    private lateinit var color: Color
    var cells = Array<Array<Cell>>(100) {Array<Cell>(100, {i -> Cell(0, 0, layer.GRASS)})}
    var teleport = ArrayList<Vector2f>()//minigan --- x = 1456, y = 1392
    lateinit var lob:Lobby
    private var mapHeight: Int = 0
    private var mapWidth: Int = 0
    private var tileHeight: Int = 0
    private var tileWidth: Int = 0
    private lateinit var camera: Camera
    var exited =false
    val net: Network
    private var playersCreated = false
    private lateinit var UI : UserInterface
    val weaponIcons = arrayOf(
            Image("res/animations/knife.png"), Image("res/animations/rapier.png"),
            Image("res/animations/rapier.png"), Image("res/animations/pistol.png"),
            Image("res/animations/minigun.png"), Image("res/animations/AWP.png")
    )

    init {
        net = Network("10.0.0.88:9092", gameName, isHost, nick, gs)
        lob= Lobby(gc,isHost,net,gameName)
        playersCreated = false
        gc.setVSync(true)
        gc.alwaysRender = true
        map = TiledMap("res/map/FowlMap2.TMX")
        mapHeight = map.height * map.tileHeight
        mapWidth = map.width * map.tileWidth
        tileHeight = map.tileHeight
        tileWidth = map.tileWidth
        comic = TrueTypeFont(Font("Comic Sans MS", Font.BOLD, 20), false)
        color = Color(Random().nextFloat(), Random().nextFloat(), Random().nextFloat())
        for (i in 0..(cells.size - 1)) {
            for (j in 0..(cells[i].size - 1)) {
                cells[i][j] = Cell(i * tileWidth, j * tileHeight, layer.GRASS)
                when{
                    (map.getTileId(i, j, 0) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            layer.ROADS)
                    (map.getTileId(i, j, 1) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            layer.CRATES)
                    (map.getTileId(i, j, 3) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            layer.WATER)
                    (map.getTileId(i, j, 4) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            layer.HOUSES)

                }
                if (map.getTileId(i, j, 5) != 0){
                    teleport.add(Vector2f(i * tileWidth.toFloat(), j * tileHeight.toFloat()))
                }
                if (map.getTileId(i, j, 6) != 0){
                    gs.weaponSpawn.add(WeaponMap(Vector2f(i * tileWidth.toFloat(), j * tileHeight.toFloat()),
                            3500f))
                }
            }
        }
        camera = Camera()
        UI = UserInterface(gc, gs, nick, cells, weaponIcons)
    }




    fun update() {
        if (!net.getGameStarted() ) {
            lob.lobbyUpdate()
            exited=lob.exited
        }
        if (net.getGameStarted() and (gs.players.isEmpty())) {
            val plrs = net.getPlayersAsHashMap()
            for (p in plrs) {
                gs.players[p.key] = Player(1800f, 1800f, 5, p.key, mouseVec = Vector2f(1f, 1f),
                        numMeeleeWeapon = 0, numRangedWeapon = 0)
            }
            playersCreated = true
            for (p in gs.players) {
                println("${p.key} - ${p.value}")
            }
        } else if (net.getGameStarted() and playersCreated) {
            //SYNC
            val tmp = net.gameState
            if (tmp is GameState) gs = tmp

            val acts = net.getActions()
            for(a in acts){
                val gamer = gs.players[a.sender]!!
                when (a.name) {
                    /**/
                    "move" -> gamer.velocity.add(Vector2f(a.params[0].toFloat(),
                            a.params[1].toFloat()))
                    "shot" -> gamer.shot = true
                    "punch" -> gamer.punch = true
                    "direction" -> gamer.mouseVec = Vector2f(a.params[0].toFloat(),
                            a.params[1].toFloat())
                    "ressurection" -> {gamer.x = a.params[0].toFloat()
                        gamer.y = a.params[1].toFloat()
                        gamer.HP = gamer.maxHP
                        ++gamer.deaths
                        gamer.killStreak = 0
                        gamer.arrayRangedWeapon = ArrayList<RangedWeapon>()
                        gamer.arrayMeeleeWeapon = ArrayList<Meelee>()
                        gamer.arrayMeeleeWeapon.add(Knife(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                    }
                    "numMeeleeWeapon" -> gamer.numMeeleeWeapon = a.params[0].toInt()
                    "numRangedWeapon" -> gamer.numRangedWeapon = a.params[0].toInt()
                    "getMeelee" -> when (a.params[0]){
                        "rapier" -> gamer.arrayMeeleeWeapon.add(Rapier(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                        "DP" -> gamer.arrayMeeleeWeapon.add(DeathPuls(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                    }
                    "getRanged" -> when (a.params[0]){
                        "pistol" -> gamer.arrayRangedWeapon.add(Pistol(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                        "MG" -> gamer.arrayRangedWeapon.add(MiniGun(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                        "awp" -> gamer.arrayRangedWeapon.add(Awp(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                    }
                }
            }
            if (gs.players.containsKey(nick)) myControls(gc)
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

            for (weapon in gs.weaponSpawn){
                if (weapon.duration == 60 * 60F) {
                    weapon.loot = when (Random().nextInt(4)){
                        0 -> Pistol (0F, 0F, 0F, Vector2f(1f,1f))
                        1 -> Awp (0F, 0F, 0F, Vector2f(1f,1f))
                        else -> Rapier (0F, 0F, 0F, Vector2f(1f,1f))
                    }
                }
                ++weapon.duration
            }

            net.gameState = gs
        }
        UI.syncState(gs)
    }

    private fun myControls(gc: GameContainer) {
        val gm = gs.players[nick]!!
        val input = gc.input
        if (input.isKeyDown(Input.KEY_D)) {
            gm.velocity.x += 1f
        }
        if (input.isKeyDown(Input.KEY_A)) {
            gm.velocity.x -= 1f
        }
        if (input.isKeyDown(Input.KEY_W)) {
            gm.velocity.y -= 1f
        }
        if (input.isKeyDown(Input.KEY_S)) {
            gm.velocity.y += 1f
        }
        gm.velocity = gm.velocity.normalise()
        net.doAction("move", asList("${gm.velocity.x}", "${gm.velocity.y}"))

        when {
            input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) -> {
                net.doAction("shot", asList(""))
                gm.shot = true
            }
            input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) -> {
                net.doAction("punch", asList(""))
                gm.punch = true
            }
            input.isKeyPressed(Input.KEY_1) -> {
                net.doAction("numRangedWeapon", asList("0"))
                gm.numRangedWeapon = 0
            }
            input.isKeyPressed(Input.KEY_2) -> {
                net.doAction("numRangedWeapon", asList("1"))
                gm.numRangedWeapon = 1
            }
            input.isKeyPressed(Input.KEY_3) -> {
                net.doAction("numRangedWeapon", asList("2"))
                gm.numRangedWeapon = 2
            }
            input.isKeyPressed(Input.KEY_5) -> {
                net.doAction("numMeeleeWeapon", asList("0"))
                gm.numMeeleeWeapon = 0
            }
            input.isKeyPressed(Input.KEY_6) -> {
                net.doAction("numMeeleeWeapon", asList("1"))
                gm.numMeeleeWeapon = 1
            }
            input.isKeyPressed(Input.KEY_7) -> {
                net.doAction("numMeeleeWeapon", asList("2"))
                gm.numMeeleeWeapon = 2
            }
        }
        gm.mouseVec = Vector2f(input.mouseX.toFloat() - ((gc.width) / 2),
                input.mouseY.toFloat() - ((gc.height) / 2))
        net.doAction("direction", asList("${gm.mouseVec.x}", "${gm.mouseVec.y}"))
    }


    private fun checkHit() {
        val toRemove = ArrayList<Bullets>()
        for (i in cells) {
            for (k in i) {
                for (j in gs.bullets) {
                    if ((distance(k.x + tileWidth / 2f, k.y + tileHeight / 2f, j.x + (j.r), j.y + (j.r))
                            <= tileWidth / 2f + (j.r)) && (k.type == layer.HOUSES)) {
                        toRemove.add(j)
                    }
                }
            }
        }
        for (b in toRemove) {
            gs.bullets.remove(b)
        }
        for (i in gs.players) {
            for (j in gs.bullets) {
                if (distance(i.value.x + i.value.R, i.value.y + i.value.R, j.x + (j.r), j.y + (j.r))
                        <= i.value.R + (j.r)) {
                    i.value.HP -= j.damage
                    if (i.value.HP <= 0){
                        j.owner.kills += if (j.owner.nick != i.value.nick) 1 else -1
                        j.owner.killStreak += if (j.owner.nick != i.value.nick) 1 else 0
                    }
                    toRemove.add(j)
                }
                if (j.y > map.height * map.tileHeight || j.y < 0) toRemove.add(j)
                if (j.x > map.width * map.tileWidth || j.x < 0) toRemove.add(j)
            }
        }
        for (b in toRemove) {
            gs.bullets.remove(b)
        }

    }

    private fun allMove(gc: GameContainer) {
        for (i in gs.players) {
            i.value.controlPlayer(gs.players, i.value, gs.bullets)
        }
        for (k in gs.bullets) {
            k.x += k.direct.x
            k.y += k.direct.y
        }
        checkHit()
        if (gs.players[nick] == null) return
        val gmr = gs.players[nick]!!
        when{
            (gmr.killStreak in 2..3) && (gmr.arrayMeeleeWeapon.size == 1) -> {
                gmr.arrayMeeleeWeapon.add(Rapier(gmr.x, gmr.y, gmr.R, gmr.mouseVec))
                net.doAction("getMeelee", asList("rapier"))
            }
            (gmr.killStreak in 4..7) && (gmr.arrayRangedWeapon.size == 0) -> {
                gmr.arrayRangedWeapon.add(Pistol(gmr.x, gmr.y, gmr.R, gmr.mouseVec))
                net.doAction("getRanged", asList("pistol"))
            }
        }
        if (gmr.HP <= 0) {
            val randomIndex = Random().nextInt(teleport.size - 1)
            gmr.x = teleport[randomIndex].x
            gmr.y = teleport[randomIndex].y
            gmr.HP = gmr.maxHP
            ++gmr.deaths
            gmr.killStreak = 0
            net.doAction("ressurection", asList("${gmr.x}", "${gmr.y}"))
            gmr.arrayRangedWeapon = ArrayList<RangedWeapon>()
            gmr.arrayMeeleeWeapon = ArrayList<Meelee>()
            gmr.arrayMeeleeWeapon.add(Knife(gmr.x, gmr.y, gmr.R, gmr.mouseVec))
        }

        //костыли
        val tmp = ArrayList<Player>()
        for (p in gs.players) tmp.add(p.value)
        for (i in 0..(tmp.size - 1)) {
            tmp[i].hit(tmp, i, cells, gs.weaponSpawn)
        }
        for (p in tmp) gs.players[p.nick] = p
        //конец косытлей
    }

    fun render(g: Graphics) {
        val HPbarDislocationHeight = 52.5f
        val HPbarDislocationWidth = 27.5f

        if (!net.getGameStarted()) {
            lob.lobbyRender(g)
        } else if (playersCreated) {
            if (gs.players.containsKey(nick)) camera.translate(g, gs.players[nick]!!, gc)
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
                if (i.key != nick) {
                    i.value.drawHP(g, i.value.x - HPbarDislocationWidth, i.value.y - HPbarDislocationHeight)
                }
            }
            if (gs.players[nick] == null) return
            gs.players[nick]!!.drawHP(g, gs.players[nick]!!.x - HPbarDislocationWidth,
                    gs.players[nick]!!.y - HPbarDislocationHeight)
            gs.players[nick]!!.drawReload(g, gs.players[nick]!!.x - HPbarDislocationWidth,
                    gs.players[nick]!!.y - HPbarDislocationHeight + 7.5f)
            UI.drawUI(g, -camera.x.toFloat(), -camera.y.toFloat())
            for (i in gs.weaponSpawn) i.draw(g, weaponIcons)
        }
    }

    fun leaveLobby() {
        net.leaveLobby()
    }
}


//Какого плакплак это было в мэйне!!!11???????777

        fun inside(x1: Float, x2: Float, y1: Float, y2: Float): Boolean {
            return when {
                y1 in x1..x2 -> true
                y2 in x1..x2 -> true
                x1 in y1..y2 -> true
                else -> false
            }
        }

        fun toDegree(someDouble: Double): Float {
            return (someDouble / PI * 180).toFloat()
        }

        fun distance(x1:Float, y1:Float, x2:Float, y2:Float):Float{
            return(sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2)))
        }
