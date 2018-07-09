package ssyp18

import org.newdawn.slick.geom.Vector2f
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class Furniture (var x: Float,  var y: Float, var R: Float) : WebInterface {

//    override fun getState(): String = "${this.x}/${this.y}/${this.R}"

    var colorBull = org.newdawn.slick.Color(Random().nextInt(255) / 255F,
            Random().nextInt(255) / 255F,
            Random().nextInt(255) / 255F)

    fun draw(g: org.newdawn.slick.Graphics) {
        g.color = colorBull
        g.fillOval(x, y, 2 * R, 2 * R)
    }

        private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return (sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2)))
        }
//    override fun setState(state : String){
//        val newState = state.split("/")
//        this.x = newState[0].toFloat()
//        this.y = newState[1].toFloat()
//        this.R = newState[2].toFloat()
//    }

}
