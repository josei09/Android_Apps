package edu.itesm.scratch.model;

import android.util.Log;
import edu.itesm.scratch.android.MainGamePanel;
import edu.itesm.scratch.android.MainGamePanel.SpriteName;

public class Script  {
	private static final String TAG = Script.class.getSimpleName();
	private static final int FOREVER = 5*60*1000; //five minutes in milliseconds
	public static enum ScriptType {WGREENFLAGCLKD, // when green flag clicked script,
		                           WKEYPRESSED,    // when key pressed script
		                           WSPRITECLKDC,   // when Sprite Clicked script
		                           WIRECEIVEMSG    // when I receive message script
	                              };
	                              
	// general methods applicable to all scripts:
	public static void whenGreenFlagClicked() {
		// wait until MainGamePanel.onTouchEvent detects green flag clicked and interrupts
		// the sleeping thread
		Log.d(TAG, "when green flag statement");
		try {
			Thread.sleep(FOREVER);
		} catch (InterruptedException e) {
        	Log.d(TAG, "interrupted");
        	//TBD: what to do if user starts app but never touches green flag and FOREVER expires
        }
	}
	
	private int scriptID;          // this script id - an integer
	private ScriptType scriptType; 
	private char keyPressed;       // if WKP script, the key to be pressed
	private String msg;            // if WIRM script, the message to be received
	private Sprite sprite;         // Sprite that owns this script 

	
	private Thread scThread;  // this Script's thread
	
	public Script(Sprite sprite, int scriptID) {
		
		this.sprite = sprite;
		this.scriptID = scriptID;
			
		switch (sprite.getSpriteName()) {
		case GATITO1:
			scThread = new Thread(new StatmntsGatito1_1(sprite));
			scriptType = ScriptType.WGREENFLAGCLKD;
			break;
			
		case POINTT:
			scThread = new Thread(new StatmntsPointt_1(sprite));
			scriptType = ScriptType.WGREENFLAGCLKD;
			break;
		case GATITO2:
			switch (scriptID) {
			case 1:
				scThread = new Thread(new StatmntsGatito2_1(sprite));
				scriptType = ScriptType.WGREENFLAGCLKD;
				break;
			case 2:
				scThread = new Thread(new StatmntsGatito2_2(sprite));
				scriptType = ScriptType.WGREENFLAGCLKD;
				break;
			default:
				Log.d(TAG, "shouldn't be here!! gatito2 scriptid");
			}	
		default:
			Log.d(TAG, "shouldn't be here!! spriteName");	
		}
		Log.d(TAG, "script thread created");
		scThread.start();
		Log.d(TAG, "script thread started");
	}
	
	public Thread     getThread ()    {return scThread;}
	public ScriptType getScriptType() {return scriptType;}

	public class StatmntsPointt_1 implements Runnable {
	    private Sprite sprite;
	    private int i;
	    public StatmntsPointt_1(Sprite sprite) {
	    	this.sprite = sprite;
	    }
	    	
	    public void run() {
	    	
	    	whenGreenFlagClicked();
	    	sprite.goToXY(190,20);
	    	sprite.pointInDirection(90);
	    	for (i=1;i<=95;i++) { //Scratch's repeat 190 times block
	    		sprite.moveSteps(-4);
	    		sprite.pointTowardsSprite(SpriteName.GATITO1);
	    		sprite.ifOnEdgeBounce();
	    		try {
	            	Thread.sleep(25);
	            } catch (InterruptedException e) {
	            	Log.d(TAG, "interrupted");
	            }
	    	}
	    }
	    		
	}
	
	public class StatmntsGatito2_1 implements Runnable {
	    private Sprite sprite;
	    private int i;
	    public StatmntsGatito2_1(Sprite sprite) {
	    	this.sprite = sprite;
	    }
	    	
	    public void run() {
	    	
	    	whenGreenFlagClicked();
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
	public class StatmntsGatito2_2 implements Runnable {
	    private Sprite sprite;
	    private int i;
		
	    public StatmntsGatito2_2(Sprite sprite) {
	    	
	    	this.sprite = sprite;
	    }
	    	
	    public void run() {
	    	
	    	whenGreenFlagClicked();
	    	for (i=1;i<=95;i++) { //Scratch's repeat 190 times block
	    		try {
	            	Thread.sleep(500);
	            } catch (InterruptedException e) {
	            	Log.d(TAG, "interrupted");
	            }
	    		sprite.nextCostume();
	    		
	       		
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
	    	
	    	whenGreenFlagClicked();
		sprite.goToXY(0,0);
	
	    	for (i=1; i<=10; i++) {
	    		sprite.changeXby(20);
	    		sprite.changeYby(40);
    			try {
            		Thread.sleep(25);
            	} catch (InterruptedException e) {
            		Log.d(TAG, "interrupted");
            	}
	    	}
	    }   		
	}	
}
