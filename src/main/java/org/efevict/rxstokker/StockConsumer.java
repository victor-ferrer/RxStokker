package org.efevict.rxstokker;

import static reactor.bus.selector.Selectors.$;

import javax.annotation.PostConstruct;

import org.efevict.rxstokker.receiver.CSVStockQuotationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.WorkQueueProcessor;

@Service
public class StockConsumer 
{
	@Autowired
	EventBus eventBus;
	
	@Autowired
	CSVStockQuotationConverter converter;
	
	private WorkQueueProcessor<Event<String>> sink;
	
	@PostConstruct
	public void init()
	{
		// Create the round robing processor and subscribe it to the source Flux
		sink = WorkQueueProcessor.create();
		eventBus.on($("quotes"), sink);
		
		// Convert the string to quotations and send them to console
		// TODO: Store the quotations instead of just printing them
		sink.map(Event::getData)
			.map(converter::convertHistoricalCSVToStockQuotation)
			.consume(i -> System.out.println(i));

	}

	public WorkQueueProcessor<Event<String>> getSink() {
		return sink;
	}
}
