package ca.mobile.lab7;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class StudentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "ca.mobile.lab7.StudentProvider";
    static final String URL = "content://"+PROVIDER_NAME+"/students";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String ID = "id";
    static final String NAME = "name";
    static final String GRADE = "grade";

    private static HashMap<String,String> STUDENT_PROJECTION_MAP;

    static final int STUDENTS=1;
    static final int STUDENT_ID = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"students",STUDENTS);
        uriMatcher.addURI(PROVIDER_NAME,"students/#",STUDENT_ID);
    }

    private SQLiteDatabase db;
    static final String DB_NAME = "College";
    static final String TABLE = "students";
    static final int DB_VERSION = 1;
    static final String CREATE_SQL = "CREATE TABLE "+TABLE+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                NAME+" TEXT NOT NULL, " +
                                GRADE+" TEXT NOT NULL);";

    private static  class DBHelper extends SQLiteOpenHelper{
        DBHelper(Context context)
        {
            super(context,DB_NAME,null,DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE);
            onCreate(db);
        }

    }

    public StudentProvider() {
    }

    @Override
    public boolean onCreate() {
       Context context = getContext();
       DBHelper dbHelper = new DBHelper(context);

       db = dbHelper.getWritableDatabase();
       return (db!=null);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
       long id = db.insert(TABLE,"",values);
       if(id>0)
       {
           Uri _uri = ContentUris.withAppendedId(CONTENT_URI,id);
           getContext().getContentResolver().notifyChange(_uri,null);
           return _uri;
       }
       throw new SQLException("insert failed"+uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE);

        switch (uriMatcher.match(uri)){
            case STUDENTS:
                qb.setProjectionMap(STUDENT_PROJECTION_MAP);
                break;

            case STUDENT_ID:
                qb.appendWhere(ID+"="+uri.getPathSegments().get(1));
                break;

            default: throw new IllegalArgumentException("Unknown uri"+uri);
        }

        if(sortOrder==null||sortOrder=="")
        {
            sortOrder = NAME;
        }
        Cursor c = qb.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri))
        {
            case STUDENTS:
                count = db.delete(TABLE,selection,selectionArgs);
                break;
            case STUDENT_ID:
                db.delete(TABLE,ID+"="+uri.getPathSegments().get(1)+(!TextUtils.isEmpty(selection)? " AND ("+selection+")":""),selectionArgs);
                break;
            default:throw new IllegalArgumentException("Unknown uri"+uri);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            /** get all students records
             *
             */
            case STUDENTS   :
                return "vnd.android.cursor.dir/vnd.ca.mobile.lab7.students";
            case STUDENT_ID:
                return "vnd.android.cursor.item/vnd.ca.mobile.lab7.students";
            default: throw new IllegalArgumentException("Unknown uri"+uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri))
        {
            case STUDENTS:
                count = db.update(TABLE,values,selection,selectionArgs);
                break;
            case STUDENT_ID:
                db.update(TABLE,values,ID+"="+uri.getPathSegments().get(1)+(!TextUtils.isEmpty(selection)? " AND ("+selection+")":""),selectionArgs);
                break;
            default:throw new IllegalArgumentException("Unknown uri"+uri);
        }
        return count;
    }
}
