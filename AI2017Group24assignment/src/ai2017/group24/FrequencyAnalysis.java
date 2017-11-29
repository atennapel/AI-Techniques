package ai2017.group24;

import java.util.ArrayList;
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
 * Group 24 Frequency analysis opponent model
 * Adapted from boaexamplepackage/HardHeadedFrequencyModel
 * 
 * !!!Only works with discrete values!!!
 * 
 * @author Albert ten Napel
 */
public class FrequencyAnalysis extends OpponentModel {
	
	private double weightDelta = 0.1;
	private int amountOfIssues;
	
	public FrequencyAnalysis() {
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
			
			// calculate the default weight of the issues (1 / amount of issues)
			amountOfIssues = domain.getIssues().size();
			double weight = amountOfIssues == 0? 0: 1.0 / (double) amountOfIssues;
			
			// for each objective
			for(Issue obj : domain.getIssues()) {
				IssueDiscrete is = (IssueDiscrete) obj;
				EvaluatorDiscrete eval = new EvaluatorDiscrete();
				opponentUtilitySpace.addEvaluator(is, eval);
				
				// set weight to the weight calculated above
				opponentUtilitySpace.unlock(is);
				eval.setWeight(weight);
				
				// set all evaluations equal
				for(ValueDiscrete val : is.getValues()) {
					eval.setEvaluation(val, 1);
				}
			}
		} catch(Exception e) {
			System.out.println("FrequencyAnalysis initialization exception");
			e.printStackTrace();
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
			differenceFromPartyProfile1(opponentUtilitySpace);
			return opponentUtilitySpace.getUtility(bid);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Party Profile 1
	 */
	private static HashMap<String, Double> weights = new HashMap<String, Double>();
	static {
		weights.put("Food", 0.19);
		weights.put("Invitations", 0.05);
		weights.put("Cleanup", 0.10);
		weights.put("Music", 0.19);
		weights.put("Location", 0.19);
		weights.put("Drinks", 0.28);
	}
	private static HashMap<String, HashMap<String, Double>> evals = new HashMap<String, HashMap<String, Double>>();
	static {
		HashMap<String, Double> food = new HashMap<String, Double>();
		food.put("Finger-Food", 2.0/3.0);
		food.put("Chips and Nuts", 1.0);
		food.put("Handmade Food", 2.0/3.0);
		food.put("Catering", 1.0/3.0);
		evals.put("Food", food);
		
		HashMap<String, Double> inv = new HashMap<String, Double>();
		inv.put("Plain", 0.25);
		inv.put("Custom, Handmade", 1.0);
		inv.put("Custom, Printed", 0.5);
		inv.put("Photo", 0.75);
		evals.put("Invitations", inv);
		
		HashMap<String, Double> cleanup = new HashMap<String, Double>();
		cleanup.put("Specialized Materials", 1.0);
		cleanup.put("Water and Soap", 2.0/3.0);
		cleanup.put("Hired Help", 1.0/3.0);
		cleanup.put("Special Equiment", 1.0/3.0);
		evals.put("Cleanup", cleanup);
		
		HashMap<String, Double> music = new HashMap<String, Double>();
		music.put("Band", 1.0/3.0);
		music.put("DJ", 1.0);
		music.put("MP3", 2.0/3.0);
		evals.put("Music", music);
		
		HashMap<String, Double> loc = new HashMap<String, Double>();
		loc.put("Your Dorm", 0.25);
		loc.put("Party Tent", 0.5);
		loc.put("Ballroom", 0.75);
		loc.put("Party Room", 1.0);
		evals.put("Location", loc);
		
		HashMap<String, Double> drinks = new HashMap<String, Double>();
		drinks.put("Beer Only", 1.0);
		drinks.put("Catering", 1.0/3.0);
		drinks.put("Non-Alcoholic", 1.0/3.0);
		drinks.put("Handmade Cocktails", 2.0/3.0);
		evals.put("Drinks", drinks);
	}
	
	private double rme(ArrayList<Double> l) {
		double sum = 0;
		for(double v : l) sum += v * v;
		return Math.sqrt(sum / l.size());
	}
	
	private void differenceFromPartyProfile1(AdditiveUtilitySpace space) throws Exception {
		ArrayList<Double> wlist = new ArrayList<Double>();
		ArrayList<Double> vlist = new ArrayList<Double>();
		for(Entry<Objective, Evaluator> entry : space.getEvaluators()) {
			IssueDiscrete obj = (IssueDiscrete) entry.getKey();
			EvaluatorDiscrete ev = (EvaluatorDiscrete) entry.getValue();
			wlist.add(ev.getWeight() - weights.get(obj.getName()));
			for(ValueDiscrete val : obj.getValues())
				vlist.add(ev.getEvaluation(val) - evals.get(obj.getName()).get(val.getValue()));
		}
		System.out.printf("%.4f %.4f %.4f\n", negotiationSession.getTime(), rme(wlist), rme(vlist));
	}

}
