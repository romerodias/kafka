#!/bin/sh

##########################################################################
# 
# Kafka Consumer Group Monitor 
#
# This script monitor some fileds from kafka, like: TOPIC, CURRENT-OFFSET, 
# LOG-END-OFFSET, LAG, DATE (actual date)
# 
# Variables ---------------------------------------------------------------
# KAFKA_SERVER - Kafka IP or hostname
# KAFKA_PORT -  Kafka Port
# INTERVAL_SECONDS - Seconds to make the next read 
# GROUP_NAME - Group to Monitor
# -------------------------------------------------------------------------
#
# You can run script with output to file  
# > ./kafka-consumer-group-monitor.sh > kafka-group.log
#
##########################################################################

export TZ=America/Sao_Paulo

KAFKA_SERVER=kafka
KAFKA_PORT=9092
INTERVAL_SECONDS=10
GROUP_NAME=groupid-metrica-consumer

while :
do
  kafka-consumer-groups --bootstrap-server $KAFKA_SERVER:9092 --describe --group $GROUP_NAME | \
     awk -v data="$(date +'%d/%m/%Y-%H:%M:%S')" '{if(NR>2)print $1",",$3",",$4",",$5",",data}'
  sleep $INTERVAL_SECONDS
done
