package game

enum class LayerType {
    ROADS, CRATES, GRASS, WATER, HOUSES
}

class Cell(val x: Int, val y: Int, val type: LayerType)