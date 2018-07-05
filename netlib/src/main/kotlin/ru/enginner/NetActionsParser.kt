package ru.enginner

import org.apache.kafka.common.TopicPartition

class NetActionsParser(val ip: String, gameNmae: String, var players: ArrayList<NetPlayer>,
                       val nick: String) : Thread("ActionParser") {
    private var actions = ArrayList<NetAction>()
    private val cons = Net.createConsumer(ip, nick)

    fun actions(): ArrayList<NetAction> {
        val toOut = actions
        actions = ArrayList()
        return toOut
    }

    override fun run() {
        val listPartitions = ArrayList<TopicPartition>()
        println("(Net)Parser initialized!")
        for (p in players) {
            //println(p.nick)
            if (p.nick == nick) continue
            listPartitions.add(TopicPartition("-PLAYER-" + p.nick, 0))
        }
        cons.assign(listPartitions)
        while (true) {
            val records = cons.poll(10)
            if (records.isEmpty) continue
            for (r in records) {
                actions.add(NetAction(r.topic().drop(8), r.key(), r.value().split('|')))
            }
        }
    }
}

class NetAction(val sender: String, val name: String, val params: List<String>)