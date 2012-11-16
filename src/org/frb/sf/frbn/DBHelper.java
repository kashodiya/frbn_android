package org.frb.sf.frbn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final class DBHelper extends SQLiteOpenHelper {

	//http://code.google.com/p/zxing/
	
  private static final int DB_VERSION = 1;
  private static final String DB_NAME = "frbn.db";
  static final String TABLE_NAME = "messages";
  static final String MESSAGE_ID = "id";
  static final String MESSAGE_BODY_COL = "messageBody";
  static final String TITLE_COL = "title";
  static final String CATEGORY_COL = "category";
  static final String DISTRICT_COL = "district";
  static final String TIMESTAMP_COL = "timestamp";

  DBHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(
            "CREATE TABLE " + TABLE_NAME + " (" +
            MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MESSAGE_BODY_COL + " TEXT, " +
            TITLE_COL + " TEXT, " +
            CATEGORY_COL + " TEXT, " +
            DISTRICT_COL + " TEXT, " +
            TIMESTAMP_COL + " INTEGER);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    onCreate(sqLiteDatabase);
  }

}
