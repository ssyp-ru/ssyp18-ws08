package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

class Player(var x: Float, var y: Float, var HP:Int, val speed:Float,
             var hit:Boolean = false, val mouseVec: Vector2f, val IDWeapon:Int = 0): Serializable {
    var weapon = when (IDWeapon){
        1 -> Rapier(x, y, mouseVec)
//        101 -> Pistol(x, y, mouseVec, R)
        else -> Knife(x, y, mouseVec)
    }
    var colorPlayer = org.newdawn.slick.Color(Random().nextInt(255) / 255F,
            Random().nextInt(255) / 255F,
            Random().nextInt(255) / 255F)

    fun draw(g: org.newdawn.slick.Graphics) {
        g.color = colorPlayer
        g.fillOval(x, y, 40F, 40F)
    }
    fun controlPlayer(gc:GameContainer, arrayPlayers:ArrayList<Player>){

        val input = gc.input
        val tempForSpeed = speed
        x += when{
            input.isKeyDown(Input.KEY_D) -> tempForSpeed
            input.isKeyDown(Input.KEY_A) -> -tempForSpeed
            else -> 0F
        }
        y += when {
            input.isKeyDown(Input.KEY_W) -> -tempForSpeed
            input.isKeyDown(Input.KEY_S) -> tempForSpeed
            else -> 0F
        }
        when {
            input.isMousePressed(Input.MOUSE_LEFT_BUTTON) -> weapon.attack(arrayPlayers)
        }
    }
    fun hit(balls:ArrayList<Player>, i:Int) {
        when {
            x < 0 -> {
                x = 0F
            }
            x > (1326) -> {
                x = 1326F
            }
            y < 0 -> {
                y = 0F
            }
            y > (723) -> {
                y = 723F
            }
        }
        for (k in (i + 1)..(balls.size - 1)){
            val dis = distance(x, y, balls[k].x, balls[k].y)
            if (dis < 40) {
                val b2 = Vector2f(balls[k].x - x, balls[k].y - y).normalise().scale((40 - dis) / 2)
                val b1 = Vector2f(x - balls[k].x, y - balls[k].y).normalise().scale((40 - dis) / 2)
                x += b1.x
                y += b1.y
                balls[k].x += b2.x
                balls[k].y += b2.y
            }
        }
        weapon.playerX = x
        weapon.playerY = y
    }

    private fun distance(x1:Float, y1:Float, x2:Float, y2:Float):Float{
        return(sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2)))
    }
}