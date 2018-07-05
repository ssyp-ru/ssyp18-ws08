package TeamGay_Player

import org.newdawn.slick.Color
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import kotlin.math.*

abstract class Meelee(val attackRange:Float, val attackAngle:Float, val cooldown:Float, val damage:Int,
                      val ID:Int):Serializable{
    abstract var playerX:Float
    abstract var playerY:Float
    abstract var mouseVec:Vector2f
    abstract val playerR:Float
    var cooldownCounter = cooldown
    fun draw(g:org.newdawn.slick.Graphics) {
        g.color = if (cooldownCounter >= cooldown) Color.blue else Color.red
        if (mouseVec.x >= 0) {
            g.fillArc(playerX - playerR * attackRange / 2, playerY - playerR * attackRange / 2,
                    playerR * (attackRange + 2), playerR * (attackRange + 2),
                    -attackAngle * cooldownCounter / cooldown / 2 + (60 * atan(mouseVec.y / mouseVec.x)),
                    attackAngle * cooldownCounter / cooldown / 2 + (60 * atan(mouseVec.y / mouseVec.x)))
        } else {
            g.fillArc(playerX - playerR * attackRange / 2, playerY - playerR * attackRange / 2,
                    playerR * (attackRange + 2), playerR * (attackRange + 2),
                    180 - attackAngle / 2 * cooldownCounter / cooldown - (60 * atan(-mouseVec.y/mouseVec.x)),
                    -180 + attackAngle / 2 * cooldownCounter / cooldown - (60 * atan(-mouseVec.y/mouseVec.x)))
        }
    }
    fun attack(arrPlayers:ArrayList<Player>){
        if (cooldownCounter == cooldown){
            cooldownCounter = 0F
            for (i in 1..arrPlayers.size - 1){
                if (hitScan(arrPlayers[i])) arrPlayers[i].HP -= damage
            }
        }
    }
    fun hitScan(enemy: Player): Boolean {

        val vecDistance = Vector2f(enemy.x - playerX,
                enemy.y - playerY)
        println(vecDistance.x)
        println(vecDistance.y)
        vecDistance.add(-90 + mouseVec.getTheta())
        println(vecDistance.x)
        println(vecDistance.y)
        val enemyX = vecDistance.x
        val enemyY = vecDistance.y
        val meCtg2aVecNormal = (playerR * (attackRange + 2F) / 2F / cos(attackAngle / 360 * PI) * sin(attackAngle / 360 * PI)).toFloat()
//        println(meCtg2aVecNormal)
//        println((playerR * (attackRange + 2)) / 2)
        return ifIn((-meCtg2aVecNormal), (meCtg2aVecNormal),
                enemyX - enemy.R, enemyX + enemy.R) &&
        ifIn((0F), (playerR * (attackRange + 2)) / 2,
                enemyY - enemy.R, enemyY + enemy.R)
    }
    private fun distance(x1:Float, y1:Float, x2:Float, y2:Float):Float{
        return(sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2)))
    }
}

class Knife(override var playerX: Float, override var playerY: Float, override var mouseVec:Vector2f,
            override val playerR: Float):Meelee(1F, 90F, 30F, 1, 0) {}
class Rapier(override var playerX: Float, override var playerY: Float, override var mouseVec:Vector2f,
             override val playerR: Float):Meelee(5F, 15F, 1F, 1, 1) {}