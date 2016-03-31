package org.efevict.rxstokker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.efevict.rxstokker.receiver.StockQuotation;
import org.efevict.rxstokker.repository.StockQuotationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.bus.Event;
import reactor.bus.EventBus;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RxStokkerApplication.class)
public class RxStokkerApplicationTests {

	private static final Double EPSILON = 0.0001d;

	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private StockQuotationRepository stockRepo;
	
	@Test
	public void contextLoads() {
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
