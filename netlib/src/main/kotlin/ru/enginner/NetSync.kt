package ru.enginner

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

class NetSync(var gs: Serializable, private var isHost: Boolean, val ip: String, nick: String,
              gameName: String) : Thread("Syncer") {
    private val prod: KafkaProducer<String, ByteArray>
    private val cons: KafkaConsumer<String, ByteArray>
    private val topicName = "-LOBBY-$gameName"

    init {
        val consProperties = Properties()
        consProperties.setProperty("bootstrap.servers", ip)
        consProperties.setProperty("key.deserializer", StringDeserializer::class.java.name)
        consProperties.setProperty("value.deserializer", ByteArrayDeserializer::class.java.name)
        consProperties.setProperty("enable.auto.commit", "true")
        consProperties.setProperty("auto.commit", "1000")
        consProperties.setProperty("group.id", "$nick-SYNC")
        cons = KafkaConsumer<String, ByteArray>(consProperties)

        val prodProperties = Properties()
        prodProperties.setProperty("bootstrap.servers", ip)
        prodProperties.setProperty("key.serializer", StringSerializer::class.java.name)
        prodProperties.setProperty("value.serializer", ByteArraySerializer::class.java.name)
        prodProperties.setProperty("retries", "5")
        prodProperties.setProperty("acks", "1")
        prod = KafkaProducer<String, ByteArray>(prodProperties)

        cons.assign(asList(TopicPartition(topicName, Net.SYNC)))
        println("(Net)Sync initiazized!")
    }

    fun setHost(b: Boolean) {
        isHost = b
        println(if (isHost) "ama host now" else "ama bomzh now")
    }

    fun setGameState(st: Serializable) {
        gs = st
    }

    fun getGameState(): Serializable {
        return gs
    }

    override fun run() {
        while (true) {
            if (isHost) {
                prod.send(ProducerRecord(topicName, Net.SYNC, "sync", serialize(gs)))
                //println("send sync")
                Thread.sleep(950)
            } else {
                val records = cons.poll(50)
                for (r in records) {

                    if (r.key() == "sync") {
                        //println("recieve sync")
                        gs = deserialize(r.value())
                    }
                }
            }
        }
    }

    fun serialize(gs: Serializable): ByteArray {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(gs)
        return baos.toByteArray()
    }

    fun deserialize(arr: ByteArray): Serializable {
        val bais = ByteArrayInputStream(arr)
        val ois = ObjectInputStream(bais)
        val obj = ois.readObject()
        when (obj) {
            is Serializable -> return obj
            else -> throw Exception()
        }
    }
}