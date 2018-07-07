package netlib

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.io.*
import java.nio.charset.Charset
import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList

class NetSync(gs: Serializable,
              private var isHost: Boolean,
              ip: String,
              private val nick: String,
              gameName: String) :
        Thread("Syncer") {
    private val prod: KafkaProducer<String, ByteArray>
    private val cons: KafkaConsumer<String, ByteArray>
    private val topicName = "-LOBBY-$gameName"
    private var gsarr: ByteArray
    private var gsarrLock = ReentrantLock()
    private val syncTime: Long = 1000
    private var host = 0
    private val cHost = 1.5f
    private var prevSync = System.currentTimeMillis()
    private var playersList = ArrayList<String>()
    fun setPlayers(pl: ArrayList<NetPlayer>) {
        if (!this.isAlive) {
            for (p in pl) {
                playersList.add(p.nick)
            }
        }
    }

    var gameState: Serializable = gs
        set(gs) {
            if (isHost) {
                gsarrLock.lock()
                gsarr = serialize(gs)
                gsarrLock.unlock()
            }
            field = gs
        }

    init {
        val consProperties = Properties()
        consProperties.setProperty("bootstrap.servers", ip)
        consProperties.setProperty("key.deserializer", StringDeserializer::class.java.name)
        consProperties.setProperty("value.deserializer", ByteArrayDeserializer::class.java.name)
        consProperties.setProperty("enable.auto.commit", "true")
        consProperties.setProperty("auto.commit", "5000")
        consProperties.setProperty("group.id", "$nick-SYNC")
        cons = KafkaConsumer(consProperties)

        val prodProperties = Properties()
        prodProperties.setProperty("bootstrap.servers", ip)
        prodProperties.setProperty("key.serializer", StringSerializer::class.java.name)
        prodProperties.setProperty("value.serializer", ByteArraySerializer::class.java.name)
        prodProperties.setProperty("retries", "3")
        prodProperties.setProperty("acks", "1")
        prodProperties.setProperty("batch.size", "0")
        prod = KafkaProducer(prodProperties)

        cons.assign(asList(TopicPartition(topicName, PartitionID.SYNC.ordinal)))
        cons.seekToEnd(asList(TopicPartition(topicName, PartitionID.SYNC.ordinal)))
        gsarr = serialize(gs)
    }

    fun setHost(b: Boolean) {
        gsarrLock.lock()
        gsarr = serialize(gameState)
        gsarrLock.unlock()
        isHost = b
        println(if(isHost)"Host" else "client")
    }

    override fun run() {
        prevSync = System.currentTimeMillis()
        while (true) {
            if (isHost) {
                for (r in cons.poll(50)) {
                    if (r.key() == "host") {
                        setHost(r.value().toString(Charset.defaultCharset()) == nick)
                    }
                }
                gsarrLock.lock()
                prod.send(ProducerRecord(topicName, PartitionID.SYNC.ordinal, "sync", gsarr))
                gsarrLock.unlock()
                var playersString = "$host"
                for (p in playersList) {
                    playersString += "|$p"
                }
                prod.send(ProducerRecord(topicName, PartitionID.SYNC.ordinal, "playersList",
                        playersString.toByteArray(Charset.defaultCharset())))
                Thread.sleep(syncTime)
            } else {
                val records = cons.poll(25)
                for (r in records) {
                    when (r.key()) {
                        "sync" -> {
                            prevSync = System.currentTimeMillis()
                            gameState = deserialize(r.value())
                        }
                        "host" -> {
                            setHost(r.value().toString() == nick)
                            host = playersList.indexOf(r.value().toString())
                        }
                        "playersList" -> {
                            val tmp = r.value().toString(Charset.defaultCharset()).split('|')
                            host = tmp[0].toInt()
                            for (i in 0 until playersList.size) playersList[i] = tmp[i + 1]
                        }
                    }
                }
                /*if ((System.currentTimeMillis() - prevSync) > (syncTime * cHost)) {
                    host = if (host < (playersList.size - 1)) (host + 1) else 0
                    if (nick == playersList[host]) {
                        prod.send(ProducerRecord(topicName, PartitionID.SYNC.ordinal, "host",
                                nick.toByteArray(Charset.defaultCharset())))
                        setHost(true)
                    }
                    prevSync = System.currentTimeMillis()
                }*/
            }
        }
    }

    private fun serialize(gs: Serializable): ByteArray {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(gs)
        return baos.toByteArray()
    }

    private fun deserialize(arr: ByteArray): Serializable {
        val bais = ByteArrayInputStream(arr)
        val ois = ObjectInputStream(bais)
        var obj: Any? = null
        try {
            obj = ois.readObject()
        } catch (e: InvalidClassException) {
            println("SYNC failed")
        }
        try {
            when (obj) {
                is Serializable -> return obj
                else -> throw Exception()
            }
        } catch (e: Exception) {
            return gameState
        }
    }
}