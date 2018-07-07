package game

import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import kotlin.math.*

abstract class Melee(val attackRange:Float,
                     val attackAngle:Float,
                     override val cooldown:Float,
                     val damage:Int,
                     val ID:Int):Weapon(), Serializable{
    override var cooldownCounter = 0F
    var x = 0F
    var y = 0F

    override fun draw(g:org.newdawn.slick.Graphics, arrBullets:ArrayList<Bullets>) {
        //println("drawing meele")
    }

    /*fun attack(arrPlayers:ArrayList<Player>, k:Player){
        if (cooldownCounter == cooldown){
            cooldownCounter = 0F
            for (i in arrPlayers){
                if (hitScan(i) && i != k) i.hp -= damage
            }
        }
    }
    */
    override fun attack(arrPlayers:HashMap<String, Player>, k:Player, arrBullets:ArrayList<Bullets>){
        if (cooldownCounter == cooldown){
            cooldownCounter = 0F
            for (player in arrPlayers){
                if (hitScan(player.value) && player.value != k) {
                    player.value.hp -= damage
                    if (player.value.hp <= 0){
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
        val meCtg2aVecNormal = (attackRange / 2F / cos(attackAngle / toDegree(PI)* 2 * PI) *
                sin(attackAngle / toDegree(PI)* 2 * PI)).toFloat()
        return inside((-meCtg2aVecNormal), (meCtg2aVecNormal),
                enemyX - enemy.R, enemyX + enemy.R) &&
                inside((playerR), attackRange / 2,
                        enemyY - enemy.R, enemyY + enemy.R)
    }
}

class Knife(override var playerX: Float, override var playerY: Float, override val playerR: Float,
            override var mouseVec:Vector2f): Melee(3F, 90F, 5F, 5, 0)
class Rapier(override var playerX: Float, override var playerY: Float, override val playerR: Float,
             override var mouseVec:Vector2f): Melee(20F, 15F, 5F, 8, 1) {}
class DeathPulse(override var playerX: Float, override var playerY: Float, override val playerR: Float,
                override var mouseVec:Vector2f) : Melee(5000F, 0.1F, 180F, 8, 2) {}

