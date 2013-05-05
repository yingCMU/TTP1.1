package ttpservice;

public class ClientCloseTimer extends Thread implements Runnable{
	private long time;
	
	public ClientCloseTimer(int t) {
		time = t * 1000;
	}
	
	public void run(){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			return;
		}
		System.out.println("System exit");
		System.exit(0);
	}
}
