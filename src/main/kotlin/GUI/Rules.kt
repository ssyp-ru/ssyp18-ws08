package GUI

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image
import org.newdawn.slick.Input

class Rules(gc: GameContainer) : Button(gc ,
        imageCommon = Image("res/rules1.png"),
        imageLighted = Image("res/rules2.png"),
        imageClicked = Image("res/rules3.png"),
        sizeX = gc.screenWidth.toFloat() / 3f,
        sizeY = gc.screenWidth.toFloat() / 8f ,
        xButton = gc.screenWidth.toFloat() / 3f ,
        yButton = gc.screenHeight.toFloat() / 3f) {
}