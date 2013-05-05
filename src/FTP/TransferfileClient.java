package FTP;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import ttpservice.*;

class TransferfileClient
{
 ClientTTPService ttps;

// DataInputStream din;
 //DataOutputStream dout;
 BufferedReader stdinBR;
 TransferfileClient(ClientTTPService ttps)
 {
     try
     {
         this.ttps=ttps;
         //din=new DataInputStream(ClientSoc.getInputStream());
         //dout=new DataOutputStream(ClientSoc.getOutputStream());
         stdinBR=new BufferedReader(new InputStreamReader(System.in));
        // InputStream m = new InputStream();
     }
     catch(Exception ex)
     {
     }        
 }
 
 
 void getFile(String fileName) throws Exception
 {
	 
	 System.out.println("!!!getFIle   >>"+fileName);
     String msgFromServer=(String) ttps.receive();
     System.out.println("msgFromserver>>>"+msgFromServer);
     if(msgFromServer.compareTo("File Not Found")==0)
     {
         System.out.println("File not found on Server ...");
         return;
     }
     else  if(msgFromServer.compareTo("READY")==0)
     {
         System.out.println("Receiving File ...");
         
         File f=new File(fileName );
         FileOutputStream fout=new FileOutputStream(f);
         
         byte[] temp = new byte[1];
         temp = (byte[])(ttps.receive());
         System.out.println("TEMP: " + temp[0]);
         System.out.println("before rec ");
         fout.write(temp);     
         fout.close();
         msgFromServer=(String) ttps.receive();
         System.out.println("msgFromserver>>>"+msgFromServer);    
     }
     
     
 }

 public void start() throws Exception {
	 printCommands();
        
    	 
         System.out.print("740ftp>");
         String input = stdinBR.readLine();
         String[] command = input.split(" ");
         
         
          if(command[0].equals("get"))  {
        	 try{
        		 String[] method = command[1].split("\\\\");
        		 File saveDir = new File("../files/");
        			String fileName = "../files/"+method[method.length-1];
        			 System.out.println("?? fileName:"+fileName);
        			if(!saveDir.exists()){
        		        saveDir.mkdir();
        		        System.out.println("no");
        			}
        			else
        				System.out.println("yes");
        	 File f=new File(fileName);
        	 if(f.exists())
             {
                 String Option = null;
                 System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
                 Option=stdinBR.readLine();            
                 if(Option.equals("N") || Option.equals("n"))    
                 {
                     //dout.flush();
                     return;
                       
                 }
                 else if (!Option.equals("Y") && !(Option.equals("y"))){
                	 System.out.println("invalid choice");
                	 //dout.flush();
                	 
                 }
             }
        	 System.out.println(">>input: "+input);
        	 ttps.send(input, (short) input.length());
        	 //dout.writeUTF(input);
             getFile(fileName);
        	 }
        	 catch(ArrayIndexOutOfBoundsException e){
        		 System.out.println("Error: a file name is required");
        	 }
        	
        	 
        	 
            
         }
        
         else {
        	 System.out.println("\n  !!! invalid commands \n  ");
        	 printCommands();
         }
         	
     
 }
 public void printCommands(){
	 System.out.println("******* commands ******");
     
     System.out.println("Receive File: get file.txt");
     
     System.out.println("**********************");
 }
}
