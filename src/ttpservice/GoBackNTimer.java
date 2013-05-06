package ttpservice;

public class GoBackNTimer extends Thread implements Runnable{
	private long time;
	private volatile int[] win;
	
	public GoBackNTimer(int seconds, int[]window) {
		time = seconds * 1000;
		win = window;
	}
	
	public void run() {
		while(win[0] < win[2] - 1) {
			System.out.println(win[0]+" "+win[2]);
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				return;
			}
			win[1] = win[0]; 
		}
	}
}
