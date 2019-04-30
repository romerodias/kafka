# docker-compose

```yml
version: '3'
services:

  zookeeper:
    image: confluentinc/cp-zookeeper:5.1.1
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - 2181:2181
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    #build:
    #  context: ./kafka
    #  dockerfile: Dockerfile
    image: confluentinc/cp-kafka
    hostname: kafka
    container_name: kafka
    ports:
      - 9092:9092
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://10.0.1.119:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  topic-creation:
    image: confluentinc/cp-kafka
    command: bash -c "cub kafka-ready -z zookeeper:2181 1 30 && kafka-topics --zookeeper zookeeper:2181 --topic bioimpedancia.trabalhador  --create --partitions 1 --replication-factor 1 && kafka-topics --zookeeper zookeeper:2181 --topic bioimpedancia.metrica  --create --partitions 1 --replication-factor 1 && kafka-topics --zookeeper zookeeper:2181 --topic bioimpedancia.lote  --create --partitions 1 --replication-factor 1 && kafka-topics --zookeeper zookeeper:2181 --topic bioimpedancia.log  --create --partitions 1 --replication-factor 1"
    depends_on:
      - zookeeper

  ksql:
    image: confluentinc/cp-ksql-server:5.1.1
    hostname: ksql
    container_name: ksql
    depends_on:
      - kafka
    ports:
      - 8088:8088
    environment:
      KSQL_BOOTSTRAP_SERVERS: kafka:9092
      KSQL_LISTENERS: http://0.0.0.0:8088
      KSQL_KSQL_SERVICE_ID: ksql_service_id
    links:
      - kafka

  kafka-rest-proxy:
    image: confluentinc/cp-kafka-rest:5.1.2
    hostname: kafka-rest-proxy
    container_name: kafka-rest-proxy
    ports:
      - "8082:8082"
    environment:
      # KAFKA_REST_ZOOKEEPER_CONNECT: zoo1:2181
      KAFKA_REST_LISTENERS: http://0.0.0.0:8082/
      #KAFKA_REST_SCHEMA_REGISTRY_URL: http://kafka-schema-registry:8081/
      KAFKA_REST_HOST_NAME: kafka-rest-proxy
      KAFKA_REST_BOOTSTRAP_SERVERS: PLAINTEXT://kafka:9092
    depends_on:
      - zookeeper
      - kafka

  kafka-topics-ui:
    image: landoop/kafka-topics-ui:0.9.4
    hostname: kafka-topics-ui
    container_name: kafka-topics-ui
    ports:
      - "8000:8000"
    environment:
      KAFKA_REST_PROXY_URL: "http://kafka-rest-proxy:8082/"
      PROXY: "true"
    depends_on:
      - zookeeper
      - kafka
      - kafka-rest-proxy
```

# Run KSQL 
```
docker run -it confluentinc/cp-ksql-cli:5.1.1 http://{id_do_host}:8088
```

# Commandos Úteis

### Criar tópico

```bash 
kafka-topics --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1
```

#### Listar Tópicos
```bash 
kafka-topics --zookeeper 127.0.0.1:2181 --list
```

#### Ver informações detalhadas de um tópico

```bash 
kafka-topics --zookeeper 127.0.0.1:2181 --topic first_topic --describe
```

#### Deletar Tópico
```bash 
kafka-topics --zookeeper 127.0.0.1:2181 --topic sedond_topic --delete
```

#### Produzir Mensagens para um Tópico
```bash 
kafka-console-producer --broker-list 127.0.0.1:9092 --topic first_topic
```

#### Consumir um tópico diretamente
```bash 
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic
```

### Consumir um tópico a partir de um grupo de consumidores
```bash 
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my_first_group
```

### Listar grupo de consumidores
```bash 
kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --list
```

#### Exibir informações do grupo
```bash 
kafka-consumer-groups --bootstrap-server 127.0.0.1:9092 --describe --group my_first_group
```
```
kafka-consumer-groups  --bootstrap-server localhost:9092 --describe --group octopus
GROUP          TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG       OWNER
octopus        test-topic      0          15              15              0         octopus-1/127.0.0.1
octopus        test-topic      1          14              15              1         octopus-2_/127.0.0.1
```

