/*
 * Copyright 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.frb.sf.frbn.data;

import java.util.List;

import org.frb.sf.frbn.MessageDisplayActivity;
import org.frb.sf.frbn.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public final class MessageListActivity extends ListActivity {

//  private static final String TAG = MessageListActivity.class.getSimpleName();

  private MessageManager messageManager;
  private MessageItemAdapter adapter;
  
  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.messageManager = new MessageManager(this);  
    adapter = new MessageItemAdapter(this);
    setListAdapter(adapter);
    ListView listview = getListView();
    this.setTitle("FRB Notifications");
    registerForContextMenu(listview);
  }

  @Override
  protected void onResume() {
    super.onResume();
    reloadHistoryItems();
  }

  private void reloadHistoryItems() {
    List<MessageItem> items = messageManager.buildMessageItems();
    adapter.clear();
    for (MessageItem item : items) {
      adapter.add(item);
    }
    if (adapter.isEmpty()) {
      adapter.add(new MessageItem(new Message("", "", "", "", 0)));
    }
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    if (adapter.getItem(position).getResult() != null) {
      Intent intent = new Intent(this, MessageDisplayActivity.class);
      setResult(Activity.RESULT_OK, intent);
	  startActivity(intent);
      //finish();
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu,
                                  View v,
                                  ContextMenu.ContextMenuInfo menuInfo) {
    int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
    if (position >= adapter.getCount() || adapter.getItem(position).getResult() != null) {
      menu.add(Menu.NONE, position, position, "Clear message");
    } // else it's just that dummy "Empty" message
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    int position = item.getItemId();
    messageManager.deleteHistoryItem(position);
    reloadHistoryItems();
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (messageManager.hasHistoryItems()) {
      MenuInflater menuInflater = getMenuInflater();
      menuInflater.inflate(R.menu.message_list, menu);
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_message_delete:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int i2) {
            messageManager.clearHistory();
            dialog.dismiss();
            finish();
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
