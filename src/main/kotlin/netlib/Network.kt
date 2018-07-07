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

class Network(private val ip: String,
              gameName: String,
              private val isHost: Boolean,
              private val nick: String,
              gs: Serializable) {
    private val prod = createProducer(ip)
    private var lobby: NetLobby
    private var lobbyTopicName = ""
    private var actionsParser: NetActionsParser
    private val players = ArrayList<NetPlayer>()
    private var gameStarted = false
    private val syncer: NetSync
    private var onliner: NetOnline? = null
    private var playersLock = ReentrantLock()
    private val actionsLock = ReentrantLock()

    init {
        lobby = NetLobby(gameName, isHost, nick, ip, this, players, playersLock)
        lobby.setDaemon(true)
        lobby.start()
        lobbyTopicName = createLobbyTopicName(gameName)
        actionsParser = NetActionsParser(ip, players, nick, actionsLock)
        actionsParser.setDaemon(true)
        syncer = NetSync(gs, isHost, ip, nick, gameName)
        syncer.setDaemon(true)
    }

    var gameState: Serializable
        get() {
            //
            return syncer.gameState
            //
        }
        set(gs: Serializable) {
            //lock
            syncer.gameState = gs
            //unlock
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
            print("")
        }
        actionsParser.start()
        syncer.start()
        onliner = NetOnline(nick, ip, lobbyTopicName, getPlayersAsHashMap(), syncer)
        onliner!!.setDaemon(true)
        //onliner!!.start()
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
            //players[i].position = i
            toOut[players[i].nick] = players[i].copy()
            toOut[players[i].nick]!!.position = i
        }
        return toOut
    }

    /*companion object {
        const val LOBBY = 0
        const val SYNC = 1
        const val ONLINE = 2
    }*/


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
    consProperties.setProperty("auto.commit", "50")
    consProperties.setProperty("group.id", gid)
    return KafkaConsumer(consProperties)
}

fun createProducer(ip: String): KafkaProducer<String, String> {
    val prodProperties = Properties()
    prodProperties.setProperty("bootstrap.servers", ip)
    prodProperties.setProperty("key.serializer", StringSerializer::class.java.name)
    prodProperties.setProperty("value.serializer", StringSerializer::class.java.name)
    prodProperties.setProperty("retries", "5")
    prodProperties.setProperty("acks", "all")
    return KafkaProducer(prodProperties)
}

fun createLobbyTopicName(gameName: String): String {
    return "-LOBBY-$gameName"
}
/*enum class GamePartitions(val p: Int){
    LOBBY (0),
    SYNC(1),
    ONLINE(2)
}*/
