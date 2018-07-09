package Game

import org.newdawn.slick.Image
import org.newdawn.slick.geom.Vector2f
import kotlin.math.PI
import kotlin.math.atan




object PlayerAnimations{
    var arrayAni = ArrayList<ArrayList<Animation>>()

    var listOfRangedAnimations = ArrayList<Animation>()
    var listOfMeeleeAnimations = ArrayList<Animation>()

    var listOfRangedImagesArrays = ArrayList<Array<Image>>()
    var listOfMeeleeImagesArrays = ArrayList<Array<Image>>()

    val pistolAnimationImages = Array<Image>(6,
            { i: Int -> Image("res/animations/pistolAnimation/hero${i+1}.png")})
    val pistolAnimation = Animation(pistolAnimationImages, 840)

    val minigunAnimationImages = Array<Image>(5,
            { i: Int -> Image("res/animations/minigunAnimation/minigun${i+1}.png")})
    val minigunAnimation = Animation(minigunAnimationImages, 500)

    val sniperAnimationImages = Array<Image>(6,
            { i: Int -> Image("res/animations/sniperAnimation/AWP${i+1}.png")})
    val sniperAnimation = Animation(sniperAnimationImages, 170)

    val knifeAnimationImages = Array<Image>(6,
            { i: Int -> Image("res/animations/knifeAttackAnimation/knife${i+1}.png")})
    val knifeAnimation = Animation(knifeAnimationImages, 500)

    val rapierAnimationImages = Array<Image>(3,
            { i: Int -> Image("res/animations/rapierAttackAnimation/rapier${i+1}.png")})
    val rapierAnimation = Animation(rapierAnimationImages, 100)

    val deathPulseImages = Array<Image>(1, {Image(1,1)})
    val deathPulseAnimation = Animation(deathPulseImages, 1)

    init {
        listOfRangedImagesArrays.add(pistolAnimationImages)
        listOfRangedImagesArrays.add(minigunAnimationImages)
        listOfRangedImagesArrays.add(sniperAnimationImages)
        listOfRangedImagesArrays.add(knifeAnimationImages)

        listOfMeeleeImagesArrays.add(knifeAnimationImages)
        listOfMeeleeImagesArrays.add(rapierAnimationImages)
        listOfMeeleeImagesArrays.add(deathPulseImages)

        listOfRangedAnimations.add(pistolAnimation)
        listOfRangedAnimations.add(minigunAnimation)
        listOfRangedAnimations.add(sniperAnimation)
        listOfRangedAnimations.add(knifeAnimation)

        listOfMeeleeAnimations.add(knifeAnimation)
        listOfMeeleeAnimations.add(rapierAnimation)
        listOfMeeleeAnimations.add(deathPulseAnimation)
    }

    fun getAnimation(animationType: String, ID: Int, mouseVec: Vector2f): Animation {
        return if(animationType == "ranged") getRangedAnimation(ID, mouseVec) else getMeeleeAnimation(ID, mouseVec)
    }
    fun getRangedArrayImages(ID: Int): Array<Image> {
            return listOfRangedImagesArrays[ID]
        }
    fun getRangedAnimation(ID: Int, mouseVec: Vector2f): Animation {
            for (i in 0..(getRangedArrayImages(ID).size-1)){
                if (mouseVec.x >= 0) {
                    listOfRangedAnimations[ID].getImage(i).setRotation(toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI) / 2)
                } else {
                    listOfRangedAnimations[ID].getImage(i).setRotation(toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI)* 3 / 2)
                }
            }
            return listOfRangedAnimations[ID]
    }

    fun getMeeleeArrayImages(ID: Int): Array<Image>{
        return listOfMeeleeImagesArrays[ID]
    }
    fun getMeeleeAnimation(ID: Int, mouseVec: Vector2f): Animation {
            //val listToReturn: ArrayList<Animation>
            for (i in 0..(getMeeleeArrayImages(ID).size-1)){
                if (mouseVec.x >= 0) {
                    listOfMeeleeAnimations[ID].getImage(i).setRotation(toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI) / 2)
                } else {
                    listOfMeeleeAnimations[ID].getImage(i).setRotation(toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI)* 3 / 2)
                }
            }
            return listOfMeeleeAnimations[ID]
        }
}