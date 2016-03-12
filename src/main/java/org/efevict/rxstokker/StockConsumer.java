package org.efevict.rxstokker;

import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import org.efevict.rxstokker.receiver.CSVStockQuotationConverter;
import org.efevict.rxstokker.receiver.StockQuotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.WorkQueueProcessor;

@Service
public class StockConsumer 
{
	@Autowired
	private CSVStockQuotationConverter quotationConverter;
	
	private WorkQueueProcessor<String> sink;
	
	@PostConstruct
	public void init()
	{
		// Create the round robing processor and subscribe it to the source Flux
		sink = WorkQueueProcessor.create();
		
		// Uncomment this and all events will be passed to all consumers
		//sink = TopicProcessor.create();
		
		// Creates a Reactive Stream from the processor (having converted the lines to Quotations)
		Flux<StockQuotation> mappedRS = sink.map(quotationConverter::convertHistoricalCSVToStockQuotation);
		
		// Sample transforamtion: Remove Quotations with values smaller than 34
		Flux<StockQuotation> filteredRS = mappedRS.filter(new Predicate<StockQuotation>() {

			@Override
			public boolean test(StockQuotation t) {
				return t.getValue() > 34.0d;
			}
		});
		
		// Each call to consume will be executed in a separated Thread
		filteredRS.consume(i -> System.out.println(Thread.currentThread() + " data=" + i));
	}

	public WorkQueueProcessor<String> getSink() {
		return sink;
	}
}
