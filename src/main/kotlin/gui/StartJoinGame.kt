package gui

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class StartJoinGame(gc: GameContainer) : Button(
        imageCommon = Image("res/startjoingame1.png"),
        imageLighted = Image("res/startjoingame.png"),
        imageClicked = Image("res/startjoingame3.png"),
        sizeX = gc.width.toFloat() / 3f,
        sizeY = gc.width.toFloat() / 8f,
        xButton = gc.width.toFloat() / 3f,
        yButton = gc.height.toFloat() / 3f) {
}