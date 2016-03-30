package com.example.devilihboii.fyp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

public class MapsActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

    //-> global variables <-\\\\\
    private LocationManager locMan;// location manager variable

    // Tages variables
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBkM7wXDlrZ-bHNC9V5Nb-p17sf-pp4IM";

    Typeface custom_font;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    double latitude, longitude;

    LatLng destination;
    GoogleMap map;
    LatLng lastLatLng = null, lasti; //     // varibales to store value of lat and lang in degress
    private Marker distMarker;
    TextView t;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        custom_font = Typeface.createFromAsset(getAssets(), "KGColdCoffee.ttf");

        final AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.atv_places2);
        Button b1 = (Button) findViewById(R.id.search_maps);
        Button b2 = (Button) findViewById(R.id.type_maps);
        t = (TextView) findViewById(R.id.tv2);
        b1.setTypeface(custom_font);
        b2.setTypeface(custom_font);
        t.setTypeface(custom_font);
        autoCompView.setTypeface(custom_font);

        try {


            autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item_autocomlete));
            autoCompView.setOnItemClickListener(this);

            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            map = fm.getMap();
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.setMyLocationEnabled(true);
            getLocation();

        } catch (Exception e) {

            Toast.makeText(getApplication(), "error \n" + e, Toast.LENGTH_LONG).show();
            Log.d("error ", e.toString());


        }

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int type = map.getMapType();
                switch (type) {
                    case MAP_TYPE_HYBRID:
                        map.setMapType(MAP_TYPE_TERRAIN);
                        break;
                    case MAP_TYPE_TERRAIN:
                        map.setMapType(MAP_TYPE_NORMAL);
                        break;
                    case MAP_TYPE_NORMAL:
                        map.setMapType(MAP_TYPE_SATELLITE);
                        break;
                    case MAP_TYPE_SATELLITE:
                        map.setMapType(MAP_TYPE_HYBRID);
                        break;
                    default:
                        map.setMapType(MAP_TYPE_NONE);
                        break;


                }
            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    String address = autoCompView.getText().toString();
                    lasti = getLocationFromAddress(address);
                  //  Toast.makeText(getApplicationContext(), "" + latitude + longitude, Toast.LENGTH_LONG).show();
                    if (distMarker != null) distMarker.remove();

                    distMarker = map.addMarker(new MarkerOptions()
                            .position(lasti)
                            .title("Hey ")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dis3))
                            .snippet("Tap to start a trip"));
                    distMarker.showInfoWindow();

                    map.animateCamera(CameraUpdateFactory.newLatLng(lasti), 5000, null);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(distMarker.getPosition(), 14));
                    LatLng origin = lastLatLng;
                    destination = lasti;
                    YoYo.with(Techniques.BounceInDown).duration(4500).playOn(t);


                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, destination);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error \n" + e, Toast.LENGTH_LONG).show();
                }
            }
        });


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               // Toast.makeText(getApplicationContext(), "" + destination.latitude + ", " + destination.longitude, Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(getBaseContext(),
                        MyScheduledReceiver.class);
                myIntent.setAction(MyScheduledReceiver.ACTION_ALARM_RECEIVER);
                boolean isWorking = (PendingIntent.getBroadcast(getBaseContext(),
                        1001, myIntent, PendingIntent.FLAG_NO_CREATE) != null);
                if (isWorking) {
                  //  Toast.makeText(getApplicationContext(), "alarm is " + (isWorking ? "" : "not") + " working...", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Stopping.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                } else {

                    Intent intent = new Intent(getApplicationContext(), Sms.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    intent.putExtra("lan", destination.latitude);
                    intent.putExtra("long", destination.longitude);
                    intent.putExtra("add", autoCompView.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.upi, R.anim.up);
                    finish();
                }
                return true;
            }
        });
    }


    public void getLocation() {
        try {

            locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location lastLoc = null;
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastLoc == null) {
                        Log.d("error", "empty");

                    } else {
                        latitude = lastLoc.getLatitude();
                        longitude = lastLoc.getLongitude();
                        lastLatLng = new LatLng(latitude, longitude);
                    }

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 14), 5000, null);

                }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (lastLoc == null) {

                    lastLoc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLoc == null) {
                        Log.d("error gps", "empty");

                    } else {
                        latitude = lastLoc.getLatitude();
                        longitude = lastLoc.getLongitude();
                        lastLatLng = new LatLng(latitude, longitude);
                    }

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 14), 5000, null);

                }

            }

        }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error \n" + e, Toast.LENGTH_LONG).show();
        }

    }


    //Lat and lang from address
    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);

            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    // getting directions from home to distination
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //  Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";
            MarkerOptions markerOptions = new MarkerOptions();
            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        if (j == 0) {// Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                            continue;
                        }
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);

                    }


                }
                t.setText("Distance:" + distance + "\nDuration:" + duration);
            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();

            }
        }
    }


    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("LongLogTag")
    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}