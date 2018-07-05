package TeamGay_Player

import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.util.*
import kotlin.math.*

class Player(var x: Float, var y: Float, val R: Float, var HP:Int,
             var hit:Boolean = false, var mouseVec: Vector2f, var IDWeapon:Int = 0): Serializable {
    var weapon = when (IDWeapon){
        1 -> Rapier(x, y, mouseVec, R)
        else -> Knife(x, y, mouseVec, R)
    }
    var colorBull = org.newdawn.slick.Color(Random().nextInt(255) / 255F,
            Random().nextInt(255) / 255F,
            Random().nextInt(255) / 255F)

    fun draw(g: org.newdawn.slick.Graphics) {
        if (hit) {
            g.color = org.newdawn.slick.Color.white

            hit = false
        }
        g.color = colorBull
        g.fillOval(x, y, 2*R, 2*R)
    }

    fun hit(balls:ArrayList<Player>, i:Int) {
        when {
            x < 0 -> {
                x = 0F
            }
            x > (1366 - 2*R) -> {
                x = 1366 - 2*R
            }
            y < 0 -> {
                y = 0F
            }
            y > (763 - 2*R) -> {
                y = 762 - 2*R
            }
        }
        for (k in (i + 1)..(balls.size - 1)){
            val dis = distance(x, y, balls[k].x, balls[k].y)
            if (dis <= R + balls[k].R) {
                val b2 = Vector2f(balls[k].x - x, balls[k].y - y).normalise()
                val b1 = Vector2f(x - balls[k].x, y - balls[k].y).normalise()
                b2.x = b2.x * (R + balls[k].R - dis)
                b2.y = b2.y * (R + balls[k].R - dis)
                b1.x = b1.x * (R + balls[k].R - dis)
                b1.y = b1.y * (R + balls[k].R - dis)
                x += b1.x
                y += b1.y
                balls[k].x += b2.x
                balls[k].y += b2.y
            }
        }
    }

    private fun distance(x1:Float, y1:Float, x2:Float, y2:Float):Float{
        return(sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2)))
    }
}