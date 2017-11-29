package ai2017.group24;

import negotiator.boaframework.AcceptanceStrategy;
import negotiator.boaframework.BOAagent;
import negotiator.boaframework.OMStrategy;
import negotiator.boaframework.OfferingStrategy;
import negotiator.boaframework.OpponentModel;

/**
 * The agent of group 24 for AI Techniques 2017
 * 
 * @author Albert ten Napel
 * @author Luc Hogervorst
 * @author Nirul Hoeba
 * @author Marc Barendse
 * 
 */
@SuppressWarnings("serial")
public class Group24 extends BOAagent {

	@Override
	public void agentSetup() {		
		// create all the boa components
		AcceptanceStrategy acStrategy = new ACCombiMAXT();
		OfferingStrategy bidder = new BestBidForOpponent();
		OpponentModel model = new FrequencyAnalysis();
		OMStrategy omStrategy = new BestBid();
		
		// initialize them
		try {
			acStrategy.init(negotiationSession, bidder, model, null);
			bidder.init(negotiationSession, model, omStrategy, null);
			model.init(negotiationSession, null);
			omStrategy.init(negotiationSession, model, null);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// set the components
		setDecoupledComponents(
			acStrategy, // Acceptance condition
			bidder, // Bidding Strategy
			model, // Opponent modeling
			omStrategy // OMStrategy
		);
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getName() {
		return "Group 24 Agent";
	}
	
	@Override
	public String getDescription() {
		return "This is the agent of group 24";
	}	

}
