package ru.enginner

import org.apache.kafka.common.TopicPartition
import java.util.concurrent.locks.ReentrantLock

class NetActionsParser(ip: String,
                       private var players: ArrayList<NetPlayer>,
                       private val nick: String,
                       private var actionsLock: ReentrantLock) :
        Thread("ActionParser") {
    private var actions = ArrayList<NetAction>()
    private val cons = Network.createConsumer(ip, nick)

    fun actions(): ArrayList<NetAction> {
        actionsLock.lock()
        val toOut = actions
        actions = ArrayList()
        actionsLock.unlock()
        return toOut
    }

    override fun run() {
        val listPartitions = ArrayList<TopicPartition>()
        println("(Network)Parser initialized!")
        for (p in players) {
            //println(p.nick)
            if (p.nick == nick) continue
            listPartitions.add(TopicPartition("-PLAYER-" + p.nick, 0))
        }
        cons.assign(listPartitions)
        cons.seekToEnd(listPartitions)
        while (true) {
            val records = cons.poll(10)
            if (records.isEmpty) continue
            actionsLock.lock()
            for (r in records) {
                actions.add(NetAction(r.topic().drop(8), r.key(), r.value().split('|')))
            }
            actionsLock.unlock()
        }
    }
}

data class NetAction(val sender: String, val name: String, val params: List<String>)