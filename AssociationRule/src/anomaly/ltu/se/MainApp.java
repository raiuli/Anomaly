/**
 * 
 */
package anomaly.ltu.se;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.frequentpatterns.apriori.AlgoApriori;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.test.MainTestAllAssociationRules_FPGrowth_saveToFile;

/**
 * @author raiuli
 *
 */
public class MainApp {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String inputPrefix="//Users//raiuli//Documents//workspaceMars//AssociationRule//input";
		String ouputPrefix="/Users/raiuli/Documents/workspaceMars/AssociationRule/output";
	
		String input=inputPrefix+"//RTLtestWithOutLabels02.txt";
		System.out.println(input);
		String output = "//Users//raiuli//Documents//workspaceMars//AssociationRule//output.txt";
//		String output = "C:\\patterns\\association_rules.txt";
		double minsupp = 0.01 ;
		int topN=378;
		int topK=378;
		AlgoApriori a= new AlgoApriori();
		Itemsets patterns = a.runAlgorithm(minsupp, input, null);
		a.printStats();
		int databaseSize =a.getDatabaseSize();
		patterns.printItemsets(databaseSize);
		List<List<Itemset>>FPS=patterns.getLevels();
		TransactionDatabase td = new TransactionDatabase();
		td.loadFile(input);
		
		//td.printDatabase();
		List<List<Integer>> transactions=td.getTransactions();
		int count = 0; 
		// for each transaction
		int[] fpofArray = new int[transactions.size()];
		int countOfTrans=0;
		
		LinkedList<TransactionFPOF> TransactionFPOFList = new LinkedList<TransactionFPOF>();
		
		for (List<Integer> itemset : transactions) { 
			for(List<Itemset> level:FPS){
				for(Itemset X: level){
					
					if(contains(itemset,X.getItems())){
						System.out.print("T"+itemset.toString()+" contains"+print(X.getItems()));
						fpofArray[countOfTrans]=fpofArray[countOfTrans]+FPOF(itemset,X.getAbsoluteSupport())+X.getAbsoluteSupport();
						//fpofArray[countOfTrans]=FPOF(itemset,X.getRelativeSupport(databaseSize));
						System.out.println("fpofArray:("+fpofArray[countOfTrans]+")");
						
					}else{
						//System.out.println("T"+itemset.toString()+"does not  contains"+print(X.getItems()));
					}
				}
			}
			//td.print(itemset); // print the transaction 
			//System.out.println("");
			TransactionFPOF tFPOF=new TransactionFPOF(itemset,fpofArray[countOfTrans]);
			TransactionFPOFList.add(tFPOF);
			countOfTrans++;
		}
		/*for (TransactionFPOF element : TransactionFPOFList)
		      System.out.println(element + "\n");*/
		System.out.println("TransactionFPOFList.size():"+TransactionFPOFList.size() + "\n");
		Collections.sort(TransactionFPOFList,new Comparator<TransactionFPOF>() {

	        public int compare(TransactionFPOF o1, TransactionFPOF o2) {
	    		if(o1.getFpof()< o2.getFpof()){
	                return -1;
	            }else if(o1.getFpof()> o2.getFpof()){
	            	return 1;
	            }
	            
	            else {
	            	return 0;
	            }
	        }
	        });
		int count_topN=1;
		for (TransactionFPOF element : TransactionFPOFList){
			if(count_topN<= topN){
				 System.out.println(element + "\n");
				 count_topN++;
				 LinkedList<Itemset> ContradictedItemSet =new LinkedList<Itemset>();
				 for(List<Itemset> level:FPS){
						for(Itemset X: level){
							if(checkSubset(element.getTransaction(),X.getItems())){
								//System.out.print(print(X.getItems())+"not a subset of "+element.getTransaction());
								//System.out.println("ContradictNess:"+calculateContradictNess(element.getTransaction(),X.getItems(),X.getAbsoluteSupport()));
							}
						}
				 }		
			}
				
		}
		     
	
	}
	public static int calculateContradictNess(List<Integer> trans,int[] x, int supportOfX){
		int t=Math.abs( x.length-findInterection(trans,x));
		t=t*supportOfX;
		return t;
	}
	public static int findInterection(List<Integer> trans,int[] x){
		int numberOfElementInInterection=0;
		for (Integer ele:trans){
			for (int j=0;j< x.length;j++){
				if(x[j]==ele){
					numberOfElementInInterection++;
				}
			}
			
		}
		return numberOfElementInInterection;
	}
	public static boolean checkSubset(List<Integer> trans,int[] x){
		for (int j=0;j< x.length;j++){
			if(trans.contains(x[j])){
				//System.out.println(x[j]+"in trans: "+trans.toString());
				
			}else{
				//System.out.println(x[j]+"not in trans: "+trans.toString());
				return true;
			}

		}
		
		return false;
	}
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestAllAssociationRules_FPGrowth_saveToFile.class.getResource(filename);
		System.out.println(url.toString());
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
	public static boolean contains(List<Integer> trans,int[] x){
		for (int j=0;j< x.length;j++){
			if(trans.contains(x[j])){
				//System.out.println(x[j]+"in trans: "+trans.toString());
				
			}else{
				//System.out.println(x[j]+"not in trans: "+trans.toString());
				return false;
			}

		}
		
		return true;
		
	}
	public static String print(int[]x){
		String s="[";
		for (int j=0;j< x.length;j++){
			s=s+" "+x[j];
		}
		s=s+"]";
		return s;
	} 
	public static int FPOF(List<Integer> transation,int supportOfX){
		int t=0;
		t=supportOfX/transation.size();
		//System.out.println("t:"+t+"=supportOfX:"+supportOfX+" /transation.size():"+transation.size());
		return t;
	}
}
