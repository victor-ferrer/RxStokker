package org.efevict.rxstokker.publisher;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.efevict.rxstokker.receiver.CSVStockQuotationConverter;
import org.efevict.rxstokker.receiver.StockQuotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockPublisher {
	@Autowired
	EventBus eventBus;

	@Autowired
	private YahooFeeder feeder;

	@Autowired
	private CSVStockQuotationConverter converter;

	public void publishQuotes(List<String> tickers) 
	{
		getQuotes(tickers).consume(quotation -> eventBus.notify("quotes", Event.wrap(quotation)));
	}
	
	public Flux<StockQuotation> getQuotes(List<String> tickers)
	{
		AtomicLong ids = new AtomicLong(0l);
		
		return 	Flux.fromIterable(tickers)
				// Get the quotes in a separate thread
				.flatMap(s -> Mono.fromCallable(() -> feeder.getCSVQuotes(s)))
				// Convert each list of raw quotes string in a new Flux<String>
				.flatMap(list -> Flux.fromIterable(list))
				// Convert the string to POJOs and notify them
				.map(stock -> converter.convertHistoricalCSVToStockQuotation(stock, ids.getAndIncrement()));	
	}
	
}