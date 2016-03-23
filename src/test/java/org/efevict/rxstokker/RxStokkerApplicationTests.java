package org.efevict.rxstokker;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.efevict.rxstokker.publisher.StockPublisher;
import org.efevict.rxstokker.publisher.YahooFeeder;
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
import org.springframework.web.client.RestTemplate;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.Flux;
import reactor.core.test.TestSubscriber;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RxStokkerApplication.class)
public class RxStokkerApplicationTests {

	private static final Double EPSILON = 0.0001d;

	@Autowired
	private StockPublisher publisher;
	
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
		URL sampleFile = RxStokkerApplicationTests.class.getResource("/response.txt");
		Assert.assertNotNull("Could not find response.txt",sampleFile);
		
		sampleFilePath = Paths.get(sampleFile.toURI());
	}
	
	@Test
	public void testPublisher() throws IOException {
		
		String stock = "T";
		
		MockRestServiceServer mockedServer = MockRestServiceServer.createServer(restTemplate);
		mockedServer.expect(requestTo(String.format(YahooFeeder.YAHOO_URL,stock)))
		            .andRespond(withSuccess(Files.readAllBytes(sampleFilePath), MediaType.TEXT_HTML));
		
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
	public void testConsumer() throws InterruptedException 
	{
		// Create a dummy request to be sent into the EventBus
		StockQuotation stockQuotation = new StockQuotation();
		stockQuotation.setStock("dummy");
		stockQuotation.setValue(Double.MAX_VALUE);
		stockQuotation.setHighValue(Double.MAX_VALUE);
		stockQuotation.setLowValue(Double.MAX_VALUE);
		stockQuotation.setVolume(Double.MAX_VALUE);
		
		// Publish an event in the event bus and check the repo?
		eventBus.notify("quotes", Event.wrap(stockQuotation ));

		// TODO Need this?
		Thread.sleep(1000);
		
		// Check that was processed ok
		List<StockQuotation> stocks = stockRepo.findByStock("dummy");
		assertEquals(1,stocks.size());
		
		assertEquals(Double.MAX_VALUE,stocks.get(0).getHighValue(), EPSILON);
		assertEquals(Double.MAX_VALUE,stocks.get(0).getLowValue(), EPSILON);
		assertEquals(Double.MAX_VALUE,stocks.get(0).getVolume(), EPSILON);
		assertEquals(Double.MAX_VALUE,stocks.get(0).getValue(), EPSILON);
		assertEquals("dummy", stocks.get(0).getStock());
	}
	
}
