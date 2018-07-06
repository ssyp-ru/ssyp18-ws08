package netlib

import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import java.util.*
import java.util.Arrays.asList

class NetOnline(private val nick: String,
                ip: String,
                private val gameTopic: String,
                private val players: HashMap<String, NetPlayer>,
                private val sync: NetSync) :
        Thread("Onliner") {

    private val cons = Network.createConsumer(ip, "$nick-ONLINE")
    private val prod = Network.createProducer(ip)
    private var host = 0

    private var pi: Array<String> = arrayOf()

    init {
        cons.assign(asList(TopicPartition(gameTopic, Network.ONLINE)))
        cons.seekToEnd(asList(TopicPartition(gameTopic, Network.ONLINE)))
    }
    override fun run() {
        var records: ConsumerRecords<String, String>
        var prevDate = Date()
        var millis: Long
        pi = Array(players.size, { _ -> "" })
        for (p in players) {
            pi[p.value.position] = p.value.nick
            println(p.value.position)
        }
        for (i in 0..(pi.size - 1)) println(players[pi[i]])

        while (true) {
            records = cons.poll(30)
            for (r in records) {
                when (r.key()) {
                    "alive" -> {
                        //println("${r.value()} is alive! ${players[r.value()]}")
                        players[r.value()]!!.onlineTimer = 1000
                    }
                    "newHost" -> {
                        sync.setHost(r.value() == nick)
                        host = pi.indexOf(r.value())
                    }
                }
            }
            millis = Date().time - prevDate.time
            prevDate = Date()

            for (p in players) {
                p.value.onlineTimer -= millis
                p.value.isOnline = (p.value.onlineTimer > 0)
            }

            if (!players[pi[host]]!!.isOnline) {
                newHost()
            }
            if(players[nick]!!.onlineTimer < 850) {
                prod.send(ProducerRecord(gameTopic, Network.ONLINE, "alive", nick))
            }
        }
    }

    private fun newHost() {
        for (i in 0..(players.size - 1)) {
            if (!players[pi[i]]!!.isOnline) continue
            host = i
            break
        }
        prod.send(ProducerRecord(gameTopic, Network.ONLINE, "newHost", players[pi[host]]!!.nick))
        if (nick == players[pi[host]]!!.nick) sync.setHost(true)
    }
}