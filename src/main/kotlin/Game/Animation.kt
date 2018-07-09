package Game

import org.newdawn.slick.Image
import org.newdawn.slick.geom.Vector2f
import kotlin.math.PI
import kotlin.math.atan

class Animation(var frames: Array<Image>, var duration: Int, var index : Int = 0) {
    var stopAt = frames.size
    var lastChange = System.currentTimeMillis()
    var isStopped = false

    val size: Int
        get() {
            return stopAt
        }

    fun draw(x: Float, y: Float, mouseVec: Vector2f){
        if (mouseVec.x >= 0) {
            frames[index].setRotation(toDegree(PI) / 3 * atan(mouseVec.y / mouseVec.x) - toDegree(PI) / 2)
            frames[index].setRotation(toDegree(PI) / 3 * atan(mouseVec.y / mouseVec.x) - toDegree(PI) / 2)
        } else {
            frames[index].setRotation(toDegree(PI) / 3 * atan(mouseVec.y / mouseVec.x) - toDegree(PI)* 3 / 2)
            frames[index].setRotation(toDegree(PI) / 3 * atan(mouseVec.y / mouseVec.x) - toDegree(PI)* 3 / 2)
        }
        frames[index].draw(x,y)
    }

    fun getImage(ind:Int): Image{
        return frames[ind]
    }
    fun update(){
        if(!isStopped) {
            if (System.currentTimeMillis() - lastChange < duration) {
                index+=1
                lastChange = System.currentTimeMillis()
            }
            if (index == stopAt) {
                index = 0
                isStopped = true
            }
        }
    }
    fun start(){
        index = 0
        isStopped = false
        lastChange = System.currentTimeMillis()
    }
}