package org.efevict.rxstokker.publisher;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class StockPublisher
{
	@Autowired
	EventBus eventBus;
	
	private RestTemplate restTemplate = new RestTemplate();

	private String yahooURL = "http://ichart.finance.yahoo.com/table.csv?s=%s&amp;a=1&amp;b=1&amp;c=2012&amp;g=ds";
	
	public void publishQuotes(List<String> tickers) {
		
		for (String ticker : tickers){
			
			// Queries Yahoo and returns a CSV
			String quoteResource = restTemplate.getForObject(String.format(yahooURL,ticker), String.class);
			String lines[] = quoteResource.split("\\r?\\n");
			
			// Ignore the header line
			List<String> lineList = Arrays.asList(lines).subList(1, lines.length - 1);
			
			lineList.forEach(line -> eventBus.notify("quotes", Event.wrap(line)));
		}
	}
}