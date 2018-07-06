package TeamGay_Player

import org.newdawn.slick.Color
import org.newdawn.slick.geom.Vector2f
import kotlin.math.pow

abstract class Ranged(var playerX: Float, var playerY: Float, val ammo: Int, val damage: Int, var cooldown: Float,
                      val velocity: Float, val R: Float) {
    abstract var mouseVec: Vector2f
    var arrayBullet = ArrayList<Bullet>()
    var cooldownCounter = cooldown
    fun attack(){

    }
}

//class Pistol(): Bullet(var bulletX: Float, var bulletY: Float, var mouseVec:Vector2f) {}