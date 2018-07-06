package Game

import org.newdawn.slick.*
import java.awt.MouseInfo
import java.util.*
import org.newdawn.slick.geom.Vector2f

class SimpleSlickGame(gamename: String) : BasicGame(gamename) {

    var arrayEnemy = ArrayList<Player>()
    var arrAllPlayers = ArrayList<Player>()

    var gamer = Player(300F, 360F, 5, false, false, false, false,
            false, Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat() - 668F,
            MouseInfo.getPointerInfo().getLocation().getY().toFloat() - 384F), 1)
    override fun init(gc: GameContainer) {
        gc.setVSync(true)

        //получаем начальные данные

        for (i in 0..4){
            arrayEnemy.add(Player((15 + i * 60F), (15 + i * 60F), 5, false, false, false,
                    false, false, Vector2f(1F, 1F)))
            for (i in arrayEnemy) arrAllPlayers.add(i)
            arrAllPlayers.add(gamer)
        }
    }

    override fun update(gc: GameContainer, i: Int) {



        for (i in arrayEnemy){
            i.goLeft = (Random().nextInt(2) == 1)
            i.goRight = (Random().nextInt(2) == 1)
            i.goUp = (Random().nextInt(2) == 1)
            i.goDown = (Random().nextInt(2) == 1)
            i.shot = (Random().nextInt(2) == 1)
        }
        //получаем экшины в больших количествах и начнаем с ними что-то делать

        myControls(gc)
        allMove(gc)
        var gun:Meelee
        for (i in arrAllPlayers) {
            gun = i.weapon
            gun.cooldownCounter += if (gun.cooldownCounter <
                    gun.cooldown) 1 else 0
        }
    }

    private fun deathCheck(){
        if (gamer.HP <= 0) arrAllPlayers.remove(gamer)
        var flag = true
        while (flag){
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

    private  fun myControls(gc:GameContainer){
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
                - gamer.x - 20F,
                MouseInfo.getPointerInfo().getLocation().getY().toFloat()
                        - gamer.y - 20F)
    }

    private fun allMove(gc:GameContainer){
        for (i in arrAllPlayers)
            i.controlPlayer(gc, arrAllPlayers, i)

        deathCheck()

        for(i in 0..(arrAllPlayers.size - 1)){
            arrAllPlayers[i].hit(arrAllPlayers, i)
        }
    }

    override fun render(gc: GameContainer, g: Graphics) {
        for (i in arrAllPlayers){
            i.weapon.draw(g)
            i.draw(g)
        }
    }
}