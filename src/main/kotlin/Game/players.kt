package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Input
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

class Player(var x: Float, var y: Float, var HP:Int, var goUp:Boolean = false, var goDown:Boolean = false,
             var goLeft:Boolean = false, var goRight:Boolean = false, var shot:Boolean = false, val mouseVec: Vector2f,
             val IDWeapon:Int = 0): Serializable {
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
    fun controlPlayer(gc:GameContainer, arrayPlayers:ArrayList<Player>, i:Player){
        val tempForSpeed = 5F
        val movement = Vector2f(0F, 0F)
        movement.x += (if (goRight) 1F else 0F) + (if (goLeft) -1F else 0F)
        movement.y += (if (goDown) 1F else 0F) + (if (goUp) -1F else 0F)
        movement.normalise().scale(tempForSpeed)
        x += movement.x
        y += movement.y
        if (shot && HP>0) weapon.attack(arrayPlayers, i)
        goDown= false
        goUp= false
        goRight= false
        goLeft= false
        shot= false
    }
    fun hit(balls:ArrayList<Player>, i:Int) {

        /*for (k in 0..(blocksWalk.size - 1)){
            if((((blocksWalk[k].x + blocksWalk[k].width) < (x - 20)) || (blocksWalk[k].x) > (x + 20)) &&
                    (((blocksWalk[k].y + blocksWalk[k].height) < (y - 20)) || (blocksWalk[k].y) > (y + 20))){
                when{
                    ((blocksWalk[k].x + blocksWalk[k].width) < (x - 20)) -> x = blocksWalk[k].x +
                            blocksWalk[k].width + 20
                    ((blocksWalk[k].x) > (x + 20)) -> blocksWalk[k].x - 20
                    ((blocksWalk[k].y + blocksWalk[k].height) < (y - 20)) -> y = blocksWalk[k].y +
                            blocksWalk[k].height + 20
                    ((blocksWalk[k].y) > (y + 20)) -> blocksWalk[k].y - 20
                }
            }
        }
        for (k in 0..(blocksFire.size - 1)){
            if((((blocksFire[k].x + blocksFire[k].width) < (x - 20)) || (blocksFire[k].x) > (x + 20)) &&
                    (((blocksFire[k].y + blocksFire[k].height) < (y - 20)) || (blocksFire[k].y) > (y + 20))){
                when{
                    ((blocksFire[k].x + blocksFire[k].width) < (x - 20)) -> x = blocksFire[k].x +
                            blocksFire[k].width + 20
                    ((blocksFire[k].x) > (x + 20)) -> blocksFire[k].x - 20
                    ((blocksFire[k].y + blocksFire[k].height) < (y - 20)) -> y = blocksFire[k].y +
                            blocksFire[k].height + 20
                    ((blocksFire[k].y) > (y + 20)) -> blocksFire[k].y - 20
                }
            }
        }*/
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