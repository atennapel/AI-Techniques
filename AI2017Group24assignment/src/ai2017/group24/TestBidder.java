package ai2017.group24;

import java.util.Map;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.*;

public class TestBidder extends OfferingStrategy {
	
	@Override
	public String getName() {
		return "TestBidder";
	}
	
	public TestBidder() {
	}
	
	@Override
	public void init(NegotiationSession session, OpponentModel model, OMStrategy oms, Map<String, Double> parameters) throws Exception {
		super.init(session, model, oms, parameters);
		negotiationSession = session;
		opponentModel = model;
		omStrategy = oms;
	}
	
	@Override
	public BidDetails determineNextBid() {
		try {
			BidDetails bid = getNewBid();
			System.out.println(bid);
			return bid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BidDetails determineOpeningBid() {
		try {
			return getNewBid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private BidDetails getNewBid() throws Exception {
		return omStrategy.getBid(Util.getBidsInRange(negotiationSession, 0.5, 1));	
	}	

}
