package br.com.rdtecnologia.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;

public class ProducerSimple {

    public static void main(String[] args) throws JsonProcessingException, InterruptedException {

        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.1.119:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , StringSerializer.class.getName());


        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        ObjectMapper mapper = new ObjectMapper();


        Metrica metrica = Metrica.builder().
            data(new Date()).
            cpf("07750092617").
            macBalanca(new Date().toString()).
            kgPeso(85D).
            percentualTaxaMusculo(45.5D).
            percentualTeorAgua(60.32D).
            kgMassaOssea(12D).
            kcalTaxaMetabolicaBasal(10.4D).
            percentualProteina(null).
            idadeCorporal(18).
            idadeGorduraVisceral(12).
            taxaGorduraSubcutanea(32.3D).
            kgGorduraCoporal(20.12D).
            indiceMassaCorporal(26D).
            percentualTaxaGorduraCoroporal(8.10D).
            build();


        String msg = mapper.writeValueAsString(metrica);

        // crea
        //
        //
        // te a producer record
        ProducerRecord<String, String> record = new ProducerRecord<String, String>( "bioimpedancia.metrica",
           msg);

        //ProducerRecord<String, String> record = new ProducerRecord<String, String>( "bio-metrica",
         //   LocalDateTime.now().toString());

        double loop = 200000000000D;

        while(loop >= 0) {
            // send data
            producer.send(record);
            loop--;
            System.out.println("sent " + loop);
            Thread.sleep(5000);

        }

        //producer.flush();
        producer.close();


    }
}
