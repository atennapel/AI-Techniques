package ai2017.group24;

import java.util.Map;

import negotiator.BidHistory;
import negotiator.boaframework.*;

/**
 * Group 24 Acceptance strategy
 * Adapted from boaexamplepackage/AC_Next
 * 
 * @author Luc Hogervorst
 */
public class ACCombiMAXW extends AcceptanceStrategy {

	private double timeConstant = 0.99;
	
	public ACCombiMAXW() {
	}
	
	public ACCombiMAXW(double timeConstant) {
		this.timeConstant = timeConstant;
	}
	
	@Override
	public String getName() {
		return "Group 24 AC Combi(MAXW)";
	}
	
	@Override 
	public void init(NegotiationSession session, OfferingStrategy strategy, OpponentModel opponentModel, Map<String, Double> parameters) throws Exception {
		super.init(session, strategy, opponentModel, parameters);
		this.negotiationSession = session;
		this.offeringStrategy = strategy;
	}
	
	@Override
	public Actions determineAcceptability() {	
		double nextBidUtil = offeringStrategy.getNextBid().getMyUndiscountedUtil();
		double opponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails().getMyUndiscountedUtil();
		
		// only accepts if the util of the opponents bid is greater or equal to the util of my next bid
		if(opponentBidUtil >= nextBidUtil)
			return Actions.Accept;
		
		double time = negotiationSession.getTime(); //value between [0,1]
		
		if(time>timeConstant) {
			double remTime = 1-time; //time that is still left
			BidHistory prevBids = negotiationSession.getOpponentBidHistory().filterBetweenTime(time-remTime, time);
			//int BidsInHist = prevBids.size();
			double maxBid = prevBids.getBestBidDetails().getMyUndiscountedUtil();
			
			// accepts if opponents bid is higher than the maximum of his previous bids in specified time frame
			if(opponentBidUtil >= maxBid)
				return Actions.Accept;
		}	
		
		return Actions.Reject;
	}
}
