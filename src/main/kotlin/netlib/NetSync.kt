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
import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.locks.ReentrantLock
import javax.swing.plaf.basic.BasicInternalFrameTitlePane

class NetSync(gs: Serializable,
              private var isHost: Boolean,
              ip: String, nick: String,
              gameName: String) :
        Thread("Syncer") {
    private val prod: KafkaProducer<String, ByteArray>
    private val cons: KafkaConsumer<String, ByteArray>
    private val topicName = "-LOBBY-$gameName"
    private var gsarr: ByteArray
    private var gsarrLock = ReentrantLock()
    var gameState: Serializable = gs
        set(gs: Serializable) {
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
        consProperties.setProperty("auto.commit", "1000")
        consProperties.setProperty("group.id", "$nick-SYNC")
        cons = KafkaConsumer(consProperties)

        val prodProperties = Properties()
        prodProperties.setProperty("bootstrap.servers", ip)
        prodProperties.setProperty("key.serializer", StringSerializer::class.java.name)
        prodProperties.setProperty("value.serializer", ByteArraySerializer::class.java.name)
        prodProperties.setProperty("retries", "5")
        prodProperties.setProperty("acks", "1")
        //prodProperties.setProperty("buffer.memory", "1000")
        prod = KafkaProducer(prodProperties)

        cons.assign(asList(TopicPartition(topicName, PartitionID.SYNC.ordinal)))
        cons.seekToEnd(asList(TopicPartition(topicName, PartitionID.SYNC.ordinal)))
        //gameState = gs
        gsarr = serialize(gs)

        println("(Network)Sync initiazized!")
    }

    fun setHost(b: Boolean) {
        isHost = b
        println(if (isHost) "ama host now" else "ama bomzh now")
    }


    /*fun setGameState(st: Serializable) {
        gameState = st
    }

    fun getGameState(): Serializable {
        return gameState
    }*/

    override fun run() {

        while (true) {
            if (isHost) {
                gsarrLock.lock()
                prod.send(ProducerRecord(topicName, PartitionID.SYNC.ordinal, "sync", gsarr))
                gsarrLock.unlock()
                //println("send sync")
                Thread.sleep(1000)
            } else {
                val records = cons.poll(50)

                for (r in records) {

                    if (r.key() == "sync") {
                        //println("recieve sync")
                        //lock
                        gameState = deserialize(r.value())
                        //unlock
                    }
                }
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
        }catch(e: Exception){
            return gameState
        }
    }
}