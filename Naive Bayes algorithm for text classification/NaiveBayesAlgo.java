
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class NaiveBayesAlgo {
	
	public ArrayList<String> getFolders(String trainingDataSet)
	{
		ArrayList<String> folderNames = new ArrayList<String>();
		File folder = new File(trainingDataSet);
		File[] listOfFiles = folder.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isDirectory())
	      {
	    	  String foldername = listOfFiles[i].getName();
	    	  //System.out.println("folder name " +foldername);
	    	  folderNames.add(foldername);
	      }
	      }
		return folderNames;	
	}	
	
	public  int getWordCount(String word, HashMap<String, Integer> wordCountMap)
	   {
	      if (wordCountMap.containsKey(word))
		   return wordCountMap.get(word); 
	      else
	       return 0;
	   }
	
	public HashMap<String, Integer> extractVocabulary(String folderName , ArrayList<String> stopWordList ) throws FileNotFoundException
	{
		HashMap<String, Integer> classWiseVocabulary = new HashMap<String,Integer>();
		
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile())
	      {
	        String f = listOfFiles[i].getName();
	        f.concat(".txt");
	        String fileName = folderName+"/"+f;
	    	File file = new File(fileName);
	  		Scanner input = new Scanner(file);
	  		while(input.hasNext()){
	  			String line = input.nextLine();
	  			String words[] = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
	    	   for (String word: words)
	    	   {			    		   
	    		   if(!stopWordList.contains(word))	 
	    		   { 
	    		   //System.out.println("class file word =" +word);	   
	    		   int wordCount = getWordCount(word,classWiseVocabulary) +  1 ; 
	    		   classWiseVocabulary.put(word, wordCount);
	    		   }		    		   
	    	   }
	      }
	  		
	  	input.close();	
	    }
	
	    /*  for(String word : classWiseVocabulary.keySet( ))
	      {
	         System.out.println("\t" + word +"\t" + classWiseVocabulary.get(word) );
	      }*/
	    }
		return classWiseVocabulary;
	}
	
	public double calculateClassPrior(String folderName, int totdoc)
	{		
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		double prior = (double) listOfFiles.length/totdoc;
		//System.out.println("prior =" +prior);
		return prior; 
	}
	
	public Double calculateConditionalProb(HashMap<String, Integer> Classvocab, String term)
	{		
		int sum=0;
		int termCount =0;
		for (Map.Entry<String, Integer> entry : Classvocab.entrySet())
		sum+=entry.getValue();
		sum+=Classvocab.size();	
		//String term = entry.getKey();
		if(Classvocab.containsKey(term))
		termCount = Classvocab.get(term) + 1;
		else
		termCount=1;
		//System.out.println("termCount =" +termCount);
		double ConditionalProb = (double) termCount/sum;			
		return ConditionalProb; 
	}
	
	public ArrayList<String> getFileWords(File testfileName , ArrayList<String> stopWordList ) throws FileNotFoundException
	{
		ArrayList<String> docWordList = new ArrayList<String>();
		//File testfileName = new File(testfile);
		Scanner input = new Scanner(testfileName);
  		while(input.hasNext()){
  			String line = input.nextLine();
  			String words[] = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
    	   for (String word: words)
    	   {	
    		   //System.out.println("test file word =" +word);
    		   if(!stopWordList.contains(word))	 
    			  docWordList.add(word); 		   
    	   }		
  		}
  	
  		input.close();
		return docWordList;
		
	}
	
	public static void main(String[] args) throws IOException {
		 Scanner scanner = new Scanner(System.in);
		 System.out.println("enter the training folder");
		 String trainingFolder= scanner.nextLine();
		 System.out.println("enter the testing folder");
		 String testingFolder=scanner.nextLine();
		//String trainingFolder = "C:/20NewsGroups_MyDataSet/20NewsGroups_MyDataSet/trainning_data_set";
		//String testingFolder = "C:/20NewsGroups_MyDataSet/20NewsGroups_MyDataSet/test_data_set";		
		ArrayList<String> classLabels = new ArrayList<String>();
		ArrayList<HashMap<String, Integer>> vocabulary = new ArrayList<HashMap<String, Integer>>();
		ArrayList<Double> prior = new ArrayList<Double>();
		ArrayList<String> testDocWordList = new ArrayList<String>();
		ArrayList<String> testLabels = new ArrayList<String>();
		File stopwordDoc = new File("stopwords.txt");
		ArrayList<String> stopWordList = new ArrayList<String>();
		Scanner in = new Scanner(stopwordDoc);
		while(in.hasNext())
		{
			String stopWord = in.nextLine();
			stopWordList.add(stopWord);
		}	
		in.close();
		
		NaiveBayesAlgo nb = new NaiveBayesAlgo();		
		classLabels = nb.getFolders(trainingFolder);
		
		int totDoc = 0;
		for (String folderName:classLabels )
		{			
			String folder = trainingFolder + "/" + folderName;
			File classfolder = new File(folder);
			File[] listOfFiles = classfolder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile())
				totDoc++;
			}	
					
		}	
