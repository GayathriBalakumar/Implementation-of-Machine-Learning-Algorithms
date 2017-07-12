import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;

public class PreProcess {
	
	public ArrayList<ArrayList<String>> readData(String fileName) throws IOException{

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
	
	public void writeData (String data[][], String ouputfilename ) throws IOException
	{
		BufferedWriter br = new BufferedWriter(new FileWriter(ouputfilename));
		StringBuilder sb = new StringBuilder();
		for (int i =0; i<data.length ; i++)
		{
		for (String element : data[i]) {
			//System.out.println("data to write in the file" +element);
		 sb.append(element);
		 sb.append(",");
			}
		sb.append("\n");
		}
		br.write(sb.toString());
		System.out.println("processed data written to output file");
		br.close();
	}

	public boolean hasNullValue(ArrayList<String> rowdata)
	{
		boolean hasNullData =false; 
		for (int i=0; i< rowdata.size() ; i++)
		{	
		if ( ( null == rowdata.get(i)) || ( rowdata.get(i).length() < 1 )   ||  ( rowdata.get(i).equals(""))  ||   ( rowdata.get(i).equals(" ") ) ||   ( rowdata.get(i).equals("?") ) )
		{
		hasNullData	= true; 
		System.out.print("missing data at column" +i);
		}
		}
		return hasNullData; 
	}
	
    public ArrayList<ArrayList<String>> missingDataCheck (ArrayList<ArrayList<String>> data1) throws IOException
	{
    	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
			for (int i=0; i< data1.size() ; i++)
			{ 			
				ArrayList<String> rowData = data1.get(i); 
				if (hasNullValue(rowData) == false)
			    data.add(rowData);
				else
				System.out.println(" \n missing data at row" +i);	
				
			}
		return data;  	
	}
	
    public double findMean(ArrayList<String> columndata) throws NumberFormatException
    {
    	double  sum =0.0;
    	double mean;
    	for (int i=0; i< columndata.size() ; i++)
		{ 	
    		double x = Double.parseDouble(columndata.get(i).trim());
    		//System.out.println("numeirc data" +x);
    		sum+=x; 			
		}
    	mean = sum/columndata.size(); 
    	System.out.println("mean" +mean);
    	return mean; 
    }
    
    public double findStandardDeviation(ArrayList<String> columndata,double mean)
    {
    	double sumMDS =0.0;
    	double standardDeviation; 
    	for (int i=0; i< columndata.size() ; i++)
		{ 	 
    		double x = Double.parseDouble(columndata.get(i).trim()); 
    		double meanDiff = (x-mean);
    		//System.out.println("x - mean" +meanDiff);
    		DecimalFormat df = new DecimalFormat("#.###");
    		df.setRoundingMode(RoundingMode.CEILING);
    		double meanDiffRound = Double.parseDouble(df.format(meanDiff));	
    		//System.out.println("x - mean round" +meanDiffRound);
    		double meanDifferenceSquare = Math.pow(meanDiffRound, 2);
    		//System.out.println("x - mean square" +meanDifferenceSquare);	
    		sumMDS+=meanDifferenceSquare ; 
		}
    	//System.out.println("sum MDS before SD" +sumMDS);
    	standardDeviation = Math.sqrt((sumMDS/columndata.size()));
    	System.out.println("SD" +standardDeviation);
    	return standardDeviation; 
    	 
    }
    
    public ArrayList<Double> standardizeNumericData (ArrayList<String> columndata,double mean,double sd) throws IOException
	{
    	ArrayList<Double> standardData = new ArrayList<Double>(); 
    	for (int i=0; i< columndata.size() ; i++)
		{
    	double x = (Double.parseDouble(columndata.get(i)) - mean); 
    	//System.out.println("standardised data " +x);
    	standardData.add(x/sd); 
		}
    	
    	return standardData;
	}
    
    public String [][] dataPreprocess (ArrayList<ArrayList<String>> data1) throws IOException
	{
    	int rows = data1.size();
    	int columns = data1.get(0).size();
    	String standardizedData [][] = new String [rows][columns] ; 
    	double sd;	
    	for (int i=0; i< columns ; i++)
		{
    		
    		ArrayList<String> columndata = new ArrayList<String>();
    		for (int j=0; j < rows ; j++)
    		{		
    			String s = data1.get(j).get(i);
    			//System.out.println("data" +s);
    			columndata.add(s);	
    			
    		}	
    		
    		//Pattern p = Pattern.compile("^([^a-zA-Z]*([a-zA-Z]+)[^a-zA-Z]*)+$");
    		//Matcher m = p.matcher(columndata.get(i));
    		//if(m.matches())
    		if ( Pattern.matches(".*[a-zA-Z].*" , columndata.get(i)) )
    		{
    			System.out.println("textual column data at column number" +i);
    			ArrayList<String> standardizedcolumn = categorizeStringData(columndata);
    			for (int j=0; j< standardizedcolumn.size() ; j++)
    			standardizedData[j][i]=	standardizedcolumn.get(j);
    		}	
    		else
    		{
    		System.out.println("numeric column data at column number" +i);
    
    		System.out.println("data at column number" +i + "row 0" +columndata.get(2).toString());
    		//for (String s: columndata )
    				
    		double mean = findMean(columndata);
    	    sd = findStandardDeviation(columndata, mean); 
    	    ArrayList<Double> standardizedcolumn = standardizeNumericData(columndata,mean,sd);
    	    for (int j=0; j< rows ; j++)
    	    {
    	    	Double d = standardizedcolumn.get(j); 
    	    	standardizedData[j][i]=  d.toString();
    	    }
    	  
		}    	
		}
/*    	for (int i=0; i< rows ; i++)
	 	{
	 		System.out.println("\n");
	 		for (int j=0; j< columns ; j++)
	 			System.out.println("\t" +standardizedData[i][j]);
	 	}*/	
    	return standardizedData; 
    	
	}
	
    
    public ArrayList<String> categorizeStringData (ArrayList<String> column) throws IOException
   	{
       	ArrayList<String> categorisedData = new ArrayList<String>();
       	ArrayList<String> uniqueData  = new ArrayList<String>();
        for(String s : column) {
            if(!uniqueData.contains(s))
            	uniqueData.add(s);
            System.out.println("\t" +uniqueData.size() +" unique data added" );           
        }
       	             
        for (int i=0; i< column.size() ; i++)
		{
        	for (int j=0; j< uniqueData.size() ; j++)
        	{	
        	if (column.get(i).equals(uniqueData.get(j)))
        		categorisedData.add(Integer.toString(j));	
        	}
        		
		}
    	
       	return categorisedData; 
       	
   	}

	public static void main(String[] args) throws IOException
	{
		String inputDataPath= args[0];
	 	String outputDataPath = args[1];
	 	PreProcess p = new PreProcess(); 
	 	ArrayList<ArrayList<String>> dataSet = p.readData(inputDataPath);
	 	ArrayList<ArrayList<String>> dataSetPreProcessed = p.missingDataCheck(dataSet);
	 	System.out.println("data size before pre process " +dataSet.size() );
	 	System.out.println("data size after pre process " +dataSetPreProcessed.size() );
	 	//for (int i=0; i< dataSetPreProcessed.size() ; i++)	
	 	//System.out.println("column size after pre process " +dataSetPreProcessed.get(i).size()  + "at row " +i);	 	
	 	String [][] processedData = p.dataPreprocess(dataSetPreProcessed);
	 	System.out.println("data size after numerical standadization " +processedData.length);
	 	p.writeData(processedData, outputDataPath);
		
	}
	
}


