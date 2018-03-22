package com.thangntt.microservice.consumer.listentqueue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.thangntt.microservice.object.message.DataQueueRequest;
import com.thangntt.microservice.object.message.DataQueueResponse;


@Component
public class ListentQueueRequest {

	
	@RabbitListener(queues = "gw.request")
	public DataQueueResponse process(DataQueueRequest request) {
		System.out.println("Received: id: " + request.getId() + " - data: " + request.getData());
		return new DataQueueResponse(request.getId(), request.getData() + " response");
	}
}