//		System.out.println("total no.of.documents :" +totDoc);
		
		
		
		for (int i=0 ; i< classLabels.size() ; i++)
		{
			
			String folder = trainingFolder + "/" + classLabels.get(i) ;
			HashMap<String, Integer> classVocab = new HashMap<String,Integer>();
			classVocab = nb.extractVocabulary(folder,stopWordList);
			vocabulary.add(classVocab);
			double classPrior = nb.calculateClassPrior(folder, totDoc);
			prior.add(classPrior);			
		}	
			
		
		
		testLabels = nb.getFolders(testingFolder);
		int totTestDoc =0;
		int correctlyClassfied = 0;
		for (int k=0 ; k< testLabels.size() ; k++)
		{		
		String testingClassFolder = testingFolder + "/" + testLabels.get(k);
		File testfolder = new File(testingClassFolder);
		File[] listOfTestFiles = testfolder.listFiles();
	    for (int j = 0; j < listOfTestFiles.length; j++) {
	    if (listOfTestFiles[j].isFile())
	    {
	    totTestDoc++;	
		testDocWordList = nb.getFileWords(listOfTestFiles[j], stopWordList);
  		HashMap<String, Double> classScoreMap = new HashMap<String, Double>();
  		//check the test file against every trained class
  		for (int i=0 ; i< classLabels.size() ; i++)
  		{ 				
  		double score = 0.0;
  		score+=Math.log(prior.get(i));
  		//score*=prior.get(i);
  		HashMap<String, Integer> Classvocab = vocabulary.get(i);
  		for (String word: testDocWordList)
  		{			
  			double conditionalProb = 0.0; 			
  			conditionalProb = nb.calculateConditionalProb(Classvocab, word);	
  			//System.out.println("CALSS =" +classLabels.get(i) +" term =" +word);
  			score+= Math.log(conditionalProb );
  			//score*=conditionalProb;
  			
  		}
  		//System.out.println("CALSS =" +classLabels.get(i) +" SCORE =" +score);
  		classScoreMap.put(classLabels.get(i), score);
  		}
  		Double maxScore=(Collections.max(classScoreMap.values())); 
  		 for (Entry<String, Double> entry : classScoreMap.entrySet()) {  
             if (entry.getValue()==maxScore) {
            // 	System.out.println("test document" +listOfTestFiles[j] +"'s class =" +entry.getKey());
             	if(testLabels.get(k).equals(entry.getKey()))
             		correctlyClassfied++;	
          		
             }
  		 }
  	
	      }	
}	
		}
	//	System.out.println("no.of.test documents classified correctly" +correctlyClassfied);
//		System.out.println("total no.of.test documents" +totTestDoc);
		double testAccuracy = ((  (double) correctlyClassfied/totTestDoc)*100);
		System.out.println("Accuracy =" +testAccuracy);
}
}