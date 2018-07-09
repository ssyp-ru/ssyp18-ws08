package GUILobby

import GUI.Button
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class Back(gc: GameContainer) : Button(gc,
        imageCommon = Image("res/back1.png"),
        imageLighted = Image("res/back2.png"),
        imageClicked = Image("res/back3.png"),
        sizeX = gc.width.toFloat() / 3f,
        sizeY = gc.width.toFloat() / 8f,
        xButton = gc.width.toFloat() - gc.width.toFloat() / 3f,
        yButton = gc.height.toFloat() - gc.width.toFloat() / 8f) {
}