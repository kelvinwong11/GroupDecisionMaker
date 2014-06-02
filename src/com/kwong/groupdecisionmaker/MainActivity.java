package com.kwong.groupdecisionmaker;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {
	private final String CLIENT_ID = "0EE5VMUHE5EBBUVJAE5HZSZIBJUNELZV0CV34PXMIGQOFGIP";
	private final String CLIENT_SECRET = "DDOV4QH3AWHGKUUZJYC4FN5KEP1MMSOHOGYDY2CETOU2B2QM";
	private final String URL_BASE = "https://api.foursquare.com/v2";

	Location userLocation;
	LocationClient locationClient;
	LocationRequest locationRequest;

	ListView sectionsListView;
	TextView locationTextView;
	List<String> sectionNames = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locationClient = new LocationClient(this, this, this);
		locationClient.connect();
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
		locationRequest.setNumUpdates(1);
		locationRequest.setInterval(5000);

		sectionNames.add("food");
		sectionNames.add("drinks");
		sectionNames.add("coffee");
		sectionNames.add("shops");
		sectionNames.add("arts");
		sectionNames.add("outdoors");
		sectionNames.add("sights");
		sectionNames.add("trending");

		sectionsListView = (ListView) findViewById(R.id.sections_list_view);
		locationTextView = (TextView) findViewById(R.id.location_text_view);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, sectionNames);

		sectionsListView.setAdapter(arrayAdapter);
		sectionsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (userLocation == null) {
					Toast.makeText(getBaseContext(), "Waiting for location..",
							Toast.LENGTH_SHORT).show();
				} else {
					new getJSONData().execute(
							String.valueOf(userLocation.getLatitude()),
							String.valueOf(userLocation.getLongitude()),
							sectionNames.get(arg2));
				}
			}
		});

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		locationClient.disconnect();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	public class getJSONData extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("ll", params[0] + ","
					+ params[1]));
			qparams.add(new BasicNameValuePair("section", params[2]));
			qparams.add(new BasicNameValuePair("limit", "1"));
			qparams.add(new BasicNameValuePair("client_id", CLIENT_ID));
			qparams.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
			qparams.add(new BasicNameValuePair("v", getCurrentTime()));
			try {
				InputStream in = null;
				URI uri = URIUtils.createURI("https", "api.foursquare.com/v2",
						-1, "/venues/explore",
						URLEncodedUtils.format(qparams, "UTF-8"), null);
				String commaFixedString = uri.toString().replaceAll("%2C", ",");
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(commaFixedString);
				Log.d("a", commaFixedString);
				// HttpResponse httpResponse = httpClient.execute(httpGet);
				// HttpEntity httpEntity = httpResponse.getEntity();
				// String body = EntityUtils.toString(httpEntity, "UTF-8");
				// Log.d("asdf", body);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				// } catch (ClientProtocolException e) {
				// e.printStackTrace();
				// } catch (IOException e) {
				// e.printStackTrace();
			}

			return null;
		}

		private String getCurrentTime() {
			return new SimpleDateFormat("yyyyMMdd").format(new Date());
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		locationTextView.setText("Connection failed");
	}

	@Override
	public void onConnected(Bundle arg0) {
		locationTextView.setText("Connected, waiting for location");
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onDisconnected() {
		locationTextView.setText("Disconnected");
	}

	@Override
	public void onLocationChanged(Location location) {
		userLocation = location;
		locationClient.disconnect();
		locationTextView.setText("Location found: "
				+ userLocation.getLatitude() + " "
				+ userLocation.getLongitude());
	}
}
