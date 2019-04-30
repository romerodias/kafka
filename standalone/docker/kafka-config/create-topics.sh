#!/bin/sh

count=0
step=10
KAFKA_PORT=9290
START_TIMEOUT=600

while netstat -lnt | awk '$4 ~ /:'"$KAFKA_PORT"'$/ {exit 1}'; do
    echo "waiting for kafka to be ready"
    sleep $step;
    count=$((count + step))
    if [ $count -gt $START_TIMEOUT ]; then
        start_timeout_exceeded=true
        break
    fi
done

kafka-topics --zookeeper zookeeper:2181 --topic bioimpedancia.trabalhador  --create --partitions 1 --replication-factor 1
kafka-topics --zookeeper zookeeper:2181 --topic bioimpedancia.metrica  --create --partitions 1 --replication-factor 1
kafka-topics --zookeeper zookeeper:2181 --topic bioimpedancia.lote  --create --partitions 1 --replication-factor 1
kafka-topics --zookeeper zookeeper:2181 --topic bioimpedancia.log  --create --partitions 1 --replication-factor 1
