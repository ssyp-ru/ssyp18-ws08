package Game

import org.newdawn.slick.Color
import org.newdawn.slick.geom.Vector2f

abstract class RangedWeapon(val rapidiy:Float, val damage:Int, override val cooldown:Float, val ID:Int, val velocity:Float,
                            val ammo:Int):Weapon(){


//    fun drow(g:org.newdawn.slick.Graphics){
//    } //this is bullets

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

        if (cooldownCounter == cooldown) {
            when (ammoCounter){
                in 1..ammo -> {
                    val bulletR = 5F
                    val direct = mouseVec.normalise()
                    val vecSpawn = direct.scale(playerR + bulletR * 2)
                    arrBullets.add(Bullets(playerX + playerR + vecSpawn.x,
                            playerY + playerR + vecSpawn.y, direct.scale(velocity), damage, bulletR))
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
             override var mouseVec:Vector2f):RangedWeapon(20F, 3, 300F, 101, 0.3F, 7){}
class MiniGun(override var playerX:Float, override var playerY:Float, override val playerR:Float,
             override var mouseVec:Vector2f):RangedWeapon(3F, 3, 600F, 101, 0.3F, 200){}