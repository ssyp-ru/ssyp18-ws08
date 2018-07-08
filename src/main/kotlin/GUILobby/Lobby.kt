package GUILobby

import GUI.State
import netlib.Network
import org.newdawn.slick.*
import java.util.*
import java.util.Arrays.asList

class Lobby(val gc: GameContainer, val IsHost: Boolean, val networckobj: Network, val lobbyName: String) {
    val startButton = Start(gc)
    val jpgNames = asList( "pepe","cat", "sun","cheb","gen","zaic")
    val listOfPlayersIamge = Image("res/lobby.png")
    val rand = Random(Date().time)
    val placeHolderIamge = Image("res/lobbymems/" + jpgNames[rand.nextInt(jpgNames.size)] + ".jpg")
    val xOfJPEG = gc.screenWidth / 40f
    val yOfJPEG = gc.screenHeight / 40f
    val lengthOfJPEG = gc.screenWidth / 5f
    val heightOfJPEG = gc.screenHeight * 0.9f
    val placeHolderX = xOfJPEG + lengthOfJPEG + 1
    val placeHoldery = yOfJPEG
    val placeHolderLength = gc.screenWidth - lengthOfJPEG - xOfJPEG - 1 - (gc.screenWidth.toFloat() / 3f)
    val fontLobbyName = TrueTypeFont(java.awt.Font("Comic Sans MS", java.awt.Font.BOLD, gc.screenHeight / 40
    ), false)
    val fontPlayerName = TrueTypeFont(java.awt.Font("Comic Sans MS", java.awt.Font.BOLD,
            gc.screenHeight / 50), false)

    fun lobbyRender(g: Graphics) {
        if (IsHost) {
            startButton.draw(gc, gc.input.mouseX.toFloat(), gc.input.mouseY.toFloat())
        }
        listOfPlayersIamge.draw(xOfJPEG, yOfJPEG, lengthOfJPEG, heightOfJPEG)
        placeHolderIamge.draw(placeHolderX, placeHoldery, placeHolderLength, heightOfJPEG)
        var playerNameShiftY = yOfJPEG + gc.screenHeight / 9f
        val playerNameShiftX = xOfJPEG + gc.screenWidth / 40f
        val players = networckobj.getPlayers()
        g.color = Color.black
        g.font = fontLobbyName
        g.drawString("Lobby: " + lobbyName, xOfJPEG + lengthOfJPEG / 5, yOfJPEG + heightOfJPEG / 30)
        g.font = fontPlayerName
        for (i in 0..(players.size - 1)) {
            g.drawString(if (i == 0) "Host: " + players[i].nick else "player: " + players[i].nick, playerNameShiftX,
                    playerNameShiftY)
            playerNameShiftY += gc.screenHeight / 20f
        }
    }

    fun lobbyUpdate() {
        if (startButton.state == State.USED) {
            networckobj.startGame()
        }
    }
}