package com.hai811i.tp3;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InscriptionApp.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_NOM = "nom";
    private static final String COLUMN_PRENOM = "prenom";
    private static final String COLUMN_DATE_NAISSANCE = "date_naissance";
    private static final String COLUMN_TELEPHONE = "telephone";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_SPORT = "sport";
    private static final String COLUMN_MUSIQUE = "musique";
    private static final String COLUMN_LECTURE = "lecture";
    private static final String TABLE_EVENTS = "events";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_8H_10H = "h8h_10h";
    public static final String COLUMN_10H_12H = "h10h_12h";
    public static final String COLUMN_14H_16H = "h14h_16h";
    public static final String COLUMN_16H_18H = "h16h_18h";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating tables...");

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LOGIN + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_NOM + " TEXT,"
                + COLUMN_PRENOM + " TEXT,"
                + COLUMN_DATE_NAISSANCE + " TEXT,"
                + COLUMN_TELEPHONE + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_SPORT + " INTEGER,"
                + COLUMN_MUSIQUE + " INTEGER,"
                + COLUMN_LECTURE + " INTEGER"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        Log.d("DatabaseHelper", "Users table created.");

        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_8H_10H + " TEXT,"
                + COLUMN_10H_12H + " TEXT,"
                + COLUMN_14H_16H + " TEXT,"
                + COLUMN_16H_18H + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_EVENTS_TABLE);

        Log.d("DatabaseHelper", "Events table created.");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(String login, String password, String nom, String prenom, String dateNaissance, String telephone, String email, boolean sport, boolean musique, boolean lecture) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NOM, nom);
        values.put(COLUMN_PRENOM, prenom);
        values.put(COLUMN_DATE_NAISSANCE, dateNaissance);
        values.put(COLUMN_TELEPHONE, telephone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_SPORT, sport ? 1 : 0);
        values.put(COLUMN_MUSIQUE, musique ? 1 : 0);
        values.put(COLUMN_LECTURE, lecture ? 1 : 0);
        db.insert(TABLE_USERS, null, values);
        db.close();
    }


    public boolean checkUser(String login, String password) {
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + COLUMN_LOGIN + " = ? AND "
                + COLUMN_PASSWORD + " = ?";


        String[] selectionArgs = {login, password};


        Cursor cursor = db.rawQuery(query, selectionArgs);


        boolean userExists = cursor.getCount() > 0;


        cursor.close();
        db.close();

        return userExists;
    }


    public boolean checkLoginExists(String login) {
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + COLUMN_LOGIN + " = ?";


        String[] selectionArgs = {login};


        Cursor cursor = db.rawQuery(query, selectionArgs);


        boolean loginExists = cursor.getCount() > 0;


        cursor.close();
        db.close();

        return loginExists;
    }

    public User getUser(String login) {
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_LOGIN + " = ?";
        String[] selectionArgs = {login};


        Cursor cursor = db.rawQuery(query, selectionArgs);


        if (cursor.moveToFirst()) {

            @SuppressLint("Range") String nom = cursor.getString(cursor.getColumnIndex(COLUMN_NOM));
            @SuppressLint("Range") String prenom = cursor.getString(cursor.getColumnIndex(COLUMN_PRENOM));
            @SuppressLint("Range") String dateNaissance = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_NAISSANCE));
            @SuppressLint("Range") String telephone = cursor.getString(cursor.getColumnIndex(COLUMN_TELEPHONE));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
            @SuppressLint("Range") boolean sport = cursor.getInt(cursor.getColumnIndex(COLUMN_SPORT)) == 1;
            @SuppressLint("Range") boolean musique = cursor.getInt(cursor.getColumnIndex(COLUMN_MUSIQUE)) == 1;
            @SuppressLint("Range") boolean lecture = cursor.getInt(cursor.getColumnIndex(COLUMN_LECTURE)) == 1;


            User user = new User(login, nom, prenom, dateNaissance, telephone, email, sport, musique, lecture);


            cursor.close();
            db.close();

            return user;
        } else {

            cursor.close();
            db.close();

            return null;
        }
    }

    @SuppressLint("Range")
    private long getUserIdFromLogin(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_LOGIN + " = ?",
                new String[]{login},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            long userId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return userId;
        }
        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }

    public long addEvent(String h8h10h, String h10h12h, String h14h16h, String h16h18h, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_8H_10H, h8h10h);
        values.put(COLUMN_10H_12H, h10h12h);
        values.put(COLUMN_14H_16H, h14h16h);
        values.put(COLUMN_16H_18H, h16h18h);
        values.put(COLUMN_USER_ID, userId);

        long result = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return result;
    }

    public boolean updateEvent(long eventId, String h8h10h, String h10h12h, String h14h16h, String h16h18h) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_8H_10H, h8h10h);
        values.put(COLUMN_10H_12H, h10h12h);
        values.put(COLUMN_14H_16H, h14h16h);
        values.put(COLUMN_16H_18H, h16h18h);

        int rowsAffected = db.update(TABLE_EVENTS, values,
                COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(eventId)});
        db.close();
        return rowsAffected > 0;
    }

    public Cursor getEventsByUser(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EVENTS,
                null,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);
    }

    public boolean deleteEvent(long eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_EVENTS,
                COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(eventId)});
        db.close();
        return rowsAffected > 0;
    }

    public Cursor getEventsByUser(String login) {
        long userId = getUserIdFromLogin(login);
        if (userId == -1) return null;

        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EVENTS,
                null,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);
    }



    public long addOrUpdateEventForUser(String login, String date, ContentValues values) {
        long userId = getUserIdFromLogin(login);
        if (userId == -1) return -1;

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.query(TABLE_EVENTS,
                new String[]{COLUMN_EVENT_ID},
                COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{String.valueOf(userId), date},
                null, null, null);

        long result;
        if (cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();
            @SuppressLint("Range") long eventId = cursor.getLong(cursor.getColumnIndex(COLUMN_EVENT_ID));
            cursor.close();
            result = db.update(TABLE_EVENTS, values,
                    COLUMN_EVENT_ID + " = ?",
                    new String[]{String.valueOf(eventId)});
        } else {

            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_DATE, date);
            result = db.insert(TABLE_EVENTS, null, values);
        }

        db.close();
        return result;
    }

    public int updateEventForUserAndDate(String login, String date, ContentValues values) {
        long userId = getUserIdFromLogin(login);
        if (userId == -1) return 0;

        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.update(TABLE_EVENTS, values,
                COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{String.valueOf(userId), date});
        db.close();
        return rows;
    }

    public boolean deleteEventForUser(String login, String date, String timeSlot) {
        long userId = getUserIdFromLogin(login);
        if (userId == -1) return false;

        ContentValues values = new ContentValues();
        switch (timeSlot) {
            case "08h-10h":
                values.put(COLUMN_8H_10H, "");
                break;
            case "10h-12h":
                values.put(COLUMN_10H_12H, "");
                break;
            case "14h-16h":
                values.put(COLUMN_14H_16H, "");
                break;
            case "16h-18h":
                values.put(COLUMN_16H_18H, "");
                break;
            default:
                return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.update(TABLE_EVENTS, values,
                COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{String.valueOf(userId), date});
        db.close();
        return rows > 0;
    }

}
