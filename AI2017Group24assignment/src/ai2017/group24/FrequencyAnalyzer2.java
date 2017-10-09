package ai2017.group24;

import java.util.HashMap;

import negotiator.Bid;
import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.ValueDiscrete;

/**
 * Model an opponent by frequency analysis
 * 
 * !!!Only works for domains with only discrete values at the moment!!!
 * 
 * @author Albert ten Napel
 */
public class FrequencyAnalyzer2 implements OpponentModeller {
	
	// for every issue the frequencies of the seen values
	private class IssueData {
		// frequency of most seen value
		public int max;
		
		// frequencies of all values
		public HashMap<ValueDiscrete, Integer> frequencies;
		
		public IssueData() {
			max = Integer.MIN_VALUE;
			frequencies = new HashMap<ValueDiscrete, Integer>();
		}
		
		public void addValue(ValueDiscrete value) {
			// initialize frequency to 0
			if(!frequencies.containsKey(value)) {
				frequencies.put(value, 0);
			}
			
			// add 1 to the frequency
			int newfreq = frequencies.get(value) + 1;
			frequencies.put(value, newfreq);
			
			// if newfrequency is greater than the maximum, replace it
			if(newfreq > max) {
				max = newfreq;
			}
		}
		
		public double getEvaluation(ValueDiscrete value) {
			// estimate evaluation value by dividing the frequency by the maximum frequency
			if(frequencies.containsKey(value)) {
				return frequencies.get(value) / max;
			}
			return 0;
		}
	}
	
	private HashMap<Integer, IssueData> issueFrequencies;
	
	
	public FrequencyAnalyzer2() throws Exception {
		issueFrequencies = new HashMap<Integer, IssueData>();
	}

	@Override
	public void receiveOpponentBid(Bid opponentBid) {
		for(Issue issue : opponentBid.getIssues()) {
			// for every discrete issue
			if(issue.getType() == ISSUETYPE.DISCRETE) {
				int issueNr = issue.getNumber();
				// initialize issue data
				if(!issueFrequencies.containsKey(issueNr)) {
					issueFrequencies.put(issueNr, new IssueData());
				}
				// add the value to the issuedata
				IssueData data = issueFrequencies.get(issueNr);
				ValueDiscrete val = (ValueDiscrete) opponentBid.getValue(issueNr);
				data.addValue(val);
			}
		}
	}

	@Override
	public double estimateUtilityOfBid(Bid opponentBid) {
		double utility = 0;
		for(Issue issue : opponentBid.getIssues()) {
			int issueNr = issue.getNumber();
			// if we have data of this issue
			if(issueFrequencies.containsKey(issueNr)) {
				IssueData data = issueFrequencies.get(issueNr);
				// add the estimated evaluation value to the total utility
				utility += data.getEvaluation((ValueDiscrete) opponentBid.getValue(issueNr));
			}
		}
		return utility;
	}
	
}
