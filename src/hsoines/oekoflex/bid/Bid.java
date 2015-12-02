package hsoines.oekoflex.bid;

public class Bid {
	private final float price;
	private final float amount;

	public Bid(float price, float amount){
		this.price = price;
		this.amount = amount;
	}
	float getPrice(){
		return price;
	}

	float getAmount(){
		return amount;
	}
}
