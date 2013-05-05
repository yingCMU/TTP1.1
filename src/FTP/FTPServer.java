package FTP;

/*FTP Server*/

import java.net.*;

import ttpservice.*;



public class FTPServer
{
 public static void main(String args[]) throws Exception
 {
     
	 ServerTTPService service;
	if(args.length != 1) {
			printUsage();
			return;
		}
		
	 
		
		int port = Integer.parseInt(args[0]);
	 
		service = new ServerTTPService("localhost", (short)port);
		System.out.println("Starting Server ... on "+port);
		ConDescriptor clientCon;
		TransferfileServer t = null;	
		
     while(true){
    	 
        // System.out.println("Waiting for Connection ...");
        // TransferfileServer t=new TransferfileServer(soc.accept());
         
		if((clientCon = service.serverListen()) != null)
        t = new TransferfileServer(clientCon);
         
     }
     
 }
 private static void printUsage() {
		System.out.println("Usage:java FTPServer <port>");
		System.exit(-1);
	}
}

