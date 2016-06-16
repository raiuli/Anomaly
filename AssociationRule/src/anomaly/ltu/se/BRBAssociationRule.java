package anomaly.ltu.se;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BRBAssociationRule {
	double data[][]=null;
	String [] rainfallReferantialLabel={"low","small","medium","high","very_high"};
	//int[] rainfallReferantialValue =  { 2000,1500,1000,500,0 } ;
	int[] rainfallReferantialValue =  { 0,500,1000,1500,2000 } ;
	
	String [] tempReferantialLabel={"VeryLow","Low","Medium","Hot","Very Hot"};
	int[] templReferantialValue = {  0,	10,	20,	30,	40 };
	int[][] HummingDistanceCountR=null;
	int[][] HummingDistanceCountT=null;
	
	public int[][] getHummingDistanceCountR() {
		return HummingDistanceCountR;
	}
	public void setHummingDistanceCountR(int[][] hummingDistanceCountR) {
		HummingDistanceCountR = hummingDistanceCountR;
	}
	public int[][] getHummingDistanceCountT() {
		return HummingDistanceCountT;
	}
	public void setHummingDistanceCountT(int[][] hummingDistanceCountT) {
		HummingDistanceCountT = hummingDistanceCountT;
	}
	public double[][] getData() {
		return data;
	}
	public void setData(double[][] data) {
		this.data = data;
	}
	public String[] getRainfallReferantialLabel() {
		return rainfallReferantialLabel;
	}
	public int[] getRainfallReferantialValue() {
		return rainfallReferantialValue;
	}
	public String[] getTempReferantialLabel() {
		return tempReferantialLabel;
	}
	public int[] getTemplReferantialValue() {
		return templReferantialValue;
	}
	public static void main(String[] args) throws IOException {
		BRBAssociationRule brbAssocRule = new BRBAssociationRule();
		//double data[][] =readFile("/Users/raiuli/Documents/workspaceMars/fuzzy/input/test.txt");
		//brbAssocRule.setData(brbAssocRule.readFile("/Users/raiuli/Documents/workspaceMars/AssociationRule/input/RTLtestWithOutLabels.txt"));
		//brbAssocRule.setData(brbAssocRule.readFile("/Users/raiuli/Documents/workspaceMars/AssociationRule/input/RTLtestWithOutLabels02.txt"));
		brbAssocRule.setData(brbAssocRule.readFile("/Users/raiuli/Documents/workspaceMars/fuzzy/input/test.txt"));

		double data[][]=brbAssocRule.brbFication(brbAssocRule.getData());
		double data1[][]=brbAssocRule.calcHummingDistanceRain(data);
		double data2[][]=brbAssocRule.calcHummingDistanceTemp(data);
		double[][] dataConf=brbAssocRule.calcConfidence(data1,data2);
		System.out.println(data.toString());
		for(int i=0;i<dataConf.length;i++){
			System.out.print(" dataConf["+i+"][0] "+dataConf[i][0]);
			System.out.println(" dataConf["+i+"][1] "+dataConf[i][1]);
			
		}
	}
	
	public double[][]calcConfidence(double[][] hdRain,double[][] hdTemp){
		double[] hdRainSum = new double[hdRain.length];
		double[] hdTempSum = new double[hdTemp.length];
		double[] hdRainTempSum = new double[hdTemp.length];
		double[][] weight = new double[hdTemp.length][hdTemp.length];
		for(int i=0;i<hdRain.length;i++){
			double temp=0;
			for(int j=0;j<hdRain[i].length;j++){
				temp=temp+hdRain[i][j];
			}
			hdRainSum[i]=temp;
		}
		for(int i=0;i<hdTemp.length;i++){
			double temp=0;
			for(int j=0;j<hdTemp[i].length;j++){
				temp=temp+hdTemp[i][j];
			}
			hdTempSum[i]=temp;
		}
		double onelSum=0.0;
		for(int i=0;i<hdRainSum.length;i++){
			
			onelSum=hdRainSum[i]+onelSum;
		}
		for(int i=0;i<hdRainSum.length;i++){
		
			weight[i][0]=hdRainSum[i]/onelSum;
		}
		
		double totalSum=0.0;
		for(int i=0;i<hdRainTempSum.length;i++){
			hdRainTempSum[i]=hdRainSum[i]+hdTempSum[i];
			totalSum=hdRainTempSum[i]+totalSum;
		}
		for(int i=0;i<hdRainTempSum.length;i++){
		
			weight[i][1]=hdRainTempSum[i]/totalSum;
		}
		return weight;
	}
	public double[][] calcHummingDistanceRain(double[][] data){
		double[][] HummingDistanceCountR =new double[data.length][data.length];
		
		System.out.println("rainfallReferantialLabel.length:"+rainfallReferantialLabel.length);
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data.length;j++){
				int count=0;
				for(int k=0;k<data[i].length;k++){
				
					if(k<this.rainfallReferantialLabel.length){
						int check1=0;
						int check2=0;
						if(data[i][k]>0){
							check1=1;
						}
						if(data[j][k]>0){
							check2=1;
						}
						if(check1!=check2){
								count++;
							}
						
					}
				
				}
				System.out.println("i:"+i+" j:"+j+" count:"+count);
				HummingDistanceCountR[i][j]=count;
				count=0;
			}
		}
		//this.setHummingDistanceCountR(HummingDistanceCountR);
		//this.setHummingDistanceCountT(HummingDistanceCountT);
		return HummingDistanceCountR;
	}
	public double[][] calcHummingDistanceTemp(double[][] data){
		//double[][] HummingDistanceCountR =new double[data.length][data.length];
		double[][] HummingDistanceCountT =new double[data.length][data.length];
		System.out.println("tempReferantialLabel.length:"+tempReferantialLabel.length);
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data.length;j++){
				int count=0;
				for(int k=0;k<data[i].length;k++){
					
					if(k>this.rainfallReferantialLabel.length-1){
						//System.out.println("i:"+i+" j:"+j+" count:"+count);
						int check1=0;
						int check2=0;
						if(data[i][k]>0){
							check1=1;
						}
						if(data[j][k]>0){
							check2=1;
						}
						if(check1!=check2){
								count++;
							}
						
					}
				
				}
				System.out.println("i:"+i+" j:"+j+" count:"+count);
				HummingDistanceCountT[i][j]=count;
				count=0;
			}
		}
		//this.setHummingDistanceCountR(HummingDistanceCountR);
		//this.setHummingDistanceCountT(HummingDistanceCountT);
		return HummingDistanceCountT;
	}
	public double[][] brbFication(double[][] data){
		double brbfiedData[][]=new double[data.length][rainfallReferantialLabel.length+tempReferantialLabel.length];
		for(int i=0;i<data.length;i++){
			double[] temp1=inputTransform(rainfallReferantialValue,data[i][0]);
			for(int j=0;j<rainfallReferantialValue.length;j++){
				brbfiedData[i][j]=temp1[j];
			}
			temp1=inputTransform(templReferantialValue,data[i][1]);
			for(int k=0;k<templReferantialValue.length;k++){
				brbfiedData[i][rainfallReferantialValue.length+k]=temp1[k]; 
			}
		}
		return brbfiedData;
	}
	public double[] inputTransform(int[] a,double data ){
		double[] transValue= new double[a.length];
		double aa=data;
		if(aa<a[0]){
			aa=a[0];
		}else if(aa>a[a.length-1]){
			aa=a[a.length-1];
		}
		for(int k=0;k<a.length;k++){
			if(aa==a[k]){
				transValue[k]=1;
			}
			
		}
		for(int m=0;m<a.length-1;m++){
			if(a[m]<aa && aa<a[m+1]){
				//transValue[m+1]=(a[m]-aa)/(a[m]-a[m+1]);
				transValue[m+1]=Math.abs(a[m]-aa)/Math.abs(a[m]-a[m+1]);
				transValue[m]=1.0-transValue[m+1];
			}
		}

		return transValue;
	}
	  public  double[][] readFile(String path){
		  
		  ArrayList<String> listOfString = new ArrayList<String>();
		  double data[][] = null;
		  String s;

			// First we open and read the file
			// just to know the number of terms
			try
			{
				// We open and read the terms file
				BufferedReader br = new BufferedReader(new FileReader( path ));
				while((s = br.readLine()) != null)
				{
					StringTokenizer linea = new StringTokenizer( s, "\t" );
					
					 
					
					listOfString.add( linea.nextToken() );
				}
				br.close();
				
				data=new double[listOfString.size()][2];
				for(int i=0;i<listOfString.size();i++){
					
					String tmp[]=listOfString.get(i).split(" ");
					data[i][0]=Double.parseDouble(tmp[0]);
					data[i][1]=Double.parseDouble(tmp[1]);
					
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		  return data;

	  }
}
