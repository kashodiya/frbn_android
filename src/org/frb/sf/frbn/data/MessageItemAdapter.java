package org.frb.sf.frbn.data;

import java.util.ArrayList;

import org.frb.sf.frbn.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

final class MessageItemAdapter extends ArrayAdapter<MessageItem> {

  private final Activity activity;

  MessageItemAdapter(Activity activity) {
    super(activity, R.layout.message_list_item, new ArrayList<MessageItem>());
    this.activity = activity;
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {
    LinearLayout layout;
    if (view instanceof LinearLayout) {
      layout = (LinearLayout) view;
    } else {
      LayoutInflater factory = LayoutInflater.from(activity);
      layout = (LinearLayout) factory.inflate(R.layout.message_list_item, viewGroup, false);
    }

    MessageItem item = getItem(position);
    Message message = item.getResult();

    String title;
    String detail;
    if (message != null) {
      title = message.getTitle();
      detail = item.getDisplayAndDetails();      
    } else {
      //Resources resources = getContext().getResources();
      title = "No messsages";
      detail = "No message";
    }

    ((TextView) layout.findViewById(R.id.message_title)).setText(detail);    
    ((TextView) layout.findViewById(R.id.message_detail)).setText(title);

    return layout;
  }

}
