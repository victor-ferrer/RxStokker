package org.efevict.rxstokker.receiver;

import java.util.Calendar;

public class StockQuotation 
{
	private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss"; 
	
	private Long  id;
	
	private String stock;
	
	private Double value;
	
	private Calendar timestamp;
	
	private Double openValue;
	
	private Double highValue;

	private Double lowValue;
	
	private Double volume;
	
	
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Calendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Double getOpenValue() {
		return openValue;
	}

	public void setOpenValue(Double openValue) {
		this.openValue = openValue;
	}

	public Double getHighValue() {
		return highValue;
	}

	public void setHighValue(Double highValue) {
		this.highValue = highValue;
	}

	public Double getLowValue() {
		return lowValue;
	}

	public void setLowValue(Double lowValue) {
		this.lowValue = lowValue;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	
	@Override
	public String toString()
	{
		return String.format("[%s]@%s (%s) H(%s)/L(%s)/O(%s)", this.stock, this.value, this.timestamp.getTime().toString(), this.highValue, this.lowValue,this.openValue); 
	}

}
