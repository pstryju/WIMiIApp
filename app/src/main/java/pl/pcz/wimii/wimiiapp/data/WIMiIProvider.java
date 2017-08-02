package pl.pcz.wimii.wimiiapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import static android.R.attr.id;
import static android.R.attr.value;

public class WIMiIProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mDbHelper;
    static final int RSS = 0;
    static final int RSS_ITEM = 1;
    static final int PLANSLIST = 2;
    static final int PLANSLIST_ITEM = 3;
    static final int PLAN = 4;
    static final int PLAN_ITEM = 5;

    private static final SQLiteQueryBuilder sRssQueryBuilder;

    static {
        sRssQueryBuilder = new SQLiteQueryBuilder();
        sRssQueryBuilder.setTables(ReaderContract.NewsEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ReaderContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, ReaderContract.PATH_RSS, RSS);
        matcher.addURI(authority, ReaderContract.PATH_RSS + "/#", RSS_ITEM);
        matcher.addURI(authority, ReaderContract.PATH_PLANSLIST, PLANSLIST);
        matcher.addURI(authority, ReaderContract.PATH_PLANSLIST + "/#", PLANSLIST_ITEM);
        matcher.addURI(authority, ReaderContract.PATH_PLAN, PLAN);
        matcher.addURI(authority, ReaderContract.PATH_PLAN + "/#", PLAN_ITEM);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case RSS:
                return ReaderContract.NewsEntry.CONTENT_TYPE;
            case RSS_ITEM:
                return ReaderContract.NewsEntry.CONTENT_ITEM_TYPE;
            case PLANSLIST:
                return ReaderContract.PlanListEntry.CONTENT_TYPE;
            case PLANSLIST_ITEM:
                return ReaderContract.PlanListEntry.CONTENT_ITEM_TYPE;
            case PLAN:
                return ReaderContract.PlanEntry.CONTENT_TYPE;
            case PLAN_ITEM:
                return ReaderContract.PlanEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case RSS:
                sRssQueryBuilder.setTables(ReaderContract.NewsEntry.TABLE_NAME);
                retCursor = sRssQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case RSS_ITEM:
                sRssQueryBuilder.setTables(ReaderContract.NewsEntry.TABLE_NAME);
                sRssQueryBuilder.appendWhere(ReaderContract.NewsEntry._ID + " = " + uri.getPathSegments().get(1));
                retCursor = sRssQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PLANSLIST:
                sRssQueryBuilder.setTables(ReaderContract.PlanListEntry.TABLE_NAME);
                retCursor = sRssQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PLANSLIST_ITEM:
                sRssQueryBuilder.setTables(ReaderContract.PlanListEntry.TABLE_NAME);
                sRssQueryBuilder.appendWhere(ReaderContract.PlanListEntry._ID + " = " + uri.getPathSegments().get(1));
                retCursor = sRssQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PLAN:
                sRssQueryBuilder.setTables(ReaderContract.PlanEntry.TABLE_NAME);
                retCursor = sRssQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PLAN_ITEM:
                sRssQueryBuilder.setTables(ReaderContract.PlanEntry.TABLE_NAME);
                sRssQueryBuilder.appendWhere(ReaderContract.PlanEntry._ID + " = " + uri.getPathSegments().get(1));
                retCursor = sRssQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.e("match", Integer.toString(sUriMatcher.match(uri)));
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        String id;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case RSS:
                count = db.delete(ReaderContract.NewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RSS_ITEM:
                id = uri.getLastPathSegment();
                count = db.delete(ReaderContract.NewsEntry.TABLE_NAME, ReaderContract.NewsEntry._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case PLANSLIST:
                count = db.delete(ReaderContract.PlanListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLANSLIST_ITEM:
                id = uri.getLastPathSegment();
                count = db.delete(ReaderContract.PlanListEntry.TABLE_NAME, ReaderContract.PlanListEntry._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case PLAN:
                count = db.delete(ReaderContract.PlanEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLAN_ITEM:
                id = uri.getLastPathSegment();
                count = db.delete(ReaderContract.PlanEntry.TABLE_NAME, ReaderContract.PlanEntry._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return count;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RSS: {
                long _id = db.insert(ReaderContract.NewsEntry.TABLE_NAME, null, values);
                if( _id > 0)
                    returnUri = ReaderContract.NewsEntry.buildRssUriWithId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PLANSLIST: {
                long _id = db.insert(ReaderContract.PlanListEntry.TABLE_NAME, null, values);
                if( _id > 0)
                    returnUri = ReaderContract.PlanListEntry.buildRssUriWithId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PLAN: {
                long _id = db.insert(ReaderContract.PlanEntry.TABLE_NAME, null, values);
                if( _id > 0)
                    returnUri = ReaderContract.PlanEntry.buildRssUriWithId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case RSS:
                count = db.update(ReaderContract.NewsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case RSS_ITEM:
                count = db.update(ReaderContract.NewsEntry.TABLE_NAME, values, ReaderContract.NewsEntry._ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            case PLANSLIST:
                count = db.update(ReaderContract.PlanListEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PLANSLIST_ITEM:
                count = db.update(ReaderContract.PlanListEntry.TABLE_NAME, values, ReaderContract.PlanListEntry._ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            case PLAN:
                count = db.update(ReaderContract.PlanEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PLAN_ITEM:
                count = db.update(ReaderContract.PlanEntry.TABLE_NAME, values, ReaderContract.PlanEntry._ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        return count;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        long _id;
        switch (match) {
            case RSS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for(ContentValues value : values) {
                        try {
                            _id = db.insertOrThrow(ReaderContract.NewsEntry.TABLE_NAME, null, value);
                        }
                        catch (SQLiteConstraintException e) {
                            _id = update(uri, value, ReaderContract.NewsEntry.COLUMN_NAME_GUID + "=?", new String[] { value.getAsString(ReaderContract.NewsEntry.COLUMN_NAME_GUID)});
                        }
                        if (_id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case PLANSLIST:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for(ContentValues value : values) {
                        try {
                            _id = db.insertOrThrow(ReaderContract.PlanListEntry.TABLE_NAME, null, value);
                        }
                        catch (SQLiteConstraintException e) {
                            _id = update(uri, value, ReaderContract.PlanListEntry.COLUMN_NAME_SUBJECT + "=?", new String[] { value.getAsString(ReaderContract.PlanListEntry.COLUMN_NAME_SUBJECT)});
                        }
                        if (_id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case PLAN:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for(ContentValues value : values) {
                        _id = db.insert(ReaderContract.PlanEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
