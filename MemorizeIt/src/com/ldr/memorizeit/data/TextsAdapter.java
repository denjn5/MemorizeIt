package com.ldr.memorizeit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TextsAdapter {

	public static final String DATABASE = "memorizeIt.db"; // Was "myDatabase"
	public static final int VERSION = 2;
	private SQLiteDatabase db; // Variable to hold the database instance
	private final Context context; // Context of the application using the database.
	private myDbHelper dbHelper; // Database open/upgrade helper

	// Tables
	public static final String TEXTS = "Texts"; // Was "mainTable"
	public static final String SCORES = "Scores"; // Was "mainTable"

	/**
	 * TEXTS Columns A list of Texts that the user wants to memorize UNIQUE
	 * PRIMARY KEY = _ID
	 **/
	public static final String _ID = "_id";
	public static final String REFERENCE = "reference_tx"; // The reference for the text to be memorized
	public static final String TEXT = "text_tx"; // The text to be memorized
	public static final String NOTE = "note_tx"; // Notes about the text, maybe a hint
	public static final String STARRED = "starred_in"; // Important (starred) indicator
	public static final String TAGS = "tags_tx"; // Important (starred) indicator
	public static final String ADD_DATE = "added_dt"; // Date added
	public static final String FIRST_DATE = "first_success_dt"; // First success date

	/**
	 * SCORES Columns The history of scores for each Text. The Set ID will only
	 * be recorded if the Text was scored as part of a Set. UNIQUE PRIMARY KEY =
	 * _ID + RECORDED_TIMESTAMP also _ID as "foreign" key also SET_ID as "foreign" key
	 **/
	public static final String TEXT_ID = "text_id"; // The _ID of the text that this score relates to
	public static final String QUIZ_DATE = "quiz_ts"; // Date added
	public static final String DURATION = "duration_ts"; // The length of time it took to memorize the text
	public static final String SUCCESS = "success_in"; // Index (key) column name for use in where clauses.

	// TEXTS create table SQL
	public static final String TEXTS_CREATE = "create table " + TEXTS + " (" + _ID + " integer primary key autoincrement, " + REFERENCE + " text not null, " + TEXT
			+ " text not null, " + NOTE + " text, " + TAGS + " text, " + STARRED + " integer not null, " + ADD_DATE + " integer not null, " + FIRST_DATE + " integer" + ");";

	// SCORES create table SQL
	public static final String SCORES_CREATE = "create table " + SCORES + " (" + _ID + " integer primary key autoincrement, " + TEXT_ID + " long not null, " + QUIZ_DATE
			+ " integer not null, " + DURATION + " integer not null, " + SUCCESS + " integer not null" + ");";

	public TextsAdapter(Context _context) {
		context = _context;
		Log.i("DB","TextAdapter Constructor");
		dbHelper = new myDbHelper(context, DATABASE, null, VERSION);
	}

	/**
	 * Opens the database for querying
	 * 
	 * @return MyDBAdapter
	 * @throws SQLException
	 */
	public TextsAdapter open() throws SQLException {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}

		return this;
	}

	public TextsAdapter openWrite() throws SQLException {

		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getWritableDatabase();
		}

		return this;
	}

	public void close() {
		db.close();
	}

	/*********************************************
	 *************** TEXTS QUERIES ***************
	 *********************************************/
	/**
	 * INSERT a new TEXT
	 * 
	 * @param ref
	 *            The text reference.
	 * @param text
	 *            The text itself.
	 * @param note
	 *            Optional notes about the text.
	 * @param tags
	 *            Tags about the text.
	 * @param starred
	 *            Is this starred by the user.
	 * @param addDate
	 *            Date added.
	 * @return the new _ID of the text.
	 */
	public long insertText(String ref, String text, String note, String tags, Integer starred, Long addDate) {

		ContentValues cv = new ContentValues();
		cv.put(REFERENCE, ref);
		cv.put(TEXT, text);
		cv.put(NOTE, note);
		cv.put(TAGS, tags);
		cv.put(STARRED, starred);
		cv.put(ADD_DATE, addDate);

		return db.insert(TEXTS, null, cv);
	}

	/**
	 * DELETE A TEXT: Delete 1 row based on a ref ID.
	 * 
	 * @param id
	 *            The id of the text to delete.
	 */
	public Integer deleteText(Long id) {
		// TODO: Throw error if db.update <> 1.
		Integer deleteCount = db.delete(TEXTS, _ID + "=" + id, null);
		return deleteCount;
	}

	/**
	 * GET ALL TEXTS
	 * 
	 * @param currentID
	 *            The ID that you'd like to end up near.
	 * @return The cursor with the texts.
	 */
	public Cursor getAllTexts(long currentID) {
		String[] columns = new String[] { _ID, REFERENCE, TEXT, NOTE, TAGS, STARRED, FIRST_DATE, ADD_DATE };
		Cursor c = db.query(TEXTS, columns, null, null, null, null, null);

		// If no records, insert one!
		if (c.getCount() == 0) {
			insertText("Welcome to Memorize!", "Swipe left or right to see other Memory Texts. Click '+' (at the bottom of the page) to enter a new memory text.", null, "instrux", 0, System.currentTimeMillis());
			insertText("Getting started...", "Click '!' to quiz yourself.  Delete a text from the Edit activity.", null, "instrux", 0, System.currentTimeMillis());
			insertText("Do good!", "Explore, enjoy, and remember.", null, "instrux", 0, System.currentTimeMillis());

			c = db.query(TEXTS, columns, null, null, null, null, null);
		}

		c.moveToFirst();

		for (int i = 0; i < c.getCount(); i++) {

			if (c.getInt(0) >= currentID || c.isLast()) {
				break;
			} // Exit if you've found the user's current row, or are at the end
				// of the line
			c.moveToNext();
		}

		return c;
	}

	// QUERY -- GENERIC
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {

		// String[] columns = new String[] {_ID, REFERENCE, TEXT, NOTE, TAGS, STARRED, FIRST_DATE};
		Cursor c = db.query(table, columns, selection, selectionArgs, null, null, orderBy);

		c.moveToFirst();

		return c;
	}

	/**
	 * UPDATE TEXTS: Update a field in the TEXTS table for an existing text.
	 * 
	 * @param refID
	 * @param ref
	 * @param text
	 * @param note
	 * @param tags
	 * @param starred
	 *            The value to update to (0 = not filled in; 1 = filled in).
	 */
	public void updateText(Long refID, String ref, String text, String note, String tags, Integer starred) {

		ContentValues cv = new ContentValues();
		cv.put(REFERENCE, ref);
		cv.put(TEXT, text);
		cv.put(NOTE, note);
		cv.put(TAGS, tags);
		cv.put(STARRED, starred);

		// TODO: Throw error if db.update <> 1.
		int updateCount = db.update(TEXTS, cv, _ID + " = " + refID, null);
	}

	/**
	 * TEXTS STARRED update: Updates the STARRED flag in the TEXTS table for an existing text.
	 * 
	 * @param refID
	 *            The reference ID of the verse to be updated.
	 * @param starred
	 *            The value to update to (0 = not filled in; 1 = filled in).
	 */
	public void updateTextStar(Integer refID, Integer starred) {

		ContentValues cv = new ContentValues();
		cv.put(STARRED, starred);

		// TODO: Throw error if db.update <> 1.
		int updateCount = db.update(TEXTS, cv, _ID + " = " + refID, null);

	}

	/**
	 * TEXTS FIRST DATE update: Updates the FIRST_DATE flag in the TEXTS table for an existing text.
	 * 
	 * @param refID 
	 *            The reference ID of the verse to be updated.
	 * @param first_date
	 *            The value to update to.
	 */
	public void updateTextFirstDate(Integer refID, Long first_date) {

		ContentValues cv = new ContentValues();
		cv.put(FIRST_DATE, first_date);

		// TODO: Throw error if db.update <> 1.
		int updateCount = db.update(TEXTS, cv, _ID + " = " + refID, null);

	}

	/**
	 * TEXTS TAGS update: Updates the TAGS in the TEXTS table for an existing
	 * text.
	 * 
	 * @param refID
	 *            The reference ID of the verse to be updated.
	 * @param tags
	 *            The value to update to.
	 */
	public void updateTextFirstDate(Integer refID, String tags) {

		ContentValues cv = new ContentValues();
		cv.put(TAGS, tags);

		// TODO: Throw error if db.update <> 1.
		int updateCount = db.update(TEXTS, cv, _ID + " = " + refID, null);

	}

	/*********************************************
	 ************** SCORES QUERIES ***************
	 *********************************************/
	/**
	 * INSERT SCORE: Insert a new score.
	 * 
	 * @param textID
	 * @param quizDate
	 * @param duration
	 * @param success
	 * @return
	 */
	public long insertScore(Long textID, Long quizDate, Long duration, Integer success) {
		ContentValues cv = new ContentValues();
		cv.put(TEXT_ID, textID);
		cv.put(QUIZ_DATE, quizDate);
		cv.put(DURATION, duration);
		cv.put(SUCCESS, success);

		return db.insert(SCORES, null, cv);
	}

	/*********************************************
	 *************** SQLITE OPENER ***************
	 *********************************************/
	static class myDbHelper extends SQLiteOpenHelper {

		public myDbHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs to create a new one.
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(TEXTS_CREATE);
			_db.execSQL(SCORES_CREATE);
			Log.i("DB","myDBHelper Constructor");
		}

		// Called when there is a database version mismatch meaning that the version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			// Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion + " to " + _newVersion + ", which will destroy all old data");

			// Upgrade the existing database to conform to the new version.
			// Multiple previous versions can be handled by comparing _oldVersion and _newVersion values.

			// The simplest case is to drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF EXISTS " + TEXTS);
			// Create a new one.
			onCreate(_db);
		}
	}

	/** Dummy object to allow class to compile */
	static class MyObject {
	}
}
