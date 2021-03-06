package ai2017.group24;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import negotiator.Bid;
import negotiator.BidHistory;
import negotiator.Domain;
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
 * Group 24 Frequency analysis opponent model for two opponents!
 * Adapted from boaexamplepackage/HardHeadedFrequencyModel
 * 
 * !!!Only works with discrete values!!!
 * 
 * @author Albert ten Napel
 */
public class FrequencyAnalysis2 extends OpponentModel {
	
	private double weightDelta = 0.1;
	private int amountOfIssues;
	
	private AdditiveUtilitySpace opponentUtilitySpace2;
	// private final PartyProfile1Measure measure;
	
	public FrequencyAnalysis2() {
		// measure = new PartyProfile1Measure();
	}
	
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
		
		initializeModel();
	}
	
	/**
	 * Initialize the model by setting all weights and values equal
	 */
	private void initializeModel() {
		try {
			Domain domain = negotiationSession.getDomain();
			opponentUtilitySpace = new AdditiveUtilitySpace(domain);
			opponentUtilitySpace2 = new AdditiveUtilitySpace(domain);
			
			// calculate the default weight of the issues (1 / amount of issues)
			amountOfIssues = domain.getIssues().size();
			double weight = amountOfIssues == 0? 0: 1.0 / (double) amountOfIssues;
			
			// for each objective
			for(Issue obj : domain.getIssues()) {
				IssueDiscrete is = (IssueDiscrete) obj;
				EvaluatorDiscrete eval = new EvaluatorDiscrete();
				opponentUtilitySpace.addEvaluator(is, eval);
				EvaluatorDiscrete eval2 = new EvaluatorDiscrete();
				opponentUtilitySpace2.addEvaluator(is, eval2);
				
				// set weight to the weight calculated above
				opponentUtilitySpace.unlock(is);
				opponentUtilitySpace2.unlock(is);
				eval.setWeight(weight);
				eval2.setWeight(weight);
				
				// set all evaluations equal
				for(ValueDiscrete val : is.getValues()) {
					eval.setEvaluation(val, 0);
					eval2.setEvaluation(val, 0);
				}
			}
		} catch(Exception e) {
			System.out.println("FrequencyAnalysis initialization exception");
			e.printStackTrace();
		}
	}

	private boolean first = false; 
	@Override
	public void updateModel(Bid bid, double time) {
		BidHistory history = negotiationSession.getOpponentBidHistory();
		
		first = !first;

		// if there are not at least three or four bids we cannot update the model yet
		if(history.size() < 5) return;
		
		// get the difference between the two last bids
		Bid opponentBid = first? history.getHistory().get(history.size() - 2).getBid(): history.getHistory().get(history.size() - 3).getBid();
		Bid prevOpponentBid = first? history.getHistory().get(history.size() - 4).getBid(): history.getHistory().get(history.size() - 5).getBid();
		Map<Integer, Boolean> diff = getBidDiff(prevOpponentBid, opponentBid);
		
		AdditiveUtilitySpace space = first? opponentUtilitySpace: opponentUtilitySpace2;
		
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
			Objective issue = space.getDomain().getObjectives().get(issueNr);
			double currentWeight = space.getWeight(issueNr);
			// divide by newSum to normalize
			space.setWeight(issue, changed? currentWeight / newSum: currentWeight + weightDelta / newSum);
		}
		
		// adjust the values
		try {
			for(Entry<Objective, Evaluator> e : space.getEvaluators()) {
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
			// measure.differenceFromPartyProfile1(opponentUtilitySpace, negotiationSession.getTime());
			return opponentUtilitySpace.getUtility(bid);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public double getBidEvaluation2(Bid bid) {
		try {
			return opponentUtilitySpace2.getUtility(bid);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
