package org.efevict.rxstokker;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.efevict.rxstokker.publisher.StockPublisher;
import org.efevict.rxstokker.publisher.YahooFeeder;
import org.efevict.rxstokker.receiver.StockQuotation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import reactor.core.publisher.Flux;
import reactor.core.test.TestSubscriber;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RxStokkerApplication.class)
public class PublisherTests {

	@Autowired
	private StockPublisher publisher;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private Path valid_response_path, invalid_response_path;

	@Before
	public void init() throws URISyntaxException{
		URL sampleFile = PublisherTests.class.getResource("/response.txt");
		Assert.assertNotNull("Could not find response.txt",sampleFile);
		
		valid_response_path = Paths.get(sampleFile.toURI());
		
		URL sampleFileKO = PublisherTests.class.getResource("/responseKO.txt");
		Assert.assertNotNull("Could not find responseKO.txt",sampleFileKO);
		
		invalid_response_path = Paths.get(sampleFileKO.toURI());
	}
	
	@Test
	public void testPublisherWithGoodPayload() throws IOException {
		
		String stock = "T";
		
		MockRestServiceServer mockedServer = MockRestServiceServer.createServer(restTemplate);
		mockedServer.expect(requestTo(String.format(YahooFeeder.YAHOO_URL,stock)))
		            .andRespond(withSuccess(Files.readAllBytes(valid_response_path), MediaType.TEXT_HTML));
		
		TestSubscriber<StockQuotation> subscriber = new TestSubscriber<>();
		
		// Ask for the quotes flux which will log whats going on
		Flux<StockQuotation> flux = publisher.getQuotes(Arrays.asList(stock)).log();
		
		subscriber.bindTo(flux)
				  // Block until onComplete()
				  .await()
					// Sample period contains 36 entries
				  .assertValueCount(36)
				  .assertComplete();
		
		mockedServer.verify();
	}
	
	@Test
	public void testPublisherWithInvalidPayload() throws IOException {
		
		String stock = "BOGUS_QUOTE";
		
		MockRestServiceServer mockedServer = MockRestServiceServer.createServer(restTemplate);
		mockedServer.expect(requestTo(String.format(YahooFeeder.YAHOO_URL,stock)))
		            .andRespond(withSuccess(Files.readAllBytes(invalid_response_path), MediaType.TEXT_HTML));
		
		TestSubscriber<StockQuotation> subscriber = new TestSubscriber<>();
		
		// Ask for the quotes flux which will log whats going on
		Flux<StockQuotation> flux = publisher.getQuotes(Arrays.asList(stock)).log();
		
		subscriber.bindTo(flux)
				  // Block until onComplete()
				  .await()
					// We should receive 36 elements (2 of them invalid)
				  .assertValueCount(34)
				  .assertComplete();
		
		mockedServer.verify();
	}
	
}
