package ai2017.group24;

import negotiator.Bid;

public interface OpponentModeller {
	// receive an opponent bid to analyze and refine the model
	public void receiveOpponentBid(Bid opponentBid);
	
	// estimate the utility of a bid
	public double estimateUtilityOfBid(Bid opponentBid);
}
