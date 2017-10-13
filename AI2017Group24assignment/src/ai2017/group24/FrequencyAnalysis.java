package ai2017.group24;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.boaframework.BOAparameter;
import negotiator.boaframework.NegotiationSession;
import negotiator.boaframework.OpponentModel;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;

/**
 * Group 24 Frequency analysis opponent model
 * Adapted from boaexamplepackage/HardHeadedFrequencyModel
 * 
 * @author Albert ten Napel
 */
public class FrequencyAnalysis extends OpponentModel {
	
	private double weightDelta;
	private int amountOfIssues;
	
	@Override
	public String getName() {
		return "Frequency analysis";
	}
	
	@Override
	public Set<BOAparameter> getParameterSpec() {
		Set<BOAparameter> set = new HashSet<BOAparameter>();
		set.add(new BOAparameter("weightDelta", 0.1,
				"How quickly issue weights are changing"));
		return set;
	}
	
	@Override
	public void init(NegotiationSession session, Map<String, Double> parameters) {
		super.init(session, parameters);
		
		negotiationSession = session;
		
		if(parameters != null && parameters.get("weightDelta") != null)
			weightDelta = parameters.get("weightDelta");
		else
			weightDelta = 0.1;
		
		initializeModel();
	}
	
	/**
	 * Initialize the model by setting all weights and values equal
	 */
	private void initializeModel() {
		opponentUtilitySpace = new AdditiveUtilitySpace(negotiationSession.getDomain());
		
		// calculate the default weight of the issues (1 / amount of issues)
		amountOfIssues = opponentUtilitySpace.getDomain().getIssues().size();
		double weight = 1.0 / (double) amountOfIssues;
		
		// for each objective
		for(Entry<Objective, Evaluator> entry : opponentUtilitySpace.getEvaluators()) {
			Objective obj = entry.getKey();
			Evaluator eval = entry.getValue();
			
			// set weight to the weight calculated above
			opponentUtilitySpace.unlock(obj);
			eval.setWeight(weight);
			
			// set all evaluations to 1 (utilities will all be equal)
			try {
				for(ValueDiscrete v : ((IssueDiscrete) obj).getValues())
					((EvaluatorDiscrete) eval).setEvaluation(v, 1);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void updateModel(Bid bid, double time) {
		BidHistory history = negotiationSession.getOpponentBidHistory();
		
		// if there are not at least two bids we cannot update the model yet
		if(history.size() < 2) return;
		
		// get the difference between the two last bids
		Bid opponentBid = history.getHistory().get(history.size() - 1).getBid();
		Bid prevOpponentBid = history.getHistory().get(history.size() - 2).getBid();
		Map<Integer, Boolean> diff = getBidDiff(prevOpponentBid, opponentBid);
		
		// count how many unchanged values there are
		int unchangedAmount = 0;
		for(Boolean changed : diff.values())
			unchangedAmount += changed? 0: 1;
		
		// new total sum of weights before normalization
		double newSum = 1 + unchangedAmount * weightDelta;
		
		// update weights of unchanged items
		for(Entry<Integer, Boolean> e : diff.entrySet()) {
			int issueNr = e.getKey();
			boolean changed = e.getValue();
			Objective issue = opponentUtilitySpace.getDomain().getObjectives().get(issueNr);
			double currentWeight = opponentUtilitySpace.getWeight(issueNr);
			// divide by newSum to normalize
			opponentUtilitySpace.setWeight(issue, changed? currentWeight / newSum: currentWeight + weightDelta / newSum);
		}
		
		// adjust the values
		try {
			for(Entry<Objective, Evaluator> e : opponentUtilitySpace.getEvaluators()) {
				int issueNr = ((IssueDiscrete) e.getKey()).getNumber();
				ValueDiscrete val = (ValueDiscrete) opponentBid.getValue(issueNr);
				EvaluatorDiscrete eval = (EvaluatorDiscrete) e.getValue();
				// add 1 to the (non-normalized) evaluation, since the value has been seen in this bid
				eval.setEvaluation(val, eval.getValue(val) + 1);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates the difference between two bids, true means the value has changed.
	 */
	private Map<Integer, Boolean> getBidDiff(Bid prevBid, Bid currentBid) {
		Map<Integer, Boolean> diff = new HashMap<Integer, Boolean>();
		try {
			// for each issue
			for(Issue iss : opponentUtilitySpace.getDomain().getIssues()) {
				int issueNr = iss.getNumber();
				// check if the last value is equal to the current value
				diff.put(
					issueNr,
					!((ValueDiscrete) prevBid.getValue(issueNr))
						.equals((ValueDiscrete) currentBid.getValue(issueNr))
				);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return diff;
	}
	
	@Override
	public double getBidEvaluation(Bid bid) {
		try {
			return opponentUtilitySpace.getUtility(bid);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
