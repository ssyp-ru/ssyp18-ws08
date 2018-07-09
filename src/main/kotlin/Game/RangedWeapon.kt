package Game

import org.newdawn.slick.Color
import org.newdawn.slick.Image
import org.newdawn.slick.geom.Vector2f
import java.util.*

abstract class RangedWeapon(val rapidiy:Float,
                            val damage:Int,
                            override val cooldown:Float,
                            val ID:Int,
                            val velocity:Float,
                            val ammo:Int,
                            val recoil:Int,
                            val bulletR:Float):Weapon(){


//    fun drow(g:org.newdawn.slick.Graphics){
//    } //this is bullets
    fun attackReady():Boolean{
        return cooldownCounter == cooldown
    }

    var ammoCounter = ammo
    override var cooldownCounter = 0F

    override fun draw(g:org.newdawn.slick.Graphics, arrBullets:ArrayList<Bullets>){
        g.color = Color.magenta
        for (i in arrBullets) {
            g.fillOval(i.x, i.y, i.r * 2, i.r * 2)
//            println("narisoval" + "${i.x - r / 2}" + "${i.y - r / 2}")
        }
    }

    override fun attack(arrPlayers:HashMap<String, Player>, k:Player, arrBullets:ArrayList<Bullets>) {

        if (attackReady()) {
            when (ammoCounter){
                in 1..ammo -> {
                    val direct = mouseVec.normalise()
                    val vecSpawn = direct.scale(playerR + bulletR * 2.5F)
                    direct.add((Random().nextInt(recoil * 2) - recoil).toDouble())
                    arrBullets.add(Bullets(playerX + playerR + vecSpawn.x,
                            playerY + playerR + vecSpawn.y, direct.scale(velocity), damage, bulletR, k))
                    cooldownCounter -= rapidiy
                    ammoCounter --
                    if (ammoCounter == 0) {cooldownCounter = 0F; ammoCounter = ammo}
                }
                else -> {
                    cooldownCounter = 0F
                    ammoCounter = ammo
                }
            }
        }
    }
}

class Pistol(override var playerX:Float, override var playerY:Float, override val playerR:Float,
             override var mouseVec:Vector2f):RangedWeapon(20F, 15, 150F, 3, 0.3F,
        7, 5, 3F)

class MiniGun(override var playerX:Float, override var playerY:Float, override val playerR:Float,
              override var mouseVec:Vector2f):RangedWeapon(3F, 5, 300F, 4, 0.2F,
        200, 45, 5F)

class Awp(override var playerX:Float, override var playerY:Float, override val playerR:Float,
              override var mouseVec:Vector2f):RangedWeapon(60F, 45, 120F, 5, 1F,
        5, 1, 10F)