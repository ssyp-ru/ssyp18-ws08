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

    private val cons = createConsumer(ip, "$nick-ONLINE")
    private val prod = createProducer(ip)
    private var host = 0

    private var pi: Array<String> = arrayOf()

    init {
        cons.assign(asList(TopicPartition(gameTopic, PartitionID.ONLINE.ordinal)))
        cons.seekToEnd(asList(TopicPartition(gameTopic, PartitionID.ONLINE.ordinal)))
    }
    override fun run() {
        var records: ConsumerRecords<String, String>
        var prevDate: Long = System.currentTimeMillis()
        var millis: Long
        pi = Array(players.size, { _ -> "" })
        for (p in players) {
            pi[p.value.position] = p.value.nick
            println(p.value.position)
        }
        //for (i in 0..(pi.size - 1)) println(players[pi[i]])

        while (true) {
            records = cons.poll(30)
            for (r in records) {
                when (r.key()) {
                    "alive" -> {
                        //println("${r.value()} is alive! ${players[r.value()]}")
                        players[r.value()]!!.onlineTimer = 2000
                    }
                    "newHost" -> {
                        sync.setHost(r.value() == nick)
                        host = pi.indexOf(r.value())
                    }
                }
            }
            millis = System.currentTimeMillis() - prevDate
            prevDate = System.currentTimeMillis()

            for (p in players) {
                p.value.onlineTimer -= millis
                p.value.isOnline = (p.value.onlineTimer > 0)
            }

            if (!players[pi[host]]!!.isOnline) {
                newHost()
            }
            if(players[nick]!!.onlineTimer < 1500) {
                prod.send(ProducerRecord(gameTopic, PartitionID.ONLINE.ordinal, "alive", nick))
            }
        }
    }

    private fun newHost() {
        for (i in 0..(players.size - 1)) {
            if (!players[pi[i]]!!.isOnline) continue
            host = i
            break
        }
        prod.send(ProducerRecord(gameTopic, PartitionID.ONLINE.ordinal, "newHost", players[pi[host]]!!.nick))
        if (nick == players[pi[host]]!!.nick) sync.setHost(true)
    }
}