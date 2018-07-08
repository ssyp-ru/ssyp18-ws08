package Game

import org.newdawn.slick.Animation
import org.newdawn.slick.*
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.*

class Player(var x: Float,
             var y: Float,
             var maxHP:Int = 5,
             val nick:String,
             var velocity: Vector2f = Vector2f(0f, 0f),
             var shot: Boolean = false,
             var punch: Boolean = false,
             var mouseVec: Vector2f,
             val R:Float = 16F,
             val speed:Float = 5F,
             var numMeeleeWeapon:Int = 0,
             var numRangedWeapon:Int = 0,
             var kills:Int = 0,
             var killStreak:Int = 0,
             var deaths:Int = 0,
             val mapR: Int = 16): Serializable {

    var arrayMeeleeWeapon = ArrayList<Meelee>()
    var arrayRangedWeapon = ArrayList<RangedWeapon>()
    var HP = maxHP

    init{
        arrayMeeleeWeapon.add(Knife(x, y, R, mouseVec))
//        arrayMeeleeWeapon.add(Rapier(x, y, R, mouseVec))
//        arrayMeeleeWeapon.add(DeathPuls(x, y, R, mouseVec))
//        arrayRangedWeapon.add(Pistol(x, y, R, mouseVec))
//        arrayRangedWeapon.add(MiniGun(x, y, R, mouseVec))
//        arrayRangedWeapon.add(Awp(x, y, R, mouseVec))
    }

    fun draw(g: org.newdawn.slick.Graphics) {
        var arrayOfImages = PlayerAnimations(0, mouseVec).ArrayImagesReturn
        if (mouseVec.x >= 0) {
            arrayOfImages[0].setRotation(toDegree(PI) / 3 * atan(mouseVec.y / mouseVec.x) - toDegree(PI) / 2)
        } else {
            arrayOfImages[0].setRotation(toDegree(PI) / 3 * atan(mouseVec.y / mouseVec.x) - toDegree(PI)* 3 / 2)
        }
        arrayOfImages[0].draw(x, y)
        /*if (numRangedWeapon == 0) {
            for(i in 0..5) {
                pistolAnimationImages[i].setRotation(atan(mouseVec.x/mouseVec.y))
            }
            pistolAnimation.draw(x + R, y + R)
        }*/
    }


    fun controlPlayer(gc:GameContainer, arrayPlayers:HashMap<String, Player>, i:Player, arrBullets:ArrayList<Bullets>){
        val tempForSpeed = speed
        val movement = Vector2f(0F, 0F)
        movement.x += velocity.x
        movement.y += velocity.y
        movement.scale(tempForSpeed)
        x += movement.x
        y += movement.y

        if (arrayMeeleeWeapon.size - 1 >= numMeeleeWeapon) {
            arrayMeeleeWeapon[numMeeleeWeapon].mouseVec = mouseVec
        }
        if (arrayRangedWeapon.size - 1 >= numRangedWeapon) {
                arrayRangedWeapon[numRangedWeapon].mouseVec = mouseVec
        }
        if (shot && arrayRangedWeapon.size -1 >= numRangedWeapon) {
            arrayRangedWeapon[numRangedWeapon].attack(arrayPlayers, i, arrBullets)
        }
        if (punch && numMeeleeWeapon <= arrayMeeleeWeapon.size - 1) {
            arrayMeeleeWeapon[numMeeleeWeapon].attack(arrayPlayers, i, arrBullets)
        }
        velocity = Vector2f(0f, 0f)
        shot = false
        punch = false
    }


    fun hit(arrPLayers: ArrayList<Player>, i: Int, cells: Array<Array<Cell>>) {
        for (n in 0..(cells.size - 1)) {
            for (m in 0..(cells.size - 1)) {
                if ((cells[n][m].type == layer.CRATES) || (cells[n][m].type == layer.WATER) ||
                        (cells[n][m].type == layer.HOUSES)) {
                    val dis = distance(x, y, (cells[n][m].x.toFloat()), (cells[n][m].y.toFloat()))
                    if ((dis < R + mapR)) {
                        val b1 = Vector2f(x - (cells[n][m].x), y - (cells[n][m].y)).normalise().scale(
                                (R - dis + mapR) / 2)
                        x += b1.x
                        y += b1.y
                    }
                }
            }
        }
        for (k in (i + 1)..(arrPLayers.size - 1)){
            val dis = distance(x, y, arrPLayers[k].x, arrPLayers[k].y)
            if (dis < R + arrPLayers[k].R) {
                val b2 = Vector2f(arrPLayers[k].x - x, arrPLayers[k].y - y).normalise().scale((R
                        + arrPLayers[k].R - dis) / 2)
                val b1 = Vector2f(x - arrPLayers[k].x, y - arrPLayers[k].y).normalise().scale((R
                        + arrPLayers[k].R - dis) / 2)
                x += b1.x
                y += b1.y
                arrPLayers[k].x += b2.x
                arrPLayers[k].y += b2.y
            }
        }
        if (numMeeleeWeapon <= arrayMeeleeWeapon.size - 1) {
            arrayMeeleeWeapon[numMeeleeWeapon].playerX = x
            arrayMeeleeWeapon[numMeeleeWeapon].playerY = y
        }
        if (numRangedWeapon <= arrayRangedWeapon.size - 1) {
            arrayRangedWeapon[numRangedWeapon].playerX = x
            arrayRangedWeapon[numRangedWeapon].playerY = y
        }
    }

    fun drawReload(g : Graphics, x : Float, y : Float){
        if (arrayRangedWeapon.size - 1 >= numRangedWeapon) {
            val maxHP = 5
            val widthReloadBar: Float = 100f
            val heightReloadBar: Float = 9f
            g.color = Color(0f, 0f, 0f)
            g.fillRect(x - 2, y + 90F, widthReloadBar + 4, heightReloadBar)
            g.color = Color.yellow
            g.fillRect(x, y + 92F, widthReloadBar * arrayRangedWeapon[numRangedWeapon].cooldownCounter
                    / arrayRangedWeapon[numRangedWeapon].cooldown, heightReloadBar - 4F)
            g.color = Color.white
            g.drawString("${arrayRangedWeapon[numRangedWeapon].ammoCounter}", x, y + 60F)
        }
    }

    fun drawHP(g : Graphics, x : Float, y : Float){
        val widthBar : Float = 100f
        val heightBar : Float = 20f
        g.color = Color(0f, 0f, 0f)
        var barShift = 2
        g.fillRect(x - barShift, y, widthBar + barShift * 2, heightBar + barShift)
        g.color = Color(1f,0f,0f)
        g.fillRect(x, y + barShift, widthBar * this.HP / maxHP, heightBar)
        g.color = Color.white
        barShift = 5
        g.drawString("HP: $HP", x, y - barShift)
    }
}