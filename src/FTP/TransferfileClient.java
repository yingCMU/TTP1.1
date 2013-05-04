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

import TTP.ClientTTPService;

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
         
         File f=new File(fileName);
         FileOutputStream fout=new FileOutputStream(f);
         int ch;
         String temp;/*
         do
         {
             temp=din.read();
             ch=Integer.parseInt(temp);
             if(ch!=-1)
             {
                 fout.write(ch);                    
             }
         }while(ch!=-1);
         */
         
        
         do
         {
             temp=(String) ttps.receive();
             ch=Integer.parseInt(temp);
             if(ch!=-1)
             {
                 fout.write(ch);                    
             }
         }while(ch!=-1);
         fout.close();
         System.out.println(ttps.receive());
             
     }
     
     
 }

 public void start() throws Exception {
	 printCommands();
        
    	 
         System.out.print("740ftp>");
         String input = stdinBR.readLine();
         String[] command = input.split(" ");
         
         
          if(command[0].equals("get"))  {
        	 try{
        	 File f=new File(command[1]);
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
        	 System.out.println(">>input: ge"+input);
        	 ttps.send(input, (short) input.length());
        	 //dout.writeUTF(input);
             getFile(command[1]);
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
