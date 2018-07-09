package Game

import java.io.Serializable


class GameState(): Serializable{
    var players = HashMap<String, Player>()
    var bullets = ArrayList<Bullets>()
}