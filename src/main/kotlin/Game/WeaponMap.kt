package Game

import org.newdawn.slick.Color
import org.newdawn.slick.Graphics
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.lang.invoke.SerializedLambda

class WeaponMap(val vect: Vector2f, var duration: Float, var loot:Weapon? = null):Serializable{
    fun draw(g: Graphics){
        if (loot is Pistol) g.color = Color.red
        if (loot is MiniGun) g.color = Color.white
        if (loot is Awp) g.color = Color.blue
        if (loot is Rapier) g.color = Color.orange
        if (loot == null ) return
        g.drawOval(vect.x, vect.y, 32F, 32F)
    }
}