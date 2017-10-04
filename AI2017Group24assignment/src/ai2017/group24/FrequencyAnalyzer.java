package ai2017.group24;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;

/**
 * Model an opponent by frequency analysis
 * 
 * !!!Only works for domains with only discrete values at the moment!!!
 * 
 * @author Albert ten Napel
 */
public class FrequencyAnalyzer implements OpponentModeller {
	private final Domain domain;
	private double delta;
	
	// issueWeights holds the estimated issue weights for the opponent
	private final HashMap<Integer, HashMap<ValueDiscrete, Double>> issueWeights;
	
	private Bid lastBid = null;
	
	public FrequencyAnalyzer(Domain domain, double delta) throws Exception {
		this.domain = domain;
		this.delta = delta;
		this.issueWeights = initializeIssueWeights(domain);
	}
	
	// initialize the issueWeights
	private HashMap<Integer, HashMap<ValueDiscrete, Double>> initializeIssueWeights(Domain domain) throws Exception {
		HashMap<Integer, HashMap<ValueDiscrete, Double>> weights = new HashMap<>();
		for(Issue issue : domain.getIssues()) {
			if(issue.getType() == ISSUETYPE.DISCRETE) {
				// give all values in the issue an equal weight
				IssueDiscrete discreteIssue = (IssueDiscrete) issue;
				double numberOfValues = discreteIssue.getNumberOfValues();
				HashMap<ValueDiscrete, Double> valueWeights = new HashMap<>();
				for(ValueDiscrete value : discreteIssue.getValues()) {
					valueWeights.put(value, 1 / numberOfValues);
				}
				weights.put(issue.getNumber(), valueWeights);
			} else throw new Exception("Only discrete issues are supported by the FrequencyAnalyzer!");
		}
		return weights;
	}
	
	// getters and setters
	public Domain getDomain() {
		return domain;
	}
	
	public double getDelta() {
		return delta;
	}
	
	public void setDelta(int delta) {
		this.delta = delta;
	}

	// handle new bids
	@Override
	public void receiveOpponentBid(Bid opponentBid) {
		if(lastBid != null) {
			for(Issue issue : opponentBid.getIssues()) {
				int issueNr = issue.getNumber();
				ValueDiscrete value = (ValueDiscrete) opponentBid.getValue(issueNr);
				if(lastBid.getValue(issueNr).equals(value)) {
					// add delta to the weight if issue value didn't change
					issueWeights.get(issueNr).put(value, issueWeights.get(issueNr).get(value) + delta);
				}
			}
			normalizeIssueWeights();
		}
		lastBid = opponentBid;
	}
	
	// normalize all weights such that the sum of the weights of each issue is 1
	private void normalizeIssueWeights() {
		for(HashMap<ValueDiscrete, Double> weights : issueWeights.values()) {
			double total = 0;
			// get total of all the weights
			for(Double weight : weights.values()) {
				total += weight;
			}
			// divide weights by total
			for(Entry<ValueDiscrete, Double> value : weights.entrySet()) {
				weights.put(value.getKey(), value.getValue() / total);
			}
		}
	}

	// estimate the utility of a bid using the current weights
	@Override
	public double estimateUtilityOfBid(Bid opponentBid) {
		double utility = 0;
		List<Issue> issues = opponentBid.getIssues();
		for(Issue issue : issues) {
			int issueNr = issue.getNumber();
			utility += issueWeights.get(issueNr).get((ValueDiscrete) opponentBid.getValue(issueNr));
		}
		return utility / issues.size();
	}
	
}
