package game

import org.newdawn.slick.Image
import org.newdawn.slick.geom.Vector2f
import kotlin.math.PI
import kotlin.math.atan




object PlayerAnimations{
    private var listOfRangedAnimations = ArrayList<Animation>()
    private var listOfMeleeAnimations = ArrayList<Animation>()

    private var listOfRangedImagesArrays = ArrayList<Array<Image>>()
    private var listOfMeleeImagesArrays = ArrayList<Array<Image>>()

    private val pistolAnimationImages = Array(6,
            { i: Int -> Image("res/animations/pistolAnimation/hero${i+1}.png")})
    private val pistolAnimation = Animation(pistolAnimationImages, 840)

    private val miniGunAnimationImages = Array(5,
            { i: Int -> Image("res/animations/miniGunAnimation/minigun${i+1}.png")})
    private val miniGunAnimation = Animation(miniGunAnimationImages, 500)

    private val sniperAnimationImages = Array(6,
            { i: Int -> Image("res/animations/sniperAnimation/AWP${i+1}.png")})
    private val sniperAnimation = Animation(sniperAnimationImages, 170)

    private val knifeAnimationImages = Array(6,
            { i: Int -> Image("res/animations/knifeAttackAnimation/knife${i+1}.png")})
    private val knifeAnimation = Animation(knifeAnimationImages, 500)

    private val rapierAnimationImages = Array(3,
            { i: Int -> Image("res/animations/rapierAttackAnimation/rapier${i+1}.png")})
    private val rapierAnimation = Animation(rapierAnimationImages, 100)

    private val deathPulseImages = Array(1, {Image(1,1)})
    private val deathPulseAnimation = Animation(deathPulseImages, 1)

    init {
        listOfRangedImagesArrays.add(pistolAnimationImages)
        listOfRangedImagesArrays.add(miniGunAnimationImages)
        listOfRangedImagesArrays.add(sniperAnimationImages)
        listOfRangedImagesArrays.add(knifeAnimationImages)

        listOfMeleeImagesArrays.add(knifeAnimationImages)
        listOfMeleeImagesArrays.add(rapierAnimationImages)
        listOfMeleeImagesArrays.add(deathPulseImages)

        listOfRangedAnimations.add(pistolAnimation)
        listOfRangedAnimations.add(miniGunAnimation)
        listOfRangedAnimations.add(sniperAnimation)
        listOfRangedAnimations.add(knifeAnimation)

        listOfMeleeAnimations.add(knifeAnimation)
        listOfMeleeAnimations.add(rapierAnimation)
        listOfMeleeAnimations.add(deathPulseAnimation)
    }

    fun getAnimation(animationType: String, ID: Int, mouseVec: Vector2f): Animation {
        return if(animationType == "ranged") getRangedAnimation(ID, mouseVec) else getMeleeAnimation(ID, mouseVec)
    }
    private fun getRangedArrayImages(ID: Int): Array<Image> {
            return listOfRangedImagesArrays[ID]
        }
    private fun getRangedAnimation(ID: Int, mouseVec: Vector2f): Animation {
            for (i in 0 until getRangedArrayImages(ID).size){
                if (mouseVec.x >= 0) {
                    listOfRangedAnimations[ID].getImage(i).rotation = (toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI) / 2)
                } else {
                    listOfRangedAnimations[ID].getImage(i).rotation = (toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI)* 3 / 2)
                }
            }
            return listOfRangedAnimations[ID]
    }

    private fun getMeleeArrayImages(ID: Int): Array<Image>{
        return listOfMeleeImagesArrays[ID]
    }
    private fun getMeleeAnimation(ID: Int, mouseVec: Vector2f): Animation {
            for (i in 0 until getMeleeArrayImages(ID).size){
                if (mouseVec.x >= 0) {
                    listOfMeleeAnimations[ID].getImage(i).rotation = (toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI) / 2)
                } else {
                    listOfMeleeAnimations[ID].getImage(i).rotation = (toDegree(PI) / 3 *
                            atan(mouseVec.y / mouseVec.x) - toDegree(PI)* 3 / 2)
                }
            }
            return listOfMeleeAnimations[ID]
        }
}