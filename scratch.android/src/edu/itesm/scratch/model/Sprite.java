package edu.itesm.scratch.model;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import edu.itesm.scratch.android.MainGamePanel;
import edu.itesm.scratch.model.Script;
import edu.itesm.scratch.android.MainGamePanel.SpriteName;

/**
 * This class is invoked from MainGamePanel to initialize sprites and from the Scripts class
 * to exectute some of the Scratch instructions. When a sprite is initialized together with its costumes and sounds,
 * its scripts' threads are created in the Scripts class, and since all scripts start with a When bock, all scripts
 * are set to sleep until the corresponding When condition occurs. 
 * @author Jose I. Icaza and students from the August 2012 Programming Project class at Tecnologico de Monterrey, Mexico
 *
 */



public class Sprite {
	
	public static final int SCREENWIDTH = MainGamePanel.SCREENWIDTH;   // Scratch's screen width in pixels
	public static final int SCREENHEIGHT = MainGamePanel.SCREENHEIGHT; // must be changed later to compute actual device dimensions
	public static final float DEGTORAD = 3.14159f/180.0f;              // degrees to radians conversion
	
	// bitmap and canvas where all sprites are painted on. Copied later to surface canvas by MainGamePanel.onDraw method
	public static Bitmap spBitmap = Bitmap.createBitmap(SCREENWIDTH, SCREENHEIGHT, Bitmap.Config.ARGB_8888);
	public static Canvas spCanvas = new Canvas(spBitmap); // sprites write here
	
	private static final String TAG = Sprite.class.getSimpleName();
	static {
	    spCanvas.drawColor(Color.BLACK);
	    // TBD - should be painted with the initial screen image, not all black
	    Log.d(TAG, "canvas painted in");
	}
	

	private SpriteName spriteName;         // Sprite name. SpriteName is a public enum declared in MainGamePanel
	
	private List<Bitmap> costumeList = new ArrayList<Bitmap>(); // list of costumes of sprite
	                                                            // TBD list of sounds 
	                                                            // TBD currently, costomes are accessed by number.
	                                                            // They need to be accessed by name for some Scratch
	                                                            // instructions. Same with sounds

	private int costumeNumber = 1;        // the current costume number
	private Bitmap bitmap;	             // the current Costume bitmap
	private int nCostumes = 0;          //number of costumes of Sprite
	
	private float x = SCREENWIDTH/2;  // the X coordinate of Sprite center in Canvas coordinate system.
	private float y = SCREENHEIGHT/2; // the Y coordinate    
	private float direction = 90;     // the current direction in degrees. 
	                                  // In Scratch terms. "UP" is zero degrees, "RIGHT" 90
	private float Ynew;
	private float Xnew;
	private double dirNew;
	
	private List<Script> scList = new ArrayList<Script>();      //list of all scripts of sprite
	private int nScripts;                                       // number of scripts of the sprite
	
	private boolean touched = false;        // if sprite has been touched
	private boolean onEdgeBounce = false;   // ifOnEdgeBounce instruction has been executed
	private boolean hidden = true;          // true if sprite is hidden
	
	public Sprite(SpriteName spriteName, int numberOfScripts) {
		Log.d(TAG, "sprite being created");
		this.spriteName = spriteName;
		this.nScripts = numberOfScripts;
		nCostumes = 0;
		for (int i=1; i<=numberOfScripts; i++) {
			Log.d(TAG, "about to create script");
			Script script = new Script(this, i);
			Log.d(TAG, "script created");
			
			scList.add (script);
		}
	}
	public void addCostume(Bitmap costume) {
		costumeList.add(costume);
		nCostumes++;
		if (nCostumes == 1) {
			Log.d(TAG, "first costume setup");
			costumeNumber = 1;
			bitmap = costume;  //default costume is the first one
		}
	}
	public void nextCostume() {
		costumeNumber++;
		if (costumeNumber > nCostumes) costumeNumber = 1;
		bitmap = costumeList.get(costumeNumber-1);
	}
	public void switchToCostume(int costumeChange) {
		
		if (costumeChange>0 && costumeChange <= nCostumes) costumeNumber = costumeChange;
		bitmap = costumeList.get(costumeNumber-1);
	
	}
	
	public SpriteName getSpriteName () {return spriteName;}
	
	public int getNScripts () {return nScripts;}
	
	public Script getScript(int scriptNumber) {return scList.get(scriptNumber);}
	
	public Bitmap getBitmap() { return bitmap;}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public float getX() { return x;}
	
	public void goToXY(float x, float y) { //scratch's " go to x:n y:n" instruction
		this.x = SCREENWIDTH/2 + x;  // convert from Scratch to canvas coordinate system
		this.y = SCREENHEIGHT/2 - y;
		draw ();
	}
	
	public void setXTo(float x) { // Scratch's "set x to n" instruction
		this.x = SCREENWIDTH/2 + x;
		draw (); //draw sprite on sprites' canvas spCanvas, at the new x position.
	}
	
	public void changeXby(float varx) {
		x = x + varx;
		draw();
	}
	
	public float getY() {
		return y;
	}
	
	public void setYTo(float y) {
		this.y = SCREENHEIGHT/2 - y;
		draw ();
	}
	

	public void changeYby(float vary) {
		y = y - vary;
		draw();
	}
	
	
	public void moveSteps (float n) {
		x += n*FloatMath.cos((90 - direction)*DEGTORAD);
		y -= n*FloatMath.sin((90 - direction)*DEGTORAD);
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
	
	
	
	public void ifOnEdgeBounce () {
		this.onEdgeBounce = true;
		// TBD falta checar si esta en el borde para darle reversa a su velocidad en x y en y.
	}
	

	public void pointTowardsSprite (SpriteName spriteName ) {
		
			for(int i=0;i< MainGamePanel.spList.size() ;i++){	
				
					Sprite nuevo= MainGamePanel.spList.get(i);
					if(nuevo.spriteName == spriteName){
					Ynew=nuevo.getY();
					Xnew=nuevo.getX();
					dirNew=Math.atan2((y-Ynew),(x- Xnew));
					direction=(float)dirNew;
					pointInDirection(direction);
					}
					
				
		}
			

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



