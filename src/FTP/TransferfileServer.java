package FTP;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;

import ttpservice.ConDescriptor;


class TransferfileServer extends Thread
{
 ConDescriptor clientCon;
 boolean live = true;
// DataInputStream din;
 //DataOutputStream dout;
 
 public TransferfileServer( ConDescriptor conDescriptor) {
     try {
         clientCon=conDescriptor;   
         
        // din=new DataInputStream(clientSocket.getInputStream());
        // dout=new DataOutputStream(clientSocket.getOutputStream());
         System.out.println("FTP Client Connected ...");
         start();
         
         
     }
     catch(Exception ex) {
    	 ex.printStackTrace();
     }        
 }
 void SendFile(String filename) throws Exception {        
    // String filename=din.readUTF();
     File f=new File(filename);
     
     if(!f.exists()){
    	 /*
    	  * serverSendData(ConDescriptor client, Object data, short dataLength, char category)
    	  */
    	// byte[] data = "File Not Found".getBytes("UTF8");
    	 clientCon.send(new String("File Not Found"));
         
         
         return;
     }
     else  {
    	 System.out.println("READY");
         clientCon.send("READY");
    	 //System.out.println("MD5");
         //dout.writeUTF("MD5");
         FileInputStream fin=new FileInputStream(f);
         

         MessageDigest md = MessageDigest.getInstance("MD5");
         
         byte[] dataBytes = new byte[10];
         int index = 0;
         
         int ch = 0; 
         System.out.println("server sending file :"+filename);
         do{
        	 ch = fin.read();
        	 
        	 if(ch != -1){
        		 dataBytes[index++] = (byte) ch;
            	 System.out.println("ch>>"+ch +"  ; sstream >"+new String(dataBytes));
        	 md.update((byte) ch);
             
        	 }
        	 
         } while(ch != -1) ;
         
         Object[] obj = new Object[1];
    	 obj[0] = dataBytes;
    	 short[] len = new short[1];
    	 len[0] = (short)(index - 2 );
    	 
         clientCon.send(obj, len);
         byte[] mdbytes = md.digest();
  
         //convert the byte to hex format method 1
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < mdbytes.length; i++) {
           sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
         }
  
         System.out.println("Digest(in hex format):: " + sb.toString());
         
         
         fin.close();    
         clientCon.send("File Receive Successfully"); 
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