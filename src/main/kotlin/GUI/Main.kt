package GUI

import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.GameContainer
import org.newdawn.slick.SlickException
import java.util.logging.Level
import java.util.logging.Logger

fun main(args: Array<String>) {
    try {
        val appgc: AppGameContainer
        appgc = AppGameContainer(SimpleSlickGame("Simple Slick Game"))
//        appgc.setDisplayMode(1366, 768, true)
//        appgc.setDisplayMode(640, 480, false)
        appgc.setDisplayMode(1280,720,false)
        appgc.setVSync(true)
        appgc.setShowFPS(false)
        appgc.start()
    } catch (ex: SlickException) {
        Logger.getLogger(SimpleSlickGame::class.java.name).log(Level.SEVERE, null, ex)
    }
}