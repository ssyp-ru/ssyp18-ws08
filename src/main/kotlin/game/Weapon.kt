package game

import org.newdawn.slick.geom.Vector2f
import java.io.Serializable

abstract class Weapon : Serializable {
    abstract fun attack(arrPlayers: HashMap<String, Player>, k: Player, arrBullets: ArrayList<Bullets>)
    abstract var playerX: Float
    abstract var playerY: Float
    abstract val playerR: Float
    abstract var mouseVec: Vector2f
    abstract val cooldown: Float
    abstract var cooldownCounter: Float
    abstract fun draw(g: org.newdawn.slick.Graphics, arrBullets: ArrayList<Bullets>)
}