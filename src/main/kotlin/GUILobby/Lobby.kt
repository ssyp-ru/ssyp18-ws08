package GUILobby

import gui.State
import netlib.Network
import org.newdawn.slick.*
import java.util.*
import java.util.Arrays.asList

class Lobby(val gc: GameContainer, val IsHost: Boolean, val networckobj: Network, val lobbyName: String
) {
    var exited: Boolean = false
    val startButton = Start(gc)
    val backButton = Back(gc)
    val jpgNames = asList("pepe", "cat", "sun", "cheb", "gen", "zaic")
    val listOfPlayersInGame = Image("res/lobby.png")
    val changeMapButt =ChangeMap(gc)
    val rand = Random(Date().time)
    val placeHolderIamge = Image("res/lobbymems/" + jpgNames[rand.nextInt(jpgNames.size)] + ".jpg")
    val xOfJPEG = gc.width / 40f
    val yOfJPEG = gc.height / 40f
    val lengthOfJPEG = gc.width / 5f
    val heightOfJPEG = gc.height * 0.9f
    val placeHolderX = xOfJPEG + lengthOfJPEG + 1
    val placeHolderY = yOfJPEG
    val placeHolderLength = gc.width - lengthOfJPEG - xOfJPEG - 1 - (gc.width.toFloat() / 3f)
    val fontLobbyName = TrueTypeFont(java.awt.Font("Comic Sans MS", java.awt.Font.BOLD, gc.height / 40
    ), false)
    val fontPlayerName = TrueTypeFont(java.awt.Font("Comic Sans MS", java.awt.Font.BOLD,
            gc.height / 50), false)
    val miniMapSize = Math.min(gc.height - gc.width.toFloat() * 3 / 8f - 1,
            gc.width - placeHolderX - placeHolderLength - 1)
    var maps = asList("FowlMap1.TMX", "FowlMap2.tmx")
    val miniMaps = Array<Image>(maps.size,{i:Int->Image("res/minimapfowlmap${i+1}.bmp")})
    var mapName = maps[0]
    var mapIndex = 0
    init{
        if(IsHost)networckobj.setMap(mapName)
    }
    fun render(g: Graphics) {
        if (IsHost) {
            startButton.draw(gc, gc.input.mouseX.toFloat(), gc.input.mouseY.toFloat())
            changeMapButt.draw(gc, gc.input.mouseX.toFloat(), gc.input.mouseY.toFloat())
        }
        backButton.draw(gc, gc.input.mouseX.toFloat(), gc.input.mouseY.toFloat())
        listOfPlayersInGame.draw(xOfJPEG, yOfJPEG, lengthOfJPEG, heightOfJPEG)
        placeHolderIamge.draw(placeHolderX, placeHolderY, placeHolderLength, heightOfJPEG)
        miniMaps[mapIndex].draw(gc.width.toFloat() - miniMapSize, 0f, miniMapSize, miniMapSize)
        var playerNameShiftY = yOfJPEG + gc.height / 9f
        val playerNameShiftX = xOfJPEG + gc.width / 40f
        val players = networckobj.getPlayers()
        g.color = Color.black
        g.font = fontLobbyName
        g.drawString("Lobby: " + lobbyName, xOfJPEG + lengthOfJPEG / 5, yOfJPEG + heightOfJPEG / 30)
        g.font = fontPlayerName
        for (i in 0..(players.size - 1)) {
            g.drawString(if (i == 0) "Host: " + players[i].nick else "player: " + players[i].nick, playerNameShiftX,
                    playerNameShiftY)
            playerNameShiftY += gc.height / 20f
        }
        g.color = Color.gray
        g.drawString("Before launching game, we recommend you to get some paper bags", 1f, gc.height - 30f)

    }

    fun update() {
        if(!IsHost){
            mapName=networckobj.getMap()

            mapIndex=maps.indexOf(mapName)
        }
        if (startButton.state == State.USED&&IsHost) {
            networckobj.startGame()
        }
        if (backButton.state == State.USED) {
            networckobj.leaveLobby()
            exited = true
        }
        if (changeMapButt.state == State.USED&&IsHost) {
            mapIndex= if(mapIndex==0)1 else (mapIndex+1)%mapIndex
            mapName=maps[mapIndex]
            networckobj.setMap(mapName)
        }
        if (networckobj.hostExited) {
            exited = true
        }

    }
}