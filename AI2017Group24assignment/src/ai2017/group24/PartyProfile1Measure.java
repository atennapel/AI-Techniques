package ai2017.group24;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import negotiator.issue.IssueDiscrete;
import negotiator.issue.Objective;
import negotiator.issue.ValueDiscrete;
import negotiator.utility.AdditiveUtilitySpace;
import negotiator.utility.Evaluator;
import negotiator.utility.EvaluatorDiscrete;

/**
 * Compares a utility space with a specific party profile 1.
 * Uses RMSE to calculate the error.
 * Used to measure the performance of the opponent modeller.
 * 
 * @author Albert ten Napel
 */
public class PartyProfile1Measure {
	
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
	
	private double lastW = -1;
	private double lastV = -1;
	public void differenceFromPartyProfile1(AdditiveUtilitySpace space, double time) throws Exception {
		ArrayList<Double> wlist = new ArrayList<Double>();
		ArrayList<Double> vlist = new ArrayList<Double>();
		for(Entry<Objective, Evaluator> entry : space.getEvaluators()) {
			IssueDiscrete obj = (IssueDiscrete) entry.getKey();
			EvaluatorDiscrete ev = (EvaluatorDiscrete) entry.getValue();
			wlist.add(ev.getWeight() - weights.get(obj.getName()));
			for(ValueDiscrete val : obj.getValues())
				vlist.add(ev.getEvaluation(val) - evals.get(obj.getName()).get(val.getValue()));
		}
		double newW = rme(wlist);
		double newV = rme(vlist);
		if(newW != lastW || newV != lastV) {
			lastW = newW;
			lastV = newV;
			System.out.printf("%.4f,%.4f,%.4f\n", time, newW, newV);
		}
	}
}
