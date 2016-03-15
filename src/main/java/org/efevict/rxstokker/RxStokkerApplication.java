package org.efevict.rxstokker;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.efevict.rxstokker.publisher.StockPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import reactor.bus.EventBus;

@SpringBootApplication
public class RxStokkerApplication implements CommandLineRunner{

	@Autowired
	EventBus eventBus;
	
    @Bean
    EventBus createEventBus() {
	    return EventBus.create();
    }

    @Autowired
	private StockPublisher publisher;

	@Autowired
	private StockConsumer consumer;
	
	@Override
	public void run(String... args) throws Exception 
	{
		publisher.publishQuotes(Arrays.asList("T","REE.MC", "AAPL"));
		
		//Shutdown and clean async resources
		consumer.getSink().onComplete();
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(RxStokkerApplication.class, args);

		// FIXME
		new CountDownLatch(2000).await(15, TimeUnit.SECONDS);
	}
}
