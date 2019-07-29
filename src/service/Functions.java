package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import entity.Cat;

public class Functions {
	
	private Double alpha = 0.1;
	private int nObj = 2;
	private int nGrid = 5;
	private double beta = 1;
	
	private Random rnd = new Random();
	private int gama = 1;

	public Cat[] determineDomination(Cat[] pop){
		Cat[] result = new Cat[pop.length];
		for(int i = 0 ; i < result.length ; i++)
			result[i] = pop[i];
		for(int i = 0 ; i < result.length ; i++)
			result[i].setDominated(false);
		
		for(int i = 0 ; i < result.length - 1 ; i++){
			for(int j = i ; j < result.length ; j++){
				if(Dominats(result[i].getCost() , result[j].getCost())){
					result[j].setDominated(true);
				}
				if(Dominats(result[j].getCost() , result[i].getCost())){
					result[i].setDominated(true);
				}
			}
		}
		
		return pop;
	}

	public boolean Dominats(HashMap<Integer, Double> cost,
			HashMap<Integer, Double> cost2) {
		boolean result = true;
		result &= (cost.get(0) <= cost2.get(0)) &&
				  (cost.get(new Integer(1)) <= cost2.get(new Integer(1)));
		result &= (cost.get(new Integer(0)) < cost2.get(new Integer(0))) || 
		  		  (cost.get(new Integer(1)) < cost2.get(new Integer(1)));
		return result;
	}

	public double[][] createGrid(ArrayList<Cat> input){
		ArrayList<Cat> result = new ArrayList<Cat>();
		for(int i = 0 ; i < input.size() ; i++){
			result.add(input.get(i));
		}
		
		HashMap<Integer, ArrayList<Double>> c = new HashMap<Integer, ArrayList<Double>>();
		
		for(int dim = 0 ; dim < nObj ; dim++){
			ArrayList<Double> temp = new ArrayList<Double>();
			for(int i = 0 ; i < input.size() ; i++){
				temp.add(input.get(i).getCost().get(dim));
			}
			c.put(new Integer(dim), temp);
		}
		
		double[] min = new double[nObj];
		double[] max = new double[nObj];
		
		double minValue;
		double maxValue;
		
		for(int i = 0 ; i < c.size() ; i++){
			minValue = c.get(i).get(0);
			maxValue = c.get(i).get(0);
			
			for(int j = 1 ; j < c.get(i).size() ; j++){
				if(c.get(i).get(j) < minValue){
					minValue = c.get(i).get(j);
				}
				if(c.get(i).get(j) > maxValue){
					maxValue = c.get(i).get(j);
				}
			}
			
			min[i] = minValue;
			max[i] = maxValue;
			
		}
		
		double[] dc = new double[nObj];
		
		for(int i = 0 ; i < dc.length ; i++){
			dc[i] = max[i] - min[i];
		}
		
		for(int i = 0 ; i < dc.length ; i++){
			max[i] += alpha * dc[i];
			min[i] -= alpha * dc[i];
		}
		
		double[][] arithProg = new double[dc.length][nGrid  + 1];
		for(int i = 0 ; i < dc.length ; i++)
			arithProg[i] = new double[nGrid + 1];
		
		for(int i = 0 ; i < arithProg.length ; i++){
			for(int j = 0 ; j < nGrid + 1 ; j++ ){
				arithProg[i][j] = min[i] + j * (((double)(max[i] - min[i])) / nGrid ) ;
			}
			 
		}
		
		double[][] grid = new double[dc.length][nGrid  + 3];
		for(int i = 0 ; i < arithProg.length ; i++){
			grid[i][0] = Double.NEGATIVE_INFINITY;
			grid[i][nGrid + 2] = Double.POSITIVE_INFINITY;
			for(int j = 0 ; j < nGrid + 1 ; j++ ){
				grid[i][j+1] = arithProg[i][j];
			}
			 
		}
		
		return grid;
	}

	public Cat findGridIndex(Cat cat , double[][] grid){
		int[] gridSubIndex = new int[nObj];
		for(int i = 0 ; i < gridSubIndex.length ; i++)
			gridSubIndex[i] = 0;
		
		for(int i = 0 ; i < nObj ; i++){
			gridSubIndex[i] = findFirstLess(cat.getCost().get(i) , grid[i]);
		}
		
		int gridIndex = gridSubIndex[0];
		
		for(int j = 1 ; j < nObj ; j++){
			gridIndex -= 1;
			gridIndex *= nGrid * gridIndex;
			gridIndex += gridSubIndex[j];
		}
		
		Cat result = new Cat(cat.getPosition(), cat.getVel(), cat.getCost(), cat.isStFlag(),
				cat.isDominated(), gridIndex , gridSubIndex , new ArrayList<Integer>() , 0 , 0);
		
		return result;
	}

	private int findFirstLess(Double double1, double[] grid) {
		for(int i = 1 ; i < grid.length ; i++){
			if(double1 < grid[i])
				return i - 1;
		}
		return 0;
	}
	
	private int findFirstLessEqual(Double double1, double[] grid) {
		for(int i = 1 ; i < grid.length ; i++){
			if(double1 <= grid[i])
				return i;
		}
		return 0;
	}
	
