/**
 * The MainGamePanel handles all the initialization of the Scratch project at the onSurfaceCreated method, including
 * creating and dressing up Sprites and starting all Scripts as waiting parallel threads.
 * It also handles all input/output at the onTouchEvent and the onKeyPressedEvent methods, passing most of the work
 * to methods in the Sprite and Script classes.
 * When the red flag is hit, it pauses the project and waits for the green flag again.
 * TBD Paint green and red flags on the canvas. (Right now, clicking the upper part of screen is green, lower part is red.
 * TBD Red flag should only pause app -leave screen as it is (right now, the red flag kills app.)
 * 
 */
package edu.itesm.scratch.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import edu.itesm.scratch.model.Script;
import edu.itesm.scratch.model.Script.ScriptType;
import edu.itesm.scratch.model.Sprite;

public class MainGamePanel extends SurfaceView implements //A surface view allows us to use the whole screen
		SurfaceHolder.Callback {                          //this callback tells Android: "We handle input/output here"

	private static final String TAG = MainGamePanel.class.getSimpleName();
	public static final int SCREENWIDTH = 480;  // Scratch's screen width in pixels
	public static final int SCREENHEIGHT = 360; // TBD must be changed to compute actual device dimensions

	public static List<Sprite> spList = new ArrayList<Sprite>(); //list of sprites

	public static enum SpriteName {GATITO1, GATITO2, POINTT,FLAG1};           // all sprite names TBM

	public int nSprites;                                       //number of sprites 
	    													     
	                           
	public static boolean greenFlagClicked = false;
	private MainThread thread;                 //main project thread
	
	
	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept input events
		getHolder().addCallback(this);
		
		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle I/O events 
		setFocusable(true);
	}

	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Not needed. It would handle turning the phone or table to horizontal or vertical position
	}

	
	public void surfaceCreated(SurfaceHolder holder) {
		/** Project initialization. Called by Android system.
		 *  INPUT STATE: The app has just been started. 
		 *               all sprite costumes and the initial project screen should be in some res/drawable folder and
		 *               all sprite sounds in some res/sounds folder
		 *               The names of all sprites should already be in the enum SpriteNames
		 *               The scripts of all sprites should already be included in the Script class
		 *  OUTPUT STATE: Main game thread created and started;
		 *                all Sprite instances, all Costumes and Sounds added on
		 *                all script threads started, all of them waiting on their "When..." blocks           
		 */
		
		Sprite sprite;
		
Sprite.spCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.flag1),430,15,null);
Sprite.spCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.flag2),410,15,null);

		// Create all sprites and their costumes.
		// Creating a sprite also creates and starts all its scripts' threads.
		// All threads will be waiting on some "When..." scratch block.
		// este comentario solo debe aparecer en josei09 test branch...
		
		// TBM - in the following lines add on all sprites, their costumes and sounds if any
		sprite = new Sprite(SpriteName.GATITO1, 1);    //integer at end is number of scripts of sprite
		sprite.addCostume(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1)); 																				 
																							 
		spList.add(sprite); //add sprite instance to sprite list
		
		sprite = new Sprite(SpriteName.POINTT, 1);    //integer at end is number of scripts of sprite
		sprite.addCostume(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1)); // TBM continue adding on all costumes																				 
		spList.add(sprite); 
		sprite = new Sprite(SpriteName.GATITO2, 2);   //TBM continue adding on all sprites and costumes
		sprite.addCostume(BitmapFactory.decodeResource(getResources(), R.drawable.cat1_a));
		sprite.addCostume(BitmapFactory.decodeResource(getResources(), R.drawable.cat1_a));

		spList.add(sprite);
		// END TBM
		
		nSprites = spList.size();
		
		// TBD - get the initial project screen and load it up on spCanvas, instead of the static method in Sprite
		// TBD - paint the green flag here, after all sprites and scripts etc have been created.
		
		// at this point the surface is created and
		// we can safely start the game loop that periodically repaints the surface canvas:
		thread.setRunning(true);
		thread.start();		
	}

	
	public void surfaceDestroyed(SurfaceHolder holder) {
		/** Project termination. Called by Android system
		 * 
		 */
		Log.d(TAG, "Surface is being destroyed");
		// tell the main thread and all script threads to shut down and wait for them to finish.
		// this is a clean shutdown
		boolean retry = true;
		
		while (retry) {
			try {
				for (int i=0; i<=nSprites-1; i++) {
					Sprite sprite = spList.get(i);
					int nScripts = sprite.getNScripts();
					for (int j=0; j<=nScripts-1; j++) {
						sprite.getScript(j).getThread().join(); //stop sprite thread
				    }
				}
				thread.join(); //stop main thread
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the threads
			}
		}
		Log.d(TAG, "Threads were shut down cleanly");
	}

	
	public boolean onTouchEvent(MotionEvent event) {

		/* Handles all input/ouput: touch, drag, keypad and keyboard input */
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) { //screen touched
			// delegating event handling to the sprites
			for (int i=0; i <= nSprites-1; i++){
				spList.get(i).handleActionDown((int)event.getX(), (int)event.getY());
			}
            // TBD - check if actual red or green flags touched.
			//  (Scratch's red stop sign)
			if (event.getY() >=  19 && event.getY() <=50  && event.getX() >= 419 && event.getX() <= 443  ) {
				thread.setRunning(false);
				((Activity)getContext()).finish();
				
				   //  (Scratch green flag sign)
			} else if (event.getY() >=  23 && event.getY() <=44  && event.getX() >= 449 && event.getX() <= 473) {
				greenFlagClicked = true;
				
				// interrupt all threads sleep/waiting for the green flag at class Script:
				for (int i=0; i<=nSprites-1; i++) {
					Sprite sprite = spList.get(i);
					int nScripts = sprite.getNScripts();
					for (int j=0; j<=nScripts-1; j++) {
						Script script = sprite.getScript(j);
						if (script.getScriptType() == ScriptType.WGREENFLAGCLKD) {
							script.getThread().interrupt(); //once interrupted, the script starts exectuting
						}
					}
				}
			}
			else {
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
			
			
		} 
//		if (event.getAction() == MotionEvent.ACTION_MOVE) { // a sprite is being dragged
			
//			if (sprite.isTouched()) {
				// the sprite was picked up and is being dragged
//				sprite.setX((float)event.getX());
//				sprite.setY((float)event.getY());
//			}
//		} if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
//			if (sprite.isTouched()) {
//				sprite.setTouched(false);
//			}
//		}
//      TBD handle keyboard input
//		TBD handle keypad input (arrows)
		return true; 
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		canvas.drawBitmap(Sprite.spBitmap, 0, 0, null); //draw all sprites on surface canvas
	}
}