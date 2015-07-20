import java.io.*;
import java.util.*;

public class SimulationControl {
	private static Scanner fileReader;
	private static BufferedWriter fileWriter;

	public static void main(String[] args) throws IOException {
		
		ArrayList<Double> inputs = new ArrayList<Double>();    
		fileReader = new Scanner(new File("input.txt"));  //input.txt path to be specified
	    File outoutFile = new File("output.txt"); //output.txt path to be specified
	    
	    if(!outoutFile.exists())
	    {
	    	outoutFile.createNewFile();
	    }
	    
	    System.out.println("Reading input.txt");
		while (fileReader.hasNextDouble()){
			inputs.add(fileReader.nextDouble());
		}
		
		double rateOfArrivals = inputs.get(0);
		double smtP = inputs.get(1);
		double smtS = inputs.get(2);
		double smtA = inputs.get(3);
		int departures = inputs.get(4).intValue();
		int batchs = inputs.get(5).intValue();
		double Sum_time1= 0;
		double  avg1=0;
		double Sumb=0;
		
		Random rand = new Random(6);
		ArrayList<Double> allTime = new ArrayList<Double>();
		
		RunSimulation simulator = new RunSimulation(rateOfArrivals,smtP,smtS,smtA,departures,rand);
	    System.out.println("Simulation Running");
		allTime = simulator.Run(0);	
		
		
		ArrayList<Double> delay1= new ArrayList<Double>();
		
		double[] percentile_Array= new double[batchs];
		double[] mean_batch = new double[batchs];
		
		double Sum_time2=0;
		double mean1=0;
		
		int i = 1; 
		int a = 0;
		double sumPerBatch = 0;
		
		System.out.println("Running Calculations");
		
		for (Double time : allTime) 
		{	
			delay1.add(time);	
			sumPerBatch = sumPerBatch+time;
			if(i == departures/batchs){
				Sum_time1 = Sum_time1 + sumPerBatch;
				Collections.sort(delay1);
				int p= (int) Math.ceil(0.95*(departures/batchs));
				percentile_Array[a]=delay1.get(p);
				mean_batch[a] = sumPerBatch/(departures/batchs);
				sumPerBatch = 0;
				a++;
				delay1.clear();
				i = 1;
			}
			else
			{
				i++;
			}
			
		}
		
		for(int j = 0;j < batchs;j++)
		{
			Sumb= Sumb + percentile_Array[j];
		}
		
		double meanb = Sumb/batchs;
		
		
		
		double meanofmeans=Sum_time1/departures;
		
		//System.out.println("mean end to end delay using batches    " + meanofmeans);
	    
		
		
		
		
		Collections.sort(allTime);
		int k = (int) Math.ceil((0.95*departures));
		
		avg1 = Sum_time1/departures;
		
		double std=0;

	    for(int m = 0;m < batchs;m++)
	    {
    		std= std + Math.pow(meanb - percentile_Array[m], 2); 
	    }

	    double std_batch = 0;
	    for(int l = 0;l < batchs;l++)
	    {
    		std_batch = std_batch + Math.pow(meanofmeans - mean_batch[l], 2); 
	    }
	    
	    std_batch = Math.sqrt(std_batch/(batchs - 1));
	    double std1 = (std)/(batchs - 1);
	    double final_std = Math.sqrt(std1);
	    double error1 = 1.96*(final_std/Math.sqrt(batchs));
	    double error2 = 0.10*meanb;
	    
	    
	    

	    double error1_batch = 1.96*(std_batch/Math.sqrt(batchs));
	    double error2_batch = 0.10*meanofmeans;
	    //confidence interval calculation
	    
	    double CI_upper = meanb + error1;
	    double CI_lower = meanb - error1;
	    
	    double CI_upper_mean= meanofmeans + error1_batch;
	    double CI_lower_mean= meanofmeans - error1_batch;
	    
	    
	    fileWriter = new BufferedWriter(new FileWriter(outoutFile));
	    
	    System.out.println("Writing output.txt");
	    
	    
	    
	    
	    fileWriter.newLine();
	    fileWriter.write("Simple Mean end to end delay for customers without using batches:     " + avg1);
	    System.out.println("Simple Mean end to end delay for customers without using batches:   "+ avg1);
	    
	    fileWriter.newLine();
	    fileWriter.write("95th percentile without using batches       :"+ allTime.get(k));
	    System.out.println("95th percentile without using batch means     :"+ allTime.get(k));
	    
	    fileWriter.newLine();
	    fileWriter.write("mean end to end delay using batches    :  " + meanofmeans);
	    System.out.println("mean end to end delay using batches    :  " + meanofmeans);
	    
	    fileWriter.newLine();
	    fileWriter.write("Tmean(mean of batches 95 percentiles)      : "+meanb);
	    System.out.println("Tmean(mean of batches 95 percentiles)    : "+meanb);
	   
	    System.out.println("Standard deviation: "+ final_std);
	    System.out.println("std dev with batches  "+ std_batch);
	    
	    fileWriter.newLine();
	    fileWriter.write("error Percentage of Tmean for 95 percentile      : "+ 100*error1);
	    System.out.println("error Percentage of Tmean for 95 percentile    : "+ 100*error1);
	    
	    fileWriter.newLine();
	   fileWriter.write("error Percentage of Tmean for batch means      : "+ 100*error1_batch);
	    System.out.println("error Percentage of Tmean for batch means   : "+ 100*error1_batch);
	    
	    fileWriter.newLine();
	    fileWriter.write("Upper limit of confidence interval for 95 percentile [tmean]    : "+ CI_upper);
	    System.out.println("Upper limit of confidence interval for 95 percentile [tmean]   :"+ CI_upper);
	    
	    fileWriter.newLine();
	    fileWriter.write("lower limit of confidence interval for 95 percentile[tmean]:     " + CI_lower);
	    System.out.println("lower limit of confidence interval for 95 percentile[tmean]:     "+ CI_lower);
	    
	    
	    fileWriter.newLine();
	    fileWriter.write("Upper limit of confidence interval for batch means  :   "+ CI_upper_mean);
	    System.out.println("Upper limit of confidence interval for batch means:    "+ CI_upper_mean);
	    
	    fileWriter.newLine();
	    fileWriter.write("lower limit of confidence interval for batch means  :    " + CI_lower_mean);
	    System.out.println("lower limit of confidence interval for batch means :    "+ CI_lower_mean);
	    
	    fileWriter.flush();
	    fileWriter.close();
	    
	    System.out.println("Simulation Done!");
	}
}
