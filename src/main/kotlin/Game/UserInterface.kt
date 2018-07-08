package Game

import org.newdawn.slick.Color
import org.newdawn.slick.Graphics

class UserInterface(val nick : String) {

    fun drawScore(g : Graphics, gs : GameState, x : Float, y : Float){
        val stringShift = 20f
        var displacement = stringShift
        g.color = Color.lightGray
        g.drawString("NICK : K / D", x, y)
        for (player in gs.players){
            if (player.value.nick == nick)g.color = Color.yellow
            else g.color = Color.white
            g.drawString("${player.value.nick} : ${player.value.kills} / ${player.value.deaths}", x,
                    y + displacement)
            displacement += stringShift
        }
    }

    fun drawUI(g : Graphics, gs : GameState, x : Float, y : Float){
        val tableSHift = 5
        drawScore(g, gs, x + tableSHift, y)
    }

}