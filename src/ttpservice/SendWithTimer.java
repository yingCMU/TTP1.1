package ttpservice;

import java.io.IOException;
import services.DatagramService;
import datatypes.Datagram;

public class SendWithTimer extends Thread implements Runnable{
	
	private int time;
	private Datagram data;
	private DatagramService datagramService;
	private int sendCount;
	
	public SendWithTimer(int seconds, Datagram datagram, DatagramService dataService, 
				int count) {
		time = seconds * 1000;
		data = datagram;
		datagramService = dataService;
		sendCount = count;
	}
	
	@Override
	public void run() {
		while(sendCount > 0 && !Thread.interrupted()) {
			
			try {
				datagramService.sendDatagram(data);
				Thread.sleep(time);
			} catch (InterruptedException e) {
				break;
			} catch (IOException e) {
				System.out.println("sending Exception");
			}	
			
			sendCount--;
		}		
	}
}
