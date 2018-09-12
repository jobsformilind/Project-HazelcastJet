package com.test.hazelcast.jet.stocks;

public class Tick {

	private String ticker;
	private float price;
	private long time = System.currentTimeMillis(); 

	public Tick(String ticker, float price) {
		this.ticker = ticker;
		this.price = price;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
