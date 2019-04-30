package br.com.rdtecnologia.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerWithCallback {

    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger(ProducerWithCallback.class);

        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);


        // create a producer record
        ProducerRecord<String, String> record = new ProducerRecord<String, String>( "first_topic",
            "olá kafka!!" );


        // send data
        producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                // execute every time a record is sucessfully sent or an excrption is trown
                if(e == null) {
                    // the record is successfully sent
                    logger.info("Received new metadata: \n "
                        + "\nTopic: " + recordMetadata.topic() + ""
                        + "\nPartition: " + recordMetadata.partition()
                        + "\nOffset: " + recordMetadata.offset()
                        + "\nTimestamp: " + recordMetadata.timestamp());
                } else {
                    e.printStackTrace();
                }
            }
        });


        producer.flush();
        producer.close();


    }
}
