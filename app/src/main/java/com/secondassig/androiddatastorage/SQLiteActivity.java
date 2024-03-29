package com.secondassig.androiddatastorage;

import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class SQLiteActivity extends Activity {
	
	public int counter=0;
	private SimpleDateFormat s=new SimpleDateFormat("MM/dd/yyyy-hh:mm a");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sqlite);
		// Show the Up button in the action bar.
		setupActionBar();
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		counter=sharedPrefs.getInt("SQL_COUNTER", 0);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		counter=sharedPrefs.getInt("SQL_COUNTER", 0);
	}


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sqlite, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void saveMessage(View view)
	{
		counter+=1;
		EditText editText=(EditText)findViewById(R.id.msg);
		String message=editText.getText().toString();
		DataController dataController=new DataController(getBaseContext());
		dataController.open();
		dataController.insert(counter, message);
		dataController.close();

		Context context = getApplicationContext();
		CharSequence text=getString(R.string.save_success_msg);
		int duration=Toast.LENGTH_LONG;
		Toast.makeText(context, text, duration).show();

		try
		{
			SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor=sharedPreferences.edit();
			editor.putInt("SQL_COUNTER", counter);
			editor.commit();

			OutputStreamWriter out=new OutputStreamWriter(openFileOutput(SetPreferencesActivity.STORE_PREFERENCES,MODE_APPEND));
			out.write("\nSQLite "+counter+", "+s.format(new Date()));
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
		
	}
	
	public void cancelMessage(View view)
	{
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
	}

	public void onDisplay(View view)
	{
		DataController dataController=new DataController(getBaseContext());
		dataController.open();
		Cursor cursor= dataController.retrieve();

		EditText editText=(EditText)findViewById(R.id.msg);

		String strMsg = "";
		// looping through all rows and adding to list
		if (cursor.moveToNext()) {
			do {
				strMsg += cursor.getInt(0) + ": " + cursor.getString(1) + "\n";
			} while (cursor.moveToNext());
		}


		editText.setText(strMsg);
		dataController.close();
	}
}
