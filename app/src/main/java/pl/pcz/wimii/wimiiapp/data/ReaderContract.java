package pl.pcz.wimii.wimiiapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ReaderContract {

    private ReaderContract() {}

    static final String CONTENT_AUTHORITY = "pl.pcz.wimii.wimiiapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_RSS = "rss";
    static final String PATH_PLANSLIST = "timetable_list";
    static final String PATH_PLAN = "single_timetable";

    public static class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RSS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RSS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RSS;

        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_GUID = "guid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_DESC = "desc";
        public static final String COLUMN_NAME_DATE = "date";

        public static Uri buildRssUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildRssUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class PlanListEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANSLIST).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLANSLIST;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLANSLIST;
        public static final String TABLE_NAME = "planslist";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_LINK = "link";

        public static Uri buildRssUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildRssUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static class PlanEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAN).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAN;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAN;
        public static final String TABLE_NAME = "plan";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_START = "start";
        public static final String COLUMN_NAME_END = "end";

        public static Uri buildRssUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildRssUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
