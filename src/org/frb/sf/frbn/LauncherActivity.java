package org.frb.sf.frbn;

import org.frb.sf.frbn.MainActivity.AppState;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class LauncherActivity extends Activity {
    static final String TAG = "frbn";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    Intent intent;
	    
    	SharedPreferences appPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);    	
    	String appStateStr = appPreferences.getString("APP_STATE", AppState.NOT_ACTIVATED.name());
        Log.i(TAG, "APP_STATE = ");
        Log.i(TAG, appStateStr);
    	
    	if(AppState.valueOf(appStateStr).equals(AppState.NOT_ACTIVATED)){
 	       //intent = new Intent(this, MainActivity.class);
	       intent = new Intent(this, ActivationActivity.class);
    	} else if(AppState.valueOf(appStateStr).equals(AppState.ACTIVATION_PENDING)){
 	       intent = new Intent(this, VerifyActivationActivity.class);
	    } else {
		       intent = new Intent(this, MessageListActivity.class);
		       //intent = new Intent(this, MainActivity.class);
	    }
    	
	    startActivity(intent);
	    finish();
	}
}
