package netlib

data class NetPlayer(val nick: String, var isOnline: Boolean, var isHost: Boolean) {
    var onlineTimer: Long = 2000
    var position = 0
}