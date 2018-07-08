package GUI

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class StartNewGame(gc : GameContainer): Button(gc,
        imageCommon = Image("res/startnewgame.png"),
        imageLighted = Image("res/startnewgame2.png"),
        imageClicked = Image("res/startnewgame3.png"),
        sizeX = gc.screenWidth.toFloat() / 3f,
        sizeY = gc.screenWidth.toFloat() / 8f,
        xButton = gc.screenWidth.toFloat() / 3f,
        yButton = gc.screenHeight.toFloat() / 12f){
}