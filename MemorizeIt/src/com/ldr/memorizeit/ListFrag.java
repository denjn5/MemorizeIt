package com.ldr.memorizeit;

import com.ldr.memorizeit.data.TextsAdapter;
//import com.ldr.memorizeit.dummy.DummyContent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListFrag extends ListFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public interface Callbacks {

		public void onItemSelected(String id);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {

		public void onItemSelected(String id) {
		}
	};

	public ListFrag() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Attach the data to the layout
		//setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(), R.layout.list_item, R.id.txtReferenceItem, DummyContent.ITEMS));
		getList("");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor prefsEditor = prefs.edit();
		
		// TODO: this should have the Text Ref ID
		prefsEditor.putLong("zid", id);
		prefsEditor.commit();
		
		mCallbacks.onItemSelected("3");
	
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	private void getList(String where) {
		
		//registerForContextMenu(getListView());
        
		// Get List of Items
		TextsAdapter db = new TextsAdapter(getActivity());
		db.open();
		Cursor cursor = (Cursor)db.getAllTexts(0);
		
		String[] showColumns = new String[] {
			TextsAdapter.REFERENCE,
			TextsAdapter.TEXT};
		 
		// the XML defined views which the data will be bound to
		int[] to = new int[] {
			R.id.txtReferenceItem,
			R.id.txtTextItem};
		
		//SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, cursor, showColumns, to, SimpleCursorAdapter.FLAG_AUTO_REQUERY);
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, cursor, showColumns, to); 
		
		// set this adapter as your ListActivity's adapter
		setListAdapter(mAdapter);
		
		db.close();

	}
	
}
