package game

import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.tiled.TiledMap

class Map(val gs: GameState, var mapName: String = "FowlMap1.TMX"): TiledMap("res/map/$mapName") {

    private var mapHeight: Int = 0
    private var mapWidth: Int = 0
    var cells = Array(100) { Array(100, { i -> Cell(0, 0, LayerType.GRASS) }) }
    var teleport = ArrayList<Vector2f>()

    init {
        mapHeight = height * tileHeight
        mapWidth = width * tileWidth
        for (i in 0 until cells.size) {
            for (j in 0 until cells[i].size) {
                cells[i][j] = Cell(i * tileWidth, j * tileHeight, LayerType.GRASS)
                when {
                    (getTileId(i, j, 0) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            LayerType.ROADS)
                    (getTileId(i, j, 1) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            LayerType.CRATES)
                    (getTileId(i, j, 3) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            LayerType.WATER)
                    (getTileId(i, j, 4) != 0) -> cells[i][j] = Cell(i * tileWidth, j * tileHeight,
                            LayerType.HOUSES)
                }
                if (getTileId(i, j, 5) != 0) {
                    teleport.add(Vector2f(i * tileWidth.toFloat(), j * tileHeight.toFloat()))
                }
                if (getTileId(i, j, 6) != 0) {
                    gs.weaponSpawn.add(WeaponMap(Vector2f(i * tileWidth.toFloat(), j * tileHeight.toFloat()),
                            3500f))
                }
            }
        }
    }
}