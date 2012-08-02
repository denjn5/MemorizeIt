package com.ldr.memorizeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class List extends FragmentActivity implements ListFrag.Callbacks {

	/**
	 * Is this a multi-pane activity?
	 */
	private boolean twoPane;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		// If the review_container exists, then we know that we're using the
		// twopain.xml activity.
		twoPane = (findViewById(R.id.review_container) != null) ? true : false;

	}

	/**
	 * Handle a text selection from the list of texts.
	 */
	public void onItemSelected(String id) {



		if (twoPane) {
			// We're in a multi-pane activity, direct the selection to the
			// embedded fragment
			ReviewFrag reviewFrag = new ReviewFrag();
			getSupportFragmentManager().beginTransaction().replace(R.id.review_container, reviewFrag).commit();

		} else {
			// Small screen: Open new activity
			Intent i = new Intent(this, Review.class);
			startActivity(i);
		}
	}
}
