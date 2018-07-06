package TeamGay_Player

import org.newdawn.slick.Color
import org.newdawn.slick.geom.Vector2f
import kotlin.math.pow

abstract class Bullet(var bulletX: Float, var bulletY: Float, val ammo: Int, val damage: Int, var cooldown: Float,
                      val velocity: Float, val attackingRange: Int, val R: Float) {
    abstract var mouseVec: Vector2f
    var cooldownCounter = cooldown

    fun draw (g: org.newdawn.slick.Graphics) {
        g.color = Color.gray
        g.fillOval(bulletX, bulletY, R*2, R*2)
    }
    fun hitScan (enemy: Player): Boolean{
        return (bulletX.pow(2) + bulletY.pow(2) <= (enemy.R + this.R).pow(2))
    }
    fun hitMaking (enemy: Player) {
        if (cooldownCounter == cooldown) {
            cooldownCounter = 0F
            for (i in 1..(attackingRange / velocity).toInt()) {
                if (hitScan(enemy)) enemy.HP -= damage
            }
        }
    }
}

//class Pistol(): Bullet(var bulletX: Float, var bulletY: Float, var mouseVec:Vector2f) {}