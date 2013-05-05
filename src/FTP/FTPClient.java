package FTP;
//FTP Client

import java.net.*;
import java.io.*;
import java.util.*;

import ttpservice.ClientTTPService;


class FTPClient{
	
 public static void main(String args[]) throws Exception {
     //Socket soc=new Socket("127.0.0.1",5218);
	 ClientTTPService ttps;
	 int dstport = 9001;
	 int srcport = 5219;
	 System.out.println("ttp service dstport "+dstport);
		ttps = new ClientTTPService("localhost", (short) srcport);
		
		ttps.connect("localhost", (short)dstport);
		//ttps.receive();
		//ttps.clientClose();
     TransferfileClient t=new TransferfileClient(ttps);
     t.start(); 
 }
 public void printusage(){
	 System.out.println("Usage: java FTPClient <server_ip> <server_port> <file_name>");
 }
}
