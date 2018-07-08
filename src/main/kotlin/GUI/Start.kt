package GUI

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image
import org.newdawn.slick.Input

class Start(gc: GameContainer) : Button(gc,
        imageCommon = Image("res/start1.png"),
        imageLighted = Image("res/start2.png"),
        imageClicked = Image("res/start3.png"),
        sizeX = gc.screenWidth.toFloat() / 3f,
        sizeY = gc.screenWidth.toFloat() / 8f,
        xButton = gc.screenWidth.toFloat() / 3f,
        yButton = gc.screenHeight.toFloat() / 12f) {
}