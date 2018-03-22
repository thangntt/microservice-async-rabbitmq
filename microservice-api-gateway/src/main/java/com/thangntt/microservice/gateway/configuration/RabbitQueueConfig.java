package com.thangntt.microservice.gateway.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Component;


@Component
@Configuration
@EnableRabbit
public class RabbitQueueConfig implements RabbitListenerConfigurer {
		
	public static String exchange;
	@Value("${jsa.rabbitmq.exchange}")
	public void setExchange(String exchange) {
		RabbitQueueConfig.exchange = exchange;
	}
		
	public static String routingKey;
	@Value("${jsa.rabbitmq.routingkey}")
	public  void setRoutingKey(String routingKey) {
		RabbitQueueConfig.routingKey = routingKey;
	}
			
	public static String requestQueue;
	@Value("${jsa.rabbitmq.request.queue}")
	public void setRequestQueue(String requestQueue) {
		this.requestQueue = requestQueue;
	}
		
	public static String replyQueue;
	@Value("${jsa.rabbitmq.reply.queue}")
	public void setReplyQueue(String replyQueue) {
		RabbitQueueConfig.replyQueue = replyQueue;
	}


	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Bean
	DirectExchange exchange() {
		return new DirectExchange(exchange);
	}
	
	@Bean
	Queue requestQueue() {
		return QueueBuilder.durable(requestQueue).build();
	}
	
	@Bean
	Queue replyQueue() {
		return QueueBuilder.durable(replyQueue).build();
	}
	
	@Bean
	Binding binding() {
		return BindingBuilder.bind(requestQueue()).to(exchange()).with(routingKey);
	}
	
    @Bean
    public MappingJackson2MessageConverter jackson2Converter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        return converter;
    }
    
    @Bean
    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(jackson2Converter());
        return factory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
    }
	
	@Bean
	AsyncRabbitTemplate template() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames(replyQueue);		
		return new AsyncRabbitTemplate(rabbitTemplate, container);
	}
}

