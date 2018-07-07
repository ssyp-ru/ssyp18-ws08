package game

import org.newdawn.slick.Image
import org.newdawn.slick.geom.Vector2f
import java.io.Serializable

class WeaponMap(val vect: Vector2f, var duration: Float, var loot: Weapon? = null) : Serializable {
    fun draw(image: Array<Image>) {
        when (loot) {
            is Pistol -> image[3].draw(vect.x, vect.y, 32F, 32F)
            is MiniGun -> image[4].draw(vect.x, vect.y, 32F, 32F)
            is Awp -> image[5].draw(vect.x, vect.y, 32F, 32F)
            is Rapier -> image[1].draw(vect.x, vect.y, 32F, 32F)
            else -> return
        }
    }
}