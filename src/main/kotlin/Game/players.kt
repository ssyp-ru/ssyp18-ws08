package Game

import org.newdawn.slick.*
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.*

enum class PlayerState {
    CHILLSHOT, CHILLMELEE, SHOOT, MELEE
}

class Player(var x: Float,
             var y: Float,
             var maxHP:Int = 100,
             val nick:String,
             var velocity: Vector2f = Vector2f(0f, 0f),
             var mouseVec: Vector2f,
             val R: Float = 16F,
             val speed: Float = 5F,
             var numMeeleeWeapon: Int = 0,
             var numRangedWeapon: Int = 0,
             var kills: Int = 0,
             var killStreak: Int = 0,
             var deaths: Int = 0,
             val mapR: Int = 16) : Serializable {

    var arrayMeeleeWeapon = ArrayList<Meelee>()
    var arrayRangedWeapon = ArrayList<RangedWeapon>()
    var HP = maxHP
    var shot = false
    var punch = false
    var playerState = PlayerState.CHILLMELEE

    init {
        arrayMeeleeWeapon.add(Knife(x, y, R, mouseVec))
//        arrayMeeleeWeapon.add(Rapier(x, y, R, mouseVec))
//        arrayMeeleeWeapon.add(DeathPuls(x, y, R, mouseVec))
//        arrayRangedWeapon.add(Pistol(x, y, R, mouseVec))
//        arrayRangedWeapon.add(MiniGun(x, y, R, mouseVec))
//        arrayRangedWeapon.add(Awp(x, y, R, mouseVec))
    }

    val rangedIdReturn: Int
        get() {
            if (arrayRangedWeapon.size - 1 >= numRangedWeapon) {
                return when (arrayRangedWeapon[numRangedWeapon]) {
                    is Awp -> 2
                    is MiniGun -> 1
                    else -> 0
                }
            }
            return 3
        }
    val meeleeIdReturn: Int
        get() {
            if (arrayMeeleeWeapon.size - 1 >= numMeeleeWeapon) {
                return when (arrayMeeleeWeapon[numMeeleeWeapon]) {
                    is DeathPulse -> 2
                    is Rapier -> 1
                    else -> 0
                }
            }
            return 0
        }

    fun shot() {
        if(arrayRangedWeapon.size == 0)return
        if (arrayRangedWeapon.size - 1 >= numRangedWeapon) {
            if (PlayerState.SHOOT != playerState && arrayRangedWeapon[numRangedWeapon].attackReady()) {
                shot = true
                playerState = PlayerState.SHOOT
                PlayerAnimations.getAnimation("ranged", rangedIdReturn, mouseVec).start()
            }
        }
    }

    fun punch() {
        punch = true
        playerState = PlayerState.MELEE
        PlayerAnimations.getAnimation("meelee", meeleeIdReturn, mouseVec).start()
    }

    fun draw(g: org.newdawn.slick.Graphics) {
        if (playerState == PlayerState.CHILLMELEE) {
            val id = meeleeIdReturn
            PlayerAnimations.getAnimation("meelee", id, mouseVec).getImage(0).draw(
                    if(id == 1)x - 24F else x, if(id == 1)y - 24F else y)
        }
        if (playerState == PlayerState.CHILLSHOT) {
            val id = rangedIdReturn
            PlayerAnimations.getAnimation("ranged", id, mouseVec).getImage(0).draw(x, y)
        }
        if (playerState == PlayerState.SHOOT) {
            val id = rangedIdReturn
            PlayerAnimations.getAnimation("ranged", id, mouseVec).update()
            PlayerAnimations.getAnimation("ranged", id, mouseVec).draw(x, y, mouseVec)
            if (PlayerAnimations.getAnimation("ranged", id, mouseVec).isStopped)
                playerState = PlayerState.CHILLSHOT
        }
        if (playerState == PlayerState.MELEE) {
            val id = meeleeIdReturn
            PlayerAnimations.getAnimation("meelee", id, mouseVec).update()
            PlayerAnimations.getAnimation("meelee", id, mouseVec).draw(
                    if(id == 1)x - 24F else x, if(id == 1)y - 24F else y, mouseVec)
            if (PlayerAnimations.getAnimation("meelee", id, mouseVec).isStopped)
                playerState = PlayerState.CHILLMELEE
        }
    }


    fun controlPlayer(arrayPlayers:HashMap<String, Player>, i:Player, arrBullets:ArrayList<Bullets>){
        val tempForSpeed = speed
        val movement = Vector2f(0F, 0F)
        movement.x += velocity.x
        movement.y += velocity.y
        movement.scale(tempForSpeed)
        x += movement.x
        y += movement.y

        if (arrayMeeleeWeapon.size - 1 >= numMeeleeWeapon) {
            arrayMeeleeWeapon[numMeeleeWeapon].mouseVec = mouseVec
        }
        if (arrayRangedWeapon.size - 1 >= numRangedWeapon) {
            arrayRangedWeapon[numRangedWeapon].mouseVec = mouseVec
        }
        if (shot && arrayRangedWeapon.size - 1 >= numRangedWeapon) {
            arrayRangedWeapon[numRangedWeapon].attack(arrayPlayers, i, arrBullets)
            shot = false
        }
        if (punch && numMeeleeWeapon <= arrayMeeleeWeapon.size - 1) {
            arrayMeeleeWeapon[numMeeleeWeapon].attack(arrayPlayers, i, arrBullets)
            punch = false
        }
        velocity = Vector2f(0f, 0f)
    }


    fun hit(arrPLayers: ArrayList<Player>, i: Int, cells: Array<Array<Cell>>, drop:ArrayList<WeaponMap>) {
        for (n in 0..(cells.size - 1)) {
            for (m in 0..(cells.size - 1)) {
                if ((cells[n][m].type == layer.CRATES) || (cells[n][m].type == layer.WATER) ||
                        (cells[n][m].type == layer.HOUSES)) {
                    val dis = distance(x, y, (cells[n][m].x.toFloat()), (cells[n][m].y.toFloat()))
                    if ((dis < R + mapR)) {
                        val b1 = Vector2f(x - (cells[n][m].x), y - (cells[n][m].y)).normalise().scale(
                                (R - dis + mapR) / 2)
                        x += b1.x
                        y += b1.y
                    }
                }
            }
        }
        var dis:Float
        for (k in (i + 1)..(arrPLayers.size - 1)){
            dis = distance(x, y, arrPLayers[k].x, arrPLayers[k].y)
            if (dis < R + arrPLayers[k].R) {
                val b2 = Vector2f(arrPLayers[k].x - x, arrPLayers[k].y - y).normalise().scale((R
                        + arrPLayers[k].R - dis) / 2)
                val b1 = Vector2f(x - arrPLayers[k].x, y - arrPLayers[k].y).normalise().scale((R
                        + arrPLayers[k].R - dis) / 2)
                x += b1.x
                y += b1.y
                arrPLayers[k].x += b2.x
                arrPLayers[k].y += b2.y
            }
        }
        for (weapon in drop){
            dis = distance(x, y, weapon.vect.x, weapon.vect.y)
            if (dis <= 2*R){
                var flag = false
                if (weapon.loot is RangedWeapon) {
                    val giveRangedWeapon = when (weapon.loot) {
                        is Pistol -> Pistol(x, y, R, mouseVec)
                        is MiniGun -> MiniGun(x, y, R, mouseVec)
                        is Awp -> Awp(x, y, R, mouseVec)
                        else -> Pistol(x, y, R, mouseVec)
                    }
                    for (rangeWeapon in arrayRangedWeapon) {
                        if (rangeWeapon.javaClass.name == giveRangedWeapon.javaClass.name) flag = true
                    }
                    if (!flag) {arrayRangedWeapon.add(giveRangedWeapon);weapon.loot = null;weapon.duration = 0F}
                }
                if (weapon.loot is Rapier){
                    for (meeleeWeapon in arrayMeeleeWeapon)
                        if (meeleeWeapon::class == Rapier::class) flag = true
                    if (!flag) {arrayMeeleeWeapon.add(Rapier(x, y, R, mouseVec));weapon.loot = null;weapon.duration = 0F}
                }
            }
        }
        if (numMeeleeWeapon <= arrayMeeleeWeapon.size - 1) {
            arrayMeeleeWeapon[numMeeleeWeapon].playerX = x
            arrayMeeleeWeapon[numMeeleeWeapon].playerY = y
        }
        if (numRangedWeapon <= arrayRangedWeapon.size - 1) {
            arrayRangedWeapon[numRangedWeapon].playerX = x
            arrayRangedWeapon[numRangedWeapon].playerY = y
        }
    }

    fun drawReload(g: Graphics, x: Float, y: Float) {
        if (arrayRangedWeapon.size - 1 >= numRangedWeapon) {
            val maxHP = 5
            val widthReloadBar: Float = 100f
            val heightReloadBar: Float = 9f
            g.color = Color(0f, 0f, 0f)
            g.fillRect(x - 2, y + 90F, widthReloadBar + 4, heightReloadBar)
            g.color = Color.yellow
            g.fillRect(x, y + 92F, widthReloadBar * arrayRangedWeapon[numRangedWeapon].cooldownCounter
                    / arrayRangedWeapon[numRangedWeapon].cooldown, heightReloadBar - 4F)
            g.color = Color.white
            g.drawString("${arrayRangedWeapon[numRangedWeapon].ammoCounter}", x, y + 60F)
        }
    }

    fun drawHP(g: Graphics, x: Float, y: Float) {
        val widthBar: Float = 100f
        val heightBar: Float = 20f
        g.color = Color(0f, 0f, 0f)
        var barShift = 2
        g.fillRect(x - barShift, y, widthBar + barShift * 2, heightBar + barShift)
        g.color = Color(1f,0f,0f)
        g.fillRect(x, y + barShift, widthBar * (if(this.HP < 0) 0f else this.HP / maxHP.toFloat()), heightBar)
        g.color = Color.white
        barShift = 5
        g.drawString("HP: $HP", x, y - barShift)
    }
}
