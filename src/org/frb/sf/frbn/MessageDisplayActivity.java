package org.frb.sf.frbn;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MessageDisplayActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_display);
        
        //Bundle b = this.getIntent().getExtras();
        Message message = (Message)this.getIntent().getParcelableExtra("Message");
        
        ((TextView)findViewById(R.id.text_msg_body)).setText(message.getMessageBody());
        ((TextView)findViewById(R.id.text_msg_category)).setText(message.getCategory());
        ((TextView)findViewById(R.id.text_msg_district)).setText(message.getDistrict());
        ((TextView)findViewById(R.id.text_msg_title)).setText(message.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_message_display, menu);
        return true;
    }
}
