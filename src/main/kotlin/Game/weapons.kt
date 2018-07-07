package Game

import org.newdawn.slick.Color
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import kotlin.math.*

abstract class Meelee(val attackRange:Float, val attackAngle:Float, val cooldown:Float, val damage:Int,
                      val ID:Int):Serializable{
    abstract var playerX:Float
    abstract var playerY:Float
    abstract val playerR:Float
    abstract var mouseVec:Vector2f
    var cooldownCounter = cooldown
    val x = playerX - playerR * attackRange / 2
    val y = playerY - playerR * attackRange / 2
    val r = playerR * (attackRange + 2)

    fun draw(g:org.newdawn.slick.Graphics) {
        g.color = if (cooldownCounter >= cooldown) Color.blue else Color.red
        val temp = 60 * atan(mouseVec.y / mouseVec.x)
        val tempAngle:Float
        if (mouseVec.x >= 0) {
            tempAngle = attackAngle * cooldownCounter / cooldown / 2 + temp
            g.fillArc(x, y, r, r, -tempAngle, tempAngle)
        } else {
            tempAngle = 180 - attackAngle * cooldownCounter / cooldown / 2
            g.fillArc(x, y, r, r,tempAngle - temp,-tempAngle - temp)
        }
    }

    /*fun attack(arrPlayers:ArrayList<Player>, k:Player){
        if (cooldownCounter == cooldown){
            cooldownCounter = 0F
            for (i in arrPlayers){
                if (hitScan(i) && i != k) i.HP -= damage
            }
        }
    }
    */
    fun attack(arrPlayers:HashMap<String, Player>, k:Player){
        if (cooldownCounter == cooldown){
            cooldownCounter = 0F
            for (i in arrPlayers){
                if (hitScan(i.value) && i.value != k) i.value.HP -= damage
            }
        }
    }

    fun hitScan(enemy: Player): Boolean {
        val vecDistance = Vector2f(enemy.x - playerX,enemy.y - playerY)
        vecDistance.add(90 - mouseVec.getTheta())
        val enemyX = vecDistance.x
        val enemyY = vecDistance.y
        val meCtg2aVecNormal = (r / 2F / cos(attackAngle / 360 * PI) *
                sin(attackAngle / 360 * PI)).toFloat()
        return inside((-meCtg2aVecNormal), (meCtg2aVecNormal),
                enemyX - enemy.R, enemyX + enemy.R) &&
                inside((playerR), r / 2,
                        enemyY - enemy.R, enemyY + enemy.R)
    }
}

class Knife(override var playerX: Float, override var playerY: Float, override val playerR: Float, override var mouseVec:Vector2f)
    : Meelee(1F, 90F, 30F, 1, 0) {}
class Rapier(override var playerX: Float, override var playerY: Float, override val playerR: Float, override var mouseVec:Vector2f)
    : Meelee(5F, 15F, 60F, 1, 1) {}
class DeathPuls(override var playerX: Float, override var playerY: Float, override val playerR: Float, override var mouseVec:Vector2f)
    : Meelee(1000F, 0.1F, 180F, 1, 2) {}