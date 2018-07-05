package ru.enginner

data class NetPlayer(val nick: String, var isOnline: Boolean, var isHost: Boolean) {
    var onlineTimer: Long = 1000
    var position = 0
}