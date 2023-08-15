package com.ericingland.conversationstarterhelp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "favoritesManager";

    // Favorites table name
    private static final String TABLE_FAVORITES = "favorites";

    // Favorites Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_POSITION = "position";
    private static final String KEY_STRING = "string";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_POSITION + " TEXT,"
                + KEY_STRING + " TEXT" + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new favorite
    void addFavorite(Favorite favorite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_POSITION, favorite.getPosition());
        values.put(KEY_STRING, favorite.getString());

        // Inserting Row
        db.insert(TABLE_FAVORITES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single favorite
    Favorite getFavorite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVORITES, new String[]{KEY_ID,
                        KEY_POSITION, KEY_STRING}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        // this will prevent CursorIndexOutofBoundsException
        if (cursor != null && cursor.moveToFirst()) {
            Favorite favorite = new Favorite(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2));
            cursor.close();
            // return favorite
            return favorite;
        }

        return null;
    }

    // Getting All Favorite
    public String[] getAllFavoriteStrings() {
        String[] favoriteList;
        String selectQuery = "SELECT string FROM " + TABLE_FAVORITES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayList<String> favorite = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                favorite.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        favoriteList = favorite.toArray(new String[favorite.size()]);

        // return favorite list
        return favoriteList;
    }

    // Getting All Contacts
    public List<Favorite> getAllFavorites() {
        List<Favorite> favoriteList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVORITES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Favorite favorite = new Favorite();
                favorite.setID(Integer.parseInt(cursor.getString(0)));
                favorite.setPosition(cursor.getString(1));
                favorite.setString(cursor.getString(2));
                // Adding contact to list
                favoriteList.add(favorite);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return favoriteList;
    }

    public List<Favorite> getFavoritesByString(String query) {
        List<Favorite> favoriteList = new ArrayList<>();
        // Select All Query
        query = DatabaseUtils.sqlEscapeString(query);
        String selectQuery = "SELECT  * FROM " + TABLE_FAVORITES + " WHERE string = "+ query;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Favorite favorite = new Favorite();
                favorite.setID(Integer.parseInt(cursor.getString(0)));
                favorite.setPosition(cursor.getString(1));
                favorite.setString(cursor.getString(2));
                // Adding contact to list
                favoriteList.add(favorite);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return favoriteList;
    }

    // Deleting single favorite
    public void deleteFavorite(Favorite favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, KEY_ID + " = ?",
                new String[]{String.valueOf(favorite.getID())});
        db.close();
    }

}