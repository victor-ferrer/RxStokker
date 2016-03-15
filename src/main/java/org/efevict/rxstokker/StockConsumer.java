package org.efevict.rxstokker;

import static reactor.bus.selector.Selectors.$;

import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import org.efevict.rxstokker.receiver.CSVStockQuotationConverter;
import org.efevict.rxstokker.receiver.StockQuotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.WorkQueueProcessor;

@Service
public class StockConsumer 
{
	@Autowired
	private CSVStockQuotationConverter quotationConverter;
	
	@Autowired
	EventBus eventBus;
	
	private WorkQueueProcessor<Event<String>> sink;
	
	@PostConstruct
	public void init()
	{
		// Create the round robing processor and subscribe it to the source Flux
		sink = WorkQueueProcessor.create();
		
		eventBus.on($("quotes"), sink);
		
		// Uncomment this and all events will be passed to all consumers
		//sink = TopicProcessor.create();
		
		// Creates a Reactive Stream from the processor (having converted the lines to Quotations)
		sink.map(Event::getData)
		    .map(quotationConverter::convertHistoricalCSVToStockQuotation)
			.filter(new Predicate<StockQuotation>() {

			@Override
			public boolean test(StockQuotation t) {
				return t.getValue() > 34.0d;
			}
		}).consume(i -> System.out.println(Thread.currentThread() + " data=" + i));
	}

	public WorkQueueProcessor<Event<String>> getSink() {
		return sink;
	}
}
