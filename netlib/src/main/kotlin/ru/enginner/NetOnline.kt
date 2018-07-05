package ru.enginner

import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import java.util.*
import java.util.Arrays.asList

class NetOnline(private val nick: String, ip: String, private val gameTopic: String,
                private val players: HashMap<String, NetPlayer>, private val sync: NetSync) :
        Thread("Onliner") {

    private val cons = Net.createConsumer(ip, "$nick-ONLINE")
    private val prod = Net.createProducer(ip)
    private var host = 0

    var pi: Array<String> = arrayOf()

    init {
        cons.assign(asList(TopicPartition(gameTopic, Net.ONLINE)))
        cons.seekToEnd(asList(TopicPartition(gameTopic, Net.ONLINE)))
    }

    override fun run() {
        var records: ConsumerRecords<String, String>
        var prevDate = Date()
        var millis: Long = 0
        pi = Array<String>(players.size, { _ -> "" })
        for (p in players) {
            pi[p.value.position] = p.value.nick
        }

        while (true) {
            records = cons.poll(30)
            for (r in records) {
                when (r.key()) {
                    "alive" -> {
                        //println("${r.value()} is alive! ${players[r.value()]}")
                        players[r.value()]!!.onlineTimer = 1000
                    }
                    "newHost" -> {
                        if (r.value() != nick) sync.setHost(false)
                    }
                }
            }
            millis = Date().time - prevDate.time
            prevDate = Date()

            for (p in players) {
                p.value.onlineTimer = p.value.onlineTimer - millis
                p.value.isOnline = p.value.onlineTimer > 0
            }
            if (!players[pi[host]]!!.isOnline) {
                newHost()
            }
            prod.send(ProducerRecord(gameTopic, Net.ONLINE, "alive", nick))
        }
    }

    private fun newHost() {
        for (i in 0..(players.size - 1)) {
            if (!players[pi[i]]!!.isOnline) continue
            host = i
            break
        }
        prod.send(ProducerRecord(gameTopic, Net.ONLINE, "newHost", players[pi[host]]!!.nick))
        if (nick == players[pi[host]]!!.nick) sync.setHost(true)
    }
}