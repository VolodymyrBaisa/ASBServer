package server.bios.asbserver.utils;

public class Timer {
	private long startTime = 0;
	private volatile boolean isStart = true;
	private int duration = 0;

	public Timer (int duration){
		this.duration = duration;
	}
	  
	  public void start(){
		  if(isStart){
		  startTime = System.currentTimeMillis();
		  isStart = false;
		  }
	  }

	  public boolean isFinish()
	  {
		  return ((System.currentTimeMillis() - startTime)/1000) > duration;
	  }
	  
	  public void reset(){
		  startTime = System.currentTimeMillis();
		  isStart = true;
	  }
}
