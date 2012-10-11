package edu.itesm.scratch.model;

import android.util.Log;
import edu.itesm.scratch.android.MainGamePanel;
import edu.itesm.scratch.android.MainGamePanel.SpriteName;

public class Script  {
	private static final String TAG = MainGamePanel.class.getSimpleName();
	private int scriptID;   // this script id - an integer
	private Sprite sprite;  // Sprite that owns this script 
	private SpriteName spriteName; // the Script's name
	
	private Thread scThread;  // this Script's thread
	
	public Script(SpriteName spriteName, Sprite sprite, int scriptID) {
		
		this.sprite = sprite;
		this.scriptID = scriptID;
		this.spriteName = spriteName;
		
		switch (spriteName) {
		case GATITO1:
			scThread = new Thread(new StatmntsGatito1_1(sprite));
			break;
		case GATITO2:
			scThread = new Thread(new StatmntsGatito2_1(sprite));
			break;
		default:
			Log.d(TAG, "shouldn't be here!!");
			
		
		}
		scThread.start();
		Log.d(TAG, "script thread started");
	}
	
	public Thread getThread (){
		return scThread;
	}
	
	public class StatmntsGatito2_1 implements Runnable {
	    private Sprite sprite;
	    private int i;
	    public StatmntsGatito2_1(Sprite sprite) {
	    	this.sprite = sprite;
	    }
	    	
	    public void run() {
	    	
	    	sprite.goToXY(190,20);
	    	sprite.pointInDirection(90);
	    	for (i=1;i<=95;i++) { //Scratch's repeat 190 times block
	    		sprite.moveSteps(-4);
	    		sprite.ifOnEdgeBounce();
	    		try {
	            	Thread.sleep(25);
	            } catch (InterruptedException e) {
	            	Log.d(TAG, "interrupted");
	            }
	    	}
	    }
	    		
	}
	
	public class StatmntsGatito1_1 implements Runnable {
	    private Sprite sprite;
	    private int i;
	    public StatmntsGatito1_1(Sprite sprite) {
	    	this.sprite = sprite;
	    }
	    	
	    public void run() {
	    	
	    	sprite.goToXY(-190,0);
	    	sprite.pointInDirection(90);
	    	for (i=1;i<=95;i++) { //Scratch's repeat 190 times block
	    		sprite.moveSteps(4);
	    		sprite.ifOnEdgeBounce();
	    		try {
	            	Thread.sleep(25);
	            } catch (InterruptedException e) {
	            	Log.d(TAG, "interrupted");
	            }
	    	}
	    }
	    		
	}
	
	
	

}
