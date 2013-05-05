package ttpservice;

import datatypes.Datagram;

public class ClientTTPService extends TTPservice{
	
	private String dstaddr;
	private short dstport;
	private int SYN;
	private int expectSYN;
	private int ACK;
	
	private int connectStatus;
	
	private SendWithTimer timer;
	
	public ClientTTPService(String srcaddr, short srcPort){
		super(srcaddr, srcPort);
		SYN = 0;
		ACK = 0;
		connectStatus = 0;
	}
	
	public void connect (String dstaddr, short dstPort) {
		System.out.println("Connecting......");
		
		int ack = 0;
		
		this.dstaddr = dstaddr;
		this.dstport = dstPort;
		
		while (connectStatus == 0) {
			System.out.println("Sending SYN data");
			clientSendData(null, (short)1, (char)0, 5);//SYN
			
			Datagram datagram = receiveData();
			timer.interrupt();
			timer = null;
			if (datagram == null) {
				return;
			}
			
			System.out.println("Receiving server ACK");
			
			TTP ttp = (TTP)datagram.getData();
			ack = ttp.getACK();
			
			System.out.println("ACK: " + ack + " ExpectSYN: " + expectSYN);
			
			if (ttp.getCategory() == (char)1 && ack == expectSYN) {				
				connectStatus = 1;
				ACK = ttp.getSYN() + ttp.getLength();
				SYN = expectSYN;
			}
		}
				
		System.out.println("Sending SYN + ACK");
			
		clientSendData(null, (short)1, (char)2, 1); //SYN + ACK
		
		connectStatus = 2;

		System.out.println("connectioin established");
	}

	public void send(Object data, short dataLength) {
		timer = sendData(ACK, SYN, dstaddr, dstport, data, dataLength, (char)3, 5);
		expectSYN = SYN + dataLength;
	}
	
	private void clientSendData(Object data, short dataLength, char category, int count) {
		timer = sendData(ACK, SYN, dstaddr, dstport, data, dataLength, category, count);
		expectSYN = SYN + dataLength;
	}
	
	public Object receive() {
		if (connectStatus == 2) {
			Datagram datagram = receiveData();
			if (datagram == null) {
				return null;
			}
			TTP ttp = (TTP)datagram.getData();
			int ack = ttp.getACK();
			
			timer.interrupt();
			
			if (ttp.getCategory() == (char)1) {
				System.out.println("Sending SYN + ACK");
				
				clientSendData(null, (short)1, (char)2, 1); //SYN + ACK
				
			} else if (ttp.getCategory() == (char)3) {
				System.out.println("ack=" + ack + " expectedSYN=" + expectSYN + " ACK=" + ACK + " ttp.getSYN =" + ttp.getSYN());
				if (ack == expectSYN && ACK == ttp.getSYN()) {
					System.out.println("ACK correct");
					ACK = ttp.getSYN() + ttp.getLength();
					System.out.println(" ttp.getSYN =" + ttp.getSYN() + " length: " + ttp.getLength());
					SYN = expectSYN;
					clientSendData(null, (short)1, (char)3, 1); //ACK
					return ttp.getData();
				}
				clientSendData(null, (short)1, (char)3, 1); //ACK
			}
		} else {
			System.out.println("Please establish the connection first");
		}
		
		return null;
	}
	
	public void clientClose() {
		timer.interrupt();
		int ack = 0;
		while (connectStatus == 2) {
			clientSendData(null, (short)1, (char)4, 5); //FIN
			
			Datagram datagram = receiveData();
			timer.interrupt();
			timer = null;
			if (datagram == null) {
				continue;
			}
			
			
			TTP ttp = (TTP)datagram.getData();
			ack = ttp.getACK();
			
			if (ttp.getCategory() == (char)5 && ack == expectSYN) {				
				connectStatus = 3;
				ACK = ttp.getSYN() + ttp.getLength();
				SYN = expectSYN;
			}
		}
		
		while (true) {
			System.out.println("Sending FIN + ACK");
			
			ClientCloseTimer closeTimer = new ClientCloseTimer(10);
			clientSendData(null, (short)1, (char)6, 1); //SYN + ACK
			closeTimer.start();
			receiveData();
			timer.interrupt();			
		}
	}
}
