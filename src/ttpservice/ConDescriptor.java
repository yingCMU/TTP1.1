package ttpservice;

import java.util.Hashtable;

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
	private EndConnectionTimer lifeTimer;
	private boolean connected;
	private Object data;
	private DataSegment[] sendData;
	private int dataLength;
	private int tempACK;
	public boolean receiveFlag;
	
	private SendWithTimer timer;
	private GoBackNTimer sendTimer;
	
	private int[] window;
	private int WINDOWSIZE;
	
	private short TTPHeaderLength;
	private DatagramService datagramService;
	private Hashtable<String, ConDescriptor> clientList;
	
	public ConDescriptor(String serveraddr, String clientaddr, short serverport, short clientport,
			 			 int clientSYN, DatagramService datagramService,
			 			 Hashtable<String, ConDescriptor> list){
		this.serveraddr = serveraddr;
		this.clientaddr = clientaddr;
		this.serverport = serverport;
		this.clientport = clientport;
		this.datagramService = datagramService;
		clientList = list;
		
		ACK = 0;
		serverSYN = 0;
		
		window = new int[3];
		WINDOWSIZE = 20;
		window[0] = 0;
		window[1] = 0;
		connected = false;
		receiveFlag = false;
		
		TTPHeaderLength = 0;
		
		lifeTimer = new EndConnectionTimer(7200, this, clientList);
		lifeTimer.start();
	}
	
	private void constructDataSegment(Object[] d, short[] dLength) {
		int count = 0;
		dataLength = d.length;
		
		window[2] = min(window[0] + WINDOWSIZE, d.length);
		int tempSYN = serverSYN;
		sendData = new DataSegment[d.length];
		for(int i = 0; i < sendData.length; i++) {
			sendData[i] = new DataSegment();
			sendData[i].offset = count;
			
			sendData[i].data = d[i];
			sendData[i].dataLength = dLength[i];
			sendData[i].SYN = tempSYN;
			tempSYN += dLength[i];
			sendData[i].expectedSYN = tempSYN;
			
			count += sendData[i].dataLength;
		}
	}
	
	public void send(byte[] d, int len) {
		short MSS = 1000;
		int num = (len + MSS - 1) / MSS;
		
		Object[] data = new Object[num];
		short[] length = new short[num];
		
		for (int i = 0; i < num; i++) {
			if (i < num - 1) {
				byte[] dataSegment = new byte[MSS];
				for (int j = 0; j < MSS; j++) {
					dataSegment[j] = d[j + i * MSS];
				}
				data[i] = dataSegment;
				length[i] = MSS;
			} else {
				byte[] dataSegment = new byte[len - MSS * i];
				for (int j = 0; j < len - MSS * i; j++) {
					dataSegment[j] = d[j + i * MSS];
				}
				data[i] = dataSegment;
				length[i] = (short)(len - MSS * i);
			}
		}
		sendDatagram(data, length);		
	}
	
	private void sendDatagram(Object[] d, short[] dLength) {
		constructDataSegment(d, dLength);
		sendTimer = new GoBackNTimer(10, window);
		sendTimer.start();
		while (window[0] < window[2]) {
			
			for (int i = window[1]; i < window[2]; i++) {
				System.out.println("Sending data " + (float)(i + 1) / d.length * 100 + "%");
				
				if (i == d.length - 1) {
					sendData[i].timer = sendData(ACK, sendData[i].SYN, clientaddr, 
							clientport, sendData[i].data, sendData[i].dataLength, 
							(char)3, 1, true, sendData[i].offset, 0);
				} else {
					sendData[i].timer = sendData(ACK, sendData[i].SYN, clientaddr, 
							clientport, sendData[i].data, sendData[i].dataLength, 
							(char)3, 1, false, sendData[i].offset, 0);
				}
				window[1] += 1;
				
				sendTimer.interrupt();
				sendTimer = new GoBackNTimer(10, window);
				sendTimer.start();
			}
			
			if (receiveFlag) {
				moveWindow();
				receiveFlag = false;
			}
		}
		sendTimer.interrupt();
		window[0] = 0;
		window[1] = 0;
		window[2] = 0;
	}
	
	public void moveWindow() {
		boolean moveFlag = false;
		for (int i = window[0]; i < window[2]; i++) {
			if (sendData[i].expectedSYN == tempACK) {
				
				window[0] = i + 1;
				window[2] = min(window[0] + WINDOWSIZE, dataLength);
				
				serverSYN = sendData[i].expectedSYN;
				
				moveFlag = true;
				break;
			}
		}
		
		if (!moveFlag) {
			window[1] = window[0];
		}
	}
	
	public SendWithTimer sendData (int ACK, int SYN, String dstaddr, short dstPort, 
			  Object data, short dataLength, char category, int count, boolean isLast,
			  int offset, int sleepTime) {
		
		TTP ttp = new TTP(ACK, SYN, data, dataLength, category);
		ttp.setID((char)9);
		ttp.setOffset(offset);
		if (isLast) {
			ttp.setFlag((char)1);
		}
		
		Datagram datagram = new Datagram(serveraddr, clientaddr, serverport, clientport, 
						(short)(dataLength + TTPHeaderLength), ttp.getCheckSum(), ttp);
		
		SendWithTimer sendWithTimer = new SendWithTimer(sleepTime, datagram, 
														datagramService, count);	
		sendWithTimer.run();
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
		closeTimer = new EndConnectionTimer(20, this, clientList);
		closeTimer.start();
	}
	
	public void stopCloseTimer() {
		if (closeTimer != null && closeTimer.isAlive()) {
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

}
