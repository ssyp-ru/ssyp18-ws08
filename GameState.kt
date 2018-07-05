package ssyp18

import java.io.Serializable
import java.nio.file.Files.size

class GameState(private var objects : Array<WebInterface>) : Serializable {
//    var getState : Array<String> = Array(objects.size, {i : Int -> objects[i].getState()})

    fun setState(state : GameState){
        for (i in 0..this.objects.size){
            this.objects[i] = state.objects[i]
        }
    }
}