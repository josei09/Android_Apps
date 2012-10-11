package edu.itesm.scratch.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import edu.itesm.scratch.android.MainGamePanel;
import edu.itesm.scratch.android.MainGamePanel.SpriteName;

/**
 * @author Jose I. Icaza based on free code by impaler
 *
 */



public class Sprite {
	
	public static final int SCREENWIDTH = MainGamePanel.SCREENWIDTH;   // Scratch's screen width in pixels
	public static final int SCREENHEIGHT = MainGamePanel.SCREENHEIGHT; // must be changed later to compute actual device dimensions
	
	public static Bitmap spBitmap = Bitmap.createBitmap(SCREENWIDTH, SCREENHEIGHT, Bitmap.Config.ARGB_8888);
	public static Canvas spCanvas = new Canvas(spBitmap); // sprites write here
	private static final String TAG = MainGamePanel.class.getSimpleName();
	static {
	    spCanvas.drawColor(Color.BLACK);
	    Log.d(TAG, "canvas painted in");
	}
	
	private SpriteName spriteName;     // Sprite name
	private Bitmap bitmap;	 // the current Costume bitmap
	private float x;		 // the X coordinate in Canvas coordinate system.  
	private float y;	     // the Y coordinate 
	private float direction; // the current direction in degrees. In Scratch terms. "UP" is zero degrees, "RIGHT" 90
	private boolean touched; // if sprite has been touched
	private Script script;   // one of the sprite's scripts. Later on a list of scripts
	private boolean onEdgeBounce;    // ifOnEdgeBounce instruction has been executed
	
	
	public Sprite(SpriteName spriteName, Bitmap bitmap, float x, float y, float direction) {
		this.spriteName = spriteName;
		this.bitmap = bitmap;
		this.x = SCREENWIDTH/2 + x;  // convert from Scratch to canvas coordinate system
		this.y = SCREENHEIGHT/2 - y;
		this.direction = direction;
		
		onEdgeBounce = false;
		touched = false;
		script = new Script(this.spriteName, this, 1);
		Log.d(TAG, "script created");
	}
	
	public Sprite(SpriteName spriteName, Bitmap bitmap) {
		this.spriteName = spriteName;
		this.bitmap = bitmap;
		x = SCREENWIDTH/2;
		y = SCREENHEIGHT/2;
		direction = 90;
		onEdgeBounce = false;
		touched = false;
		script = new Script(this.spriteName,this, 1);
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = SCREENWIDTH/2 + x;
		draw ();
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = SCREENHEIGHT/2 - y;
		draw ();
	}
	
	public float getDirection() {
		return direction;
	}
	public void pointInDirection(float direction) {
		this.direction = direction;
		draw (); //falta rotar el bitmap or whatever...
	}

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	private void draw() {
		spCanvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
	}
	
	public void goToXY(float x, float y) {
		this.x = SCREENWIDTH/2 + x;  // convert from Scratch to canvas coordinate system
		this.y = SCREENHEIGHT/2 - y;
		draw ();
	}
	
	public void ifOnEdgeBounce () {
		this.onEdgeBounce = true;
		//falta checar si esta en el borde para darle reversa a su velocidad en x y en y.
	}
	
	public void moveSteps (float n) {
		x += n*FloatMath.cos(90 - direction);
		y += n*FloatMath.sin(90 - direction);
		draw ();
	}
	
	public Script getScript() {
		return script;
	}
	
	
	
	
	
	

	/**
	 * Handles the {@link MotionEvent.ACTION_DOWN} event. If the event happens on the 
	 * bitmap surface then the touched state is set to <code>true</code> otherwise to <code>false</code>
	 * @param eventX - the event's X coordinate
	 * @param eventY - the event's Y coordinate
	 */
	public void handleActionDown(int eventX, int eventY) {
		if (eventX >= (x - bitmap.getWidth() / 2) && (eventX <= (x + bitmap.getWidth()/2))) {
			if (eventY >= (y - bitmap.getHeight() / 2) && (y <= (y + bitmap.getHeight() / 2))) {
				// sprite touched
				setTouched(true);
			} else {
				setTouched(false);
			}
		} else {
			setTouched(false);
		}

	}
}



