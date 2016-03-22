package org.efevict.rxstokker;

import java.util.Arrays;

import org.efevict.rxstokker.publisher.StockPublisher;
import org.efevict.rxstokker.receiver.StockConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import reactor.bus.EventBus;

@SpringBootApplication
public class RxStokkerApplication {

	@Autowired
	EventBus eventBus;
	
    @Bean
    EventBus createEventBus() {
	    return EventBus.create();
    }

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext appContext = SpringApplication.run(RxStokkerApplication.class, args);

		appContext.getBean(StockPublisher.class).publishQuotes(Arrays.asList("T","REE.MC", "AAPL", "OHI", "MAP.MC", "SAN.MC"));	
		
		//Shutdown and clean async resources
		appContext.getBean(StockConsumer.class).getSink().onComplete();
	}
}
