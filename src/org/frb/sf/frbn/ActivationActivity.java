package org.frb.sf.frbn;

import org.frb.sf.frbn.MainActivity.AppState;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ActivationActivity extends Activity {

    static final String TAG = "ActivationActivity";
    AsyncTask<Void, Void, Void> activationTask;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation);
    }
    
    public void onClickBtnReqActivation(View v){
    	TextView editEmail = (TextView)this.findViewById(R.id.editEmail);
    	final String email = editEmail.getText().toString();
    	//ServerUtilities.requestActivation(this, email);
    	
    	final Context context = this;
        
        activationTask = new AsyncTask<Void, Void, Void>() {
        	private ProgressDialog dialog = null;
        	private boolean requestSuccess;
        	
        	@Override
        	protected void onPreExecute() {
        		dialog = ProgressDialog.show(context, "Activation", "Sending request", true);
                dialog.setIndeterminate(true);        		
        	};
        	
            @Override
            protected Void doInBackground(Void... params) {
                requestSuccess =
                        ServerUtilities.requestActivation(context, email);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            	dialog.dismiss();
                if(requestSuccess){
                	Log.v(TAG, "Request was successful");
                	SharedPreferences appPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);    	
                	Editor editor = appPreferences.edit();
                	editor.putString("APP_STATE", AppState.ACTIVATION_PENDING.name());
                	editor.putString("EMAIL", email);
                	editor.commit();
					Intent intent = new Intent(context, VerifyActivationActivity.class);
					startActivity(intent);
					finish();
                }else{
                	Log.v(TAG, "Request FAILED!");
                }
                activationTask = null;
            }
        };
        activationTask.execute(null, null, null);
        //If successful, launch LauncherActivity again
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activation, menu);
        return true;
    }
}
