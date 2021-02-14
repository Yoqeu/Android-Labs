package com.example.tabatatimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH;
    private static String DB_NAME = "TabataTimer.db";
    private static final int SCHEMA = 1;
    static final String TABLE = "timers";

    public static final String ID_COLUMN = "_id";
    public static final String TITLE = "title";
    public static final String PREP_TIME = "prepTime";
    public static final String WORK_TIME = "workTime";
    public static final String REST_TIME = "restTime";
    public static final String CYCLES_AMOUNT = "cyclesAmount";
    public static final String SETS_AMOUNT = "setsAmount";
    public static final String REST_SETS = "rest_sets";
    public static final String COLOR = "color";

    private Context context;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, SCHEMA);
        this.context = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/" + DB_NAME;
        copyDataBase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE + " (" + ID_COLUMN
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITLE
                + " TEXT, " + PREP_TIME + " INTEGER, " + WORK_TIME
                + " INTEGER, " + REST_TIME + " INTEGER, " + CYCLES_AMOUNT
                + " INTEGER, " + SETS_AMOUNT + " INTEGER, " + REST_SETS
                + " INTEGER, " + COLOR + " INTEGER);");
    }

    public SQLiteDatabase openDatabase(){
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH);
        return dbFile.exists();
    }

    private void copyFile() throws IOException {
        InputStream mInput = context.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}