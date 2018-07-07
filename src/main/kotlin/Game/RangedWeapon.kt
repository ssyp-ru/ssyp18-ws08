package Game

import org.newdawn.slick.geom.Vector2f

abstract class RangedWeapon(val rapidiy:Float, val damage:Int, override val cooldown:Float, val ID:Int, val velocity:Float,
                            val ammo:Int):Weapon(){


//    fun drow(g:org.newdawn.slick.Graphics){
//    } //this is bullets

    var ammoCounter = ammo
    override var cooldownCounter = cooldown

    override fun draw(g:org.newdawn.slick.Graphics, arrBullets:ArrayList<Bullets>){
        val r:Float = 5F
        for (i in arrBullets) g.fillOval(i.x - r / 2, i.y - r / 2, r, r)
    }

    override fun attack(arrPlayers:HashMap<String, Player>, k:Player, arrBullets:ArrayList<Bullets>) {
        if (cooldownCounter == cooldown) {
            when (ammoCounter){
                in 0..ammo -> {
                    val direct = mouseVec.normalise()
                    arrBullets.add(Bullets(playerX - playerR + direct.scale(playerR).x,
                            playerY - playerR + direct.scale(playerR).y, direct.scale(velocity), damage))
                    cooldownCounter -= rapidiy
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
             override var mouseVec:Vector2f):RangedWeapon(2F, 1, 10F, 101, 15F, 7){}