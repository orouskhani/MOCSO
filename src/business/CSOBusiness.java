package business;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import service.Functions;

import entity.Cat;

public class CSOBusiness {
	
	private static int SMP = 5;
	private static double SRD = 0.2;
	private static double CDC = 0.8;
	private static boolean SPC = true;
	private static double C = 2;
	private static double MR = 0.01;
	private static double W = 1;
	private static double WDAMP = 0.99;
	
	private Cat[] pop;
	private Cat bestCat;
	
	private double minVar;
	private double maxVar;
	private int nVar;
	
	private double maxVel = 10;
	private double minVel = 0;
	
	private Benchmark benchmark;
	
	ArrayList<Cat> bestCats = new ArrayList<Cat>();
	public static ArrayList<Cat> Rep = new ArrayList<Cat>(); 
	
	Random rnd = new Random();
	Functions func;
	FileWriter wrt = null;
	private int nRep;
	public ArrayList<Cat> getBestCats() {
		return bestCats;
	}

	public CSOBusiness(int nPop , int nVar , double minVar , double maxVar){
		this.pop = new Cat[nPop];
		this.minVar = minVar;
		this.maxVar = maxVar;
		this.nVar = nVar;
		
		nRep = nPop;
		
		benchmark = new Benchmark(this.nVar, this.minVar, this.maxVar);
		func = new Functions();
		
		try {
			wrt = new FileWriter("result.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void replaceArrays(Cat[] source , Cat[] target){
		for(int i = 0 ; i < target.length ; i++){
			target[i].setCost(source[i].getCost());
			target[i].setDominated(source[i].isDominated());
			target[i].setGridIndex(source[i].getGridIndex());
			target[i].setPosition(source[i].getPosition());
			target[i].setStFlag(source[i].isStFlag());
			target[i].setSubIndex(source[i].getSubIndex());
			target[i].setVel(source[i].getVel());
		}
		
	}
	
	public void CSOInitialization() throws IOException{
		for(int i = 0 ; i < pop.length ; i++){
			
			double[] rndPos = new double[this.nVar];
			for(int j = 0 ; j < rndPos.length ; j++)
				rndPos[j] = minVar + (maxVar - minVar) * rnd.nextDouble();
			
			double[] rndVel = new double[this.nVar];
			for(int j = 0 ; j < rndVel.length ; j++)
				rndVel[j] = minVar + (maxVar - minVar) * rnd.nextDouble();
			HashMap<Integer , Double> tempMap = new HashMap<Integer, Double>();
			
			int[] subIndex = new int[]{1 , 2};
			pop[i] = new Cat(rndPos, rndVel, tempMap , false , false , 1 , subIndex , new ArrayList<Integer>() , 0 , 0);
		}
		
		double[] rndPos = new double[this.nVar];
		for(int j = 0 ; j < rndPos.length ; j++)
			rndPos[j] = minVar + (maxVar - minVar) * rnd.nextDouble();
		
		double[] rndVel = new double[this.nVar];
		for(int j = 0 ; j < rndVel.length ; j++)
			rndVel[j] = minVar + (maxVar - minVar) * rnd.nextDouble();
		
		HashMap<Integer , Double> tempMap = new HashMap<Integer, Double>();
		int[] subIndex = new int[]{1 , 2};
		for(Cat cat : pop){
			cat.setCost(benchmark.evaluate(cat));
		}
		
		bestCat = new Cat(rndPos, rndVel, tempMap, false , false , 1 , subIndex , new ArrayList<Integer>() , 0 , 0);
		bestCat.setCost(pop[0].getCost());
		bestCat.setDominated(pop[0].isDominated());
		bestCat.setGridIndex(pop[0].getGridIndex());
		bestCat.setPosition(pop[0].getPosition());
		bestCat.setStFlag(pop[0].isStFlag());
		bestCat.setSubIndex(pop[0].getSubIndex());
		bestCat.setVel(pop[0].getVel());
		

		Cat[] detDominats = func.determineDomination(pop);
		replaceArrays(detDominats, pop);
		for(int i = 0 ; i < pop.length ; i++){
			if(!pop[i].isDominated()){
				Rep.add(pop[i]);
			}
		}
		
		double[][] grid = func.createGrid(Rep);
		
		for(int i = 0 ; i < Rep.size() ; i++){
			Rep.set(i, func.findGridIndex(Rep.get(i), grid));
		}
		//System.out.println(Rep.size());
		for(Cat cat : Rep){
			//wrt.write(":D" + "\n");
			//System.out.println(cat.getCost().toString());
			//System.out.println("Cat is :" + cat.getCost().get(0) + " " + cat.getCost().get(1));
		}
//		for(Cat cat : pop){
//			if(cat.getCost() < bestCat.getCost()){
//				bestCat.setCost(cat.getCost());
//				bestCat.setPosition(cat.getPosition());
//				bestCat.setStFlag(cat.isStFlag());
//				bestCat.setVel(cat.getVel());
//			}
//		}
	}
	
	public void CSOMovement(int iteration){
		
		int counter = 0;
		int randomCat;
		
		for(Cat cat : pop)
			cat.setStFlag(false);
		
		while(true){
			if(counter >= (((int)(MR * pop.length))))
				break;
			
			randomCat = ( Math.abs(rnd.nextInt()) % pop.length );
			if(!pop[randomCat].isStFlag()){
				pop[randomCat].setStFlag(true);
				counter++;
			}
		}
		ArrayList<Cat> seekingCats = new ArrayList<Cat>();
		boolean seekingMode = false;
		boolean tracingMode = false;
		for(Cat cat : pop){
			if(cat.isStFlag()){
				seekingCats.addAll(CSOSeekingMode(cat));
				seekingMode = true;
			}
			else{
				CSOTracingMode(cat);
				tracingMode = true;
			}
		}
		if(seekingMode){
			Cat[] seekingPop = new Cat[seekingCats.size()];
			int j = 0 ;
			for(int i = 0 ; i < seekingCats.size() ; i++){
					seekingPop[i] = seekingCats.get(i);
			}
			ArrayList result = func.nonDominatedSorting(seekingPop);
			Cat[] nonSorted = (Cat[])(result.get(0));
			Cat[] rankSorted = func.sortCats(nonSorted);
			int temp = 0;
			int tempRank = 0 ;
			ArrayList<Cat> finalCats = new ArrayList<Cat>();
			while(temp <= counter){
				ArrayList<Cat> hasSameRank = new ArrayList<Cat>();
				for(Cat c : rankSorted){
					if(c.getRank() == tempRank){
						hasSameRank.add(c);
					}	
				}
				tempRank++;
				if(hasSameRank.size() + finalCats.size() <= counter){
					for(int i = 0 ; i < hasSameRank.size() ; i++){
						finalCats.add(hasSameRank.get(i));
					}
					temp += hasSameRank.size();
				}
				else{
					int rndSize = counter - temp;
					int[] rndNumbers = new int[rndSize];
					for(int i = 0 ; i < rndNumbers.length ; i++){
						rndNumbers[i] = Math.abs(rnd.nextInt()) % hasSameRank.size();
					}
					for(int i = 0 ; i < rndSize ; i++){
						finalCats.add(hasSameRank.get(rndNumbers[i]));
					}
					break;
				}
			}
			int seekingIndex = 0 ;
			for(int i = 0 ; i < pop.length ; i++){
				if(pop[i].isStFlag()){
					pop[i].setPosition(finalCats.get(seekingIndex).getPosition());
				}
			}
		}
		if(tracingMode){
			Cat[] tracingPop = new Cat[pop.length - counter];
			int j = 0 ;
			for(int i = 0 ; i < pop.length ; i++){
				if(!pop[i].isStFlag()){
					tracingPop[j] = pop[i];
					j++;
				}
			}
			
			Cat[] newPop = func.determineDomination(tracingPop);
			for(int i = 0 ; i < newPop.length ; i++){
				if(!(newPop[i].isDominated())){
					Rep.add(newPop[i]);
				}
			}
			
			Cat[] temp = new Cat[Rep.size()];
			for(int i = 0 ; i < Rep.size() ; i++){
				temp[i] = Rep.get(i);
			}
			Cat[] newPop1 = func.determineDomination(temp);

			Rep.clear();
			
			for(int i = 0 ; i < newPop1.length ; i++){
				if(!(newPop1[i].isDominated())){		
					Rep.add(newPop1[i]);
				}
			
			}
			
			double[][] grid = func.createGrid(Rep);
			
			
			for(int i = 0 ; i < Rep.size() ; i++){
				Rep.set(i, func.findGridIndex(Rep.get(i), grid));
			}
			
			int extra = 0;
			ArrayList<Cat> delete = new ArrayList<Cat>();
			if(Rep.size() > nRep){
				extra = Rep.size() - nRep;
				//System.out.println("extra is : " + extra);
				for(int i = 0 ; i < extra ; i++){
					delete = func.deleteOneRepMember(Rep);
					Rep.clear();
					Rep.addAll(delete);
				}
			}

		}
		
				
		bestCats.add(bestCat);
		W *= WDAMP;
		
		if(iteration == 999){
			for(int i = 0 ; i < Rep.size() ; i++){
				System.out.println(Rep.get(i).getCost().get(0));
			}
			System.out.println();
			for(int i = 0 ; i < Rep.size() ; i++){
				System.out.println(Rep.get(i).getCost().get(1));
			}
		}
	}

	private ArrayList<Cat> CSOSeekingMode(Cat cat) {
		if(SPC){
			ArrayList<Cat> copies = new ArrayList<Cat>();
			for(int i = 0 ; i < SMP - 1 ; i++){
				copies.add(cat);
			}
			
			int dimToChange = (int)(nVar * CDC);
			
			for(Cat copyCat : copies){
				
				int[] changedDims = new int[dimToChange];
				for(int i = 0 ; i < changedDims.length ; i++){
					changedDims[i] = Math.abs(rnd.nextInt()) % nVar;
				}
				for(int dim : changedDims){
					double[] temp = copyCat.getPosition();
					temp[dim] = (rnd.nextBoolean()) ? ( copyCat.getPosition()[dim] + SRD ) : (copyCat.getPosition()[dim] - SRD);
					copyCat.setPosition(temp); 
				}
				
			}
			
			benchmark.evaluate(cat);
			for(Cat copyCat : copies){
				benchmark.evaluate(copyCat);
			}
			
			ArrayList<Cat> result = new ArrayList<Cat>();
			result.add(cat);
			result.addAll(copies);
			return result;
			/*HashMap<Integer , HashMap<Integer , Double>> probilitiesCat = new HashMap<Integer , HashMap<Integer , Double>>();
			
			probilitiesCat.put(new Integer(0) , benchmark.evaluate(cat));
			for(int i = 0 ; i < copies.size() ; i++){
				probilitiesCat.put(new Integer(i) , benchmark.evaluate(copies.get(i)));
			}
			
			double max = probilitiesCat.get(0);
			double min = probilitiesCat.get(0);
			
			for(int i = 1 ; i < probilitiesCat.size() ; i++){
				if(max < probilitiesCat.get(i)){
					max = probilitiesCat.get(i);
				}
				if(min > probilitiesCat.get(i)){
					min = probilitiesCat.get(i);
				}
			}
			
			if(max == min){
				for(int i = 0 ; i < probilitiesCat.size() ; i++){
					probilitiesCat.put(new Integer(i), new Double(1.0));
				}
			}
			else{
				for(int i = 0 ; i < probilitiesCat.size() ; i++){
					double p = Math.abs(((double)(probilitiesCat.get(i) - max)) / ((double)( max - min )));
					probilitiesCat.put(new Integer(i), new Double(p));
				}
			}
			
			double rndNumber = Math.abs(rnd.nextDouble());
			double delta = 2.0;
			double tempDelta = 0;
			int index = 0;
			for(int i = 0 ; i < probilitiesCat.size() ; i++){
				if(probilitiesCat.get(i) > rndNumber){
					tempDelta = probilitiesCat.get(i) - rndNumber;
					if(tempDelta < delta){
						delta = tempDelta;
						index = i;
					}
				}
			}
			
			if(index != 0){
				cat.setPosition(copies.get(index - 1).getPosition());
			}*/
			
		}
		else{
			
		}
		return null;
	}

	private void CSOTracingMode(Cat cat) {
			
		Cat leader = func.selectLeader(Rep);
			// Update Velocity
			double[] tempVel = new double[cat.getVel().length];
			double[] randomDouble = new double[cat.getVel().length];
			for(int i = 0 ; i < randomDouble.length; i++){
				randomDouble[i] = 0.0 + (1.0 - 0.0) * rnd.nextDouble();
			}
			
			for(int i = 0 ; i < cat.getVel().length ; i++){
				tempVel[i] = cat.getVel()[i];
			}
			
			for(int i = 0 ; i < cat.getVel().length ; i++){
				double temp = W * tempVel[i] + C * randomDouble[i] * (leader.getPosition()[i] - cat.getPosition()[i]);
//				if(temp > maxVel )
//					//tempVel[i] = maxVel;
//				else if(temp < minVel)
//					tempVel[i] = minVel;
//				else{
					tempVel[i] = temp;
//				}	
			}
			
			cat.setVel(tempVel);
			
			// End Update of Velocity
		
			
			// Update Position
			double[] tempPos = new double[cat.getPosition().length];
			for(int i = 0 ; i < cat.getPosition().length ; i++){
				tempPos[i] = cat.getPosition()[i];
			}
			for(int i = 0 ; i < tempPos.length ; i++){
				double temp = tempPos[i] + tempVel[i]; 
//				if(temp > maxVar )
//					temp = maxVar;
//					//tempPos[i] = maxVar;
//				if(temp < minVar)
//					temp = minVar;
//					//tempPos[i] = minVar;
				//else{
				tempPos[i] = temp;
				//}
			}
			cat.setPosition(tempPos);
			// End Update Position
			
			cat.setCost(benchmark.evaluate(cat));
			//System.out.println("This cat's cost is " + bestCat.getCost());
			if(func.Dominats(cat.getCost() , bestCat.getCost())){
				//System.out.println("In Tracing Mode:" + cat.getCost());
				//System.out.println("Before setting + " + bestCat.getCost());
				bestCat.setCost(cat.getCost());
				bestCat.setDominated(cat.isDominated());
				bestCat.setGridIndex(cat.getGridIndex());
				bestCat.setPosition(cat.getPosition());
				bestCat.setStFlag(cat.isStFlag());
				bestCat.setSubIndex(cat.getSubIndex());
				bestCat.setVel(cat.getVel());
				//System.out.println("After setting + " + bestCat.getCost());
			}
			else if(func.Dominats(bestCat.getCost() , cat.getCost())){
				
			}
			else{
				if(rnd.nextDouble() < 0.5){
					bestCat.setCost(cat.getCost());
					bestCat.setDominated(cat.isDominated());
					bestCat.setGridIndex(cat.getGridIndex());
					bestCat.setPosition(cat.getPosition());
					bestCat.setStFlag(cat.isStFlag());
					bestCat.setSubIndex(cat.getSubIndex());
					bestCat.setVel(cat.getVel());
				}
			}
		
	}
	
}
