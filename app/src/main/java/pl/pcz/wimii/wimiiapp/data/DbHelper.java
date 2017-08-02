package pl.pcz.wimii.wimiiapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import pl.pcz.wimii.wimiiapp.data.ReaderContract.NewsEntry;


public class DbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES_RSS =
            "CREATE TABLE " + NewsEntry.TABLE_NAME + " (" +
                    NewsEntry._ID + " INTEGER PRIMARY KEY," +
                    NewsEntry.COLUMN_NAME_GUID + TEXT_TYPE + "NOT NULL" + COMMA_SEP +
                    NewsEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    NewsEntry.COLUMN_NAME_DESC + TEXT_TYPE + COMMA_SEP +
                    NewsEntry.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    NewsEntry.COLUMN_NAME_DATE + TEXT_TYPE + ",unique(guid)" + " )";

    private static final String SQL_CREATE_ENTRIES_PLANSLIST =
            "CREATE TABLE " + ReaderContract.PlanListEntry.TABLE_NAME + " (" +
                    ReaderContract.PlanListEntry._ID + " INTEGER PRIMARY KEY," +
                    ReaderContract.PlanListEntry.COLUMN_NAME_SUBJECT + TEXT_TYPE + "NOT NULL" + COMMA_SEP +
                    ReaderContract.PlanListEntry.COLUMN_NAME_LINK + TEXT_TYPE + ")";

    private static final String SQL_CREATE_ENTRIES_PLAN =
            "CREATE TABLE " + ReaderContract.PlanEntry.TABLE_NAME + " (" +
                    ReaderContract.PlanEntry._ID + " INTEGER PRIMARY KEY," +
                    ReaderContract.PlanEntry.COLUMN_NAME_SUBJECT + TEXT_TYPE + "NOT NULL" + COMMA_SEP +
                    ReaderContract.PlanEntry.COLUMN_NAME_DAY + " INTEGER " + COMMA_SEP +
                    ReaderContract.PlanEntry.COLUMN_NAME_START + " INTEGER" + COMMA_SEP +
                    ReaderContract.PlanEntry.COLUMN_NAME_END + " INTEGER" + ")";

    private static final String SQL_DELETE_ENTRIES_RSS =
            "DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_PLANSLIST =
            "DROP TABLE IF EXISTS " + ReaderContract.PlanListEntry.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_PLAN =
            "DROP TABLE IF EXISTS " + ReaderContract.PlanEntry.TABLE_NAME;

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "RssReader.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_RSS);
        db.execSQL(SQL_CREATE_ENTRIES_PLANSLIST);
        db.execSQL(SQL_CREATE_ENTRIES_PLAN);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_RSS);
        db.execSQL(SQL_DELETE_ENTRIES_PLANSLIST);
        db.execSQL(SQL_DELETE_ENTRIES_PLAN);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

