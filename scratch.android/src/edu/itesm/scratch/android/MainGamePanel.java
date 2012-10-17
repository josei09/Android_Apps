/**
 * The MainGamePanel handles all the initialization of the Scratch project at the onSurfaceCreated method, including
 * creating and dressing up Sprites and starting all Scripts as waiting parallel threads.
 * It also handles all input/output at the onTouchEvent and the onKeyPressedEvent methods, passing most of the work
 * to methods in the Sprite and Script classes.
 * When the red flag is hit, it pauses the project and waits for the green flag again.
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

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	public static final int SCREENWIDTH = 480;  // Scratch's screen width in pixels
	public static final int SCREENHEIGHT = 360; // must be changed later to compute actual device dimensions

	public static List<Sprite> spList = new ArrayList<Sprite>(); //list of sprites
	public static enum SpriteName {GATITO1, GATITO2};           // all sprite names TBM
	public int nSprites;                                       //number of sprites 
	                           
	public static boolean greenFlagClicked = false;
	private MainThread thread;                 //main project thread
	
	
	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept input events
		getHolder().addCallback(this);
		
		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// to be done
	}

	
	public void surfaceCreated(SurfaceHolder holder) {
		/** Project initialization
		 *  INPUT STATE: The app has just been started. 
		 *               all sprite costumes should be in some res/drawable folder and
		 *               all sprite sounds in some res/sounds folder
		 *               The names of all sprites should already be in the enum SpriteNames
		 *               The scripts of all sprites should already be included in the Script class
		 *  OUTPUT STATE: Main game thread created and started;
		 *                all Sprite instances, all Costumes added on
		 *                all script threads started, all of them waiting on their "When..." blocks           
		 */
		
		Sprite sprite;
		
		// Create all sprites and their costumes.
		// Creating a sprite also creates and starts all its scripts' threads.
		// All threads will be waiting on some "When..." scratch block.
		
		sprite = new Sprite(SpriteName.GATITO1, 1);    //integer at end is number of scripts of sprite
		sprite.addCostume(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1)); // TBM continue adding on all costumes																				 
		spList.add(sprite); 
		
		sprite = new Sprite(SpriteName.GATITO2, 2);   //TBM continue adding on all sprites and costumes
		sprite.addCostume(BitmapFactory.decodeResource(getResources(), R.drawable.cat1_a_9));
		sprite.addCostume(BitmapFactory.decodeResource(getResources(), R.drawable.cat1_b));

		spList.add(sprite);
		
		nSprites = spList.size();
		
		// TBD - get the initial project image and load it up on spCanvas, instead of the static method in Sprite
		// TBD - the green flag should appear in the interface here, after all sprites and scripts etc
		//       have been created.
		// TBD - get all sounds into a spSounds list in Sprite class
		
		// at this point the surface is created and
		// we can safely start the game loop that periodically repaints the surface canvas:
		thread.setRunning(true);
		thread.start();
		
	}

	
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the main thread and all script threads to shut down and wait for them to finish
		// this is a clean shutdown
		boolean retry = true;
		
		while (retry) {
			try {
				for (int i=0; i<=nSprites-1; i++) {
					Sprite sprite = spList.get(i);
					int nScripts = sprite.getNScripts();
					for (int j=0; j<=nScripts-1; j++) {
						sprite.getScript(j).getThread().join();
				    }
				}
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the threads
			}
		}
		Log.d(TAG, "Threads were shut down cleanly");
	}

	
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) { //screen touched
			// delegating event handling to the sprites
			for (int i=0; i <= nSprites-1; i++){
				spList.get(i).handleActionDown((int)event.getX(), (int)event.getY());
			}

			// check if in the lower part of the screen we exit (Scratch's red stop sign)
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity)getContext()).finish();
				
				   // or in the upper part of the screen we start (Scratch green flag sign)
			} else if (event.getY() < 50) {
				greenFlagClicked = true;
				
				// interrupt all threads sleep/waiting for the green flag:
				for (int i=0; i<=nSprites-1; i++) {
					Sprite sprite = spList.get(i);
					int nScripts = sprite.getNScripts();
					for (int j=0; j<=nScripts-1; j++) {
						Script script = sprite.getScript(j);
						if (script.getScriptType() == ScriptType.WGREENFLAGCLKD) {
							script.getThread().interrupt();
						}
					}
				}
			}
			else {
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
			
			
		} 
//		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// the gestures
//			if (sprite.isTouched()) {
				// the droid was picked up and is being dragged
//				sprite.setX((int)event.getX());
//				sprite.setY((int)event.getY());
//			}
//		} if (event.getAction() == MotionEvent.ACTION_UP) {
			// touch was released
//			if (sprite.isTouched()) {
//				sprite.setTouched(false);
//			}
//		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		canvas.drawBitmap(Sprite.spBitmap, 0, 0, null);
	}
}