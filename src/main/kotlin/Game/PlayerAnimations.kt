package Game

import org.newdawn.slick.Animation
import org.newdawn.slick.Image
import org.newdawn.slick.geom.Vector2f
import kotlin.math.PI
import kotlin.math.atan

data class PlayerAnimations (var ID: Int, var mouseVec: Vector2f){
    var listOfAnimations = ArrayList<Animation>()
    var listOfImagesArrays = ArrayList<Array<Image>>()

    val pistolAnimationImages = Array<Image>(6, { i: Int -> Image("res/animations/pistolAnimation/hero${i+1}.png")})
    val pistolAnimation = Animation(pistolAnimationImages, 30)
    val minigunAnimationImages = Array<Image>(5, { i: Int -> Image("res/animations/minigunAnimation/minigun${i+1}.png")})
    val minigunAnimation = Animation(minigunAnimationImages, 10)

    init {
        listOfImagesArrays.add(pistolAnimationImages)
        listOfImagesArrays.add(minigunAnimationImages)
        listOfAnimations.add(minigunAnimation)
        listOfAnimations.add(pistolAnimation)
    }

    val animationReturn: Animation
        get() {
            for (i in 0..(ArrayImagesReturn[ID].size-1)){
                if (mouseVec.x >= 0) {
                    listOfAnimations[ID].getImage(i).setRotation(toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI) / 2)
                } else {
                    listOfAnimations[ID].getImage(i).setRotation(toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI)* 3 / 2)
                }
            }
            return
    }
    val ArrayImagesReturn: Array<Image>
            get() {
        return listOfImagesArrays[ID]
    }
}