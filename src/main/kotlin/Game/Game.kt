package Game

import org.newdawn.slick.*
import org.newdawn.slick.geom.Rectangle
import java.awt.MouseInfo
import java.util.*
import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.tiled.TiledMap

class SimpleSlickGame(gamename: String) : BasicGame(gamename) {

    var arrayEnemy = ArrayList<Player>()
    var arrAllPlayers = ArrayList<Player>()

    var gamer = Player(300F, 360F, 5, false, false, false, false,
            false, Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat() - 668F,
            MouseInfo.getPointerInfo().getLocation().getY().toFloat() - 384F), 1)
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
    override fun init(gc: GameContainer) {
        gc.setVSync(true)

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
        for (i in 0..4) {
            arrayEnemy.add(Player((15 + i * 60F), (15 + i * 60F), 5, false, false, false,
                    false, false, Vector2f(1F, 1F)))
            for (i in arrayEnemy) arrAllPlayers.add(i)
            arrAllPlayers.add(gamer)
        }
        camera = Camera(map, mapWidth, mapHeight)
    }

    override fun update(gc: GameContainer, i: Int) {


        for (i in arrayEnemy) {
            i.goLeft = (Random().nextInt(2) == 1)
            i.goRight = (Random().nextInt(2) == 1)
            i.goUp = (Random().nextInt(2) == 1)
            i.goDown = (Random().nextInt(2) == 1)
            i.shot = (Random().nextInt(2) == 1)
        }
        //получаем экшины в больших количествах и начнаем с ними что-то делать

        myControls(gc)
        allMove(gc)
        var gun: Meelee
        for (i in arrAllPlayers) {
            gun = i.weapon
            gun.cooldownCounter += if (gun.cooldownCounter <
                    gun.cooldown) 1 else 0
        }
    }

    private fun deathCheck() {
        if (gamer.HP <= 0) arrAllPlayers.remove(gamer)
        var flag = true
        while (flag) {
            flag = false
            for (i in arrAllPlayers)
                if (i.HP <= 0) {
                    arrAllPlayers.remove(i)
                    arrayEnemy.remove(i)
                    flag = true
                    break
                }
        }
    }

    private fun myControls(gc: GameContainer) {
        val input = gc.input
        if (input.isKeyDown(Input.KEY_D)) gamer.goRight = true
        if (input.isKeyDown(Input.KEY_A)) gamer.goLeft = true
        if (input.isKeyDown(Input.KEY_W)) gamer.goUp = true
        if (input.isKeyDown(Input.KEY_S)) gamer.goDown = true

        when {
            input.isMousePressed(Input.MOUSE_LEFT_BUTTON) -> gamer.shot = true
        }

        val gun = gamer.weapon
        gun.mouseVec = Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat()
                - 640,
                MouseInfo.getPointerInfo().getLocation().getY().toFloat()
                        - 360)
    }

    private fun allMove(gc: GameContainer) {
        for (i in arrAllPlayers)
            i.controlPlayer(gc, arrAllPlayers, i)

        deathCheck()

        for (i in 0..(arrAllPlayers.size - 1)) {
            arrAllPlayers[i].hit(arrAllPlayers, i)
        }
    }

    override fun render(gc: GameContainer, g: Graphics) {
        camera.translate(g, gamer, gc)
        g.background = Color.blue
        map.render(0, 0)
        for (i in arrAllPlayers) {
            i.weapon.draw(g)
            i.draw(g)
        }
    }
}