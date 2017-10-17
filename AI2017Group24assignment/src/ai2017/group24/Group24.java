package ai2017.group24;

import negotiator.SupportedNegotiationSetting;
import negotiator.boaframework.BOAagent;
import negotiator.utility.UTILITYSPACETYPE;

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
		getSupportedNegotiationSetting().setUtilityspaceType(UTILITYSPACETYPE.LINEAR);
		setDecoupledComponents(
			new ACNext(), // Acceptance condition
			new RandomWalker(this.negotiationSession), // Bidding Strategy
			new FrequencyAnalysis(), // Opponent modeling
			new BestBid() // OMStrategy
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
