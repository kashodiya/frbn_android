package org.frb.sf.frbn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public final class MessageManager {

  private static final String TAG = MessageManager.class.getSimpleName();

//  private static final int MAX_ITEMS = 500;

  private static final String[] COLUMNS = {
      DBHelper.MESSAGE_BODY_COL,
      DBHelper.TITLE_COL,
      DBHelper.CATEGORY_COL,
      DBHelper.DISTRICT_COL,
      DBHelper.TIMESTAMP_COL,
  };

  private static final String[] COUNT_COLUMN = { "COUNT(1)" };

  private static final String[] ID_COL_PROJECTION = { DBHelper.MESSAGE_ID };
//  private static final String[] ID_DETAIL_COL_PROJECTION = { DBHelper.ID_COL, DBHelper.DETAILS_COL };
//  private static final DateFormat EXPORT_DATE_TIME_FORMAT =
//      DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

  private final Context activity;

  public MessageManager(Context activity) {
    this.activity = activity;
  }

  public boolean hasHistoryItems() {
    SQLiteOpenHelper helper = new DBHelper(activity);
    SQLiteDatabase db = null;
    Cursor cursor = null;
    try {
      db = helper.getReadableDatabase();
      cursor = db.query(DBHelper.TABLE_NAME, COUNT_COLUMN, null, null, null, null, null);
      cursor.moveToFirst();
      return cursor.getInt(0) > 0;
    } finally {
      close(cursor, db);
    }
  }

  public List<Message> buildMessageItems() {
    SQLiteOpenHelper helper = new DBHelper(activity);
    List<Message> items = new ArrayList<Message>();
    SQLiteDatabase db = null;
    Cursor cursor = null;
    try {
      db = helper.getReadableDatabase();
      cursor = db.query(DBHelper.TABLE_NAME, COLUMNS, null, null, null, null, DBHelper.TIMESTAMP_COL + " DESC");
      while (cursor.moveToNext()) {
        String messageBody = cursor.getString(0);
        String title = cursor.getString(1);
        String category = cursor.getString(2);
        String district = cursor.getString(3);
        long timestamp = cursor.getLong(4);
        Message message = new Message(messageBody, title, category, district, timestamp);
        items.add(message);
        //items.add(new MessageItem(message));
      }
    } finally {
      close(cursor, db);
    }
    return items;
  }

  public Message buildHistoryItem(int number) {
    SQLiteOpenHelper helper = new DBHelper(activity);
    SQLiteDatabase db = null;
    Cursor cursor = null;
    try {
      db = helper.getReadableDatabase();
      cursor = db.query(DBHelper.TABLE_NAME, COLUMNS, null, null, null, null, DBHelper.TIMESTAMP_COL + " DESC");
      cursor.move(number + 1);
      String messageBody = cursor.getString(0);
      String title = cursor.getString(1);
      String category = cursor.getString(2);
      String district = cursor.getString(3);
      long timestamp = cursor.getLong(4);
      Message message = new Message(messageBody, title, category, district, timestamp);
      return message;
    } finally {
      close(cursor, db);
    }
  }
  
  public void deleteHistoryItem(int number) {
    SQLiteOpenHelper helper = new DBHelper(activity);
    SQLiteDatabase db = null;
    Cursor cursor = null;
    try {
      db = helper.getWritableDatabase();      
      cursor = db.query(DBHelper.TABLE_NAME,
                        ID_COL_PROJECTION,
                        null, null, null, null,
                        DBHelper.TIMESTAMP_COL + " DESC");
      cursor.move(number + 1);
      db.delete(DBHelper.TABLE_NAME, DBHelper.MESSAGE_ID + '=' + cursor.getString(0), null);
    } finally {
      close(cursor, db);
    }
  }
  /*  
*/
  
  public void addMessageItem(Message message) {

    ContentValues values = new ContentValues();
    values.put(DBHelper.MESSAGE_BODY_COL, message.getMessageBody());
    values.put(DBHelper.TITLE_COL, message.getTitle());
    values.put(DBHelper.CATEGORY_COL, message.getCategory());
    values.put(DBHelper.DISTRICT_COL, message.getDistrict());
    values.put(DBHelper.TIMESTAMP_COL, message.getTimestamp());

    SQLiteOpenHelper helper = new DBHelper(activity);
    SQLiteDatabase db = null;
    try {
      db = helper.getWritableDatabase();      
      // Insert the new entry into the DB.
      db.insert(DBHelper.TABLE_NAME, DBHelper.TIMESTAMP_COL, values);
    } finally {
      close(null, db);
    }
  }


  private void deleteMessage(long id) {
    SQLiteOpenHelper helper = new DBHelper(activity);
    SQLiteDatabase db = null;
    try {
      db = helper.getWritableDatabase();      
      db.delete(DBHelper.TABLE_NAME, DBHelper.MESSAGE_ID + "=?", new String[] { String.valueOf(id) });
    } finally {
      close(null, db);
    }
  }
  
  void clearHistory() {
    SQLiteOpenHelper helper = new DBHelper(activity);
    SQLiteDatabase db = null;
    try {
      db = helper.getWritableDatabase();      
      db.delete(DBHelper.TABLE_NAME, null, null);
    } finally {
      close(null, db);
    }
  }

  static Uri saveHistory(String history) {
    File bsRoot = new File(Environment.getExternalStorageDirectory(), "BarcodeScanner");
    File historyRoot = new File(bsRoot, "History");
    if (!historyRoot.exists() && !historyRoot.mkdirs()) {
      Log.w(TAG, "Couldn't make dir " + historyRoot);
      return null;
    }
    File historyFile = new File(historyRoot, "history-" + System.currentTimeMillis() + ".csv");
    OutputStreamWriter out = null;
    try {
      out = new OutputStreamWriter(new FileOutputStream(historyFile), Charset.forName("UTF-8"));
      out.write(history);
      return Uri.parse("file://" + historyFile.getAbsolutePath());
    } catch (IOException ioe) {
      Log.w(TAG, "Couldn't access file " + historyFile + " due to " + ioe);
      return null;
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException ioe) {
          // do nothing
        }
      }
    }
  }

  private static void close(Cursor cursor, SQLiteDatabase database) {
    if (cursor != null) {
      cursor.close();
    }
    if (database != null) {
      database.close();
    }
  }

}
