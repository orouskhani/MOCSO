import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import entity.Cat;
import business.CSOBusiness;

/**
 * 
 */

/**
 * @author Yasin
 *
 */
public class CSO {

	public static void main(String[] args) throws Exception{
		// Parameter Definition
		int nVar = 3;				// number of variables
		double minVar = -4;		// lowerbound of variables
		double maxVar = 4;			// upperbound of variables

		int nPop = 200;
		
		int iter = 1000;
		
		CSOBusiness buss = new CSOBusiness(nPop , nVar , minVar , maxVar);
		buss.CSOInitialization();
		
		for(int i = 0 ; i < iter ; i++){
			//System.out.println("Iteration is : " + i);
			buss.CSOMovement(i);
			//System.out.println("Iteration is : " + i + " Best Cost is : " +buss.getBestCats().get(buss.getBestCats().size() - 1).getCost());
			
			
		}
		
		//for(int i = 0 ; i <  50 ; i++){
			//System.out.println("Iteration is :" + i + " BestCat is : " + buss.getBestCats().get(i).getCost());

		//}
					
	}
}
