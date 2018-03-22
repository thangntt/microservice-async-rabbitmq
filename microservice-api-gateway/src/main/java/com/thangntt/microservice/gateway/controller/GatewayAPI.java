package com.thangntt.microservice.gateway.controller;


import java.util.Random;
import java.util.concurrent.ExecutionException;


import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate.RabbitConverterFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.thangntt.microservice.gateway.configuration.RabbitQueueConfig;
import com.thangntt.microservice.object.message.DataQueueRequest;
import com.thangntt.microservice.object.message.DataQueueResponse;


@RestController
public class GatewayAPI {

	@Autowired
	private AsyncRabbitTemplate asyncRabbitTemplate;
	
	@RequestMapping(value = {"/test"}, method = RequestMethod.GET)
	public String getData() {
		
		System.out.println("======================= Begin ======================");
		
		
		DataQueueRequest dataQueue = new DataQueueRequest(new Random().nextInt(20), "testqueue"); 
		RabbitConverterFuture<DataQueueResponse> future = send(dataQueue);
		
		//here can doSomeThing()
		System.out.println("doSomeThing1()");		
		
		//await future success
		try {
			DataQueueResponse  dataQueueResponse =  future.get();
			if (dataQueueResponse.getId() == dataQueue.getId())
				System.out.println("Success Response with Id: " 
						+ dataQueueResponse.getId());
			else System.out.println("Not Success Response with Id: " 
					+ dataQueueResponse.getId());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("======================= Done =======================");
		
		return "{\"api\": \"done\"}";
					
	}
	
	@Scheduled(fixedDelay = 1000L)
	public RabbitConverterFuture<DataQueueResponse> send(DataQueueRequest dataQueue) {
					
        RabbitConverterFuture<DataQueueResponse> future =
                asyncRabbitTemplate.convertSendAndReceive(RabbitQueueConfig.exchange, 
                		RabbitQueueConfig.routingKey, dataQueue);
        
        System.out.println("Thread: " + Thread.currentThread().getName() + " Id: " + dataQueue.getId());      		
        
        return future;
	}
}

