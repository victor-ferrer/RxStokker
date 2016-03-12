package org.efevict.rxstokker.receiver;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.springframework.stereotype.Component;

@Component
public class CSVStockQuotationConverter {

	private DateTimeFormatter historicalnputFormatter;
	
	 /* Data from Yahoo Finance comes in this CSV format:
		 *  - Date
		 *  - Open
		 *  - High
		 *  - Low
		 *  - Close
		 *  - Volume
		 *  - Adjusted Close
	*/
	private static final int HISTORICAL_DATE_COLUMN = 0;
	private static final int HISTORICAL_OPEN_COLUMN = 1;
	private static final int HISTORICAL_HIGH_COLUMN = 2;
	private static final int HISTORICAL_LOW_COLUMN = 3;
	private static final int HISTORICAL_CLOSE_COLUMN = 4;
	private static final int HISTORICAL_VOLUME_COLUMN = 5;
	
	
	public CSVStockQuotationConverter() 
	{
//		LocalDateTimeParser formatterBuilder = new LocalDateTimeParser();
//		historicalnputFormatter = formatterBuilder.appendPattern("yy-MM-dd").toFormatter();
	}
	
	public StockQuotation convertHistoricalCSVToStockQuotation(String input)
	{
 		String[] chunks = input.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
 		
		if (chunks.length != 7)
		{
			throw new IllegalArgumentException("Invalid CSV stock quotation: " + input);
		}
			
		StockQuotation quotation = new StockQuotation();
//		quotation.setStock(ticker);
		quotation.setValue(Double.parseDouble(chunks[HISTORICAL_CLOSE_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		
		quotation.setHighValue(Double.parseDouble(chunks[HISTORICAL_HIGH_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		quotation.setLowValue(Double.parseDouble(chunks[HISTORICAL_LOW_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		quotation.setOpenValue(Double.parseDouble(chunks[HISTORICAL_OPEN_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		quotation.setVolume(Double.parseDouble(chunks[HISTORICAL_VOLUME_COLUMN].replaceAll("\"", "").replaceAll(",", ".") + "d"));
		
//		TemporalAccessor time = historicalnputFormatter.parse(chunks[HISTORICAL_DATE_COLUMN].replaceAll("\"", ""));
//		LocalDate date = new Loca
		
//		DateTime time = DateTime.parse(),historicalnputFormatter);
//		Calendar calendar = new GregorianCalendar(time.getYear(), time.getMonthOfYear() -1, time.getDayOfMonth(), time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
		quotation.setTimestamp(Calendar.getInstance());
		
		return quotation;
		
	}

}
