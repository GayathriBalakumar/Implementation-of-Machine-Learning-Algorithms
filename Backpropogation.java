import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

class Neuron {
	
	private String type;
	private int noOfInputs;
	private double input[];
	private double weight[];
	private double bias;
	private double output;
	private double delta;

	public Neuron(double ip[] )
	{
		this.noOfInputs = ip.length;
		this.setInput(ip);
		Random random = new Random();
		this.setBias(random.nextGaussian());
		double weightVector [] = new double [ip.length];
		for (int i =0; i< ip.length ; i++)		
			weightVector[i] = -0.5 + random.nextGaussian();			
		this.setWeight(ip);
		this.setOutput();
	}
	
	public Neuron (double ip)
	{
		this.noOfInputs = 1;
		this.setOutput(ip);
		this.setType("inputNode");
		System.out.println("input node set with" +ip);
	}	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNoOfInputs() {
		return noOfInputs;
	}

	public void setNoOfInputs(int noOfInputs) {
		this.noOfInputs = noOfInputs;
	}

	public double[] getInput() {
		return input;
	}

	public void setInput(double[] input) {
		this.input = input;
	}
	public double getBias() {
		return bias;
	}
	public void setBias(double bias) {
		this.bias = bias;
	}
	public double[] getWeight() {
		return weight;
	}
	
	public void setWeight(double[] weight) {
		this.weight = weight;
	}
	
	public void updateWeight( double eta, double delta, double [] prevOutput  ){
		double [] 	updatedW = new double [prevOutput.length];
		double [] currentW = this.getWeight();
		for (int i=0;i<prevOutput.length ; i++)
		updatedW[i] = currentW[i] + (eta)*(delta)*(prevOutput[i]);
		this.setWeight(updatedW);
	}
	public double getOutput() {
		
		return output;
	}
	public void setOutput(double output) {
		this.output = output;
	}
	public void setOutput() {
		double sum =0.0;
		for(int i=0; i< this.noOfInputs ; i++)
		{
			double product = (this.input[i])*(this.weight[i]);
			sum+=product;
		}
		double op = this.bias + sum;	
		double x =  1 / (1 + Math.pow(Math.E,(-1*op)));
		//System.out.println("sigmoid op" + x);
		this.output = x;
	}
	
	public void setOutput(double [] input , double [] weight) {
		double sum =0.0;
		for(int i=0; i< input.length ; i++)
		{
			double product = (input[i])*(weight[i]);
			sum+=product;
		}
		double op = this.bias + sum;	
		double x =  1 / (1 + Math.pow(Math.E,(-1*op)));
		this.output = x;
	}
	
	public double getDelta() {
		return delta;
	}

	public void setDelta(double x , double w) {
		double d = 0.00;
		if (this.getType().equalsIgnoreCase("outputNode"))
		d = (x)*(1 - x)*(w - x); 
		else 
		d = (x)*(1-x)*(w*x);	
		this.delta = d;
	}
	
	 	
}
	
class NeuralNetwork {
	
	
	
	public Neuron [] buildInputLayer(double [] rowData)
	{		
		Neuron inputLayer [] = new Neuron[rowData.length];
		for (int j=0; j<rowData.length ; j++)
	 	{
	 		double ip = rowData[j]; 
	 		inputLayer[j] = new Neuron(ip);
	 	}

		return inputLayer;
	}
	
	public ArrayList<Neuron> buildHiddenLayer(int noOfNodes, double [] inputVector)
	{		
		ArrayList<Neuron> HiddenLayer = new ArrayList<Neuron> ();
		
		for (int j=0; j<noOfNodes ; j++)
	 	{ 
			Neuron H = new Neuron(inputVector);
			H.setType("hiddenNode");
			HiddenLayer.add(H);
			System.out.println(j+1 + "hidden neuron set");
	 	}

		return HiddenLayer;
	}
	
