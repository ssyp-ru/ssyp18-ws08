package TeamGay_Player

import org.newdawn.slick.*
import org.newdawn.slick.Input.MOUSE_LEFT_BUTTON
import java.awt.MouseInfo
import java.util.*
import org.newdawn.slick.geom.Vector2f

class SimpleSlickGame(gamename: String) : BasicGame(gamename) {

    var arrayPlayers = ArrayList<Player>()
    override fun init(gc: GameContainer) {
        gc.setVSync(true)
        arrayPlayers.add(Player(300F, 360F, 20F, 5, false,
                Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat() - 668F,
                        MouseInfo.getPointerInfo().getLocation().getY().toFloat() - 384F), 1))
//        arrayPlayers.add(Player(300F, 300F, 20F, 1,
//                    false, Vector2f(1F, 1F)))
//        for (i in 1..50){  //Рандомное созадние шаров в случайных местах
//            arrayPlayers.add(Ball(((Random().nextInt(1300) + 33.5F)),
//                    ((Random().nextInt(700) + 25F)),
//                    ((Random().nextInt( 250) + 100F)),
//                    ((Random().nextInt(5) - 2.5F)),
//                    ((Random().nextInt(5) - 2.5F)),
//                    (Random().nextInt(15) + 10F)))
//        }
        for (i in 0..4){
            arrayPlayers.add(Player((15 + i * 60F) , (15 + i * 60F), 20F, 1,
                    false, Vector2f(1F, 1F)))
        }
    }

    override fun update(gc: GameContainer, i: Int) {
        val input = gc.input
        arrayPlayers[0].x += when{
            input.isKeyDown(Input.KEY_D) -> 5F
            input.isKeyDown(Input.KEY_A) -> -5F
            else -> 0F
        }
        arrayPlayers[0].y += when {
            input.isKeyDown(Input.KEY_W) -> -5f
            input.isKeyDown(Input.KEY_S) -> 5f
            else ->0F
        }
        when {
            input.isMousePressed(MOUSE_LEFT_BUTTON) -> arrayPlayers[0].weapon.attack(arrayPlayers)
        }
        killer()
        arrayPlayers[0].weapon.mouseVec = Vector2f(MouseInfo.getPointerInfo().getLocation().getX().toFloat()
                - arrayPlayers[0].x - arrayPlayers[0].R,
                MouseInfo.getPointerInfo().getLocation().getY().toFloat()
                        - arrayPlayers[0].y - arrayPlayers[0].R)

        for(i in 0..(arrayPlayers.size - 1)){
            if (i == 0) arrayPlayers[i].colorBull = org.newdawn.slick.Color(Random().nextInt(255)
                    / 255F, Random().nextInt(255) / 255F,
                    Random().nextInt(255) / 255F)
            arrayPlayers[i].hit(arrayPlayers, i)
            arrayPlayers[i].weapon.cooldownCounter += if (arrayPlayers[i].weapon.cooldownCounter <
                    arrayPlayers[i].weapon.cooldown) 1 else 0
            arrayPlayers[i].weapon.playerX = arrayPlayers[i].x
            arrayPlayers[i].weapon.playerY = arrayPlayers[i].y
        }
    }
    private fun killer(){
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
    override fun render(gc: GameContainer, g: Graphics) {
        for (i in arrayPlayers){
            i.weapon!!.draw(g)
            i.draw(g)
        }
    }
}