package org.frb.sf.frbn;

import java.util.ArrayList;

import org.frb.sf.frbn.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

final class MessageItemAdapter extends ArrayAdapter<Message> {

  private final Activity activity;

  MessageItemAdapter(Activity activity) {
    super(activity, R.layout.message_list_item, new ArrayList<Message>());
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

    Message message = getItem(position);

    String title;
    String category_district;
    
    if (message != null) {
      title = message.getTitle();
      category_district = "[" + message.getCategory() + "] - "+ message.getDistrict();
    } else {
      //Resources resources = getContext().getResources();
      title = "No messsages";
      category_district = "No message";
    }

    ((TextView) layout.findViewById(R.id.message_title)).setText(title);    
    ((TextView) layout.findViewById(R.id.message_category_district)).setText(category_district);

    return layout;
  }

}
