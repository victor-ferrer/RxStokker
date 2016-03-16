package org.efevict.rxstokker.receiver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Component;

@Component
public class CSVStockQuotationConverter {

	 /* Data from Yahoo Finance comes in this CSV format:
		 *  - Date
		 *  - Open
		 *  - High
		 *  - Low
		 *  - Close
		 *  - Volume
		 *  - Adjusted Close
	*/
	private static final int TICKER_COLUMN = 0;
	private static final int HISTORICAL_DATE_COLUMN = 1;
	private static final int HISTORICAL_OPEN_COLUMN = 2;
	private static final int HISTORICAL_HIGH_COLUMN = 3;
	private static final int HISTORICAL_LOW_COLUMN = 4;
	private static final int HISTORICAL_CLOSE_COLUMN = 5;
	private static final int HISTORICAL_VOLUME_COLUMN = 6;

	
	
	public CSVStockQuotationConverter() 
	{
	}
	
	public StockQuotation convertHistoricalCSVToStockQuotation(String input)
	{
 		String[] chunks = input.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
 		
		if (chunks.length != 8)
		{
			throw new IllegalArgumentException("Invalid CSV stock quotation: " + input);
		}
			
		StockQuotation quotation = new StockQuotation();
		quotation.setStock(chunks[TICKER_COLUMN]);
		quotation.setValue(Double.parseDouble(chunks[HISTORICAL_CLOSE_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		
		quotation.setHighValue(Double.parseDouble(chunks[HISTORICAL_HIGH_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		quotation.setLowValue(Double.parseDouble(chunks[HISTORICAL_LOW_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		quotation.setOpenValue(Double.parseDouble(chunks[HISTORICAL_OPEN_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		quotation.setVolume(Double.parseDouble(chunks[HISTORICAL_VOLUME_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		
		LocalDate time = LocalDate.parse(chunks[HISTORICAL_DATE_COLUMN].replaceAll("\"", ""), DateTimeFormatter.ISO_DATE);
		Calendar calendar = new GregorianCalendar(time.getYear(), time.getMonthValue(), time.getDayOfMonth());
		quotation.setTimestamp(calendar);
		
		return quotation;
		
	}

}
