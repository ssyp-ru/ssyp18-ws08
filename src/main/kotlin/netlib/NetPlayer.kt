package netlib

import java.io.Serializable

data class NetPlayer(val nick: String, var isOnline: Boolean, var isHost: Boolean): Serializable {
    var position = 0
}