package server.bios.asbserver.utils;

public class Timer {
	private volatile long startTime = 0;
	private int duration = 0;

	public Timer (int duration){
		this.duration = duration;
	}
	  
	  public void start(){
		  startTime = System.currentTimeMillis();
	  }

	  public boolean isFinish()
	  {
		  System.out.println((System.currentTimeMillis() - startTime)/1000);
		  return ((System.currentTimeMillis() - startTime)/1000) > duration;
	  }
	  
	  public void reset(){
		  startTime = System.currentTimeMillis();
	  }
}
