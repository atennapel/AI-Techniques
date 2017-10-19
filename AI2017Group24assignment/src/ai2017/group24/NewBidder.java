package ai2017.group24;

import java.util.Map;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.*;

/**
 * Group 24 NewBidder
 * Adapted from src/RandomWalker.java
 * 
 * @author Nirul Hoeba
 */

public class NewBidder extends OfferingStrategy {

	private SortedOutcomeSpace outcomespace;
	
	public NewBidder() {
	}
	
	@Override
	public String getName() {
		return "NewBidder";
	}
	
	@Override
	public void init(NegotiationSession session, OpponentModel model, OMStrategy oms, Map<String, Double> parameters) throws Exception {
		super.init(session, model, oms, parameters);
		
		this.negotiationSession = session;
		this.opponentModel = model;
		this.omStrategy = oms;
		
		outcomespace = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(outcomespace);
	}
	
	@Override
	public BidDetails determineNextBid() {
		try {
			return getNewBid();
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
		double utilityGoal = 0;
		nextBid = omStrategy.getBid(outcomespace, utilityGoal);
		return new BidDetails(null,0);
	}
				

}
