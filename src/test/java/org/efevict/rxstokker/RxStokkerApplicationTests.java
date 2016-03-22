package org.efevict.rxstokker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Supplier;

import org.efevict.rxstokker.publisher.StockPublisher;
import org.efevict.rxstokker.publisher.YahooFeeder;
import org.efevict.rxstokker.receiver.StockConsumer;
import org.efevict.rxstokker.receiver.StockQuotation;
import org.efevict.rxstokker.repository.StockQuotationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.Flux;
import reactor.core.test.TestSubscriber;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RxStokkerApplication.class)
public class RxStokkerApplicationTests {

	@Autowired
	private StockPublisher publisher;
	
	@Autowired
	private StockConsumer consumer;
	
	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private StockQuotationRepository stockRepo;
	
	@Autowired
	private RestTemplate restTemplate;

	private Path sampleFilePath;
	
	@Test
	public void contextLoads() {
	}

	@Before
	public void init() throws URISyntaxException{
		URL sampleFile = RxStokkerApplicationTests.class.getResource("/sample_T.csv");
		Assert.assertNotNull("Could not find sample_T.csv",sampleFile);
		
		sampleFilePath = Paths.get(sampleFile.toURI());
	}
	
	@Test
	public void testPublisher() throws IOException {
		
		String stock = "T";
		
		// FIXME Mocked requeset still needs some work
		//MockRestServiceServer mockedServer = MockRestServiceServer.createServer(restTemplate);
		//mockedServer.expect(requestTo(String.format(YahooFeeder.YAHOO_URL,stock)));
		            //.andRespond(withSuccess(Files.readAllBytes(sampleFilePath), MediaType.TEXT_PLAIN));
		
		TestSubscriber<StockQuotation> subscriber = new TestSubscriber<>();
		
		// Ask for the quotes flux which will log whats going on
		Flux<StockQuotation> flux = publisher.getQuotes(Arrays.asList(stock)).log();
		
		subscriber.bindTo(flux)
				  // Block until onComplete()
				  .await()
					// Sample period contains 1040 entries
				  .assertValueCount(1040)
				  .assertComplete();
		
		//mockedServer.verify();
		
		
	}
	
	@Test
	public void testConsumer() throws InterruptedException 
	{
		// Create a dummy request to be sent into the EventBus
		StockQuotation stockQuotation = new StockQuotation();
		stockQuotation.setStock("dummy");
		stockQuotation.setValue(Double.MAX_VALUE);
		
		// Publish an event in the event bus and check the repo?
		eventBus.notify("quotes", Event.wrap(stockQuotation ));

		// TODO Need this?
		Thread.sleep(5000);
		
		// Check that was processed ok
		Assert.assertEquals(1,stockRepo.findByStock("dummy").size());
		
	}
}
