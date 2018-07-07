package gui

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class Regulations(gc: GameContainer) : Button(
        imageCommon = Image("res/menu.png"),
        imageLighted = Image("res/menu.png"),
        imageClicked = Image("res/menu.png"),
        sizeX = gc.width.toFloat() / 2.1f,
        sizeY = gc.height.toFloat() - 200f,
        xButton = gc.width.toFloat() / 3f,
        yButton = 100f) {
}
