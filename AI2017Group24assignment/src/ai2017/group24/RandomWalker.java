package ai2017.group24;

import java.util.Map;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.*;

/**
 * Group 24 Random bidder
 * Adapted from boaexamplepackage/TimeDependent_Offering
 * 
 * @author Albert ten Napel
 */
public class RandomWalker extends OfferingStrategy {
	
	final static double MINIMUM_BID_UTILITY = 0.5;

	@Override
	public String getName() {
		return "Random walker";
	}
	
	public RandomWalker() {
	}
	
	@Override
	public void init(NegotiationSession session, OpponentModel model, OMStrategy oms, Map<String, Double> parameters) throws Exception {
		super.init(session, model, oms, parameters);
		this.negotiationSession = session;
	}
	
	@Override
	public BidDetails determineNextBid() {
		try {
			return Util.getRandomBid(negotiationSession, MINIMUM_BID_UTILITY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BidDetails determineOpeningBid() {
		try {
			return Util.getRandomBid(negotiationSession, MINIMUM_BID_UTILITY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
