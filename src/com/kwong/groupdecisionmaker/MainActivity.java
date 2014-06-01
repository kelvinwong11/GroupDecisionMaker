package com.kwong.groupdecisionmaker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	LocationManager locationManager;
	LocationListener locationListener;
	private final String CLIENT_ID = "0EE5VMUHE5EBBUVJAE5HZSZIBJUNELZV0CV34PXMIGQOFGIP";
	private final String CLIENT_SECRET = "DDOV4QH3AWHGKUUZJYC4FN5KEP1MMSOHOGYDY2CETOU2B2QM";
	private final String URL_BASE = "https://api.foursquare.com/v2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}

			@Override
			public void onLocationChanged(Location location) {
				locationManager.removeUpdates(locationListener);
				new getJSONData().execute(
						String.valueOf(location.getLatitude()),
						String.valueOf(location.getLongitude()));
			}
		};
		String gpsProvider = LocationManager.GPS_PROVIDER;
		if (locationManager.isProviderEnabled(gpsProvider)) {
			locationManager.requestLocationUpdates(gpsProvider, 1000, 0,
					locationListener);
		}
		String networkProvider = LocationManager.NETWORK_PROVIDER;
		if (locationManager.isProviderEnabled(networkProvider)) {
			locationManager.requestLocationUpdates(networkProvider, 1000, 0,
					locationListener);
		}
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
			qparams.add(new BasicNameValuePair("client_id", CLIENT_ID));
			qparams.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
			qparams.add(new BasicNameValuePair("v", getCurrentTime()));
			try {
				InputStream in = null;
				URI uri = URIUtils.createURI("https", "api.foursquare.com/v2", -1,
						"/venues/explore",
						URLEncodedUtils.format(qparams, "UTF-8"), null);
				String commaFixedString = uri.toString().replaceAll("%2C", ",");
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(commaFixedString);
				Log.d("a", commaFixedString);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				 String body = EntityUtils.toString(httpEntity, "UTF-8");
				 Log.d("asdf", body);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		private String getCurrentTime() {
			return new SimpleDateFormat("yyyyMMdd").format(new Date());
		}
	}
}
