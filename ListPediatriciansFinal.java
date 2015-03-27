/*---------------------------------------------------------------------------------
 * Programming assignment #1 is the following:
 * 
 * Given the data file found here -> http://nppes.viva-it.com/NPI_Files.html
 * 
 * Create a C++ program to produce a count of the number of Pediatricians per zip code.
 * 
 * The output should look like the following:
 * 
 * Zip Code|Count|State Code
 * 73034|5|OK
 * 29201|33|SC
 * 
 *     .
 *     .
 *     .
 */


/* This program is only meant to function with the databases given in the assignment, 
 * If different databases are used loadingBarLength must be changed for loading bar to function
 * Output text file should still be created
 * 
 * This program has the following features not in the assignment description:
 * Output is organized by zip code
 * Records for other countries included in the database have been omitted
 * A loading bar was added
 * Author: Leo Conley
*/

// To Do: Improve readability

//Import statements
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ListPediatriciansFinal 
{
	//Create text fields
	Label Headers = new Label("Headers");
	TextField HeadersField = new TextField("npidata_20050523-20150308FileHeader.csv",110);
	Label Data = new Label("Data");
	TextField DataField = new TextField("npidata_20050523-20150308.csv",110);
	Label Output = new Label("Output");
	TextField OutputField = new TextField("PediatriciansByZip.txt",110);
	
	//Set loading bar length      NOTE: Change as each time new database is used
	int loadingBarLength = 75056;
	
    public static void main(String[] args) 
    {
    	new ListPediatriciansFinal();//Create instance of main method
    }//public static void main(String[] args) ends
    public ListPediatriciansFinal()//Main method
    {
        EventQueue.invokeLater(new Runnable() //Run Gui and Logic on seperate threads
        {
            @Override
            public void run() 
            {
            	//print error message if exception occurs while starting program
                try 
            	{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
             	}//try ends 
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
                {
                	ex.printStackTrace();
     	        	JOptionPane.showMessageDialog(null, ex.toString(), "Error",JOptionPane.ERROR_MESSAGE);
                }//catch ends
                
                //Create Frame
                JFrame frame = new JFrame("Pediatrician Counter");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setSize(1200,240);
                frame.add(new TestPane());
                frame.setVisible(true); 
            }//public void run() ends
        });//EventQueue.invokeLater(new Runnable() ends
    }//public ListPediatriciansFinal() ends

    @SuppressWarnings("serial")
	public class TestPane extends JPanel 
    {
    	// create loading bar and button
        private JProgressBar pbProgress;
        private JButton start;
        public TestPane()
        {
        	//add gui components
            pbProgress = new JProgressBar();
            pbProgress.setStringPainted(true);
            start = new JButton("Start");
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(Headers);
            add(HeadersField);
            add(Data);
            add(DataField);
            add(Output);
            add(OutputField);
            add(start);
            add(pbProgress);
            
            start.addActionListener(new ActionListener() 
            {
            	
                @Override
                public void actionPerformed(ActionEvent e) 
                {
                    start.setEnabled(false);//enable start button
                    Logic logicInstance = new Logic();//initialize Logic
                    
                    logicInstance.addPropertyChangeListener(new PropertyChangeListener()//listen for property changes 
                    {
                        @SuppressWarnings("incomplete-switch")
						@Override
						
                        public void propertyChange(PropertyChangeEvent event) 
                        {
                            String name = event.getPropertyName();//Get type of event and save as name 
                            
                            if (name.equals("progress")) 
                            {
                            	//Update loading bar when progress is made
                                int progress = (int) event.getNewValue();
                                pbProgress.setValue(progress);
                                repaint();
                            }//if (name.equals("progress")) ends 
                            
                            else if (name.equals("state")) 
                            {
                            	//re-enable start button when finished loading
                                SwingWorker.StateValue state = (SwingWorker.StateValue) event.getNewValue();
                                switch (state) 
                                {
                                    case DONE:
                                        start.setEnabled(true);
                                        break;
                                }//switch (state) ends
                            }//else if (name.equals("state") ends
                        }// ends

                    });//logicInstance.addPropertyChangeListener(new PropertyChangeListener() ends
                    logicInstance.execute();//Execute Logic
                }//public void actionPerformed(ActionEvent e) ends
            });//start.addActionListener(new ActionListener() ends

        }//public TestPane() ends
    }//public class TestPane ends

    
    
    
    public class Logic extends SwingWorker<Object, Object>//Runs in background
    {
        @Override
        protected Object doInBackground() throws Exception
        {
        	//print error message if unable to find files specified by user
 	        try
 	        {
 	        	new BufferedReader(new FileReader(HeadersField.getText()));
 	        	new BufferedReader(new FileReader(DataField.getText()));
 	        }//try ends
 	        catch (Exception ex)
 	        {
 	        	ex.printStackTrace();
 	        	JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
 	        }//catch ends
        	int ColumnNumber = 0;//initialize ColumnNumber

        	BufferedReader CSVFileHeaders = new BufferedReader(new FileReader(HeadersField.getText()));//Initialize CSVFileHeaders using path from text field
 	        //Initialize arrays
        	ArrayList<String> HeaderArray = new ArrayList<String>();  
 	        ArrayList<String> AllZipsArray = new ArrayList<String>();
 	        ArrayList<String> FinalZipCountArray = new ArrayList<String>();
 	        
 	        String headerRow = CSVFileHeaders.readLine();//Read first line
 	        int loadingBarCount = 1;
 	        while (headerRow != null)//Checks for end of file 
 	        {
 	        	String[] headerRowArray = headerRow.split(",");
	 	        for (String item:headerRowArray) 
	 	        { 
		 	        if ((item.replaceAll("\"","").isEmpty()) == false) 
			 	    {
			 	        HeaderArray.add(item);//Add item to HeaderArray
			 	    }//if ((item.replaceAll("\"","").isEmpty()) == false) ends 
	 	        }//for (String item:headerRowArray) ends
	 	        headerRow = CSVFileHeaders.readLine();//Read next line of headers file
 	        }//while (headerRow != null) ends
 	        
 	        CSVFileHeaders.close();//Close headers file upon reaching end of file
 	        
 	        
 	        
 	        BufferedReader CSVFileData = new BufferedReader(new FileReader(DataField.getText()));//Initialize CSVFileData using path from text field
 	        String dataRow = CSVFileData.readLine();//Read first line of file
 	        
 	        while (dataRow != null)//Checks for end of file
 	        {
 	        	String[] dataArray = dataRow.split(",");//Split comma delimited csv file row into array of records
 	        	ArrayList<String> DataRowArray = new ArrayList<String>();// initialize DataRowArray
 	        	for (String item:dataArray)//for each record in row 
 	        	{
 	        		if ((item.replaceAll("\"","").isEmpty()) == false) 
 	        		{
 	        			DataRowArray.add(HeaderArray.get(ColumnNumber) + ":" +  item);//Add item and corresponding header to DataRowArray
 	        		}//if ((item.replaceAll("\"","").isEmpty()) == false) ends
 	        			
 	        		if (ColumnNumber < HeaderArray.size()-1)
 	        		{
 	        			ColumnNumber++;
 	        		}//if (ColumnNumber < HeaderArray.size()-1) ends
 	        	}//for (String item:dataArray) ends
 	        	String zip = "";//initialize zip
 	        	String state = "";//initialize state
 	        	
 	        	//This line checks to see if taxonomy code represents that of a pediatrician
 	        	if (DataRowArray.toString().contains("\"Healthcare Provider Taxonomy Code_1\":\"2080") )
 	        	{
 	        		
 	        		for(int i = 0; i < DataRowArray.size()-1; i++)
 	        		{	
 	        			//Check each record in row for available postal codes
 	        			if (DataRowArray.get(i).contains("Postal Code")== true)
 	        			{
 	        	
 	        				//Use mailing address postal code if available
 	        				if (DataRowArray.get(i).contains("Provider Business Mailing Address Postal Code")== true)
 	        				{
 	        					zip = (DataRowArray.get(i).toString().replace("Provider Business Mailing Address Postal Code", "").replace("\"", "").replace(":", ""));
 	        				}//if (DataRowArray.get(i).contains("Provider Business Mailing Address Postal Code")== true) ends
 	        				
 	        				//If mailing address postal code not available use business practice location postal code
 	        				else if (DataRowArray.get(i).contains("Provider Business Practice Location Address Postal Code")!= true && DataRowArray.get(i).contains("Provider Business Mailing Address Postal Code")!= true)
 	        				{
 	        					zip = (DataRowArray.get(i).toString().replace("\"", ""));
 	        				}//else if (DataRowArray.get(i).contains("Provider Business Practice Location Address Postal Code")!= true && DataRowArray.get(i).contains("Provider Business Mailing Address Postal Code")!= true) ends						
 	        			}//if (DataRowArray.get(i).contains("Postal Code")== true) ends
 	        			//Check each record in row for available state codes
 	        			if (DataRowArray.get(i).contains("State Name")== true)
 	        			{
 	        				//Use mailing address state code if available
 	        				if (DataRowArray.get(i).contains("Provider Business Mailing Address State Name")== true)
 	        				{
 	        					state =(DataRowArray.get(i).toString().replace(":", "").replace("\"", "").replace("Provider Business Mailing Address State Name", ""));
 	        				}//if (DataRowArray.get(i).contains("Provider Business Mailing Address State Name")== true) ends
 	        				
 	        				//If mailing address state code not available use business practice location state code
 	        				else if (DataRowArray.get(i).contains("Provider Business Practice Location Address State Name")!= true && DataRowArray.get(i).contains("Provider Business Mailing Address State Name")!= true)
 	        				{
 	        					state =(DataRowArray.get(i).toString().replace("\"", ""));
 	        				}//else if (DataRowArray.get(i).contains("Provider Business Practice Location Address State Name")!= true && DataRowArray.get(i).contains("Provider Business Mailing Address State Name")!= true) ends		
 	        			}//if (DataRowArray.get(i).contains("State Name")== true) ends
 	        		}
 	        	int MatchingZips = 1;//initialize MatchingZips
 	        	//if zip code is a number of at least 5 digits
 	        	if (zip.length() > 5 &&state.length() == 2 && zip.substring(0, 5).matches("\\d+$") == true)
 	        	{
 	        		for(int i = 0; i < AllZipsArray.size(); i++)
 	        		{
 	        			//Increment counter for every matching zip
 	        			if (AllZipsArray.get(i).contains(zip.substring(0, 5))== true)
 	        			{
 	        				MatchingZips++;
 	        			}//if (AllZipsArray.get(i).contains(zip.substring(0, 5))== true) ends
 	        							   
 	        		}//for(int i = 0; i < AllZipsArray.size(); i++) ends
 	        						   
 	        		for(int x = 0; x < FinalZipCountArray.size(); x++ )
 	        		{
 	        			//Remove records with matching zip codes
 	        			if (FinalZipCountArray.get(x).contains(zip.substring(0, 5))== true)
 	        			{
 	        				FinalZipCountArray.remove(x);
 	        			}//if (FinalZipCountArray.get(x).contains(zip.substring(0, 5))== true) ends
 	        		}//for(int x = 0; x < FinalZipCountArray.size(); x++ ) ends
 	        		AllZipsArray.add(zip.substring(0, 5) + state);//Add zip to array of all AllZipsArray which contains all zip codes including records with matching zip codes
 	        		FinalZipCountArray.add(zip.substring(0, 5) +"|"+MatchingZips+"|"+ state);//Add zip and number of pediatricians in that zip so far to FinalZipCountArray
 	        		loadingBarCount++;
 	        		setProgress(loadingBarCount * 100 / loadingBarLength);//Convert loadingBarCount to percentage
 	        		
 	        		//Print statements not needed in final version
 	        		///*
 	        		System.out.println(loadingBarCount);
 	        		System.out.println(loadingBarCount * 100 / loadingBarLength+"%");
 	        		System.out.println();
 	        		//*/
 	        		
 	        	}//if (zip.length() > 5 &&state.length() == 2 && zip.substring(0, 5).matches("\\d+$") == true) ends
 	        }//if (DataRowArray.toString().contains("\"Healthcare Provider Taxonomy Code_1\":\"2080") ) ends
 	        DataRowArray = null;
 	        dataRow = CSVFileData.readLine();//Read next line of cvs file
 	        ColumnNumber = 0;//reset ColumnNumber
 	        }//while (dataRow != null) ends
 	        Collections.sort(FinalZipCountArray);// sort by zip code			  
 	        Writer writer = null;//initialize writer
 	        //write results to text file
 	        
 	        //print error message if exception occurs while writing text file
 	        try
 	        {
	 	        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutputField.getText()), "utf-8"));//Initialize writer using path from text field
	 	        writer.write(" Zip Code|Count|State Code\n"+ FinalZipCountArray.toString().replace(",", "\n").replace("[", " ").replace("]", " "));
	 	    }//try ends 
 	        catch (IOException ex) 
 	        {
 	        	ex.printStackTrace();
 	        	JOptionPane.showMessageDialog(null, ex.toString(), "Error",JOptionPane.ERROR_MESSAGE);
 	        }//catch ends 
 	        finally 
 	        {
 	        	//print error message if exception occurs while closing writer
 	        	try 
 	        	{
 	        		writer.close();
 	        	}//try ends 
 	        	catch (Exception ex) 
 	        	{
 	        		ex.printStackTrace();
 	 	        	JOptionPane.showMessageDialog(null, ex.toString(), "Error",JOptionPane.ERROR_MESSAGE);
 	        	}//catch ends
 	        }//finally ends
 	        
 	        //Close open files and exit program
 	        CSVFileData.close();
 	        System.exit(0);	


            return null;
        }//protected Object doInBackground() ends
    }//public class Logic ends
    
    
}//public class ListPediatriciansFinal ends
