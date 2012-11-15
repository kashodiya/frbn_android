package org.frb.sf.frbn;

import org.frb.sf.frbn.MainActivity.AppState;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class VerifyActivationActivity extends Activity {

    AsyncTask<Void, Void, Void> activationTask;
    static final String TAG = "ActivationActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_activation);
    	SharedPreferences appPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);    	
    	String email = appPreferences.getString("EMAIL", "[no email!]");
    	TextView textEmail = (TextView)findViewById(R.id.textEmail);
    	textEmail.setText("Your email: " + email);
    }
    
    public void onClickBtnStartOver(View v){
    	SharedPreferences appPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);    	
    	Editor editor = appPreferences.edit();
    	editor.putString("APP_STATE", AppState.NOT_ACTIVATED.name());
    	editor.commit();
		Intent intent = new Intent(this, ActivationActivity.class);
		startActivity(intent);
		finish();
    }

    public void onClickBtnSendToken(View v){
    	TextView editToken = (TextView)this.findViewById(R.id.editToken);
    	SharedPreferences appPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);    	
    	final String email = appPreferences.getString("EMAIL", "");
    	final String token = editToken.getText().toString();
    	//ServerUtilities.requestActivation(this, email);
    	
    	final Context context = this;
        
        activationTask = new AsyncTask<Void, Void, Void>() {
        	private ProgressDialog dialog = null;
        	private boolean requestSuccess;
        	
        	@Override
        	protected void onPreExecute() {
        		dialog = ProgressDialog.show(context, "Activation", "Verifying...", true);
                dialog.setIndeterminate(true);        		
        	};
        	
            @Override
            protected Void doInBackground(Void... params) {
                requestSuccess =
                        ServerUtilities.verifyActivation(context, email, token);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            	dialog.dismiss();
                if(requestSuccess){
                	Log.v(TAG, "Request was successful");
                	SharedPreferences appPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);    	
                	Editor editor = appPreferences.edit();
                	editor.putString("APP_STATE", AppState.ACTIVATED.name());
                	editor.putString("TOKEN", token);
                	editor.commit();
					Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);
					finish();
                }else{
                	Log.v(TAG, "Verification Request FAILED!");
                }
                activationTask = null;
            }
        };
        activationTask.execute(null, null, null);
        //If successful, launch LauncherActivity again
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_verify_activation, menu);
        return true;
    }
}