	public Neuron buildOutputLayer(double [] inputVector)
	{		
		Neuron outputLayer  = new Neuron(inputVector);
		System.out.println("output neuron set");
		return outputLayer;
	}
	
	
	public double forwardPass (Neuron [] ip ,  ArrayList<ArrayList<Neuron>> hl , Neuron op, double nwInputs [])
	{
		double nwOutput = 0.00;
		double inputVector [] = new double [ip.length]; 
		for (int i =0 ; i<ip.length ; i++)
		{
			ip[i].setOutput(nwInputs[i]);
			inputVector[i] = ip[i].getOutput(); 
		}	
		
		for (int i=0; i<hl.size() ; i++)
		{
			double hiddenVector[] =new double [hl.get(i).size()]; 
			for (int j=0; j< hl.get(i).size() ; j++)
			{	
			hl.get(i).get(j).setInput(inputVector);	
			hl.get(i).get(j).setOutput(hl.get(i).get(j).getInput() , hl.get(i).get(j).getWeight());
			hiddenVector[j] = hl.get(i).get(j).getOutput();
			}
			inputVector = hiddenVector;
		}
		op.setInput(inputVector);
		op.setOutput(op.getInput() , op.getWeight());
		nwOutput = op.getOutput();
		return nwOutput;
		
	}
	
	
	public ArrayList<Neuron> backpropHiddenLayerFirst(ArrayList<Neuron> currentLayer, ArrayList<Neuron> nextLayer, Neuron [] ipLayer , double eta  )
	{		
		double [] prevOutput = new double [ipLayer.length];
		double weigtsNextLayer [][] = new double [currentLayer.size()][nextLayer.size()];
		double weigts [] = new double [currentLayer.size()];
		double updatedBias =0.0;
		
		for (int j=0; j<ipLayer.length; j++)
			prevOutput[j] = ipLayer[j].getOutput();
			
		for (int j=0; j<nextLayer.size(); j++)
		{
		double w [] = nextLayer.get(j).getWeight();
		for ( int i=0 ; i < currentLayer.size() ; i++)
			weigtsNextLayer [i][j] = w[i]; 
		}
		
		for ( int i=0 ; i < currentLayer.size() ; i++)
		{
			double sum =0.0;
			for (int j=0; j<nextLayer.size(); j++)
				sum += weigtsNextLayer[i][j];
			weigts[i]=sum;
		}
		
		for ( int i=0 ; i < currentLayer.size() ; i++)
			{		
			currentLayer.get(i).setDelta(currentLayer.get(i).getOutput(), weigts[i]);
			currentLayer.get(i).updateWeight(eta, currentLayer.get(i).getDelta(), prevOutput);
			updatedBias = (eta)*(currentLayer.get(i).getDelta());
			currentLayer.get(i).setBias(updatedBias);
			}
			
		return currentLayer;
		
	}
	
	public ArrayList<Neuron> backpropHiddenLayers(ArrayList<Neuron> currentLayer, ArrayList<Neuron> nextLayer, ArrayList<Neuron> prevLayer , double eta  )
	{		
			
		double [] prevOutput = new double [prevLayer.size()];
		double weigtsNextLayer [][] = new double [currentLayer.size()][nextLayer.size()];
		double weigts [] = new double [currentLayer.size()];
		double updatedBias =0.0;
		
		for (int j=0; j<prevLayer.size(); j++)
			prevOutput[j] = prevLayer.get(j).getOutput();
		
		
		for (int j=0; j<nextLayer.size(); j++)
		{
		double w [] = nextLayer.get(j).getWeight();
		for ( int i=0 ; i < currentLayer.size() ; i++)
			weigtsNextLayer [i][j] = w[i]; 
		}
		
		for ( int i=0 ; i < currentLayer.size() ; i++)
		{
			double sum =0.0;
			for (int j=0; j<nextLayer.size(); j++)
				sum += weigtsNextLayer[i][j];
			weigts[i]=sum;
		}
		
		for ( int i=0 ; i < currentLayer.size() ; i++)
			{			
			currentLayer.get(i).setDelta(currentLayer.get(i).getOutput(), weigts[i]);
			currentLayer.get(i).updateWeight(eta, currentLayer.get(i).getDelta(), prevOutput);
			updatedBias = (eta)*(currentLayer.get(i).getDelta());
			currentLayer.get(i).setBias(updatedBias);
			}
			
		return currentLayer;
		
	}
	
	
	public ArrayList<Neuron> backpropHiddenLayerLast(Neuron out, ArrayList<Neuron> hl, ArrayList<Neuron> prevLayer , double eta  )
	{		
		double weigts [] = out.getWeight(); 	
		double [] prevOutput = new double [prevLayer.size()];
		double updatedBias =0.0;
		for (int j=0; j<prevLayer.size(); j++)
			prevOutput[j] = prevLayer.get(j).getOutput();
		for ( int i=0 ; i < hl.size() ; i++)
			{
			hl.get(i).setDelta(hl.get(i).getOutput(), weigts[i]);
			hl.get(i).updateWeight(eta, hl.get(i).getDelta(), prevOutput);
			updatedBias = (eta)*(hl.get(i).getDelta());
			hl.get(i).setBias(updatedBias);
			}
			
		return hl;
		
	}
	public  ArrayList<Neuron> backpropHiddenLayer1(Neuron out,  ArrayList<Neuron> hl, Neuron [] input, double eta  )
	{		
		double weigts [] = out.getWeight(); 	
		double [] prevOutput = new double [input.length];
		double updatedBias =0.0;
		for (int j=0; j< input.length; j++)
			prevOutput[j] = input[j].getOutput();
		for ( int i=0 ; i < hl.size() ; i++)
			{
			hl.get(i).setDelta(hl.get(i).getOutput(), weigts[i]);
			hl.get(i).updateWeight(eta, hl.get(i).getDelta(), prevOutput);
			updatedBias = (eta)*(hl.get(i).getDelta());
			hl.get(i).setBias(updatedBias);
			}
			
		return hl;
		
	}
	
