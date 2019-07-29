package business;

import java.util.ArrayList;
import java.util.HashMap;

import entity.Cat;

public class Benchmark {
	
	private int nVar;
	private double minVar;
	private double maxVar;
	
	
	public Benchmark(int nVar, double minVar, double maxVar) {
		super();
		this.nVar = nVar;
		this.minVar = minVar;
		this.maxVar = maxVar;
	}


	public HashMap<Integer , Double> evaluate(Cat cat){
		HashMap<Integer , Double> result = new HashMap<Integer , Double>();
		double[] temp = new double[4];
		for(int i = 0 ; i < cat.getPosition().length ; i++){
			temp[0] += Math.pow(cat.getPosition()[i] - (1.0/((double)Math.sqrt(cat.getPosition().length))) , 2);
		}
		for(int i = 0 ; i < cat.getPosition().length - 1 ; i++){
			temp[1] += Math.pow(cat.getPosition()[i+1] + (1.0/((double)Math.sqrt(cat.getPosition().length))) , 2);
		}
		result.put(new Integer(0) , new Double(1 - Math.exp(-1 * temp[0])));
		result.put(new Integer(1) , new Double(1 - Math.exp(-1 * temp[1])));
		return result;
	}
}
