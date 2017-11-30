package ai2017.group24;

import java.util.Map;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.*;

/**
 * Get the best bid for an opponent selected from a generated list of bids.
 * 
 * @author Albert ten Napel
 * @author Nirul Hoeba
 */
public class BestBidForOpponent extends OfferingStrategy {
	
	private double minUtility = 0.8;
	private double maxUtility = 1;
	
	@Override
	public String getName() {
		return "BestBidForOpponent";
	}
	
	public BestBidForOpponent() {
	}
	
	public BestBidForOpponent(double minUtility, double maxUtility) {
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
		/*
		 * Get the bids we can make that have an utility between minUtility and maxUtility and select one according to the OM strategy.
		 */
		return omStrategy.getBid(Util.getBidsInRange(negotiationSession, minUtility, maxUtility));
	}

}
