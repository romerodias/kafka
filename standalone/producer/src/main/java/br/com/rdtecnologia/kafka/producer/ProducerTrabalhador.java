package br.com.rdtecnologia.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;

public class ProducerTrabalhador {

    public static void main(String[] args) throws JsonProcessingException, InterruptedException {

        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.1.119:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        ObjectMapper mapper = new ObjectMapper();

        Trabalhador trabalhador = Trabalhador.builder()
            .altura(1.85d)
            .celular("55558855")
            .circunferencia(10d)
            .cpf("07750092617")
            .cracha("x")
            .dataAlteracao(new Date())
            .dataCriacao(new Date())
            .email("romero@outlook.com")
            .genero("M")
            .id(10l)
            .nascimento(new Date())
            .nome("Romero Gonçalves Dias " + LocalDateTime.now())
            .build();

        String msg = mapper.writeValueAsString(trabalhador);

        ProducerRecord<String, String> record = new ProducerRecord<String, String>( "bioimpedancia.trabalhador",
           msg);

       // double loop = 200000000000D;

        //while(loop >= 0) {
            // send data
            producer.send(record);
        //    loop--;
         //   System.out.println("sent " + loop);
         //   Thread.sleep(5000);
        //}

        //producer.flush();
        producer.close();


    }
}
