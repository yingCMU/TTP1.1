package ttpservice;

import services.DatagramService;
import datatypes.Datagram;

public class ConDescriptor{
	private String serveraddr;
	private String clientaddr;
	private short serverport;
	private short clientport;
	private int serverSYN;
	//private int expectSYN;
	private int ACK;
	private EndConnectionTimer closeTimer;
	private boolean connected;
	private Object data;
	private DataSegment[] sendData;
	private int dataLength;
	private int tempACK;
	public boolean receiveFlag;
	
	private SendWithTimer timer;
	
	private int windowBegin;
	private int windowEnd;
	private int windowSend;
	private int WINDOWSIZE;
	
	private short TTPHeaderLength;
	private DatagramService datagramService;
	
	public ConDescriptor(String serveraddr, String clientaddr, short serverport, short clientport,
			 			 int clientSYN, DatagramService datagramService){
		this.serveraddr = serveraddr;
		this.clientaddr = clientaddr;
		this.serverport = serverport;
		this.clientport = clientport;
		this.datagramService = datagramService;
		ACK = 0;
		serverSYN = 0;
		WINDOWSIZE = 5;
		windowBegin = 0;
		windowSend = 0;
		connected = false;
		receiveFlag = false;
		
		TTPHeaderLength = 0;
	}
	
	private void constructDataSegment(Object[] d, short[] dLength) {
		dataLength = d.length;
		windowEnd = min(windowBegin + WINDOWSIZE, d.length);
		int tempSYN = serverSYN;
		sendData = new DataSegment[d.length];
		for(int i = 0; i < sendData.length; i++) {
			sendData[i] = new DataSegment();
			
			if (sendData[i] == null) {
				System.out.println(i + " NULL HEHEEHEHEHEHEH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.exit(0);
			}
			
			sendData[i].data = d[i];
			sendData[i].dataLength = dLength[i];
			sendData[i].SYN = tempSYN;
			tempSYN += dLength[i];
			sendData[i].expectedSYN = tempSYN;
		}
	}
	
	public void send(Object[] d, short[] dLength) {
		for (int i  = 0; i < d.length; i++) {
			System.out.println("Data to be sent: " + d[i]);
		}
		constructDataSegment(d, dLength);
		
		System.out.println("windowBegin: " + windowBegin + " windowEnd: " + windowEnd);
		while (windowBegin < windowEnd) {
			
			for (int i = windowSend; i < windowEnd; i++) {
				System.out.println("here2");
				System.out.println("BEGIN: " + windowBegin + " END: " + windowEnd + " SEND: " + windowSend);
				sendData[i].timer = sendData(ACK, sendData[i].SYN, 
						clientaddr, clientport, sendData[i].data, 
						sendData[i].dataLength, (char)3, 5);
				windowSend += 1;
				System.out.println("BEGIN: " + windowBegin + " END: " + windowEnd + " SEND: " + windowSend);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (receiveFlag) {
				moveWindow();
				receiveFlag = false;
			}
		}
		windowBegin = 0;
		windowSend = 0;
	}
	
	public void moveWindow() {
		boolean moveFlag = false;
		for (int i = windowBegin; i < windowEnd; i++) {
			if (sendData[i].expectedSYN == tempACK) {
				for (int j = windowBegin; j <= i; j++) {
					//System.out.println("J: " + j + "windowBegin: " + windowBegin);
					if (sendData[j].timer != null && sendData[j].timer.isAlive()) {
						sendData[j].timer.interrupt();
					}
				}
				
				windowBegin = i + 1;
				windowEnd = min(windowBegin + WINDOWSIZE, dataLength) - 1;
				
				serverSYN = sendData[i].expectedSYN;
				
				moveFlag = true;
			}
		}
		
		if (!moveFlag) {
			windowSend = windowBegin;
		}
	}
	
	public SendWithTimer sendData (int ACK, int SYN, String dstaddr, short dstPort, 
			  Object data, short dataLength, char category, int count) {
		
		TTP ttp = new TTP(ACK, SYN, data, dataLength, category);
		Datagram datagram = new Datagram(serveraddr, clientaddr, serverport, clientport, 
						(short)(dataLength + TTPHeaderLength), ttp.getCheckSum(), ttp);
		
		System.out.println("Sending data to " + datagram.getDstaddr() + ":" 
						+ datagram.getDstport());
		
		SendWithTimer sendWithTimer = new SendWithTimer(10000, datagram, datagramService, count);	
		sendWithTimer.start();
		return sendWithTimer;
	}
	
	public void setTempACK(int val) {
		tempACK = val;
	}
	
	public Object[] getSendData() {
		return sendData;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public Object readData() {
		return data;
	}
	
	public void runCloseTimer() {
		closeTimer.start();
	}
	
	public void stopCloseTimer() {
		if (closeTimer.isAlive()) {
			closeTimer.interrupt();
		}
	}
	
	public String getKey() {
		return clientaddr + clientport;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setConnected(boolean status) {
		connected = status;
	}
	
	public String getServerAddr() {
		return serveraddr;
	}
	
	public String getClientAddr() {
		return clientaddr;
	}
	
	public short getServerPort() {
		return serverport;
	}
	
	public short getClientPort() {
		return clientport;
	}


	public void setTimer(SendWithTimer timer) {
		this.timer = timer;
	}
	
	public void killTimer() {
		if (timer != null && timer.isAlive()) {
			timer.interrupt();
		}
	}

	public int getACK() {
		return ACK;
	}

	public void setACK(int ACK) {
		this.ACK = ACK;
	}

	public int getServerSYN() {
		return serverSYN;
	}

	public void setServerSYN(int serverSYN) {
		this.serverSYN = serverSYN;
	}
	

	
	private int min(int a, int b) {
		return a > b ? b : a;
	}

	public void send(String string) {
		// TODO Auto-generated method stub
		short[] len = new short[1];
		 short length = (short) string.length();
		len[0] = length;
		Object[] data = new Object[1];
		data[0] = string;
		send(data, len);
	}
	public void send(byte[] dataByte) {
		// TODO Auto-generated method stub
		short[] len = new short[1];
		 short length = (short) 1;
		len[0] = length;
		Object[] data = new Object[1];
		data[0] = dataByte;
		send(data, len);
	}
}
