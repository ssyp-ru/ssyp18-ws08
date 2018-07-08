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

class Player(var x: Float,
             var y: Float,
             private var maxHP:Int = 5,
             val nick:String,
             var velocity: Vector2f = Vector2f(0f, 0f),
             var shot:Boolean = false,
             var punch:Boolean = false,
             var mouseVec: Vector2f,
             val R:Float = 16F,
             val speed:Float = 5F,
             val IDMeeleeWeapon:Int = 0,
             val IDRangedWeapon:Int = 0,
             var kills:Int = 0,
             var deaths:Int = 0,
             var isDead : Boolean = false): Serializable {


    var meeleeWeapon = when (IDMeeleeWeapon){
        1 -> Rapier(x, y, R, mouseVec)
        2 -> DeathPuls(x, y, R, mouseVec)
        else -> Knife(x, y, R, mouseVec)
    }
    var rangedWeapon = when (IDRangedWeapon){
        1 -> MiniGun(x, y, R, mouseVec)
        2 -> Awp(x, y, R, mouseVec)
        else -> Pistol(x, y, R, mouseVec)
    }
    var HP = maxHP
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

        meeleeWeapon.mouseVec = mouseVec
        rangedWeapon.mouseVec = mouseVec

        if (shot && HP>0) {
            rangedWeapon.attack(arrayPlayers, i, arrBullets)
        }
        if (punch && HP>0) {
            meeleeWeapon.attack(arrayPlayers, i, arrBullets)
        }
        velocity = Vector2f(0f, 0f)
        shot= false
        punch = false
    }


    fun hit(arrPLayers:ArrayList<Player>, i:Int, cells:Array<Array<Cell>>) {
        for (n in 0..(cells.size - 1)){
            for (m in 0..(cells.size - 1)){
                if (cells[n][m].type > 1) {
                    val dis = distance(x, y, (cells[n][m].x.toFloat()), (cells[n][m].y.toFloat()))
                    if ((dis < R + 16)) {
                        val b1 = Vector2f(x - (cells[n][m].x), y - (cells[n][m].y)).normalise().scale((R - dis + 16) / 2)
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
        meeleeWeapon.playerX = x
        meeleeWeapon.playerY = y
        rangedWeapon.playerX = x
        rangedWeapon.playerY = y
    }

    fun drawReload(g : Graphics, x : Float, y : Float){
        if(isDead) return
        val maxHP = 5
        val widthReloadBar : Float = 100f
        val heightReloadBar : Float = 9f
        g.color = Color(0f, 0f, 0f)
        g.fillRect(x - 2, y + 90F, widthReloadBar + 4, heightReloadBar)
        g.color = Color.yellow
        g.fillRect(x, y + 92F, widthReloadBar * rangedWeapon.cooldownCounter / rangedWeapon.cooldown, heightReloadBar - 4F)
        g.color = Color.white
        g.drawString("${rangedWeapon.ammoCounter}", x, y + 60F)
    }

    fun drawHP(g : Graphics, x : Float, y : Float){
        if(isDead) return
        val widthBar : Float = 100f
        val heightBar : Float = 20f
        g.color = Color(0f, 0f, 0f)
        g.fillRect(x - 2, y, widthBar + 4, heightBar + 3)
        g.color = Color(1f,0f,0f)
        g.fillRect(x, y + 2, widthBar * this.HP / maxHP, heightBar)
        g.color = Color.white
        g.drawString("HP: $HP", x, y - 4f)
    }
}