- CURRENT-OFFSET: is the last commited offset of the consumer instance
- LOG-END-OFFSET: is the highest offset of the partition (hence, summing this column gives you the total number of messages for the topic)
- LAG: is the difference between the current consumer offset and the highest offset, hence how far behind the consumer is
- OWNER: is the cliente.id of the consumer (if not specified, a default one is deisplayed)

#### Deletar um grupo de consumidor
``` kafka-consumer-groups --bootstrap-server localhost:9092 --delete --group rdconsumergroup ```


#### Resetar os offsets commitados em um tópico
```bash 
kafka-consumer-groups --bootstrap-server localhost:9092 --group my_first_group --reset-offsets --to-earliest --execute --topic first_topic
```

### Produzir Mensagem com Chave e Valor
```bash 
kafka-console-producer --broker-list 127.0.0.1:9092 --topic first_topic --property parse.key=true --property key.separator=,
> key,value
> another key,another value
```

#### Consumir Mensagens Exibindo a Chave e Valor
```bash 
kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=,
```

#### Console Web para Kafka
https://github.com/yahoo/kafka-manager

## Kafka Uses Zookeeper for:

* **Electin a Controller** The controller is one of the brokers and is reponsisble for maintaning the leader/follower relationshiop for all the partitions. When a node shutdownm its is the controller that tells other replicas to become partition leaders to replace the partition leader on the node that is going away. Zookeeper is used to elect a controller, make sure there is only one elect a new one it if it crashes.

* **Cluster Membership** Which brokers area alive and part of the cluster? this is also managed through Zookeepr

* **Topic Configuration** - which topics exist, how many partitions each has, where are the replicas, who is the preferred leader, what configurations overrides are set for each topic.

* **Quotas** - how much data is each client allowed to read and write.

* **ACLs** - who is allowed to read and write to which topic (old high level consumer) - Which consumer groups exist, who are their members and what is the letest offset each groupo got from each partitions.



## kafka ConnectWorkes Standalone vc Distributed Mode
* Standalone:
    * A single process runs your connectors and tasks
    * Configuration is bundled with your process
    * very * easy to get started with
    * Not fault tolerant, no scalability, hard to monitor
* Distributed:
    * Muiltiple workers run your connectors and tasks
    * Configuration is submitted using a REST API
    * Easy to scale, and fault tolerant (rebalancing in case a worker die)
    * useful for production deployments of connectors

## Linger.ms & batch.size
* By default, Kafka tries to send records as soon as possibile
    * It will have up to 5 requests in flight, meaning up to 5 messages individually sent at the same time.
    * After this, it more messages hava to be sent while others are in flight, Kafka is smart and will start batching 
them while they wait to send them all at once.
* This smart batching allows Kafka to increase throughput while maitaining very low latency.
* Batches hava higher compression ratio so better efficiency.

* So how can we control the batching mechanism?
* Linger.ms: Number of milliseconds a produces is willing to wait before sending a batch out (default 0)
* By introducing some lag (for example linger.ms=5), we increase the chances of messages being
send together in a batch.
* If a batch is full (see batch.size) before the end of the linger.ms period, it will be sent to 
Kafka right away.
* **batch.size**: Maximum number of bytes that will be included in a batch. The default is 16kb.
* Increasing a batch size to something like 32kb or 64kb can help increasing the compression, 
throughput, and efficiency of requests.
* Any message that is bigger than the batch size will not be batched.
* A batch is allocated per partition, so make sure that you don't set it to a number that's too
high, otherwise you'll run waste memory.
* Note: you can monitor the average batch size metric using Kafka Producer Metrics.

## High Throughput Producer Demo
* We'll add snappy message compression in our producer
* Snappy is very helpful if your message are text based, for example log lines of JSON documents
* Snappy has a good balance of CPU / compression ratio
* We'll also increase the batch.size to 32kb and introduce a small delay through linger.ms (20 ms)

``` 
properties.setProperty(ProducerConfig.COMPRESION_TYPE_CONFIG, "snappy");
properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024)); //32kb batch size 
```

## Producer Default Partition and how keys are hashed
* By default, your keys area hashed using the "murmur2" algorithm
* It is most likely preferred to not override the behavior of the partitioner, but it is possibile
to do so (partitioner.class)
* The formula is: targetPartition = Utils.abs(Utils.mumur2(record.key())) % numParticions;
* This mean that same key will go to the same partition (we already know this) and adding
partitions to a topic will completely alter the formula.

