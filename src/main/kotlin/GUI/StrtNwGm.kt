package GUI

import org.newdawn.slick.GameContainer
import org.newdawn.slick.Image

class StartNewGame(gc : GameContainer): Button(gc,
        imageCommon = Image("res/startnewgame.png"),
        imageLighted = Image("res/startnewgame2.png"),
        imageClicked = Image("res/startnewgame3.png"),
        sizeX = gc.width.toFloat() / 3f,
        sizeY = gc.width.toFloat() / 8f,
        xButton = gc.width.toFloat() / 3f,
        yButton = gc.height.toFloat() / 12f){
        
}