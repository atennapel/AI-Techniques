package ai2017.group24;

import java.util.Map;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.*;

/**
 * Get the best bid for an opponent selected from a generated list of bids.
 * 
 * @author Albert ten Napel
 */
public class BestBidForOpponentStubborn extends OfferingStrategy {
	
	private double firstN = 5;
	private double minUtility = 0.8;
	private double maxUtility = 1;
	
	@Override
	public String getName() {
		return "TestBidder";
	}
	
	public BestBidForOpponentStubborn() {
	}
	
	public BestBidForOpponentStubborn(double minUtility, double maxUtility) {
		this.minUtility = minUtility;
		this.maxUtility = maxUtility;
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
		int amountOfBidsDone = negotiationSession.getOwnBidHistory().size();
		if(amountOfBidsDone < firstN) {
			return omStrategy.getBid(Util.getBidsInRange(negotiationSession, 1, 1));
		} else {		
			/*
			 * Get the bids we can make that have an utility between minUtility and maxUtility and select one according to the OM strategy.
			 */
			return omStrategy.getBid(Util.getBidsInRange(negotiationSession, minUtility, maxUtility));
		}
	}

}
