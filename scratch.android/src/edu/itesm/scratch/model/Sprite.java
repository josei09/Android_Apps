package edu.itesm.scratch.model;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import edu.itesm.scratch.android.MainGamePanel;
import edu.itesm.scratch.model.Script;
import edu.itesm.scratch.android.MainGamePanel.SpriteName;


/**
 * @author Jose I. Icaza based on free code by impaler
 *
 */



public class Sprite {
	
	public static final int SCREENWIDTH = MainGamePanel.SCREENWIDTH;   // Scratch's screen width in pixels
	public static final int SCREENHEIGHT = MainGamePanel.SCREENHEIGHT; // must be changed later to compute actual device dimensions
	public static final float DEGTORAD = 3.14159f/180.0f;
	public static Bitmap spBitmap = Bitmap.createBitmap(SCREENWIDTH, SCREENHEIGHT, Bitmap.Config.ARGB_8888);
	public static Canvas spCanvas = new Canvas(spBitmap); // sprites write here
	private static final String TAG = Sprite.class.getSimpleName();
	static {
	    spCanvas.drawColor(Color.BLACK);
	    // TBD - should be painted with the initial screen image, not all black
	    Log.d(TAG, "canvas painted in");
	}
	
	private SpriteName spriteName;         // Sprite name
	
	private List<Bitmap> costumeList = new ArrayList<Bitmap>(); //list of costumes
	private int costumeNumber = 1;        // the current costume number
	private Bitmap bitmap;	             // the current Costume bitmap
	private int nCostumes = 0;          //number of costumes
	
	private float x = SCREENWIDTH/2;  // the X coordinate of Sprite center in Canvas coordinate system.
    private float y = SCREENHEIGHT/2; // the Y coordinate 
	private float direction = 90;     // the current direction in degrees. 
	                                  // In Scratch terms. "UP" is zero degrees, "RIGHT" 90
	
	private List<Script> scList = new ArrayList<Script>();      //list of all scripts of sprite
	private int nScripts;                                       // number of scripts of the sprite
	
	private boolean touched = false;        // if sprite has been touched
	private boolean onEdgeBounce = false;   // ifOnEdgeBounce instruction has been executed
	private boolean hidden = true;          // true if sprite is hidden
	
	public Sprite(SpriteName spriteName, int nScripts) {
		Log.d(TAG, "sprite being created");
		this.spriteName = spriteName;
		this.nScripts = nScripts;
		nCostumes = 0;
		for (int i=1; i<=nScripts; i++) {
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
	public SpriteName getSpriteName () {return spriteName;}
	
	public int getNScripts () {return nScripts;}
	
	public Script getScript(int scriptNumber) {return scList.get(scriptNumber);}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public float getX() {
		return x;
	}
	public void setXTo(float x) {
		this.x = SCREENWIDTH/2 + x;
		draw ();
	}
	public float getY() {
		return y;
	}
	public void setYTo(float y) {
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
		x += n*FloatMath.cos((90 - direction)*DEGTORAD);
		y -= n*FloatMath.sin((90 - direction)*DEGTORAD);
		draw ();
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



class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context){
        super(context);
        
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        
        // Set the Renderer for drawing on the GLSurfaceView
        //setRenderer(new MyRenderer()); //TODO
        
        // Render the view only when there is a change in the drawing data
        /* This setting prevents the GLSurfaceView frame from being redrawn 
         * until you call requestRender(), which is more efficient for this sample app.
         */
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        
    }
}

