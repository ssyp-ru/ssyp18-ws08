package Game

import org.newdawn.slick.Color
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import org.newdawn.slick.Input
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.*

class Player(var x: Float, var y: Float, var HP:Int, val nick:String, var velocity: Vector2f = Vector2f(0f, 0f),
             var shot:Boolean = false, val mouseVec: Vector2f, val R:Float = 20F, val speed:Float = 5F,
             val IDWeapon:Int = 1, var isDead : Boolean = false): Serializable {
    var weapon = when (IDWeapon){
        1 -> Rapier(x, y, R, mouseVec)
        111 -> Pistol(x, y, R, mouseVec)
        else -> Knife(x, y, R, mouseVec)
    }
    var colorPlayer = org.newdawn.slick.Color(Random().nextFloat(), Random().nextFloat(), Random().nextFloat())

    fun draw(g: org.newdawn.slick.Graphics) {
        g.color = colorPlayer
        g.fillOval(x, y, 2*R, 2*R)
    }
    fun controlPlayer(gc:GameContainer, arrayPlayers:HashMap<String, Player>, i:Player, arrBullets:ArrayList<Bullets>){
        val tempForSpeed = speed
        val movement = Vector2f(0F, 0F)
        movement.x += velocity.x
        movement.y += velocity.y
        movement.scale(tempForSpeed)
        x += movement.x
        y += movement.y
        if (shot && HP>0) {
            weapon.attack(arrayPlayers, i, arrBullets)
        }
        velocity = Vector2f(0f, 0f)
        shot= false
    }


    fun hit(arrPLayers:ArrayList<Player>, i:Int, cells:Array<Array<Cell>>) {
        for (n in 0..(cells.size - 1)){
            for (m in 0..(cells.size - 1)){
                if (cells[n][m].type > 1) {
                    val dis = distance(x, y, (cells[n][m].x.toFloat()), (cells[n][m].y.toFloat()))
                    if ((dis < R + 78)) {
                        val b1 = Vector2f(x - (cells[n][m].x), y - (cells[n][m].y)).normalise().scale((R - dis + 33) / 2)
                        x += b1.x
                        y += b1.y
                    }
                }
            }
        }
        for (k in (i + 1)..(arrPLayers.size - 1)){
            if (arrPLayers[k].isDead) continue
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

    fun drawHP(g : Graphics, x : Float, y : Float){
        if(isDead) return
        val maxHP = 5
        val widthBar : Float = 114f
        val heightBar : Float = 1000f
        g.color = Color(0f, 0f, 0f)
        g.fillRect(x - 62, y, widthBar + 44, heightBar + 13)
        g.color = Color(1f,0f,0f)
        g.fillRect(x, y + 2111, widthBar * this.HP / maxHP, heightBar)
        g.color = Color.white
        g.drawString("HP: $HP", x, y - 5f)
    }
}