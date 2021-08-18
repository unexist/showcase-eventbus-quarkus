RPK_PORT := 55025

define JSON_TODO_QUARKUS
curl -X 'POST' \
  'http://localhost:8080/todo' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "description": "string",
  "done": true,
  "dueDate": {
    "due": "2021-05-07",
    "start": "2021-05-07"
  },
  "title": "string"
}'
endef
export JSON_TODO_QUARKUS

define TODO_KAFKA_CE1_T1
echo 'test%{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "todo_in1",
  "time": "2021-07-23T12:02:23.812262+02:00[Europe/Berlin]",
  "data": {
    "description": "string",
    "done": false,
    "dueDate": {
      "due": "2022-05-08",
      "start": "2022-05-07"
    },
    "title": "string"
  }
}' | kafkacat -t todo_in1 -b localhost:$(RPK_PORT) -P -K%
endef
export TODO_KAFKA_CE1_T1

define TODO_KAFKA_CE2_T2
echo 'test%{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "todo_in1",
  "time": "2021-07-23T12:02:23.812262+02:00[Europe/Berlin]",
  "data": {
    "description": "string",
    "done": false,
    "dueDate": {
      "due": "2022-05-08",
      "start": "2022-05-07"
    },
    "title": "string"
  }
}' | kafkacat -t todo_in2 -b localhost:$(RPK_PORT) -P -K%
endef
export TODO_KAFKA_CE2_T2

define TODO_KAFKA_CE1
echo 'test%{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "todo_in1",
  "time": "2021-07-23T12:02:23.812262+02:00[Europe/Berlin]",
  "data": {
    "description": "string",
    "done": false,
    "dueDate": {
      "due": "2022-05-08",
      "start": "2022-05-07"
    },
    "title": "string"
  }
}' | kafkacat -t todo_in -b localhost:$(RPK_PORT) -P -K%
endef
export TODO_KAFKA_CE1

define TODO_KAFKA_CE2
echo 'test%{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "todo_in2",
  "time": "2021-07-23T12:02:23.812262+02:00[Europe/Berlin]",
  "data": {
    "description": "string",
    "done": false,
    "dueDate": {
      "due": "2022-05-08",
      "start": "2022-05-07"
    },
    "title": "string"
  }
}' | kafkacat -t todo_in -b localhost:$(RPK_PORT) -P -K%
endef
export TODO_KAFKA_CE2

define TODO_KAFKA_CE3
echo 'test%{
  "specversion": "0.3",
  "id": "21627e26-31eb-43e7-8343-92a696fd96b1",
  "source": "",
  "type": "todo_in3",
  "time": "2021-07-23T12:02:23.812262+02:00[Europe/Berlin]",
  "data": {
    "description": "string",
    "done": false,
    "dueDate": {
      "due": "2022-05-08",
      "start": "2022-05-07"
    },
    "title": "string"
  }
}' | kafkacat -t todo_in -b localhost:$(RPK_PORT) -P -K%
endef
export TODO_KAFKA_CE3

# Docker
.PHONY: docker
docker:
	@lazydocker -f docker/docker-compose-kafka.yaml

# Tools
todo-quarkus:
	@echo $$JSON_TODO_QUARKUS | bash

list:
	@curl -X 'GET' 'http://localhost:8080/todo' -H 'accept: */*' | jq .

# RPK
rpk-port:
	$(eval RPK_PORT := $(shell docker inspect --format='{{(index (index .NetworkSettings.Ports "9092/tcp") 0).HostPort}}' $(shell docker ps --format "{{.ID}}" --filter="ancestor=vectorized/redpanda:v21.5.5")))

rpk-topics: rpk-port
	rpk topic --brokers localhost:$(RPK_PORT) create todo_in --replicas 1
	rpk topic --brokers localhost:$(RPK_PORT) create todo_out --replicas 1

rpk-list: rpk-port
	rpk topic --brokers localhost:$(RPK_PORT) list

# Kafkacat
kat-send11: rpk-port
	@echo $$TODO_KAFKA_CE1_T1 | bash

kat-send22: rpk-port
	@echo $$TODO_KAFKA_CE2_T2 | bash

kat-send1: rpk-port
	@echo $$TODO_KAFKA_CE1 | bash

kat-send2: rpk-port
	@echo $$TODO_KAFKA_CE2 | bash

kat-send3: rpk-port
	@echo $$TODO_KAFKA_CE3 | bash

kat-listen-in: rpk-port
	kafkacat -t todo_in -b localhost:$(RPK_PORT) -C

kat-listen-out: rpk-port
	kafkacat -t todo_out -b localhost:$(RPK_PORT) -C

kat-test: rpk-port
	kafkacat -t todo_in -b localhost:$(RPK_PORT) -P