package org.efevict.rxstokker.publisher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class StockPublisher implements Publisher<String>
{
	private RestTemplate restTemplate = new RestTemplate();

	private Set<Subscriber<? super String>> subscribers = new HashSet<>();
	
	private String yahooURL = "http://ichart.finance.yahoo.com/table.csv?s=%s&amp;a=1&amp;b=1&amp;c=2015&amp;g=ds";
	
	public void publishQuotes(String ticker) {
		
		// Queries Yahoo and returns a CSV
		String quoteResource = restTemplate.getForObject(String.format(yahooURL,ticker), String.class);
		String lines[] = quoteResource.split("\\r?\\n");

		// Ignore the header line
		List<String> lineList = Arrays.asList(lines).subList(1, lines.length - 1);

		for (Subscriber<? super String> subscriber : subscribers) {
			lineList.forEach(line -> subscriber.onNext(line));
		}

	}

	@Override
	public void subscribe(Subscriber<? super String> subscriber) {
		subscribers.add(subscriber);
		
	}
}