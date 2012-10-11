/**
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
import edu.itesm.scratch.model.Sprite;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();

	private MainThread thread;                            //main game thread
	
	public List<Sprite> spList = new ArrayList<Sprite>(); //list of sprites
	public static enum SpriteName {GATITO1, GATITO2};     // all sprite names TBM
	public int spListSize = 2;                            //number of sprites TBM
	
	public static final int SCREENWIDTH = 480;  // Scratch's screen width in pixels
	public static final int SCREENHEIGHT = 360; // must be changed later to compute actual device dimensions

	public MainGamePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
		
		// create sprites, load their bitmaps and start their threads
		spList.add(new Sprite(SpriteName.GATITO1, BitmapFactory.decodeResource(getResources(), R.drawable.cat1_a))); //TBM
		spList.add(new Sprite(SpriteName.GATITO2, BitmapFactory.decodeResource(getResources(), R.drawable.cat1_a))); //TBM
	}

	
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the main thread and all script threads to shut down and wait for them to finish
		// this is a clean shutdown
		boolean retry = true;
		
		while (retry) {
			try {
				thread.join();
				 
				for (int i=1; i<spListSize; i++){
					spList.get(i).getScript().getThread().join();
				}
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
			for (int i=1; i<spListSize; i++){
				spList.get(i).handleActionDown((int)event.getX(), (int)event.getY());
			}

			// check if in the lower part of the screen we exit (Scratch's red stop sign)
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity)getContext()).finish();
			} else {
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