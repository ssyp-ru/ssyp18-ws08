package Game

import org.newdawn.slick.Color
import org.newdawn.slick.Image
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import kotlin.math.*

abstract class Meelee(val attackRange:Float,
                      val attackAngle:Float,
                      override val cooldown:Float,
                      val damage:Int,
                      val ID:Int):Weapon(), Serializable{
    override var cooldownCounter = 0F
    var x = 0F
    var y = 0F
    var r = 0F

    override fun draw(g:org.newdawn.slick.Graphics, arrBullets:ArrayList<Bullets>) {
        //println("drawing meele")
//        x = playerX - playerR * attackRange / 2
//        y = playerY - playerR * attackRange / 2
//        r = playerR * (attackRange + 2)
//        g.color = Color.transparent
//        val temp = toDegree(PI) / 3 * atan(mouseVec.y / mouseVec.x)
//        val tempAngle:Float
//        if (mouseVec.x >= 0) {
//            tempAngle = attackAngle * cooldownCounter / cooldown / 2
//            g.fillArc(x, y, r, r, -tempAngle + temp, tempAngle + temp)
//        } else {
//            tempAngle = toDegree(PI) - attackAngle * cooldownCounter / cooldown / 2
//            g.fillArc(x, y, r, r,tempAngle + temp,-tempAngle + temp)
//        }
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
    override fun attack(arrPlayers:HashMap<String, Player>, k:Player, arrBullets:ArrayList<Bullets>){
        if (cooldownCounter == cooldown){
            cooldownCounter = 0F
            for (player in arrPlayers){
                if (hitScan(player.value) && player.value != k) {
                    player.value.HP -= damage
                    if (player.value.HP <= 0){
                        ++k.kills
                        ++k.killStreak
                    }
                }
            }
        }
    }

    fun hitScan(enemy: Player): Boolean {
        val vecDistance = Vector2f(enemy.x - playerX,enemy.y - playerY)
        vecDistance.add(toDegree(PI) / 2 - mouseVec.getTheta())
        val enemyX = vecDistance.x
        val enemyY = vecDistance.y
        val meCtg2aVecNormal = (r / 2F / cos(attackAngle / toDegree(PI)* 2 * PI) *
                sin(attackAngle / toDegree(PI)* 2 * PI)).toFloat()
        return inside((-meCtg2aVecNormal), (meCtg2aVecNormal),
                enemyX - enemy.R, enemyX + enemy.R) &&
                inside((playerR), r / 2,
                        enemyY - enemy.R, enemyY + enemy.R)
    }
}

class Knife(override var playerX: Float, override var playerY: Float, override val playerR: Float,
            override var mouseVec:Vector2f): Meelee(3F, 90F, 30F, 40, 0)
class Rapier(override var playerX: Float, override var playerY: Float, override val playerR: Float,
             override var mouseVec:Vector2f): Meelee(10F, 15F, 60F, 30, 1)
class DeathPuls(override var playerX: Float, override var playerY: Float, override val playerR: Float,
                override var mouseVec:Vector2f) : Meelee(5000F, 0.1F, 180F, 189, 2)