	public Cat selectLeader(ArrayList<Cat> rep){
		int[] gi = new int[rep.size()];
		for(int i = 0 ; i < gi.length ; i++)
			gi[i] = rep.get(i).getGridIndex();
		
		ArrayList<Integer> OC = new ArrayList<Integer>();
		for(int i = 0 ; i < rep.size() ; i++){
			if(!OC.contains(rep.get(i).getGridIndex())){
				OC.add(rep.get(i).getGridIndex());
			}
		}
		
		int[] N = new int[OC.size()];
		for(int k = 0 ; k < OC.size() ; k++){
			N[k] = findNumberOfEquality(OC.get(k) , gi);
		}
		
		double[] p = new double[N.length];
		double sum = 0;
		
		for(int i= 0 ; i < p.length ; i++ ){
			p[i] = Math.exp(-1 * beta * N[i]);
			sum += p[i];
		}
		
		for(int i= 0 ; i < p.length ; i++ ){
			p[i] /= sum;
		}
		
		int sci = rouletteWheelSelection(p);
		
		double sc = OC.get(sci);
		
		ArrayList<Integer> scm = new ArrayList<Integer>();
		for(int i = 0 ; i < gi.length ; i++){
			if(gi[i] == sc){
				scm.add(i);
			}
		}
		
		int smi = Math.abs(rnd.nextInt()) % scm.size();
		
		int scmMember = scm.get(smi);
		
		return rep.get(scmMember); 
		
		
	}

	private int rouletteWheelSelection(double[] p) {
		double r = rnd.nextDouble();
		
		double[] c = new double[p.length];
		double sum = 0;
		for(int i = 0 ; i < c.length ; i++){
			sum += p[i];
			c[i] = sum;
		}
		
		int result = findFirstLessEqual(r, c);
		
		return result;
	}

	private int findNumberOfEquality(Integer integer, int[] gi) {
		int counter = 0 ;
		for(int i : gi){
			if(i == integer){
				counter++;
			}
		}
		return counter;
	}

	public ArrayList<Cat> deleteOneRepMember(ArrayList<Cat> rep) {
		ArrayList<Cat> result = new ArrayList<Cat>();
		result.addAll(rep);
		
		int[] gi = new int[rep.size()];
		
		for(int i = 0 ; i < gi.length ; i++)
			gi[i] = rep.get(i).getGridIndex();
		
		ArrayList<Integer> OC = new ArrayList<Integer>();
		for(int i = 0 ; i < rep.size() ; i++){
			if(!OC.contains(rep.get(i).getGridIndex())){
				OC.add(rep.get(i).getGridIndex());
			}
		}
		
		int[] N = new int[OC.size()];
		for(int k = 0 ; k < OC.size() ; k++){
			N[k] = findNumberOfEquality(OC.get(k) , gi);
		}
		
		double[] p = new double[N.length];
		double sum = 0;
		
		for(int i= 0 ; i < p.length ; i++ ){
			p[i] = Math.exp(gama  * N[i]);
			sum += p[i];
		}
		
		for(int i= 0 ; i < p.length ; i++ ){
			p[i] /= sum;
		}
		
		int sci = rouletteWheelSelection(p);
		
		double sc = OC.get(sci);
		
		ArrayList<Integer> scm = new ArrayList<Integer>();
		for(int i = 0 ; i < gi.length ; i++){
			if(gi[i] == sc){
				scm.add(i);
			}
		}
		
		int smi = Math.abs(rnd.nextInt() % scm.size());

		int scmMember = scm.get(smi);
		
		result.remove(scmMember);
		return result;
	}
	
	public ArrayList nonDominatedSorting(Cat[] input){
		ArrayList res = new ArrayList();
		
		Cat[] result = new Cat[input.length];
		for(int i = 0 ; i < result.length ; i++){
			result[i] = input[i];
		}
		
		for(int i = 0 ; i < result.length ; i++){
			result[i].setDominatedSet(new ArrayList<Integer>());
			result[i].setDominatedCount(0);
		}
		
		HashMap<Integer , ArrayList<Integer>> f = new HashMap<Integer, ArrayList<Integer>>();
		for(int i = 0 ; i < result.length ; i++){
			for(int j = i + 1 ; j < result.length ; j++){
				if(Dominats(input[i].getCost(), input[j].getCost())){
					result[i].getDominatedSet().add(j);
					result[j].setDominatedCount(result[j].getDominatedCount() + 1);
				}
				if(Dominats(input[j].getCost(), input[i].getCost())){
					result[j].getDominatedSet().add(i);
					result[i].setDominatedCount(result[i].getDominatedCount() + 1);
				}
			}
			if(result[i].getDominatedCount() == 0){
				result[i].setRank(0);
				if(f.containsKey(0)){
					ArrayList<Integer> temp = f.get(0);
					temp.add(i);
					f.put(0, temp);
				}
				else{
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.add(i);
					f.put(0, temp);
				}
			}
		}
		int k = 0;
		while(true){
			ArrayList<Integer> q = new ArrayList<Integer>();
			for(int i : f.get(k)){
				for(int j : result[i].getDominatedSet()){
					result[j].setDominatedCount(result[j].getDominatedCount() - 1);
					if(result[j].getDominatedCount() == 0){
						q.add(j);
						result[j].setRank(k+1);
					}
				}
			}
			if(q.isEmpty()){
				break;
			}
			f.put(k+1, q);
			k++;
		}
		res.add(result);
		res.add(f);
		return res;
	}
	
	public Cat[] sortCats(Cat[] input){
		Cat[] result = new Cat[input.length];
		for(int i = 0 ; i < result.length ; i++)
			result[i] = input[i];
		Arrays.sort(result);
		
		return result;
		
	}
}