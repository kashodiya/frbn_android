package org.frb.sf.frbn;

import java.util.List;

import static org.frb.sf.frbn.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gcm.GCMRegistrar;

public final class MessageListActivity extends ListActivity {

	// private static final String TAG = MessageListActivity.class.getSimpleName();

	private MessageManager messageManager;
	private MessageItemAdapter adapter;
    AsyncTask<Void, Void, Void> mRegisterTask;
    static final String TAG = "frbn";

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            //mDisplay.append(newMessage + "\n");
        	reloadMessageItems();
        }
    };
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.messageManager = new MessageManager(this);
		adapter = new MessageItemAdapter(this);
		setListAdapter(adapter);
		ListView listview = getListView();
		this.setTitle("FRB Notifications");
		registerForContextMenu(listview);
		doInit();
	}
	
    public void doInit(){
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
        } else {
            Log.i(TAG, "regId = ");
            Log.i(TAG, regId);
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                //mDisplay.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                    	SharedPreferences appPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);    	
                    	String email = appPreferences.getString("EMAIL", "[no email!]");
                        boolean registered =
                                ServerUtilities.register(context, regId, email);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }
	
	

	@Override
	protected void onResume() {
		super.onResume();
		reloadMessageItems();
	}

	private void reloadMessageItems() {
		List<Message> items = messageManager.buildMessageItems();
		adapter.clear();
		for (Message item : items) {
			adapter.add(item);
		}
		/*
		if (adapter.isEmpty()) {
			adapter.add(new MessageItem(new Message("", "", "", "", 0)));
		}
		*/
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (adapter.getItem(position) != null){
			Message message = (Message)getListView().getItemAtPosition(position);
			Intent intent = new Intent(this, MessageDisplayActivity.class);
			//Bundle b = new Bundle();
			//b.putParcelable("Message", message);
			//intent.putExtras(b);			
			intent.putExtra("Message", message);
			setResult(Activity.RESULT_OK, intent);
			startActivity(intent);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
		if (position >= adapter.getCount()
				|| adapter.getItem(position) != null) {
			menu.add(Menu.NONE, position, position, "Clear message");
		} // else it's just that dummy "Empty" message
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int position = item.getItemId();
		messageManager.deleteHistoryItem(position);
		reloadMessageItems();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (messageManager.hasHistoryItems()) {
		}
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.message_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_message_delete:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure");
			builder.setCancelable(true);
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int i2) {
							messageManager.clearHistory();
							dialog.dismiss();
							reloadMessageItems();
							//finish();
						}
					});
			builder.setNegativeButton("Cancel", null);
			builder.show();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}
