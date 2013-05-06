package FTP;
//FTP Client
import ttpservice.ClientTTPService;

class FTPClient {
	
	public static void main(String args[]) throws Exception {
		if(args.length != 3) {
			printusage();
			return;
		}
		ClientTTPService ttps;
		int dstport = Integer.parseInt(args[1]);
		String dstip = args[0];
		int srcport = Integer.parseInt(args[2]);
		 
		System.out.println("ttp service dstport "+dstport);
		ttps = new ClientTTPService("localhost", (short) srcport);
		
		ttps.connect(dstip, (short)dstport);
	    TransferfileClient t=new TransferfileClient(ttps);
	    t.start(); 
	}
	
	public static void printusage() {
		 System.out.println("Usage: java FTPClient <server_ip> <server_port> ");
	}
}
