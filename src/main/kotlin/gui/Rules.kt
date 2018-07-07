package gui

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class Rules(gc: GameContainer) : Button(
        imageCommon = Image("res/rules1.png"),
        imageLighted = Image("res/rules2.png"),
        imageClicked = Image("res/rules3.png"),
        sizeX = gc.width.toFloat() / 3f,
        sizeY = gc.width.toFloat() / 8f ,
        xButton = gc.width.toFloat() / 3f ,
        yButton = gc.height.toFloat() / 3f) {

}