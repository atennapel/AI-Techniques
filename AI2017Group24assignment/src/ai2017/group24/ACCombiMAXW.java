package ai2017.group24;

import java.util.Map;

import negotiator.boaframework.*;

/**
 * Group 24 Acceptance strategy
 * Adapted from boaexamplepackage/AC_Next
 * 
 * @author Albert ten Napel
 */
public class ACCombiMAXW extends AcceptanceStrategy {

	public ACCombiMAXW() {
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
		
		return Actions.Reject;
	}
}
