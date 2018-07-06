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

        arrayPlayers.add(Player(300F, 360F, 5, 5F, false,
                Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat() - 668F,
                        MouseInfo.getPointerInfo().getLocation().getY().toFloat() - 384F), 1))
        for (i in 0..4){
            arrayPlayers.add(Player((15 + i * 60F) , (15 + i * 60F), 20F, 5, 5F,
                    false, Vector2f(1F, 1F)))
        }
    }

    override fun update(gc: GameContainer, i: Int) {

        //получаем экшины в больших количествах и начнаем с ними что-то делать

        allMove(gc)


        val gun = arrayPlayers[0].weapon
        gun.mouseVec = Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat()
                - arrayPlayers[0].x - arrayPlayers[0].R,
                MouseInfo.getPointerInfo().getLocation().getY().toFloat()
                        - arrayPlayers[0].y - arrayPlayers[0].R)

        gun.cooldownCounter += if (gun.cooldownCounter <
                gun.cooldown) 1 else 0

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