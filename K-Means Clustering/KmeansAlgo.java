import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;


 class DataPoint {
	 	
	private int id;
    private double x_co_ordinate;
    private double y_co_ordinate;
    
    
    public DataPoint (int id , double x , double y)
    {
    	this.id = id; 
    	this.x_co_ordinate = x;
    	this.y_co_ordinate = y; 
    	
	}
    
    public DataPoint (double x , double y)
    {
    	this.x_co_ordinate = x;
    	this.y_co_ordinate = y; 
		
	}
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getX_co_ordinate() {
		return x_co_ordinate;
	}
	public void setX_co_ordinate(double x_co_ordinate) {
		this.x_co_ordinate = x_co_ordinate;
	}
	public double getY_co_ordinate() {
		return y_co_ordinate;
	}
	public void setY_co_ordinate(double y_co_ordinate) {
		this.y_co_ordinate = y_co_ordinate;
	}
   
    

}
 
 
  class Cluster {
		
	 	private int id;
	 	private ArrayList<DataPoint> dataInstaces = new ArrayList<DataPoint>();;
		private DataPoint centroid;
		
		public Cluster(int id , double x , double y)
		{
			this.id = id;
			this.centroid  = new DataPoint(x,y);		 
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public ArrayList<DataPoint> getDataInstaces() {
			return dataInstaces;
		}
		public void setDataInstaces(ArrayList<DataPoint> dataInstaces) {
			this.dataInstaces = dataInstaces;
		}
		public DataPoint getCentroid() {
			return centroid;
		}
		public void setCentroid(DataPoint centroid  ) {
					
			this.centroid = centroid;
		}
		
		public void assignPoint( DataPoint d)
		{
			this.dataInstaces.add(d);
			
		}
		public String toStringDataInstaces()
		{
			String dataInstanceIDs =""; 
			
			for (DataPoint d : this.getDataInstaces())
			{
				dataInstanceIDs+= Integer.toString(d.getId())+",";
			}
			return dataInstanceIDs; 
		}
		
		public void clearDataInstaces()
		{
			this.dataInstaces.clear();
		}
		
		public boolean centroidEquals(DataPoint d)
		{
			boolean equalpoints = false; 
			if ( (d.getX_co_ordinate() == this.centroid.getX_co_ordinate()) && (d.getY_co_ordinate() == this.getCentroid().getY_co_ordinate()) )
				equalpoints = true; 
			
			return equalpoints;
		}
 }

public class KmeansAlgo {

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
	
	public double calculateEuclideanDistance(DataPoint a , DataPoint b ) 
	{
		double distance = 0.0; 
	
		double xdiff = (b.getX_co_ordinate() - a.getX_co_ordinate());
		double ydiff = (b.getY_co_ordinate() - a.getY_co_ordinate());
		distance = Math.sqrt( (xdiff * xdiff) + (ydiff * ydiff) );
		return distance; 
	}
	
	public double calculateSSE(ArrayList<Cluster> kclusters )
	{
		double distanceSum=0;
		double sse = 0;
		
		for ( Cluster c : kclusters )
		{
			for (DataPoint d : c.getDataInstaces())
				distanceSum+= calculateEuclideanDistance( d , c.getCentroid());
			
			sse+=(distanceSum * distanceSum);
			
		}
		return sse;
	}
	
	
	
	
	public static void main(String[] args) throws IOException
	{
	
		KmeansAlgo ka = new KmeansAlgo();
		int k = Integer.parseInt(args[0]); //no of clusters
		String ipfilename = args[1];
		String opfilename = args[2];
		ArrayList<ArrayList<String>> ipdata = ka.readData(ipfilename);
		ArrayList<Cluster> kclusters = new ArrayList<Cluster>();
		ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
		HashMap<Integer, ArrayList<Integer>> outputmap = new HashMap<Integer, ArrayList<Integer>>();
		double min_x =0.0 , min_y=0.0 ,max_x =0.0, max_y=0.0; 
		
		for (int i=0; i< ipdata.size() ; i++)
		{
				int id = Integer.parseInt( ipdata.get(i).get(0)); 		
				
				double x = Double.parseDouble( ipdata.get(i).get(1));
				
				if(x<min_x)
					min_x = x;	
				else if (x > max_x)
					max_x = x;
				
				double y = Double.parseDouble( ipdata.get(i).get(2));
				
				if(y<min_y)
					min_y = y;	
				else if (y > max_y)
					max_y = y;
				
				DataPoint d = new DataPoint(id,x,y);
				datapoints.add(d);	
			
		}
		
		
		
		//Considering k Random Centroid
		for (int i=0 ; i<k ; i++)
		{	
		
		Random r = new Random();
    	double cx = min_x + (max_x - min_x) * r.nextDouble();
    	double cy = min_y + (max_y - min_y) * r.nextDouble();
    	Cluster c = new Cluster(i,cx,cy);
    	kclusters.add(c);
 	
		}
		
		int iteration = 0; 
		boolean recomputeNotRequired [] = new boolean [k];
		boolean convergence = false; 
		
		
		while ( (!convergence) && iteration < 25)
		{	
			
		//clear clusters

			for (int j=0 ; j< kclusters.size() ; j++)
			{
				kclusters.get(j).clearDataInstaces();
			}
			
		//Assignment Step
			
		for (int i=0 ; i<datapoints.size() ; i++  )
		{	
			
			HashMap<Integer, Double> distanceMap = new HashMap<Integer, Double>();
			int clusterID ; 
			for (int j=0 ; j< kclusters.size() ; j++)
			{
				double distance = ka.calculateEuclideanDistance(datapoints.get(i), kclusters.get(j).getCentroid());
				distanceMap.put(j, distance);
			}	
			
			Double minDistance=(Collections.min(distanceMap.values())); 		
			
			for (Entry<Integer, Double> entry : distanceMap.entrySet())
			{  
	             if (entry.getValue()==minDistance) 
	             {
	            	 clusterID = entry.getKey();
	            	 kclusters.get(clusterID).assignPoint(datapoints.get(i));
	            	 
	             }
	  		 }
			
			
			
		}
		
		//Update Step
		
		for (int j=0 ; j< kclusters.size() ; j++)
		{
		
			
			
			ArrayList<DataPoint> clusterPoints = kclusters.get(j).getDataInstaces();
			int size = clusterPoints.size();
			double sumX = 0.0; 
			double sumY = 0.0;
			for(DataPoint d : clusterPoints)
			{
				sumX+=d.getX_co_ordinate();
				sumY+=d.getY_co_ordinate();
			}
			double cx = sumX/size ; 
	    	double cy = sumY/size ;
	    	DataPoint updatedCentroid =  new DataPoint(cx,cy);
	    	if( ! kclusters.get(j).centroidEquals(updatedCentroid))
		    	kclusters.get(j).setCentroid(updatedCentroid);	    	
	       	else 
	       		recomputeNotRequired[j] = true;
	    		
		}
	    	
		
		for (boolean r : recomputeNotRequired )
		{
		int convergedClusters =0;	
	    if	( r )
	    	convergedClusters++;
	    
	    if ( convergedClusters == k)
	    convergence = true;	
	    
	    	
		}
		iteration++;
		}
		
		
		/*for (int j=0 ; j< kclusters.size() ; j++)
		{
		System.out.println( " \n cluster" + kclusters.get(j).getId() + "\t Points \t") ; 
		for (DataPoint d :  kclusters.get(j).getDataInstaces() )
		System.out.print( "\t \t" + d.getId()  );		
		}*/
		
		
		BufferedWriter br = new BufferedWriter(new FileWriter(opfilename));
		StringBuilder sb = new StringBuilder();
		for (int j=0 ; j< kclusters.size() ; j++)
		{
		String row = Integer.toString(kclusters.get(j).getId())+"\t"+kclusters.get(j).toStringDataInstaces();
		sb.append(row);			
		sb.append("\n");
		}
		double sse = ka.calculateSSE(kclusters);
		String SSE = "\n SSE = \t"+sse;
		System.out.println(SSE);
		
		sb.append(SSE);
		br.write(sb.toString());
		System.out.println("Written to output file");
		br.close();
		
		
		
	}
		
}
