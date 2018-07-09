package Game

enum class layer{
    ROADS, CRATES, GRASS, WATER, HOUSES
}

class Cell(val x : Int, val y : Int, val type : layer)