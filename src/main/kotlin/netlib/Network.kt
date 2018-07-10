package netlib

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.io.Serializable
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Network(ip: String,
              private val gameName: String,
              private val isHost: Boolean,
              private val nick: String,
              gs: Serializable,
              defaultMap: String) {
    private val prod = createProducer(ip)
    private var lobby: NetLobby
    private var lobbyTopicName = ""
    private var actionsParser: NetActionsParser
    private val players = ArrayList<NetPlayer>()
    private var gameStarted = false
    private val syncer: NetSync
    private var playersLock = ReentrantLock()
    private val actionsLock = ReentrantLock()
    var hostExited
        get() = lobby.hostExited
        set(b){}

    init {
        lobby = NetLobby(gameName, isHost, nick, ip, this, players, playersLock)
        lobby.isDaemon = true
        lobby.map = defaultMap
        lobby.start()
        lobbyTopicName = createLobbyTopicName(gameName)
        actionsParser = NetActionsParser(ip, players, nick, actionsLock)
        actionsParser.setDaemon(true)
        syncer = NetSync(gs, isHost, ip, nick, gameName)
        syncer.setDaemon(true)
    }

    var gameState: Serializable
        get() {
            return syncer.gameState
        }
        set(gs) {
            syncer.gameState = gs
        }

    fun getActions(): ArrayList<NetAction> {
        return actionsParser.actions()
    }

    fun doAction(name: String, params: List<String>) {
        var toSend = ""
        for (p in params) {
            toSend += "$p|"
        }
        prod.send(ProducerRecord("-PLAYER-$nick", 0, name, toSend))
    }

    @Synchronized
    fun startGame() {
        if (isHost) {
            prod.send(ProducerRecord(lobbyTopicName, PartitionID.LOBBY.ordinal, "state", "ready"))
        }
        while (!gameStarted) {
            print("")//Magic!! Do NOT Remove!!!!!!!!!!!!!!
        }
        syncer.setPlayers(players)
        actionsParser.start()
        syncer.start()
    }

    fun setGameStarted() {
        gameStarted = true
    }

    fun getGameStarted(): Boolean {
        return gameStarted
    }

    fun getPlayers(): ArrayList<NetPlayer> {
        val toOut = ArrayList<NetPlayer>()
        playersLock.lock()
        for (p in players) toOut.add(p.copy())
        playersLock.unlock()
        return toOut
    }

    fun getPlayersAsHashMap(): HashMap<String, NetPlayer> {
        val toOut = HashMap<String, NetPlayer>()
        for (i in 0..(players.size - 1)) {
            toOut[players[i].nick] = players[i].copy()
            toOut[players[i].nick]!!.position = i
        }
        return toOut
    }

    fun leaveLobby(){
        if(!gameStarted){
            lobby.leave()
            prod.send(ProducerRecord(createLobbyTopicName(gameName),
                    PartitionID.LOBBY.ordinal, "leave", nick)).get()
        }
    }
    fun getMap(): String{
        return lobby.map
    }
    fun setMap(m: String){
        if(isHost) {
            lobby.map = m
            prod.send(ProducerRecord(createLobbyTopicName(gameName),
                    PartitionID.LOBBY.ordinal, "map", m)).get()
        }
    }

    fun stopThreads(){
        syncer.stop()
        actionsParser.stop()
        lobby.stop()
    }
}

enum class PartitionID{
    LOBBY, SYNC, ONLINE
}

fun createConsumer(ip: String, gid: String): KafkaConsumer<String, String> {
    val consProperties = Properties()
    consProperties.setProperty("bootstrap.servers", ip)
    consProperties.setProperty("key.deserializer", StringDeserializer::class.java.name)
    consProperties.setProperty("value.deserializer", StringDeserializer::class.java.name)
    consProperties.setProperty("enable.auto.commit", "true")
    consProperties.setProperty("auto.commit", "5000")
    consProperties.setProperty("group.id", gid)
    return KafkaConsumer(consProperties)
}

fun createProducer(ip: String): KafkaProducer<String, String> {
    val prodProperties = Properties()
    prodProperties.setProperty("bootstrap.servers", ip)
    prodProperties.setProperty("key.serializer", StringSerializer::class.java.name)
    prodProperties.setProperty("value.serializer", StringSerializer::class.java.name)
    prodProperties.setProperty("retries", "3")
    prodProperties.setProperty("acks", "1")
    prodProperties.setProperty("batch.size", "0")
    return KafkaProducer(prodProperties)
}

fun createLobbyTopicName(gameName: String): String {
    return "-LOBBY-$gameName"
}