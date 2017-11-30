package ai2017.group24;

import java.util.List;
import java.util.Map;
import java.util.Random;

import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * Group 24 Best bid OM strategy
 * Adapted from boaexamplepackage/BestBid
 * 
 * @author Albert ten Napel
 * @author Nirul Hoeba
 */
public class BestBid extends OMStrategy {
	
	public BestBid() {
	}
	
	@Override
	public String getName() {
		return "Best Bid";
	}

	@Override
	public void init(NegotiationSession negotiationSession, OpponentModel model, Map<String, Double> parameters) {
		super.init(negotiationSession, model, parameters);
		this.model = model;
	}
	
	@Override
	public boolean canUpdateOM() {
		return true;
	}
	
	private boolean bidsAreAllZero(List<BidDetails> bids) {
		for(BidDetails bid : bids) {
			if(model.getBidEvaluation(bid.getBid()) > 0.0001)
				return false;
		}
		return true;
	}

	private BidDetails randomBid(List<BidDetails> bids) {
		Random r = new Random();
		return bids.get(r.nextInt(bids.size()));
	}
	
	@Override
	public BidDetails getBid(List<BidDetails> bids) {
		if(bids.size() == 1) return bids.get(0);
		if(bidsAreAllZero(bids)) return randomBid(bids);
		
		int maxIndex = 0;
		double maxEval = 0;
		int index = 0;
		for(BidDetails bid : bids) {
			double eval = model.getBidEvaluation(bid.getBid());
			if(eval > maxEval) {
				maxIndex = index;
				maxEval = eval;
			}
			index++;
		}
		return bids.get(maxIndex);
	}

}
