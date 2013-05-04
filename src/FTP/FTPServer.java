package FTP;

/*FTP Server*/

import java.net.*;

import TTP.ConDescriptor;
import TTP.ServerTTPService;


public class FTPServer
{
 public static void main(String args[]) throws Exception
 {
     //ServerSocket soc=new ServerSocket(5218);
	 ServerTTPService service;
	/* if(args.length != 1) {
			printUsage();
		}
		
	 
		
		int port = Integer.parseInt(args[0]);*/
	 int port = 5218;
		service = new ServerTTPService("localhost", (short)port);
		System.out.println("Starting Server ... on "+port);
		ConDescriptor clientCon;
		TransferfileServer t = null;	
		
     while(true){
    	 
         System.out.println("Waiting for Connection ...");
        // TransferfileServer t=new TransferfileServer(soc.accept());
         
		if((clientCon = service.serverListen()) != null)
        t = new TransferfileServer(clientCon);
         
     }
     
 }
 private static void printUsage() {
		System.out.println("Usage: server <port>");
		System.exit(-1);
	}
}

