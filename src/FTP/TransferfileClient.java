package FTP;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import ttpservice.*;

class TransferfileClient
{
	ClientTTPService ttps;

	BufferedReader stdinBR;
 
	 TransferfileClient(ClientTTPService ttps) {	 
	     try {
	         this.ttps=ttps;
	         stdinBR=new BufferedReader(new InputStreamReader(System.in));
	     } catch(Exception ex) {
	    	 System.out.println("Standard input error");
	     }          
	 }
 
 
	void getFile(String fileName) throws Exception {	 
	     String msgFromServer = new String(ttps.receive(), "UTF8");
	     System.out.println("msgFromserver>>>"+msgFromServer);
	     
	     if(msgFromServer.compareTo("File Not Found")==0) {
	         System.out.println("File not found on Server ...");
	         return;
	     } else  if(msgFromServer.compareTo("READY")==0) {
	         System.out.println("Receiving File ...");
	         
	         File f=new File(fileName );
	         FileOutputStream fout=new FileOutputStream(f);
	         
	         byte[] temp = (byte[])(ttps.receive());
	         fout.write(temp);  
	         fout.close();
	         msgFromServer=new String(ttps.receive());
	         System.out.println("msgFromserver>>>"+msgFromServer);    
	         System.out.println("Retrieved file is saved at " + fileName);
	     }
     }

	public void start() throws Exception {
		printCommands();
        System.out.print("740ftp>");
        String input = stdinBR.readLine();
        String[] command = input.split(" ");
         
        if(command[0].equals("get"))  {
        	try{
        		String[] method = command[1].split("\\/");
        		File saveDir = new File("../files/");
        		String fileName = "../files/"+method[method.length-1];
        		if(!saveDir.exists()){
        			saveDir.mkdir();
        		}
	        	File f=new File(fileName);
	        	if(f.exists()) {
	        		String Option = null;
	                System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
	                Option=stdinBR.readLine();            
	                if(Option.equals("N") || Option.equals("n")) {
	                	return; 
	                } else if(!Option.equals("Y") && !(Option.equals("y"))) {
	                	 System.out.println("invalid choice");	 
	                }
	            }
	        	ttps.send(input, (short) input.length());
	            getFile(fileName);
        	} catch(ArrayIndexOutOfBoundsException e){
        		System.out.println("Error: a file name is required");
        	} finally {
        		ttps.clientClose();
        	}
        } else {
        	System.out.println("\n  !!! invalid commands \n  ");
        	printCommands();
        }
 	}
 	public void printCommands() {
		 System.out.println("******* commands ******");
	     System.out.println("Receive File: get /directory/on/the/server/filename");
	     System.out.println("**********************");
 	}
}
