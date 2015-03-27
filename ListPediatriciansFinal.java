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

// To Do: Add Better Comments

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
	int loadingBarLength = 75056;
	
    public static void main(String[] args) 
    {
    	new ListPediatriciansFinal();
    }
    public ListPediatriciansFinal()
    {
        EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                try 
            	{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
             	} 
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) 
                {
                	ex.printStackTrace();
     	        	JOptionPane.showMessageDialog(null, ex.toString(), "Error",JOptionPane.ERROR_MESSAGE);
                }
                //Create Frame
                JFrame frame = new JFrame("Pediatrician Counter");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setSize(1200,240);
                frame.add(new TestPane());
                frame.setVisible(true); 
            }
        });
    }

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
            	//Update loading bar when progress is made
                @Override
                public void actionPerformed(ActionEvent e) 
                {
                    start.setEnabled(false);
                    ProgressWorker pw = new ProgressWorker();
                    
                    pw.addPropertyChangeListener(new PropertyChangeListener() 
                    {
                        @SuppressWarnings("incomplete-switch")
						@Override
						
                        public void propertyChange(PropertyChangeEvent evt) 
                        {
                            String name = evt.getPropertyName();
                            
                            if (name.equals("progress")) 
                            {
                            	
                                int progress = (int) evt.getNewValue();
                                pbProgress.setValue(progress);
                                repaint();
                            } 
                            
                            else if (name.equals("state")) 
                            {
                                SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
                                
                                switch (state) 
                                {
                                    case DONE:
                                        start.setEnabled(true);
                                        break;
                                }
                            }
                        }

                    });
                    pw.execute();
                }
            });

        }
    }

    public class ProgressWorker extends SwingWorker<Object, Object>
    {
        @Override
        protected Object doInBackground() throws Exception
        {
 	        try
 	        {
 	        	new BufferedReader(new FileReader(HeadersField.getText()));
 	        	new BufferedReader(new FileReader(DataField.getText()));
 	        }
 	        catch (Exception ex)
 	        {
 	        	ex.printStackTrace();
 	        	JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
 	        }
        	
        	
        	//add all headers to array
        	int count = 0;

        	BufferedReader CSVFileHeaders = new BufferedReader(new FileReader(HeadersField.getText()));
 	        ArrayList<String> HeaderArray = new ArrayList<String>();
 	        ArrayList<String> AllZipsArray = new ArrayList<String>();
 	        ArrayList<String> FinalZipCountArray = new ArrayList<String>();
 	        String headerRow = CSVFileHeaders.readLine();
 	        int v = 1;
 	        while (headerRow != null) 
 	        {
 	        	String[] headerRowArray = headerRow.split(",");
	 	        for (String item:headerRowArray) 
	 	        { 
		 	        if ((item.replaceAll("\"","").isEmpty()) == false) 
			 	    {
			 	        HeaderArray.add(item);
			 	    } 
	 	        }
	 	        headerRow = CSVFileHeaders.readLine(); 
 	        }
 	        
 	        CSVFileHeaders.close();
 	        
 	        
 	        
 	        //add all records in row to array
 	        BufferedReader CSVFileData = new BufferedReader(new FileReader(DataField.getText()));
 	        String dataRow = CSVFileData.readLine();
 	        CSVFileData.readLine().toString().contains("");
 	        
 	        while (dataRow != null) 
 	        {
 	        	String[] dataRowArray = dataRow.split(",");
 	        	ArrayList<String> ColumnArray = new ArrayList<String>();
 	        	for (String item:dataRowArray) 
 	        	{
 	        		if ((item.replaceAll("\"","").isEmpty()) == false) 
 	        		{
 	        			ColumnArray.add(HeaderArray.get(count) + ":" +  item); 
 	        		}
 	        				
 	        		if (count < HeaderArray.size()-1)
 	        		{
 	        			count++;
 	        		}
 	        	}
 	        	String zip = "";
 	        	String state = "";
 	        	
 	        	//This line checks to see if record represents a pediatrician
 	        	if (ColumnArray.toString().contains("\"Healthcare Provider Taxonomy Code_1\":\"2080") )
 	        	{
 	        		
 	        		for(int i = 0; i < ColumnArray.size()-1; i++)
 	        		{	
 	        			//Check each Column for available postal codes
 	        			if (ColumnArray.get(i).contains("Postal Code")== true)
 	        			{
 	        	
 	        				if (ColumnArray.get(i).contains("Provider Business Mailing Address Postal Code")== true)
 	        				{
 	        					zip = (ColumnArray.get(i).toString().replace("Provider Business Mailing Address Postal Code", "").replace("\"", "").replace(":", ""));
 	        				}
 	        				else if (ColumnArray.get(i).contains("Provider Business Practice Location Address Postal Code")!= true && ColumnArray.get(i).contains("Provider Business Mailing Address Postal Code")!= true)
 	        				{
 	        					zip = (ColumnArray.get(i).toString().replace("\"", ""));
 	        				}						
 	        			}
 	        			//Check each Column for available state codes
 	        			if (ColumnArray.get(i).contains("State Name")== true)
 	        			{
 	        				if (ColumnArray.get(i).contains("Provider Business Mailing Address State Name")== true)
 	        				{
 	        					state =(ColumnArray.get(i).toString().replace(":", "").replace("\"", "").replace("Provider Business Mailing Address State Name", ""));
 	        				}
 	        				else if (ColumnArray.get(i).contains("Provider Business Practice Location Address State Name")!= true && ColumnArray.get(i).contains("Provider Business Mailing Address State Name")!= true)
 	        				{
 	        					state =(ColumnArray.get(i).toString().replace("\"", ""));
 	        				}		
 	        			}
 	        		}
 	        	int z = 1;
 	        	//if zip code is a number of at least 5 digits
 	        	if(zip.length() > 5 &&state.length() == 2 && zip.substring(0, 5).matches("\\d+$") == true)
 	        	{
 	        		for(int i = 0; i < AllZipsArray.size(); i++)
 	        		{
 	        			//Increment counter for every matching zip
 	        			if (AllZipsArray.get(i).contains(zip.substring(0, 5))== true)
 	        			{
 	        				z++;
 	        			}
 	        							   
 	        		}
 	        						   
 	        		for(int x = 0; x < FinalZipCountArray.size(); x++ )
 	        		{
 	        			//Remove records with matching zip codes
 	        			if (FinalZipCountArray.get(x).contains(zip.substring(0, 5))== true)
 	        			{
 	        				FinalZipCountArray.remove(x);
 	        			}
 	        		}
 	        		AllZipsArray.add(zip.substring(0, 5) + state);//Add zip to array of all zip codes
 	        		FinalZipCountArray.add(zip.substring(0, 5) +"|"+z+"|"+ state);//Add zip and number of pediatricians in that zip so far to array
 	        		v++;
 	        		setProgress(v * 100 / loadingBarLength);
 	        		
 	        		//Print statements not needed in final version
 	        		///*
 	        		System.out.println(v);
 	        		System.out.println(v * 100 / loadingBarLength+"%");
 	        		System.out.println();
 	        		 //*/
 	        	}
 	        }
 	        ColumnArray = null;
 	        dataRow = CSVFileData.readLine();
 	        count = 0;
 	        }
 	        Collections.sort(FinalZipCountArray);// sort by zip code			  
 	        Writer writer = null;
 	        //write results to text file
 	        try
 	        {
	 	        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OutputField.getText()), "utf-8"));
	 	        writer.write(" Zip Code|Count|State Code\n"+ FinalZipCountArray.toString().replace(",", "\n").replace("[", " ").replace("]", " "));
	 	    } 
 	        catch (IOException ex) 
 	        {
 	        	ex.printStackTrace();
 	        	JOptionPane.showMessageDialog(null, ex.toString(), "Error",JOptionPane.ERROR_MESSAGE);
 	        } 
 	        finally 
 	        {
 	        	try 
 	        	{
 	        		writer.close();
 	        	} 
 	        	catch (Exception ex) 
 	        	{
 	        		ex.printStackTrace();
 	 	        	JOptionPane.showMessageDialog(null, ex.toString(), "Error",JOptionPane.ERROR_MESSAGE);
 	        	}
 	        }
 	        			  
 	        CSVFileData.close();
 	        System.exit(0);	


            return null;
        }
    }
}
