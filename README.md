# spring-jaeger

1. cd ENV  
2. docker-compose up -d  
3. run service-delay, service-b, service-a  
4. make a request at POST _[http://localhost:8080/sendMessage](http://localhost:8080/sendMessage)_  
body:  
{"recipientId": 1, "text": "Hi, Jack! How do you do?"}  

5. make a request at POST _[http://localhost:8080/sendMessage](http://localhost:8080/sendMessage)_  
body:  
{"recipientEmail": "jack@mail.com", "text": "Hi, Jack! How do you do?"} 

6. Go to the jaeger address: _[http://localhost:16686/search](http://localhost:16686/search)_
7. Select service: "A-SERVICE" and press key "Find Traces"
8. Select trace "A-SERVICE: MessageController: sendMessage"
9. and press key "Alternate Views"

#
###API SERVICE-A
#####Send message:
**POST** _[http://localhost:8080/sendMessage](http://localhost:8080/sendMessage)_

body:  
{"recipientId": 1, "text": "Hi, Jack! How do you do?"}  
    **OR**  
body:  
{"recipientEmail": "jack@mail.com", "text": "Hi, Jack! How do you do?"}  
    **OR**  
body:
{"recipientId": 1, "recipientEmail": "jack@mail.com", "text": "Hi, Jack! How do you do?"}  


###API SERVICE-B
#####Get all persons:
**GET** _[http://localhost:8081/person](http://localhost:8081/person)_

#####Get person by id:
**GET** _[http://localhost:8081/person/1/](http://localhost:8081/person/1)_

###API SERVICE-DELAY
#####random delay:
**GET** _[http://localhost:8082/delay](http://localhost:8082/delay)_


***
##### _using jaegertracing/all-in-one:_
docker run -d --rm --name=jaeger -p6831:6831/udp -p16686:16686 jaegertracing/all-in-one:1.17

docker run -d --rm --name postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=test-jaeger postgres:12.2___
