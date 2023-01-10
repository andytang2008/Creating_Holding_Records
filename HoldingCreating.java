import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JFileChooser;

import javax.swing.*;
import javax.swing.JFileChooser;
import java.io.File;   


 class HoldingCreating { //Using JAVA Swing to open a file first, then running holding creation manipulation against MMS id. After that, close the Jchooser and Jframe windows in Swing.

   public static void main(String[] args) {
	   FileSelecting m = new FileSelecting();
       m.createWindow();
	   // System.out.println("2nd show:"+m.returnStringValue());
   }

 }
class FileSelecting{
	
	public String globalPath;
	public String ding;
	
      public void createWindow() { 
		  String path="";	 
		  JFrame frame = new JFrame("Swing Tester");
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		  createUI(frame, path);
		  frame.setSize(560, 200);      
		  frame.setLocationRelativeTo(null);  
		  frame.setVisible(true);
		}

     public void createUI(final JFrame frame, String path){ 
		     JPanel panel = new JPanel();
			 JLabel headerLabel = new JLabel("",JLabel.CENTER );
			 headerLabel.setText("Layout in action: FlowLayout");
      		 LayoutManager layout = new FlowLayout() ;
			//LayoutManager layout = new GridLayout(3, 2);;
			panel.setLayout(layout);

		final JLabel label_1 = new JLabel("Please input the location code!",JLabel.CENTER );
				  JTextField field = new JTextField(10);
		final JLabel label_2 = new JLabel("Please select the MMS id file you would like to open!",JLabel.LEFT );
				  JButton button = new JButton("Click Me!");
		final JLabel label_3 = new JLabel(" ",JLabel.CENTER );
				  
				panel.add(label_1)  ;
				panel.add(field);
				panel.add(label_2)  ;
				panel.add(button);				
				panel.add(label_3);
				frame.getContentPane().add(panel, BorderLayout.CENTER);
				
		  button.addActionListener(
			  new ActionListener() {
				 @Override
				 public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						int option = fileChooser.showOpenDialog(frame);
						if(option == JFileChooser.APPROVE_OPTION){
						   File file = fileChooser.getSelectedFile();
											System.out.println(file.getAbsolutePath());
								 String paths=file.getAbsolutePath();
								 
								 globalPath=paths;
						    System.out.println("globalpath:"+globalPath);
						    label_3.setText("File Selected: " + globalPath);
						    String locationValue = field.getText();
						    System.out.println("field value:"+locationValue);
						    
						    prepareProcess(globalPath,locationValue);
						    System.out.println(globalPath.getClass().getName());

							frame.setVisible(false); //you can't see me! 
							frame.dispose();

						}else{
						   label_3.setText("Open command canceled");
						}
					}
				}
		  );
 
   } 
   
   public void prepareProcess(String globalPath, String locationValue){
	   
		Process m=new Process();
		String path=globalPath;
		System.out.println("func1 global path:"+path);
		String location=locationValue; 
		String xmlInputString = " <holding> <suppress_from_publishing>false</suppress_from_publishing> <record> <leader>     nx  a22     1n 4500</leader> <controlfield tag=\"008\">1011252u    8   4001uueng0000000</controlfield> <datafield ind1=\"0\" ind2=\" \" tag=\"852\"> <subfield code=\"b\">UNLVLAW</subfield>  <subfield code=\"c\">"+location+"</subfield> <subfield code=\"h\"></subfield> <subfield code=\"i\"></subfield></datafield> </record> </holding>";
				
		m.update(path, location, xmlInputString);
   }
   

} 


class Process{  //Process all MMS IDs and created corrsponding holding records.
	String bibID;
/*	public Process(String bib_ID){
		bibID=bib_ID;
		
	}*/

	  void  update(String path, String location, String xmlInputString){
			try  
			{ 
				//BufferedReader in = new BufferedReader(new FileReader("mms_id_books.txt"));
				BufferedReader in = new BufferedReader(new FileReader(path));
				String str;

				List<String> list = new ArrayList<String>();
				while((str = in.readLine()) != null){
					list.add(str);
				}

				String[] stringArr = list.toArray(new String[0]);
				System.out.println(Arrays.toString(stringArr));
				
				
				for (int i=0;i<stringArr.length; i++){
					//Process m=new Process(stringArr[i]);
					apiProcessing(stringArr[i],xmlInputString);
				}
			}
			catch(Exception e)  
			{  
				e.printStackTrace();  
			} 
	  }
	  


	void apiProcessing(String bibID_var, String xmlInputString){
		try{
				
			URL url = new URL("https://api-na.hosted.exlibrisgroup.com/almaws/v1/bibs/"+bibID_var+"/holdings?apikey=Your library API key for read and write holding records");
				try {
				  HttpURLConnection con = (HttpURLConnection) url.openConnection();

					con.setRequestProperty("accept", "application/xml");
					con.setRequestProperty("Content-Type", "application/xml");
					con.setRequestMethod("POST");
					con.setDoOutput(true);

					String jsonInputString =xmlInputString;
					try (OutputStream os = con.getOutputStream()) {
						byte[] input = jsonInputString.getBytes("utf-8");
						os.write(input, 0, input.length);
					}

					int responseCode = con.getResponseCode();
					System.out.println("Response code: " + responseCode);

					InputStreamReader inputStreamReader = null;
					if (responseCode >= 200 && responseCode < 400) {
						inputStreamReader = new InputStreamReader(con.getInputStream());
					} else {
						inputStreamReader = new InputStreamReader(con.getErrorStream());
					}
					BufferedReader in = new BufferedReader(inputStreamReader);
					String inputLine;
					StringBuilder response = new StringBuilder();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

				System.out.println(response.toString());
				}
				catch(IOException e) {
				  e.printStackTrace();
				}
				

			}
			catch(MalformedURLException ex){
				//System.out.println("The url is not well formed: " + url);
					System.out.println(ex);
					 System.out.println("Enter valid URL");
					//do exception handling here
			}
		}
	
}
