package FTP;

import java.io.File;
import java.io.FileInputStream;

import java.net.SocketException;
import java.security.MessageDigest;

import ttpservice.ConDescriptor;


class TransferfileServer extends Thread
{
 ConDescriptor clientCon;
 boolean live = true;
 
 public TransferfileServer( ConDescriptor conDescriptor) {
     try {
         clientCon=conDescriptor;   
         System.out.println("FTP Client Connected ...");
         start();
         
         
     }
     catch(Exception ex) {
    	 ex.printStackTrace();
     }        
 }
 void SendFile(String filename) throws Exception {        
     File f=new File(filename);
     
     if(!f.exists()){

    	 clientCon.send("File Not Found".getBytes("UTF8"), (short)14);
         
         
         return;
     }
     else  {
    	 System.out.println("READY");
         clientCon.send("READY".getBytes("UTF8"), (short)5);
         FileInputStream fin=new FileInputStream(f);
         

         MessageDigest md = MessageDigest.getInstance("MD5");
         
         byte[] buffer = new byte[300000000];
         int index = 0;
         
         int ch = 0; 
         System.out.println("server sending file :"+filename);
         do{
        	 ch = fin.read();
        	 
        	 if(ch != -1){
        		 buffer[index++] = (byte) ch;
        		 md.update((byte) ch);
        	 }
        	 
        	 if (index < 0) {
        		 System.out.println("Error: File Too Large");
        		 fin.close(); 
        		 return;
        	 }
        	 
         } while(ch != -1) ;
         
         byte[] dataBytes = new byte[index];
         for (int i = 0; i < dataBytes.length; i++) {
        	 dataBytes[i] = buffer[i];
         }
    	 
         clientCon.send(dataBytes, index);
         byte[] mdbytes = md.digest();
  
         //convert the byte to hex format method 1
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < mdbytes.length; i++) {
           sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
         }
  
         System.out.println("Digest(in hex format):: " + sb.toString());
         
         
         fin.close();    
         clientCon.send("File Receive Successfully".getBytes("UTF8"), (short)"File Receive Successfully".length()); 
         System.out.println("file send successfully");
         return;
     }
 }
 
 


	 public void run() {
		 try   {
	        	 
	        System.out.println("current thread"+Thread.currentThread()+"active>>"+Thread.activeCount());
		    System.out.println("Waiting for Command ...");
		         
		    String Commands=(String) clientCon.readData();
		    String[] command = Commands.split(" " );
		    System.out.println(command[0]);
		    if(command[0].equals("get"))  {
		    	System.out.println("\tget Command Received ...");
		        SendFile(command[1]);
		    } else {
		    	System.out.println("invalid command");
		    }
	    } catch(SocketException e) {
	        	 //clientCon.close();
				//din.close();
				live = false;
	        	 
	         }
	         catch(Exception e) {
	        	 e.printStackTrace();
	         }
	     }
}