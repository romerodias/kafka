FROM confluentinc/cp-kafka:5.1.1

ADD create-topics.sh /

RUN apt-get update && apt-get install net-tools -y
RUN /create-topics.sh
