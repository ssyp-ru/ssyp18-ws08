package Game

import org.newdawn.slick.Color
import org.newdawn.slick.Graphics
import org.newdawn.slick.Image
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable
import java.lang.invoke.SerializedLambda

class WeaponMap(val vect: Vector2f, var duration: Float, var loot:Weapon? = null):Serializable{
    fun draw(g: Graphics, image:Array<Image>){
        if (loot is Pistol) image[3].draw(vect.x, vect.y, 32F, 32F)
        if (loot is MiniGun) image[4].draw(vect.x, vect.y, 32F, 32F)
        if (loot is Awp) image[5].draw(vect.x, vect.y, 32F, 32F)
        if (loot is Rapier) image[1].draw(vect.x, vect.y, 32F, 32F)
        if (loot == null ) return
    }
}