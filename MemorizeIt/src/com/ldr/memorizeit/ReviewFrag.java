package com.ldr.memorizeit;

import com.ldr.memorizeit.data.TextsAdapter;
//import com.ldr.memorizeit.dummy.DummyContent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewFrag extends Fragment {

	private TextsAdapter _db;
	public static final String ARG_ITEM_ID = "item_id";

	//DummyContent.DummyItem mItem;

	public ReviewFrag() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Long id = prefs.getLong("zid", 1);
		
		_db = new TextsAdapter(getActivity());
		_db.open();
		Cursor cursor = _db.getAllTexts(id);

		View rootView = inflater.inflate(R.layout.review_frag, container, false);

		TextView text_detail = (TextView) rootView.findViewById(R.id.text_detail);
		text_detail.setText(cursor.getString(1));
		
		_db.close();
		
		return rootView;
	}
	
	
	
}