	public Neuron backpropOutputLayer(Neuron out, double actualoutput, ArrayList<Neuron> prevLayer , double eta  )
	{		
		double nwOutput = out.getOutput(); 	
		double [] prevOutput = new double [prevLayer.size()];
		double updatedBias =0.0;
		out.setDelta(nwOutput, actualoutput);
		for (int j=0; j<prevLayer.size(); j++)
			prevOutput[j] = prevLayer.get(j).getOutput();
		out.updateWeight(eta, out.getDelta(), prevOutput);
		updatedBias = (eta)*(out.getDelta());
		out.setBias(updatedBias);
		return out;
		
	}

	public void printNetwork( Neuron [] inputLayer ,  ArrayList<ArrayList<Neuron>> hiddenLayers , Neuron outputLayers)
	{ 
		System.out.println("\t-----input Layer------");
		for (int i =0 ; i<inputLayer.length ; i++)
		{
			System.out.println("\t\t Neuron" + (i+1) + " : Input \t" + inputLayer[i].getOutput());	
		}	
		
		for (int i=0; i<hiddenLayers.size() ; i++)
		{
		System.out.println("\t\t\t-----hidden Layer"+ (i+1) + "------");	 
			for (int j=0; j< hiddenLayers.get(i).size() ; j++)
			{
				for (double w : hiddenLayers.get(i).get(j).getWeight())
				System.out.println("\t\t\t\t Neuron" + (j+1) + " : Weight \t" +w );	
				System.out.println("\t\t\t\t Neuron" + (j+1) + " : Bias \t"+ hiddenLayers.get(i).get(j).getBias());	
			}
		}	
	
		System.out.println("\t-----output Layer------");
		for (double w : outputLayers.getWeight())
		System.out.println("\t\t Neuron 1  : Weight \t" +w);
		System.out.println("\t\t Neuron 1  : Bias \t" + outputLayers.getBias());
		System.out.println("\t\t Neuron 1  : Output \t" + outputLayers.getOutput());
		
	}
}	


