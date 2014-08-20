package com.codebrahma.kamesh.todo;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kamesh on 19/8/14.
 */

public class DBContract {

    public static final String DB_NAME = "Test.db";
    public static final int DB_VERSION = 1;
    public static final String FEED_TABLE = "todo";

    public static final String AUTHORITY = "com.codebrahma.kamesh.todo.FeedProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + FEED_TABLE);
    public static final int STATUS_ITEM = 1;
    public static final int STATUS_DIR = 2;
    public static final String STATUS_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.com.codebrahma.kamesh.todo.provider.status";
    public static final String STATUS_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.codebrahma.kamesh.todo.provider.status";

    //public static final String FEED_DEFAULT_SORT = Feed_Column.ID + " DESC";

    public class Feed_Column {
        public static final String ID = BaseColumns._ID; //
        public static final String TEXT= "text";
        public static final String STATUS= "status";

    }
}
