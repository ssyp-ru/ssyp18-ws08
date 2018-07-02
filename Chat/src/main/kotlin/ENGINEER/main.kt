package ENGINEER

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*
import java.util.Arrays.asList

fun main(args: Array<String>){
    print("Введите ник:")
    val nick = readLine()?:return

    val prodProperties = Properties()
    prodProperties.setProperty("bootstrap.servers", "127.0.0.1:9092")
    prodProperties.setProperty("key.serializer", StringSerializer::class.java.name)
    prodProperties.setProperty("value.serializer", StringSerializer::class.java.name)
    prodProperties.setProperty("retries", "3")
    prodProperties.setProperty("acks", "1")

    /*val consPoperties = Properties()
    consPoperties.setProperty("bootstrap.servers", "127.0.0.1:9092")
    consPoperties.setProperty("key.deserializer", "StringSerializer::class.java.name")
    consPoperties.setProperty("value.deserializer", "StringSerializer::class.java.name")
    consPoperties.setProperty("enable.auto.commit", "true")
    consPoperties.setProperty("auto.commit", "1000")
    consPoperties.setProperty("group.id", "$nick")
    val cons = KafkaConsumer<String, String>(consPoperties)
    cons.subscribe(asList("chat"))*/
    val cons = chatConsumer(nick)
    val prod = KafkaProducer<String, String>(prodProperties)
    cons.start()
    while(true){
        //print(">")

        val toSend = "$nick>${readLine()}"
        prod.send(ProducerRecord<String, String>("chat", "0", "$toSend")).get()
    }

}

class chatConsumer(val nick:String): Thread(){
    val consPoperties = Properties()
    val cons: KafkaConsumer<String, String>
    init{
        consPoperties.setProperty("bootstrap.servers", "127.0.0.1:9092")
        consPoperties.setProperty("key.deserializer", StringDeserializer::class.java.name)
        consPoperties.setProperty("value.deserializer", StringDeserializer::class.java.name)
        consPoperties.setProperty("enable.auto.commit", "true")
        consPoperties.setProperty("auto.commit", "1000")
        consPoperties.setProperty("group.id", "$nick")

        cons = KafkaConsumer<String, String>(consPoperties)
        cons.subscribe(asList("chat"))
    }

    override fun run(){
        println("Consumer initialized")
        while(true){
            val records = cons.poll(100)
            for(r in records)println(r.value())
        }
    }

}