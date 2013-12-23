package com.example.mysqltest;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ReadComments extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	

	// testing on Emulator:
	private static final String READ_COMMENTS_URL = "http://192.168.1.103/webservice/comments.php";

	
	// JSON IDS:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_TITLE = "title";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_MESSAGE = "message";
	
	// An array of all of our comments
	private JSONArray mComments = null;
	// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mCommentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// note that use read_comments.xml instead of our single_post.xml
		setContentView(R.layout.read_comments);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// loading the comments via AsyncTask
		new LoadComments().execute();
	}

	public void addComment(View v) {
		Intent i = new Intent(ReadComments.this, AddComment.class);
		startActivity(i);
	}

	/**
	 * Retrieves recent post data from the server.
	 */
	public void updateJSONdata() {

		
		mCommentList = new ArrayList<HashMap<String, String>>();

		
		JSONParser jParser = new JSONParser();
		
		// back a JSON object. Boo-yeah Jerome.
		JSONObject json = jParser.getJSONFromUrl(READ_COMMENTS_URL);

		
		// try to catch any exceptions:
		try {

			
			mComments = json.getJSONArray(TAG_POSTS);

			// looping through all posts according to the json object returned
			for (int i = 0; i < mComments.length(); i++) {
				JSONObject c = mComments.getJSONObject(i);

				// gets the content of each tag
				String title = c.getString(TAG_TITLE);
				String content = c.getString(TAG_MESSAGE);
				String username = c.getString(TAG_USERNAME);

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_TITLE, title);
				map.put(TAG_MESSAGE, content);
				map.put(TAG_USERNAME, username);

				// adding HashList to ArrayList
				mCommentList.add(map);

				// annndddd, our JSON data is up to date same with our array
				// list
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the parsed data into the listview.
	 */
	private void updateList() {
		
		ListAdapter adapter = new SimpleAdapter(this, mCommentList,
				R.layout.single_post, new String[] { TAG_TITLE, TAG_MESSAGE,
						TAG_USERNAME }, new int[] { R.id.title, R.id.message,
						R.id.username });

		
		setListAdapter(adapter);
		
		
		ListView lv = getListView();	
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				
			}
		});
	}

	public class LoadComments extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ReadComments.this);
			pDialog.setMessage("Loading Comments...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			updateJSONdata();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			updateList();
		}
	}
}
