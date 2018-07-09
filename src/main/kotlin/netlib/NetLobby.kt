package netlib

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import java.util.Arrays.asList
import java.util.concurrent.locks.ReentrantLock

class NetLobby(private val gameName: String,
               private val isHost: Boolean,
               private val nick: String,
               private val ip: String,
               private val net: Network,
               private val players: ArrayList<NetPlayer>,
               private val playersLock: ReentrantLock) :
        Thread("Lobby") {
    private val cons = createConsumer(ip, nick)
    private val prod = createProducer(ip)
    private val adm = NetAdmin(ip)
    private val topicName = "-LOBBY-$gameName"
    private var isGameReady = false
    private var exit = false
    var hostExited = false
    var map = ""
        get() = field
        set(m){
            if(isHost)
                //mapLock.lock()
                field = m
                //mapLock.unlock()
        }
    //private var mapLock = ReentrantLock()

    override fun run() {
        cons.assign(asList(TopicPartition(topicName, PartitionID.LOBBY.ordinal)))
        cons.seekToEnd(asList(TopicPartition(topicName, PartitionID.LOBBY.ordinal)))
        if (isHost) {
            if (!adm.lisTopics().contains(topicName)) adm.createTopic(topicName, 3)
            prod.send(ProducerRecord(topicName, PartitionID.LOBBY.ordinal, "newGame", gameName)).get()
        }
        adm.createTopic("-PLAYER-$nick", 1)
        prod.send(ProducerRecord(topicName, PartitionID.LOBBY.ordinal, "player", nick)).get()
        waitPlayers()
        if(exit or hostExited)return
        net.setGameStarted()
        if (!isHost) net.startGame()
    }

    private fun waitPlayers() {
//        val syncPart = TopicPartition(topicName, PartitionID.SYNC.ordinal)
//        val syncCons = createConsumer(ip, nick)
//        syncCons.assign(asList(syncPart))
//        var off = syncCons.endOffsets(asList(syncPart))[syncPart]!! - 1
//        syncCons.seek(syncPart, off)
//        var gameFree = true
//        try {
//            gameFree = (System.currentTimeMillis() - syncCons.poll(20).last().timestamp()) > 10000
//        }catch (e: Exception){}
//
//        if(!gameFree){
//            hostExited = true
//            exit = true
//            return
//        }
        val part = TopicPartition(topicName, 0)
        cons.seekToEnd(asList(part))
        var records = cons.poll(10)
        var offset: Long = cons.endOffsets(asList(part))[part]!! - 1
        while (if (records.iterator().hasNext()) (records.iterator().next().key() != "newGame") else true) {
            cons.seek(part, offset)
            records = cons.poll(10)
            if (records.isEmpty) continue
            //println("$offset")
            offset--
        }
        cons.seek(part, ++offset)
        while (!isGameReady or exit) {
            records = cons.poll(40)
            for (r in records) {
                when(r.key()) {
                    "player" -> {
                        if(!players.contains(NetPlayer(r.value(), true, false))) {
                            playersLock.lock()
                            players.add(NetPlayer(r.value(), true, false))
                            playersLock.unlock()
                        }
                    }
                    "leave" -> {
                        if(r.value() == players[0].nick){
                            hostExited = true
                            return
                        }
                        players.remove(NetPlayer(r.value(), true, false))
                    }
                    "map" -> map = r.value()
                    "state" -> if (r.value() == "ready")isGameReady = true

                }
            }
        }
        if (isHost) players[0].isHost = true
    }

    fun leave(){
        exit = true
    }
}
