package ai2017.group24;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import negotiator.Bid;
import negotiator.bidding.BidDetails;
import negotiator.boaframework.*;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;

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
	
	public RandomWalker(NegotiationSession session) {
		negotiationSession = session;
	}
	
	@Override
	public void init(NegotiationSession session, OpponentModel model, OMStrategy oms, Map<String, Double> parameters) throws Exception {
		super.init(session, model, oms, parameters);
		this.negotiationSession = session;
	}
	
	@Override
	public BidDetails determineNextBid() {
		try {
			return getRandomBid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BidDetails determineOpeningBid() {
		try {
			return getRandomBid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private BidDetails getRandomBid() throws Exception {
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		List<Issue> issues = negotiationSession.getUtilitySpace().getDomain().getIssues();
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
			bid = new Bid(negotiationSession.getUtilitySpace().getDomain(), values);
		} while (negotiationSession.getDiscountedUtility(bid, negotiationSession.getTime()) < MINIMUM_BID_UTILITY);
		
		return new BidDetails(bid, negotiationSession.getDiscountedUtility(bid, negotiationSession.getTime()));
	}

}
