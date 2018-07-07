package netlib

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*

class NetAdmin(ip: String) {
    private val admProperties = Properties()
    private val admin: AdminClient
    private val cons: KafkaConsumer<String, String>

    init {
        admProperties.setProperty("bootstrap.servers", ip)
        admin = AdminClient.create(admProperties)
        cons = createConsumer(ip, "")
    }

    fun createTopic(topicName: String, parts: Int) {
        println("(Adm)creating topic $topicName")
        admin.createTopics(listOf(NewTopic(topicName, parts, 1)))
    }

    fun lisTopics(): Array<String> {
        return cons.listTopics().keys.toTypedArray()
    }
}