## Max.block.ms & buffer.memory
* If the producer produces faster than the broker can take, the records will be buffered in memory
* **buffer.memory=33554432 (32mb)** the size of the send buffer
* That buffer will fill up over time and fill back down when the throughput to the broker increases
* If that buffer is full (all 32mb), then the .send() method will start to block (won't return right away)
* **max.block.ms=60000**: the time the .send() will block until throwing an exception. Exceptions are basically thrown when
    * The producer has filled up its buffer
    * The brocker is not accepting any new data
    * 60 seconds has elapsed.
* If you hit an exception hit that usually means your brokers are down overload as they can`t respond to request

## Delivery Semantics 
* **At most once**: offsets are committed as soon as the message batch is received. If the processing goes wrong the message will be lost (it won`t be read again).
* **At Least once**: offsset are committed after the message is processed. If the processing goes wrong, the message will be read again. This can result in duplicate processing of message. Make sure you processing is **idmpotent** (i.e. processing again the message won`t impact your systems).
* **Exactly once:** can be achived for Kafka => Kafka workflows using Kafka Stream API. For Kafka => Sink workflows, use an idempotent consumer.  
**Bottom line**: for most applications you should use at least once processing and ensure you tranformations / processing are idempotent.

## Consumer Offset Commits Strategies
* There are two most common patterns for committing offsets in a consumer application
* 2 strategies:
    * (easy) enable.auto.commit = true & synchronous processing of batches
    * (medium) enable.auto.commit = false & manual commit of offsets

## Consumer Offset Reset Behaviour
* A ocnsumer is expected to read form a log continuously
* But if your application has a bug, your consumer can be down
* If kafka has a retention of 7 days, and your consumer is down for more than 7 days, the offsets are "invalid" 
* The behavior for the consumer is to then use:
  * auto.offset.reset=latest: will read from the end of the log
  * auto.offset.reset=earliest: will read form the star of the log
  * auto.offset.reset=none: will throw exception if no offset is found
* Additionally, consumer offset can be lost:
  * if a consumer hasn`t read new data in 1 day (Kafka < 2.0)
  * If a consumer hasn`t read new data in 7 days (Kafka >- 2.0)
* This can be controlled by the broker setting offset.retention.minutes
#

## Replaying data for Consumer
* To replay data for a consumer group:
  * Take all consumer from a specific group down
  * User 'kafka-consumer-groups' command to set offset to what you want
  * Restar consumer
* Bottom line:
  * Set proper data retention periodo & offset retention periodo
  * Ensure the auto offset reset behavior is the one you expect / want
  * User replay capability in case of unexpected behavior

 
## Controlling Consumer Livliness
* Consumer in a Group talk to a Consumer Group Coordinator
* To detect consumer that are down, there is a heartbeat mechanism and a poll mechanism
* To avoid issues, consumer are encouraged to process data fast and poll oftem

## Consumer Heartbeat Thread
* Session.timeout.ms (default 10 seconds)
  * Heartbeat are sent periodically to the broker
  * If no heartbeat is sent during a periodo, the consumer is considered dead.
  * Set even lower to fast consumer rebalances
* **heartbeat.inveral.ms** (default 3 seconds)
  * How often to send heartbeat;
  * Usually set to 1/3rd of session.timeout.ms
* Take-away: this mechanism is used to detec a consumer application being down

## Consumer Poll Thread
* **max.poll.invertal.ms** (default 5 minutes)
  * Maximum amount of time between two .poll() call before declaring the consumer dead
  * This is particularly relevent for Big Dta frameworks like Spark in case the processing takes time
* Take-away: this mechanism is used to detec a data processing issue with the consumer


# Kafka Connect Introduction 
* Do you feel you are not the first person in the word to write a way to get data out of Twitter?
* Do you feel like you are not the first person in the word to send data from kafka to PostgreSQL / ELK / Mongo?
* Additionally, the bugs you`ll have, won't someone have fixed them already?
* Kafka Connect is all about code & connectors re-use!

## Kafka Connect API A Brief History
* (2013) Kafka 0.8.x:
  * Topic replication, Log compaction
  * Simplified producer client API
* (Nov 2015) Kafka 0.9.x:
  * Simplified high level consumer APIS, with Zookeeper dependency 
  * Added security (Encryption and Authentication)
  * Kafka Connect APIs
* (May 2016): Kafka 0.10.0:
  * Kafka Streams APIs
* (end 2016 - Match 2017) Kafka 0.10.1, 0.10.2
  * Improved Connect API, Single message Tranform API


## The need for a schema registry
* What if the producer sends bad data?
* What if a field gets renamed?
* What if the data format chantes form one day to another?
* **The consumer Break!!**
* We need data to be self describable
* We need to be able to envolve data without breaking downstream consumers.
* We need **schemas** and a chema registry.
* What if the Kafka Brokers were verifying the messages they receive?
* It would break that makes Kafka so good.
  * Kafka doesn't parse or event read your data (no CPU usage)
  * Kafka takes bytes as an input without even loading them into memory (that's called zero copy)
  * Kafka distributes bytes
  * As far as Kafka is concerned, it doesn't even know if your data is an integer, a sting etc.
* The schema registry has to be a separated components
* Producers and Consumers need to be able to talk to it
* The Schema Registry must be able to reject bad data
* A common data format must be agreed upon
  * It needs to support schemas
  * It needs to support evolution
  * It needs to be lightweight
* Enter the Confluent Schema Registry
* And Apache Avro as the data format

## Confluent Schema Registry Purpose
* Store and retrieve schemas for Produces / Consumers
* Enforce Backward / Forward / Full compatibility on topics
* Decrease the size of the payload of data sent to kafka

## Schema Registry: gotchas
* Utilizing a schema registry has a lot of benefits
* BUT it implies you need to
  * Set it up well
  * Make sure it's highly available
  * Partially change the producer and consuemr code
* Apache Avro as a format is awesome but has a learning curve
* The schema registry is free and open sourced, create by Confluent (creators of Kafka)
* As it takes time to setup, we wont't cover the usage in this course

## Partitions Count, Replication Factor
  * The two most important parameters when creatinga a topic.
  * They impact performance and durability or the system overall.
  * It is best ot get the parameters right the first time!
    * In the Partitions Count increases during a topic lifecycle, you will break your keys ordering guarantees.
    * If the Repliation Factory increases during a topic lifecycle, you put more pressure on your cluster, which can lead to aunexpected performance decrease.

  ### Particions Count
  * Each partition can handle a throughput of a few MB/s (measure it for your setup)
  * More partitions implies:
    * Better parallelism, better throughput
    * Ability to run more consumer in a group to scale
    * Ability to levarage more brokers if you have  a large cluster
    * BUT more elections to perform for Zookeeper
    * BUT more files opned on Kafka
  * Guidelines:
    * **Partitions por potic = MILLION DOLLAR QUESTION**
      * (Intuition) Small cluster (< brokers): 2 x #brokers
      * (Intuition) Big Cluster (> 12 brokers): 1 x #brokers
      * Adjust for number of consumer you need to run in parallel at peak throughput
      * Adjust for producer throughput (increase if super-hight throughput or projected increase in the next 2 years)
    * **TESTE!** Every Kafka cluster will have different performance
    * Dont't create a topi with 1000 particions!

  ### Replication Factory
  * Should be at least 2, usually 3, maximum 4
  * The higher the replication facytor (N):
    * Better resilience of your system (N-1 brokers can fail)
    * BUT more replication (higher latency if acks=all)
    * BUT more disk space on your system (50% more if RF is 3 instead of 2)
  * Guidelines:
    * **Set if to 3 to get started** (you must have at least 3 brokers for that)
    * If replication performance is an issue, get a better broker inseted of less RF
    * **Never set it to 1 in production**

  ### Cluster Guildelines
  * It is pretty much accepted that a broker should not hold more than 2000 to 4000 partitions (across all topics of that broker).
  * Additionlly, a kafka cluster should have a maximum of 20.000 partitions across all brokers.
  * The reason is that case of brokers going down, Zookeeper needs to perform a lot of lead elections.
  * If you need more partitions in your cluster, add brokers instead.
  * If you need more than 20.000 partitions in your cluster (it will take time to get there!),  follow the Netflix model and create more kafka cluster.
  * Overall, you don't need a topic with 1000 partitions to achieve high throughput Start at a resonable number and test the

  ## Kafka Cluster Setup High Leval Architecture
  * Server_1
    * Zookeeper_1
    * Kafka_1

  * Server_2
    * Zookeeper_2
    * Kafka_2
  
  * Server_3
    * Zookeeper_3
    * Kafka_3

## Kafka Monitoring and Operations
* Kafka exposes metrics through JMX
* These metrics area highly important for monitoring Kafka, and ensuring the systems are behaving correctly under load.
* Common places to host the Kafka metrics:
  * ELK
  * Datadog
  * NewRelic
  * Confluent Control Centre
  * Prometheus

* Some of the most important metric are:
  * Under Replicated Partitions: number of partitions are have problems with the ISR (in-sync replicas). May indicate a high load on the system.
  * Request Handlers: utilization of threads for IO, network, etc... overall utilization of an Apache Kafka broker.
  * Request Timing: how long is takes to reply to requests. Lower is better, as latency will be imporoved.

* Kafka Operations team must be able to perform the following tasks:
  * Rolling Restart of Brokers
  * Updating Configurations
  * Rebalancing Partitions
  * Increasing replication factor
  * Adding a Broker
  * Replacing a Broker
  * Removing a Broker
  I Upgrading a Kafka Cluster with zero downtime

## The need for encryption, authentication & authorization in Kafka
* Currently, any client can access your kafka cluster (authentication)
* The clients can publish / consume any topic data (authoriation)
* All the data being sent is fully visible on the network (encryption)

* Someone could intercept data being sent
* Someone could pubish bad data / steal data
* Someone could delete topics

* All these reasons push for more security and an authentication model

* Encryption in Kafka ensures that the data exchanged between clients and brokers is secret to routers on the way
* This is similar concept to an https website

[Kafka Cliente]       ---------->     a0873y879jxhxs     --------> [Kafka Brokers]
(produzer/consumer)                  (Encrypted data)              Port 9093 - SSL

* Authentication in Kafka ensures that only clientes that can prove their identity can connect to our Kafka Cluster
* This is similar concept to a login (username/password)

+----------------+  ----- (Authentication data) ---> +---------------+ 
| Kafka Cliente  |                                   | Kafka Broker  |
+----------------+ <---- (Client is Authenticated)-- +---------------+

* Authentication in Kafka can take a few forms
* SSL Authentication: clientes authenticate of Kafka using SSL certificates
* SASL Authentication:
  * PLAIN: clients authenticate using username / password (week - easy to setup)
  * Kerberos: sutch a Microsof AD (strong hard to setup)
  * SCRAM: user/pass (strong - medium to setup)

* Once a client is authenticate, Kafka can verify its identity
* It still to be combined with authorisation, so that Kafka knows that
  * User alice can view topic finance
  * User bobo cannot view topic trucks
* ACL have to maintained by administration and onboard new users

* You can mix
  * Encryption
  * Authentication
  * Authorization
* This allows your Kafka clients to:
  * Communicate securely to Kafka
  * Clients would authenticate agains Kafka
  * Kafka can authorize clients to read / write to topics
* Kafka Security is fairly new (0.10)
* Kafka Security improves over time and becomes more flexible / easier to setup as time goes
* Currently, it is hard to setup Kafka Security
* Best support for Kafka Security for appication is with Java

## Kafka Multi Cluster + Replication
* Kafka can only operate well in a single region
* Therefore, it is very common if enterprises to hava Kafka cluster across the world, with some level of replication between then.
* A replication application at iss core is just a consumer + a producer
* There are differents tools to perform it:
  * Mirror Maker - open source tool that ships with Kafka
  * Netflix uses Flink - they wrote their own application
  * Uber uses uReplicator - addresses performance and operation issues with MM
  * Comcast has their own open source Kafka Connect Source
  * Confluent has their own Kafka Connect Source (paid)
* Overall, try these and see if it works for your use case before writing your own.
* There are two design for cluster replication:
  * Active  => Passive
  * Active <=> Active 

* Active <=> Active:
  * You have a global application
  * You have a global dataset
* Active => Passive:
  * You want to have aggregation cluster (for example for analytics)
  * YOu want to create some form of disaster recovery strategy (*its hard)
  * Cloud Migration (from on-premisse cluster to cloud)
* Replication does not preserve offsets, just data.  

## Advanced Kafka - Topic Configurations
* Brokers have defaults for all the topic configuration parameters
* These parameteres impact performance and topic behavior
* Some topics may need different values than the defaults
  * Replication Factor
  * # of Partitions
  * Message size
  * Compression level
  * Log Cleanup Policy
  * Min Insync Replicas
  * Other configurations
* A list of configuration can be found at:
https://kafka.apache.org/documentation/#/brokerconfigs

## Segments: Why should I care?
* A samller **log.segment.bytes** (size, default: 1gb) means:
  * more segments per pertitions
  * Log Compaction happens more often
  * BUT kafka has to keep more files opened (Error: Too many open files)
  * Ask yourself: how fast will i have new segments based on throughput?

* A sammler **log.segment.ms** (time, default 1 week) means:
  * You set a max frequency for log compaction (more frequent triggers)
  * Maybe you want daily compaction insetead of weekly?
  * Ask yourself: how often do i need log compaction to happen?

## Log Cleanup Polices
* Many Kafka cluster make data expire, according to a policy
* That concept is called log cleanup
* Policy 1: log.cleanup.policy=delete (Kafak default for all user topics)
  * Delete based on age of data (default is a week)
  * Delete based on max size of log (default is -1 == infinite)
* Policy 2: log.cleanup.policy=compact (Kafka default for topic __consumer__offset)
  * Delete based on keys of your messages
  * Will delete old duplicate keys after the ative segment is committed
  * Infinite time and space retention
* Deleting data from Kafka allows you to:
  * Control the size of the data on the disk, delete obsolete data
  * Overall: limit maintenance work on the Kafka Cluster
* How often does log cleanup happen?
  * Log cleanup happens on your partitions segments!
  * Smaller / More segments means that log cleanup will happen more often.
  * Log cleanup shouldn`t happen to often => take CPU and RAM resources
  * The cleaner checks for work every 15 seconds (log.cleaner.backoff.ms)

## Log Cleanup Policy: Delete
* **log.retention.hours**
  * Number of hours to keep data for (default is 168 - one week)
  * Higher number means more disk space
  * Lower number means that less data is retained (if your consumers are down for too long, they can miss data)
* **log.retantion.bytes**:
  * Max size in Bytes for each partition (defaults is -1 infinite)
  * Userful to keep the size of a log under a threshold

## Log Cleanup Policy: Compact
* Log compaction ensures that your log contains at least the last know vaue for a specific key within a partition.
* Very usefuil if we just reqrite a SNAPSHOT insetead of full history (such as for a data table in a database)
* The ideia is that we only  keep the latest "update" for a key in our log.
* Any consumer that is reading from the tail of a log (most current data) will still see all the messages sent to the topic.
* Ordering of messages it kept, log compaction only removes some messages, but does not re-order them
* The offset of a message is immutable (it never changes). Offsets are just skipped if a message is missing.
* Deleted records can still be seen by consumer for a period of **delete.retention.ms** (default is 24 hours)
* It doesn`t prevent you from pushing duplicated data to kafka
  * De-duplication is done after a segment is committed
  * Your consumer will still read from tail as soon as the data arrives
* It doesn`t prevent you from reading duplicate date from Kafka
  * Sme points as above
* Log compaction can fail from time to time
  * It is an optimization and it the compaction thread might crash
  * Make sure you assing enough memory to it and that it gets triggered.
  * Restar Kafka if log compaction is broken (this is a bug and may get fiexed in the future)
* You can`t trigger Log Compaction using API call (for now...)
* Log compaction is configured by **log.cleanup.policy=compct**
  * Segment.ms (default 7 days): max amount of time to wait to close active segment
  * Segment.bytes (default 1G): max siez of a segment
  * Min.compaction.lag.ms (default 0): how long wait before a message can be compacted
  * Delete.retention.ms (default 24 hours): wait before deleting data market for compaction
  * Min.Cleanable.dirty.ratio (default 0.5): higher => less, more efficient cleaning Lower => opposite
