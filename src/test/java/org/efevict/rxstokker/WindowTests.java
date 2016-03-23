package org.efevict.rxstokker;

import static reactor.bus.selector.Selectors.$;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.efevict.rxstokker.publisher.StockPublisher;
import org.efevict.rxstokker.receiver.StockQuotation;
import org.efevict.rxstokker.repository.StockQuotationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.WorkQueueProcessor;
import reactor.core.test.TestSubscriber;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RxStokkerApplication.class)
public class WindowTests 
{
	@Autowired
	private StockPublisher publisher;
	
	@Test
	public void testWindow() {
		
	    Function<StockQuotation, Mono<StockQuotation>> closingFunction = new Function<StockQuotation, Mono<StockQuotation>>(){

			@Override
			public Mono<StockQuotation> apply(StockQuotation t) {
				return Mono.delay(50l).map(x -> new StockQuotation());
			}
	    	
	    };
		
		// Ask for the quotes flux which will log whats going on
		Flux<StockQuotation> stockFlux = publisher.getQuotes(Arrays.asList("T"));	
		
		Flux<Flux<StockQuotation>> slidingWindows = stockFlux.window(stockFlux,closingFunction);
		
	    slidingWindows.consume(flux -> flux.map(x -> x.getValue().toString())
	    			  					   .reduceWith(()-> "=",(x,y) -> x + "-" + y)
	    			  					   .consume(result -> System.out.println("Reduced:" + result))
	    			  		  );
		
		TestSubscriber<Flux<StockQuotation>> subscriber = new TestSubscriber<>();
		
		subscriber.bindTo(slidingWindows).await().assertComplete();
	}
}