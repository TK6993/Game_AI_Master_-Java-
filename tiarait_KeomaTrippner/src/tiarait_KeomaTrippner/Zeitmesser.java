package tiarait_KeomaTrippner;

public class Zeitmesser {
	
	
	long start;
	long finish; 
	
	public long elapsedTime =0;
	
	
	public Zeitmesser() {
		start = System.currentTimeMillis();
	}
	
	
	public void play() {
		start = elapsedTime;
	}
	
	public void reset() {
		elapsedTime = 0;
		start = System.currentTimeMillis();
	}
	
	
	public void pause() {
		finish =  System.currentTimeMillis();
		elapsedTime = finish-start;
	}
	
	public long getElapsedTime() {
		
		finish =  System.currentTimeMillis();
		elapsedTime = finish-start;	
		return elapsedTime;
	}
	

}
