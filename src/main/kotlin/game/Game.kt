package game

import gui.State
import GUILobby.Lobby
import netlib.Network
import org.newdawn.slick.*
import org.newdawn.slick.geom.Vector2f
import java.awt.Font
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

class Game(var gc: GameContainer, gameName: String,
           var nick: String, var isHost: Boolean) {
    private var gs = GameState()
    private var comic: TrueTypeFont
    private var color: Color
    private var lob: Lobby
    private var map = Map(gs)
    private var camera: Camera
    private var extViewed = false
    private var exit = Exit(gc)
    var exited = false
    val net: Network
    private var playersCreated = false
    private var userInterface: UserInterface
    lateinit var weaponIcons:Array<Image>
    init {
        net = Network("10.0.0.88:9092", gameName, isHost, nick, gs, map.mapName)
        lob = Lobby(gc, isHost, net, gameName)
        playersCreated = false
        gc.setVSync(true)
        gc.alwaysRender = true
        comic = TrueTypeFont(Font("Comic Sans MS", Font.BOLD, 20), false)
        color = Color(Random().nextFloat(), Random().nextFloat(), Random().nextFloat())
        camera = Camera(gc)
        weaponIcons = arrayOf(
                Image("res/animations/knife.png"), Image("res/animations/rapier.png"),
                Image("res/animations/rapier.png"), Image("res/animations/pistol.png"),
                Image("res/animations/minigun.png"), Image("res/animations/AWP.png")
        )
        userInterface = UserInterface(gc, gs, nick, map.cells, weaponIcons)
    }


    fun update() {
        if (!net.getGameStarted()) {
            lob.update()
            map.mapName = lob.mapName
            exited = lob.exited
        }
        if (net.getGameStarted() and (gs.players.isEmpty())) {
            map.mapName = net.getMap()
            map= Map(gs,map.mapName)
            userInterface = UserInterface(gc, gs, nick, map.cells, weaponIcons)
            userInterface.isMinimapCreated=false
            val playerHashMap = net.getPlayersAsHashMap()
            for (player in playerHashMap) {
                val randomIndex = Random().nextInt(map.teleport.size - 1)
                gs.players[player.key] = Player(map.teleport[randomIndex].x, map.teleport[randomIndex].y, 100, player.key,
                        mouseVec = Vector2f(1f, 1f), numMeeleeWeapon = 0, numRangedWeapon = 0)
            }
            playersCreated = true
        } else if (net.getGameStarted() and playersCreated) {
            if (gc.input.isKeyPressed(Input.KEY_ESCAPE))
                extViewed = !extViewed

            //SYNC
            val tmp = net.gameState
            if (tmp is GameState) gs = tmp
            if (extViewed && exit.state == State.USED) {
                exited = true
            }

            val acts = net.getActions()
            for (act in acts) {
                val gamer = gs.players[act.sender] ?: continue
                when (act.name) {
                    "move" -> gamer.velocity.add(Vector2f(act.params[0].toFloat(),
                            act.params[1].toFloat()))
                    "shot" -> gamer.shot()
                    "punch" -> gamer.punch()
                    "direction" -> gamer.mouseVec = Vector2f(act.params[0].toFloat(),
                            act.params[1].toFloat())
                    "ressurection" -> {
                        gamer.x = act.params[0].toFloat()
                        gamer.y = act.params[1].toFloat()
                        gamer.hp = gamer.maxHP
                        gamer.killStreak = 0
                        gamer.arrayRangedWeapon = ArrayList()
                        gamer.arrayMeleeWeapon = ArrayList()
                        gamer.arrayMeleeWeapon.add(Knife(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                        gamer.isDead = false
                    }
                    "numMeeleeWeapon" -> gamer.numMeeleeWeapon = act.params[0].toInt()
                    "numRangedWeapon" -> gamer.numRangedWeapon = act.params[0].toInt()
                    "getMeelee" -> when (act.params[0]) {
                        "rapier" -> gamer.arrayMeleeWeapon.add(Rapier(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                        "DP" -> gamer.arrayMeleeWeapon.add(DeathPulse(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                    }
                    "getRanged" -> when (act.params[0]) {
                        "pistol" -> gamer.arrayRangedWeapon.add(Pistol(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                        "MG" -> gamer.arrayRangedWeapon.add(MiniGun(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                        "awp" -> gamer.arrayRangedWeapon.add(Awp(gamer.x, gamer.y, gamer.R, gamer.mouseVec))
                    }
                }
            }
            if (gs.players.containsKey(nick)) myControls(gc)
            allMove()
            var meeleeGun: Weapon
            var rangedGun: Weapon
            for (i in gs.players) {
                if (i.value.numMeeleeWeapon <= i.value.arrayMeleeWeapon.size - 1) {
                    meeleeGun = i.value.arrayMeleeWeapon[i.value.numMeeleeWeapon]
                    meeleeGun.cooldownCounter += if (meeleeGun.cooldownCounter <
                            meeleeGun.cooldown) 1 else 0
                }
                if (i.value.numRangedWeapon <= i.value.arrayRangedWeapon.size - 1) {
                    rangedGun = i.value.arrayRangedWeapon[i.value.numRangedWeapon]
                    rangedGun.cooldownCounter += if (rangedGun.cooldownCounter <
                            rangedGun.cooldown) 1 else 0
                }
            }

            for (weapon in gs.weaponSpawn) {
                if (weapon.duration == 60 * 60F) {
                    weapon.loot = when (Random().nextInt(99)) {
                        in 0..29 -> Pistol(0F, 0F, 0F, Vector2f(1f, 1f))
                        in 30..44 -> Awp(0F, 0F, 0F, Vector2f(1f, 1f))
                        in 44..49 -> MiniGun(0F, 0F, 0F, Vector2f(1f, 1f))
                        else -> Rapier(0F, 0F, 0F, Vector2f(1f, 1f))
                    }
                }
                ++weapon.duration
            }

            for (p in gs.players) {
                if (p.value.hp <= 0) {
                    if (!p.value.isDead) p.value.deaths++
                    p.value.isDead = true
                    p.value.x = 9999f
                    p.value.y = 9999f
                }
            }
            userInterface.syncState(gs)
            net.gameState = gs
        }
    }

    private fun myControls(gc: GameContainer) {
        val gm = gs.players[nick] ?: return
        if (gm.isDead) return
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
                gm.shot()
                net.doAction("shot", asList(""))
            }
            input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) -> {
                gm.punch()
                net.doAction("punch", asList(""))
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
        }
        gm.mouseVec = Vector2f(input.mouseX.toFloat() - ((gc.width) / 2),
                input.mouseY.toFloat() - ((gc.height) / 2))
        net.doAction("direction", asList("${gm.mouseVec.x}", "${gm.mouseVec.y}"))
    }


    private fun checkHit() {
        val toRemove = ArrayList<Bullets>()
        for (i in map.cells) {
            for (k in i) {
                for (j in gs.bullets) {
                    if ((distance(k.x + map.tileWidth / 2f, k.y + map.tileHeight / 2f, j.x + (j.r), j.y + (j.r))
                                    <= map.tileWidth / 2f + (j.r)) && (k.type == LayerType.HOUSES)) {
                        toRemove.add(j)
                    }
                }
            }
        }
        for (b in toRemove) {
            gs.bullets.remove(b)
        }
        for (i in gs.players) {
            if (i.value.isDead) continue
            for (j in gs.bullets) {
                if (distance(i.value.x + i.value.R, i.value.y + i.value.R, j.x + (j.r), j.y + (j.r))
                        <= i.value.R + (j.r)) {
                    i.value.hp -= j.damage
                    if (i.value.hp <= 0) {
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

    private fun allMove() {
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
        if (gmr.hp <= 0) {
            val randomIndex = Random().nextInt(map.teleport.size - 1)
            gmr.x = map.teleport[randomIndex].x
            gmr.y = map.teleport[randomIndex].y
            gmr.hp = gmr.maxHP
            if (!gmr.isDead) gmr.deaths++
            gmr.killStreak = 0
            net.doAction("ressurection", asList("${gmr.x}", "${gmr.y}"))
            gmr.arrayRangedWeapon = ArrayList()
            gmr.arrayMeleeWeapon = ArrayList()
            gmr.arrayMeleeWeapon.add(Knife(gmr.x, gmr.y, gmr.R, gmr.mouseVec))
        }

        val tmp = ArrayList<Player>()
        for (p in gs.players) tmp.add(p.value)
        for (i in 0..(tmp.size - 1)) {
            tmp[i].hit(tmp, i, map.cells, gs.weaponSpawn)
        }
        for (p in tmp) gs.players[p.nick] = p
    }

    fun render(g: Graphics) {
        val hpBarDislocationHeight = 52.5f
        val hpBarDislocationWidth = 27.5f
        if (!net.getGameStarted()) {
            lob.render(g)
        } else if (playersCreated) {
            if (gs.players.containsKey(nick)) camera.translate(g, gs.players[nick]!!)
            g.background = Color.blue
            map.render(0, 0)
            g.font = comic
            g.color = color
            g.drawString("SSYP 20!8", 10f, 10f)
            for (player in gs.players) {
                if (player.value.numMeeleeWeapon <= player.value.arrayMeleeWeapon.size - 1) {
                    player.value.arrayMeleeWeapon[player.value.numMeeleeWeapon].draw(g, gs.bullets)
                }
                if (player.value.numRangedWeapon <= player.value.arrayRangedWeapon.size - 1) {
                    player.value.arrayRangedWeapon[player.value.numRangedWeapon].draw(g, gs.bullets)
                }
                player.value.draw()
                if (player.key != nick) {
                    player.value.drawHP(g, player.value.x - hpBarDislocationWidth,
                            player.value.y - hpBarDislocationHeight)
                }
            }

            if (gs.players[nick] == null) return
            gs.players[nick]!!.drawHP(g, gs.players[nick]!!.x - hpBarDislocationWidth,
                    gs.players[nick]!!.y - hpBarDislocationHeight)
            gs.players[nick]!!.drawReload(g, gs.players[nick]!!.x - hpBarDislocationWidth,
                    gs.players[nick]!!.y - hpBarDislocationHeight + 7.5f)
            userInterface.drawUI(g, -camera.x.toFloat(), -camera.y.toFloat())
            if (extViewed) {
                exit.xButton = -camera.x.toFloat()
                exit.yButton = -camera.y.toFloat()
                exit.draw(gc, -camera.x + gc.input.mouseX.toFloat(), -camera.y + gc.input.mouseY.toFloat())
            }
            if (exited) g.background = Color.black
            for (i in gs.weaponSpawn) i.draw(userInterface.weaponIcons)
        }

    }
}


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

fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    return (sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2)))
}
