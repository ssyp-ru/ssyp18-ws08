package netlib

import org.apache.kafka.common.TopicPartition
import java.util.concurrent.locks.ReentrantLock

class NetActionsParser(ip: String,
                       private var players: ArrayList<NetPlayer>,
                       private val nick: String,
                       private var actionsLock: ReentrantLock) :
        Thread("ActionParser") {
    private var actions = ArrayList<NetAction>()
    private val cons = createConsumer(ip, nick)

    fun actions(): ArrayList<NetAction> {
        actionsLock.lock()
        val toOut = actions
        actions = ArrayList()
        actionsLock.unlock()
        return toOut
    }

    override fun run() {
        var prevTime: Long = 0
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
            val records = cons.poll(5)
            if (records.isEmpty) continue
            //println(System.currentTimeMillis() - prevTime)
            //prevTime = System.currentTimeMillis()
            actionsLock.lock()
            for (r in records) {
                actions.add(NetAction(r.topic().drop(8), r.key(), r.value().split('|')))
            }
            actionsLock.unlock()
        }
    }
}

data class NetAction(val sender: String, val name: String, val params: List<String>)