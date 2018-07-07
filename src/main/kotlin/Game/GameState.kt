package Game

import java.io.Serializable

/*class GameState(var gamer: Player, var arrAllPlayers: ArrayList<Player>, var arrayEnemy: ArrayList<Player>):
        Serializable
*/

class GameState(): Serializable{
    var players = HashMap<String, Player>()
    var bullets = ArrayList<Bullets>()
}