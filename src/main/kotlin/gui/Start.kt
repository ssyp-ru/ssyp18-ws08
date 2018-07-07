package gui

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class Start(gc: GameContainer) : Button(
        imageCommon = Image("res/start1.png"),
        imageLighted = Image("res/start2.png"),
        imageClicked = Image("res/start3.png"),
        sizeX = gc.width.toFloat() / 3f,
        sizeY = gc.width.toFloat() / 8f,
        xButton = gc.width.toFloat() / 3f,
        yButton = gc.height.toFloat() / 12f) {

}