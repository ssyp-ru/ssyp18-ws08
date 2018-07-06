package Game

import org.newdawn.slick.*
import java.awt.MouseInfo
import java.util.*
import org.newdawn.slick.geom.Vector2f

class SimpleSlickGame(gamename: String) : BasicGame(gamename) {

    var arrayPlayers = ArrayList<Player>()
    override fun init(gc: GameContainer) {
        gc.setVSync(true)

        //получаем начальные данные

        arrayPlayers.add(Player(300F, 360F, 5, false, false, false, false,
                false, Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat() - 668F,
                        MouseInfo.getPointerInfo().getLocation().getY().toFloat() - 384F), 1))
        for (i in 0..4){
            arrayPlayers.add(Player((15 + i * 60F), (15 + i * 60F), 5, false, false, false,
                    false, false, Vector2f(1F, 1F)))
        }
    }

    override fun update(gc: GameContainer, i: Int) {

        //получаем экшины в больших количествах и начнаем с ними что-то делать

        myControls(gc)
        allMove(gc)
        var gun:Meelee
        for (i in arrayPlayers) {
            gun = i.weapon
            gun.cooldownCounter += if (gun.cooldownCounter <
                    gun.cooldown) 1 else 0
        }
    }

    private fun deathCheck(){
        var flag = true
        while (flag){
            flag = false
            for (i in arrayPlayers)
                if (i.HP <= 0) {
                    arrayPlayers.remove(i)
                    flag = true
                    break
                }
        }
    }

    private  fun myControls(gc:GameContainer){
        val person = arrayPlayers[0]
        val input = gc.input
        when{
            input.isKeyDown(Input.KEY_D) -> person.goRight = true
            input.isKeyDown(Input.KEY_A) -> person.goLeft = true
        }
        when {
            input.isKeyDown(Input.KEY_W) -> person.goUp = true
            input.isKeyDown(Input.KEY_S) -> person.goDown = true
        }
        when {
            input.isMousePressed(Input.MOUSE_LEFT_BUTTON) -> person.shot = true
        }

        val gun = person.weapon
        gun.mouseVec = Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat()
                - person.x - 20F,
                MouseInfo.getPointerInfo().getLocation().getY().toFloat()
                        - person.y - 20F)
    }

    private fun allMove(gc:GameContainer){
        for (i in arrayPlayers)
            i.controlPlayer(gc, arrayPlayers)

        deathCheck()

        for(i in 0..(arrayPlayers.size - 1)){
            arrayPlayers[i].hit(arrayPlayers, i)
        }
    }

    override fun render(gc: GameContainer, g: Graphics) {
        for (i in arrayPlayers){
            i.weapon.draw(g)
            i.draw(g)
        }
    }
}