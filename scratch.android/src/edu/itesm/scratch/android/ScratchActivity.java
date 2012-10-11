package edu.itesm.scratch.android;

/**
 * Java framework used to implement MIT's Scratch programs as set of cooperating threads in Google's Android SDK
 * Partially based on free code and tutorial by impaler, http://
 * That tutorial is based on the typical "modify the model and then draw whatever results". This however doesn't turn
 * out to be natural for Scratch programs which are naturally written as a set of parallel threads, with the Scratch
 * infrastructure automatically handling thread synchronization. 
 * 
 * @author Jose I. Icaza 
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class ScratchActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static final String TAG = ScratchActivity.class.getSimpleName();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set our MainGamePanel as the View
        setContentView(new MainGamePanel(this));
        Log.d(TAG, "View added");
    }

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");
		super.onStop();
	} 
}

