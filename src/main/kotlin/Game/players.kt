package Game

import org.newdawn.slick.GameContainer
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.*

class Player(var x: Float, var y: Float, var HP:Int, val nick:String, var goUp:Boolean = false, var goDown:Boolean = false,
             var goLeft:Boolean = false, var goRight:Boolean = false, var shot:Boolean = false, val mouseVec: Vector2f,
             val R:Float = 20F, val speed:Float = 5F, val IDWeapon:Int = 0): Serializable {
    val arrBullets = ArrayList<Bullets>()
    var weapon = when (IDWeapon){
        1 -> Rapier(x, y, R, mouseVec)
        101 -> Pistol(x, y, R, mouseVec)
        else -> Knife(x, y, R, mouseVec)
    }
    var colorPlayer = org.newdawn.slick.Color(Random().nextFloat(),
            Random().nextFloat(),
            Random().nextFloat())

    fun draw(g: org.newdawn.slick.Graphics) {
        weapon.draw(g, arrBullets)
        g.color = colorPlayer
        g.fillOval(x, y, 2*R, 2*R)
    }
    fun controlPlayer(gc:GameContainer, arrayPlayers:HashMap<String, Player>, i:Player){
        val tempForSpeed = speed
        val movement = Vector2f(0F, 0F)
        movement.x += (if (goRight) 1F else 0F) + (if (goLeft) -1F else 0F)
        movement.y += (if (goDown) 1F else 0F) + (if (goUp) -1F else 0F)
        movement.normalise().scale(tempForSpeed)
        x += movement.x
        y += movement.y
        if (shot && HP>0) weapon.attack(arrayPlayers, i, arrBullets)
        goDown= false
        goUp= false
        goRight= false
        goLeft= false
        shot= false
    }

    fun hit(arrPLayers:ArrayList<Player>, i:Int) {
        for (k in (i + 1)..(arrPLayers.size - 1)){
            val dis = distance(x, y, arrPLayers[k].x, arrPLayers[k].y)
            if (dis < R + arrPLayers[k].R) {
                val b2 = Vector2f(arrPLayers[k].x - x, arrPLayers[k].y - y).normalise().scale((R + arrPLayers[k].R - dis) / 2)
                val b1 = Vector2f(x - arrPLayers[k].x, y - arrPLayers[k].y).normalise().scale((R + arrPLayers[k].R - dis) / 2)
                x += b1.x
                y += b1.y
                arrPLayers[k].x += b2.x
                arrPLayers[k].y += b2.y
            }
        }
        weapon.playerX = x
        weapon.playerY = y
    }
}