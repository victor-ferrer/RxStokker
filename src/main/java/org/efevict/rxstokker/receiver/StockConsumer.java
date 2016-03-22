package org.efevict.rxstokker.receiver;

import static reactor.bus.selector.Selectors.$;

import javax.annotation.PostConstruct;

import org.efevict.rxstokker.repository.StockQuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.Mono;
import reactor.core.publisher.WorkQueueProcessor;

@Service
public class StockConsumer 
{
	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private StockQuotationRepository stockRepo;
	
	private WorkQueueProcessor<Event<StockQuotation>> sink;
	
	@PostConstruct
	public void init()
	{
		// Create the round robing processor and subscribe it to the source Flux
		sink = WorkQueueProcessor.create();
		eventBus.on($("quotes"), sink);
		
		sink.map(Event::getData)
			// Each save in the DB is sent to an executor that 
			.flatMap(s -> Mono.fromCallable(() ->  stockRepo.save(s)))
			.consume(i -> System.out.println(i));

	}

	public WorkQueueProcessor<Event<StockQuotation>> getSink() {
		return sink;
	}
}
