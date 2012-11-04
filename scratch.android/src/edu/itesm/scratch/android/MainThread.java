/**
 * The main game thread simply calls MainGamePanel.onDraw() repeatedly to paint the canvas
 */
package edu.itesm.scratch.android;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;



/**
 * @author impaler
 *
 * The Main thread which contains the game loop. The thread must have access to 
 * the surface view and holder to trigger events every game tick.
 */
public class MainThread extends Thread {
	
	private static final String TAG = MainThread.class.getSimpleName();

	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private MainGamePanel gamePanel;

	// flag to hold game state 
	private boolean running;
	public void setRunning(boolean running) {
		this.running = running;
	}

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	@Override
	public void run() {
        Canvas canvas;
        Log.d(TAG, "Starting game loop");
        while (running) {
            canvas = null;

            // try locking the canvas for exclusive pixel editing on the surface
            try {
                canvas = this.surfaceHolder.lockCanvas();
                
                synchronized (surfaceHolder) {
                    // draws the canvas on the panel
                    this.gamePanel.onDraw(canvas);
               }
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }   // end finally
            try {
            	Thread.sleep(33); //TBD. this gives about 30 canvas paints per second. But that depends on 
            	                  //how many sprites are there and what they are doing and what kind of android device
            	                  //and Android version is being used. Must be changed
            	                  //to reflect all this. See Impaler's tutorial http: for a super clear explanation
            	                  //and implementation of "frame rate"
            } catch (InterruptedException e) {
            	Log.d(TAG, "interrupted");
            }
            
        }
    }
	
}
