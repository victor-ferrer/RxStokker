package org.efevict.rxstokker.publisher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class YahooFeeder {
	
	
	public static final String YAHOO_URL = "http://ichart.finance.yahoo.com/table.csv?s=%s&amp;a=1&amp;b=1&amp;c=2012&amp;g=ds";
	
	@Autowired
	private RestTemplate restTemplate;
	
	public List<String> getCSVQuotes(String ticker){
		// Queries Yahoo and returns a CSV
		String quoteResource = restTemplate.getForObject(String.format(YAHOO_URL,ticker), String.class);
		String lines[] = quoteResource.split("\\r?\\n");
		
		
		return Arrays.asList(lines).stream()
		        // Skip header
			   .skip(1)
			   .map(line -> ticker + "," + line).collect(Collectors.toList());
	}
}
