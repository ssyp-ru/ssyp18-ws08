package game

import java.io.Serializable


class GameState(): Serializable{
    var players = HashMap<String, Player>()
    var bullets = ArrayList<Bullets>()
    var weaponSpawn = ArrayList<WeaponMap>()
}