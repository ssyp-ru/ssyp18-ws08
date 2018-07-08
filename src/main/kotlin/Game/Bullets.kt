package Game

import org.newdawn.slick.geom.Vector2f
import java.io.Serializable

class Bullets(var x:Float, var y:Float, val direct:Vector2f, val damage:Int, val r:Float = 5F): Serializable{
}