package GUILobby

import GUI.Button
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class Start(gc: GameContainer) : Button(gc,
        imageCommon = Image("res/start1.png"),
        imageLighted = Image("res/start2.png"),
        imageClicked = Image("res/start3.png"),
        sizeX = gc.screenWidth.toFloat() / 3f,
        sizeY = gc.screenWidth.toFloat() / 8f,
        xButton = gc.screenWidth.toFloat() - gc.screenWidth.toFloat() / 3f,
        yButton = gc.screenHeight.toFloat() - gc.screenWidth.toFloat() / 4f) {
}