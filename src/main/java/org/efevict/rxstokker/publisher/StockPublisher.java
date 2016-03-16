package org.efevict.rxstokker.publisher;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.Flux;

@Service
public class StockPublisher
{
	@Autowired
	EventBus eventBus;
	
	@Autowired
	private YahooFeeder feeder;
	
	public void publishQuotes(List<String> tickers) {
		
		Flux.fromIterable(tickers)
		    .map(ticker -> feeder.getCSVQuotes(ticker))
		    .consume(list -> list.forEach(line -> eventBus.notify("quotes",Event.wrap(line))));

	}
}