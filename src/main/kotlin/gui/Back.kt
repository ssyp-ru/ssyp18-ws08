package gui

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class Back(gc: GameContainer) : Button(
        imageCommon = Image("res/back1.png"),
        imageLighted = Image("res/back2.png"),
        imageClicked = Image("res/back3.png"),
        sizeX = gc.width.toFloat() / 6f,
        sizeY = gc.width.toFloat() / 16f,
        xButton = 30f, yButton = 30f) {
}