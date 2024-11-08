#!/usr/bin/env python
import threading, time

from kafka import KafkaAdminClient, KafkaConsumer, KafkaProducer
from kafka.admin import NewTopic

class Producer(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.stop_event = threading.Event()

    def stop(self):
        self.stop_event.set()

    def run(self):
        server='brocker:porta'
        topic='nome-do-topico'        
        producer = KafkaProducer(bootstrap_servers=server)
        msg="{\"id\": \"00568\", \"nome\": \"José da Silva Soares\"}"
        print("Publicar evento no topico: "+topic+"  - kafka server: " + server)
        producer.send(topic, msg.encode('UTF-8'))
        time.sleep(1)

        producer.close()    


class Consumer(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.stop_event = threading.Event()

    def stop(self):
        self.stop_event.set()


    def run(self):
        server='brocker:porta'
        topic='nome-do-topico'  
        consumer = KafkaConsumer(bootstrap_servers=server,
                                 auto_offset_reset='earliest',
                                 consumer_timeout_ms=1000,enable_auto_commit=True)
        consumer.subscribe([topic])
        print("Consumindo eventos to topico: "+topic+"  - kafka server: " + server)
        while not self.stop_event.is_set():
            for message in consumer:
                print(message)
                if self.stop_event.is_set():
                    break

        consumer.commit()
        consumer.close()


def main():
    tasks = [
        Producer(),
        Consumer()
    ]

    # Start threads of a publisher/producer and a subscriber/consumer 
    for t in tasks:
        t.start()

    time.sleep(10)

    # Stop threads
    for task in tasks:
        task.stop()

    for task in tasks:
        task.join()


if __name__ == "__main__":
    main()

