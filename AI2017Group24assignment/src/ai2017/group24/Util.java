package ai2017.group24;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import misc.Range;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.boaframework.OutcomeSpace;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.AbstractUtilitySpace;

public class Util {
	
	/**
	 * Get the bid with the highest utility from some utility space
	 */
	static public BidDetails getMaximumBid(AbstractUtilitySpace utilitySpace) {
		return new OutcomeSpace(utilitySpace).getMaxBidPossible();
	}
	
	/**
	 * Get the bid with the lowest utility from some utility space
	 */
	static public BidDetails getMinimumBid(AbstractUtilitySpace utilitySpace) {
		return new OutcomeSpace(utilitySpace).getMinBidPossible();
	}
	
	/**
	 * Get the bid with the highest utility from some session
	 */
	static public BidDetails getMaximumBid(NegotiationSession session) {
		return getMaximumBid(session.getUtilitySpace());
	}
	
	/**
	 * Get the bid with the lowest utility from some session
	 */
	static public BidDetails getMinimumBid(NegotiationSession session) {
		return getMinimumBid(session.getUtilitySpace());
	}
	
	/**
	 * Get the bid with the highest utility from some opponentmodel
	 */
	static public BidDetails getMaximumBid(OpponentModel model) {
		return getMaximumBid(model.getOpponentUtilitySpace());
	}
	
	/**
	 * Get the bid with the lowest utility from some opponentmodel
	 */
	static public BidDetails getMinimumBid(OpponentModel model) {
		return getMinimumBid(model.getOpponentUtilitySpace());
	}
	
	/**
	 * Get a bid from an utility space that is closest to the utility given.
	 */
	static public BidDetails getClosestBid(AbstractUtilitySpace utilitySpace, double utility) {
		return new OutcomeSpace(utilitySpace).getBidNearUtility(utility);
	}
	
	/**
	 * Get all bids from an utility space that have utilities within a certain range.
	 */
	static public List<BidDetails> getBidsInRange(AbstractUtilitySpace utilitySpace, double minimumUtility, double maximumUtility) {
		return new OutcomeSpace(utilitySpace).getBidsinRange(new Range(minimumUtility, maximumUtility));
	}
	
	/**
	 * Get a bid from the session that is closest to the utility given.
	 */
	static public BidDetails getClosestBid(NegotiationSession session, double utility) {
		return getClosestBid(session.getUtilitySpace(), utility);
	}
	
	/**
	 * Get all bids that have utilities within a certain range.
	 */
	static public List<BidDetails> getBidsInRange(NegotiationSession session, double minimumUtility, double maximumUtility) {
		return getBidsInRange(session.getUtilitySpace(), minimumUtility, maximumUtility);
	}
	
	/**
	 * Get a bid from an opponent model that is closest to the utility given.
	 */
	static public BidDetails getClosestBid(OpponentModel model, double utility) {
		return getClosestBid(model.getOpponentUtilitySpace(), utility);
	}
	
	/**
	 * Get all bids from an opponent model that have utilities within a certain range.
	 */
	static public List<BidDetails> getBidsInRange(OpponentModel model, double minimumUtility, double maximumUtility) {
		return getBidsInRange(model.getOpponentUtilitySpace(), minimumUtility, maximumUtility);
	}
	
	/**
	 * Get a random bid in utility space with at least a minimum utility.
	 * 
	 * Adapted from the example boa agent
	 */
	static public BidDetails getRandomBid(AbstractUtilitySpace utilitySpace, double minimumBidUtility, double time) throws Exception {
		Domain domain = utilitySpace.getDomain();
		
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		List<Issue> issues = domain.getIssues();
		Random randomnr = new Random();

		Bid bid = null;
		do {
			for (Issue lIssue : issues) {
				switch (lIssue.getType()) {
				case DISCRETE:
					IssueDiscrete lIssueDiscrete = (IssueDiscrete) lIssue;
					int optionIndex = randomnr.nextInt(lIssueDiscrete.getNumberOfValues());
					values.put(lIssue.getNumber(), lIssueDiscrete.getValue(optionIndex));
					break;
				case REAL:
					IssueReal lIssueReal = (IssueReal) lIssue;
					int optionInd = randomnr.nextInt(lIssueReal.getNumberOfDiscretizationSteps() - 1);
					values.put(lIssueReal.getNumber(),
							new ValueReal(lIssueReal.getLowerBound()
									+ (lIssueReal.getUpperBound() - lIssueReal.getLowerBound()) * (double) (optionInd)
											/ (double) (lIssueReal.getNumberOfDiscretizationSteps())));
					break;
				case INTEGER:
					IssueInteger lIssueInteger = (IssueInteger) lIssue;
					int optionIndex2 = lIssueInteger.getLowerBound()
							+ randomnr.nextInt(lIssueInteger.getUpperBound() - lIssueInteger.getLowerBound());
					values.put(lIssueInteger.getNumber(), new ValueInteger(optionIndex2));
					break;
				default:
					throw new Exception("issue type " + lIssue.getType() + " not supported by SimpleAgent2");
				}
			}
			bid = new Bid(domain, values);
		} while (utilitySpace.getUtilityWithDiscount(bid, time) < minimumBidUtility);
		
		return new BidDetails(bid, utilitySpace.getUtilityWithDiscount(bid, time));
	}
	
	/**
	 * Get a random bid in a negotiation session with at least a minimum utility.
	 */
	static public BidDetails getRandomBid(NegotiationSession session, double minimumBidUtility) throws Exception {
		return getRandomBid(session.getUtilitySpace(), minimumBidUtility, session.getTime());
	}
	
	/**
	 * Get a random bid in a opponent model with at least a minimum utility.
	 */
	static public BidDetails getRandomBid(OpponentModel model, double minimumBidUtility, double time) throws Exception {
		return getRandomBid(model.getOpponentUtilitySpace(), minimumBidUtility, time);
	}
	
}