public class Backpropogation {

	
public 	ArrayList<ArrayList<String>> readData(String fileName) throws IOException{

			ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
			
			File file = new File(fileName);
			Scanner input;
			input = new Scanner(file);
			
			while(input.hasNext()){
				String[] RowData = input.next().split(",");
				data.add(new ArrayList<String>(Arrays.asList(RowData)));
			}
			
			
			input.close();
			return data;
		}

public double calculateNetworkError(double nwOutput, double actualOutput)
{
	double error =0.00;					
 	double outputDiff = nwOutput - actualOutput;
 	error =  (0.5)*(Math.pow(outputDiff, 2));
	
	return error;
}
	
public static void main(String[] args) throws IOException
{
	
		Backpropogation b = new Backpropogation();
		String inputDataset= args[0];
	 	double trainingPercent = Double.parseDouble(args[1]);
	 	double errorTolerance = Double.parseDouble(args[2]);
	 	int noOfHiddenLayers = Integer.parseInt(args[3]);
	 	int HiddenNeurons[] = new int[noOfHiddenLayers] ; 
	 	for (int i=0; i< noOfHiddenLayers ; i++)
	 	HiddenNeurons[i] = Integer.parseInt(args[4+i]);
	 	double learningRate = 0.2; //assumption 
	 	
		ArrayList<ArrayList<String>> inputData = b.readData(inputDataset);
	 	 	 	
	 	int rows = inputData.size();
		int columns =  inputData.get(0).size();
		int noOfInputNodes = columns-1; 		
		
		int trainingdatarows = (int) (rows*trainingPercent)/100;
		double trainingData [][] = new double [trainingdatarows][noOfInputNodes];
		for (int i=0; i< trainingdatarows ; i++)
		{
		for (int j=0; j< noOfInputNodes; j++)
			trainingData[i][j]  = Double.parseDouble(inputData.get(i).get(j).trim());
		}
		
		System.out.println("************building network with given input data and specified hidden layers and randon weights************");	
		
		double ipVector[] = trainingData[0]; 
		double ip[] = ipVector ;
		NeuralNetwork nn = new NeuralNetwork();
	    
	    Neuron [] inputNodes = nn.buildInputLayer(ipVector);
	    for (int i=0; i<inputNodes.length ; i++)
	    	ip[i] = inputNodes[i].getOutput();
	    
	    ArrayList<ArrayList<Neuron>> hiddenNodes = new ArrayList<ArrayList<Neuron>>();
	    
		for (int i=0; i<noOfHiddenLayers ; i++)
			{	
			System.out.println("Hidden Layer" +(i+1));	
			ArrayList<Neuron> h = nn.buildHiddenLayer(HiddenNeurons[i], ip);
			hiddenNodes.add(h); 
				
			for (int j=0; j<HiddenNeurons[i] ; j++)
			ip[j] = h.get(j).getOutput(); 		
			
			}
		 	
		Neuron outputNode = nn.buildOutputLayer( ip);
		outputNode.setType("outputNode");
		
		System.out.println("************ Network Built! *************");
		
		nn.printNetwork(inputNodes, hiddenNodes, outputNode); //print network
		
		System.out.println("************ Network Training ...... *************");
		double nwError =0.0;
		double testingError =0.0;
		System.out.println("No.Of.Training.Records" +trainingData.length);
		
		for (int k=0; k<trainingData.length ; k++ )
		{
		//System.out.println("training with record " + (k+1));	
		ipVector = trainingData[k];
		double nwOutput  = nn.forwardPass(inputNodes, hiddenNodes, outputNode, ipVector);
		double actualOutput = Double.parseDouble(inputData.get(k).get(noOfInputNodes).trim());	
		//System.out.println("actual output  : " + actualOutput );
		//System.out.println("network output  : " + nwOutput );
		nwError = b.calculateNetworkError(nwOutput, actualOutput);
		int iterantion=0;
		while ((nwError > errorTolerance) && (iterantion < 35))	
		{
			outputNode= nn.backpropOutputLayer(outputNode, actualOutput, hiddenNodes.get(noOfHiddenLayers -1), learningRate);
			//System.out.println("delta = " +outputNode.getDelta() );
			
			for (int i=0; i<noOfHiddenLayers ; i++)
			{	
				
				 if (noOfHiddenLayers == 1)
				 {
				 	
					 ArrayList<Neuron> n = nn.backpropHiddenLayer1(outputNode, hiddenNodes.get(i), inputNodes, learningRate);
					 hiddenNodes.set(i, n);
				 }
				 else if (i == 0)
				 {
					 ArrayList<Neuron> n =  nn.backpropHiddenLayerFirst(hiddenNodes.get(i), hiddenNodes.get(i+1), inputNodes, learningRate);
					 hiddenNodes.set(i, n);
				 }	  			
				 else if (i == (noOfHiddenLayers -1))
				 {
					 ArrayList<Neuron> n  =nn.backpropHiddenLayerLast(outputNode, hiddenNodes.get(i), hiddenNodes.get(i-1), learningRate);
					 hiddenNodes.set(i, n);
				 }
				 else
				 {
					 ArrayList<Neuron> n =  nn.backpropHiddenLayers(hiddenNodes.get(i), hiddenNodes.get(i+1), hiddenNodes.get(i-1), learningRate);
					 hiddenNodes.set(i, n);		 
				 }	 
								
			}	
			double newNwOutput  = nn.forwardPass(inputNodes, hiddenNodes, outputNode, ipVector);
			nwError = b.calculateNetworkError(newNwOutput, actualOutput);
			iterantion++;
			} 
			
		}
		System.out.println("computed training error  : " + nwError );
		System.out.println("************* Network training completed! *************");
		System.out.println("************* Updated network details! ****************");
		nn.printNetwork(inputNodes, hiddenNodes, outputNode);
		
		System.out.println("************* Network testing.......... ***************");
		
		
		int noOfTestingdatarows = rows - trainingdatarows;
		double testingData [][] = new double [noOfTestingdatarows][noOfInputNodes];
		int k=0;
		System.out.println("No.Of.Testing.Records" +noOfTestingdatarows);
		while (k<noOfTestingdatarows)
		{
		for (int i=trainingdatarows; i< rows ; i++)
		{
		for (int j=0; j< noOfInputNodes; j++)
			testingData[k][j]  = Double.parseDouble(inputData.get(i).get(j).trim());
		
		}
		
		ipVector = testingData[k];
		double nwOutput  = nn.forwardPass(inputNodes, hiddenNodes, outputNode, ipVector);
		double actualOutput = Double.parseDouble(inputData.get(k).get(noOfInputNodes).trim());	
		//System.out.println("actual output  : " + actualOutput );
		//System.out.println("network output  : " + nwOutput );
		testingError = b.calculateNetworkError(nwOutput, actualOutput);
			
		k++;    
		
		}
		
		System.out.println("computed testing error  : " + testingError );
		
	}
	}
	


