package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Cat implements Comparable<Cat>{
	private double[] position;
	private double[] vel;
	private HashMap<Integer , Double> cost;
	private boolean stFlag;
	private boolean dominated;
	private int gridIndex;
	private int[] subIndex;
	private ArrayList<Integer> dominatedSet;
	private int dominatedCount;
	private int rank;
	
	public Cat(double[] position, double[] vel, HashMap<Integer , Double>cost, boolean stFlag , boolean dominated
			, int gridIndex , int[] subIndex , ArrayList<Integer> dominatedSet , int dominatedCount , int rank) {
		super();
		this.position = new double[position.length];
		for(int i = 0 ; i < position.length ; i++)
			this.position[i] = position[i];
		
		this.vel = new double[vel.length];
		for(int i = 0 ; i < vel.length ; i++)
			this.vel[i] = vel[i];
		
		this.cost = new HashMap<Integer, Double>();
		for(Entry<Integer, Double> e : cost.entrySet()){
			this.cost.put(e.getKey(), e.getValue());
		}
		this.stFlag = stFlag;
		
		this.dominated = dominated;
		this.gridIndex = gridIndex;
		
		this.subIndex = new int[subIndex.length];
		for(int i = 0 ; i < subIndex.length ; i++){
			this.subIndex[i] = subIndex[i];
		}
		
		this.dominatedSet = new ArrayList<Integer>();
		for(int i = 0 ; i < dominatedSet.size() ; i++){
			this.dominatedSet.add(dominatedSet.get(i));
		}
		
		this.dominatedCount = dominatedCount;
		
		this.rank = rank;
	}

	public void setRank(int rank){
		this.rank = rank;
	}
	
	public int getRank(){
		return rank;
	}
	
	public ArrayList<Integer> getDominatedSet() {
		return dominatedSet;
	}


	public void setDominatedSet(ArrayList<Integer> dominatedSet) {
		this.dominatedSet.clear();
		this.dominatedSet.addAll(dominatedSet);
	}


	public int getDominatedCount() {
		return dominatedCount;
	}


	public void setDominatedCount(int dominatedCount) {
		this.dominatedCount = dominatedCount;
	}


	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = new double[position.length];
		for(int i = 0 ; i < this.position.length ; i++)
			this.position[i] = position[i];
	}

	public double[] getVel() {
		return vel;
	}

	public void setVel(double[] vel) {
		this.vel = new double[vel.length];
		for(int i = 0 ; i < this.vel.length ; i++)
			this.vel[i] = vel[i];
	}

	public HashMap<Integer , Double> getCost() {
		return cost;
	}

	public void setCost(HashMap<Integer , Double> cost) {
		for(Entry<Integer, Double> e : cost.entrySet()){
			this.cost.put(e.getKey(), e.getValue());
		}
	}

	public boolean isStFlag() {
		return stFlag;
	}

	public void setStFlag(boolean stFlag) {
		this.stFlag = stFlag;
	}

	public boolean isDominated() {
		return dominated;
	}

	public void setDominated(boolean dominated) {
		this.dominated = dominated;
	}

	public int getGridIndex() {
		return gridIndex;
	}

	public void setGridIndex(int gridIndex) {
		this.gridIndex = gridIndex;
	}

	public int[] getSubIndex() {
		return subIndex;
	}

	public void setSubIndex(int[] subIndex) {
		this.subIndex = new int[subIndex.length];
		for(int i = 0 ; i < this.subIndex.length ; i++)
			this.subIndex[i] = subIndex[i];
	}

	@Override
	public int compareTo(Cat o) {
		int oRank = o.getRank();
		return this.rank - oRank;
	}
	
	

}
