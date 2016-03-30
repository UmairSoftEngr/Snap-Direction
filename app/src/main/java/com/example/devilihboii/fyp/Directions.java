package com.example.devilihboii.fyp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

public class Directions extends FragmentActivity implements AdapterView.OnItemClickListener {

    //global variables as a tags
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBkM7wXDlrRZ-bHNC9V5Nb-p17sf-pp4IM";


    //latitude and longitude
    //latitude and longitude
    double lat, lng;
    //String of places from autocomplete
    String place;
    //Mapv2 variable
    GoogleMap map;
    // flag for gps status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;

    // varibale to store value of lat and lang in degress
    LatLng lastLatLng = null, lasti;
    //textview
    TextView t;
    //location  manager provides access to the system location services
    private LocationManager locMan;
    // MAP markers
    private Marker homeMarker, distMarker;

    Typeface custom_font;

  /*                        */

    //Autocompelet Edit Text funtion
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
//                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
//                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        custom_font = Typeface.createFromAsset(getAssets(), "KGColdCoffee.ttf");

        //XML bridging
        final AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.atv_places);
        Button b1 = (Button) findViewById(R.id.search_dir);
        Button b2 = (Button) findViewById(R.id.type_dir);
        t = (TextView) findViewById(R.id.tv);
        t.setTypeface(custom_font);
        b1.setTypeface(custom_font);
        autoCompView.setTypeface(custom_font);
        b2.setTypeface(custom_font);
        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //setting array adapter
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item_autocomlete));
        autoCompView.setOnItemClickListener(this);


        // Getting Map for the SupportMapFragment
        map = fm.getMap();


        // setting marker for home location oncreate
        try {
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location lastLoc = null;
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Toast.makeText(getApplicationContext(), "Check Network Connectivity", Toast.LENGTH_LONG).show();
            } else {

                if (isNetworkEnabled) {
                    lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (lastLoc == null) {
                        Log.d("error", "empty");

                    } else {
                        lat = lastLoc.getLatitude();
                        lng = lastLoc.getLongitude();
                        lastLatLng = new LatLng(lat, lng);
                    }

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 14), 5000, null);

                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (lastLoc == null) {

                    lastLoc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastLoc == null) {
                        Log.d("error", "empty");

                    } else {
                        lat = lastLoc.getLatitude();
                        lng = lastLoc.getLongitude();
                        lastLatLng = new LatLng(lat, lng);
                    }
                }
            }
            if (homeMarker != null) homeMarker.remove();
            homeMarker = map.addMarker(new MarkerOptions()
                    .position(lastLatLng)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                    .snippet("Your Current Location"));
            map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 5000, null);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(homeMarker.getPosition(), 15));

        } catch (Exception e) {

            Toast.makeText(getApplication(), "Faild Try again" + e, Toast.LENGTH_LONG).show();
            Log.d("error ", e.toString());


        }


        //changing map type
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


        if (map != null) {

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (distMarker != null) {
                        distMarker.remove();
                        map.clear();

                        try {

                            locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            // getting GPS status
                            isGPSEnabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            // getting network status
                            isNetworkEnabled = locMan.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                            Location lastLoc = null;
                            if (!isGPSEnabled && !isNetworkEnabled) {
                                // no network provider is enabled
                                Toast.makeText(getApplicationContext(), "Check Network Connectivity", Toast.LENGTH_LONG).show();

                            } else {

                                if (isNetworkEnabled) {
                                    lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    if (lastLoc == null) {
                                        Log.d("error", "empty");

                                    } else {
                                        lat = lastLoc.getLatitude();
                                        lng = lastLoc.getLongitude();
                                        lastLatLng = new LatLng(lat, lng);
                                    }


                                }
                            }
                            // if GPS Enabled get lat/long using GPS Services
                            if (isGPSEnabled) {
                                if (lastLoc == null) {
                                    lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    if (lastLoc == null) {
                                        Log.d("error", "empty");

                                    } else {
                                        lat = lastLoc.getLatitude();
                                        lng = lastLoc.getLongitude();
                                        lastLatLng = new LatLng(lat, lng);
                                    }
                                }
                            }
                            if (homeMarker != null) homeMarker.remove();
                            homeMarker = map.addMarker(new MarkerOptions()
                                    .position(lastLatLng)
                                    .title("You are here")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                                    .snippet("Your Current Location"));
                            map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 5000, null);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(homeMarker.getPosition(), 15));


                        } catch (Exception e) {

                            Toast.makeText(getApplication(), "error \n" + e, Toast.LENGTH_LONG).show();
                            Log.d("error ", e.toString());


                        }

                    }
                    try {
                        String address = autoCompView.getText().toString();
                        //passing data to get lat and lng from address
                        lasti = getLocationFromAddress(address);

                       // Toast.makeText(getApplicationContext(), "" + lasti, Toast.LENGTH_LONG).show();
                        distMarker = map.addMarker(new MarkerOptions()
                                .position(lasti)
                                .title("You want to go here")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dis3))
                                );

                        map.animateCamera(CameraUpdateFactory.newLatLng(lasti), 5000, null);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(distMarker.getPosition(), 14));
                        LatLng origin = lastLatLng;
                        LatLng dest = lasti;
                        YoYo.with(Techniques.BounceInDown).duration(3500).playOn(t);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);

                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(), "error In network " + e, Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

    }


    //get direction from start to end point using json

    //Geocoder funtion to convert address to lat and lng
    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);

            if (address == null || address.size() == 0) {
                return null;
            }
            for (int i = 0; i < address.size(); i++) {


                Address location = (Address) address.get(i);
                location.getLatitude();
                location.getLongitude();

                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

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

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

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
               // Toast.makeText(getApplicationContext(), "Background Task :" + e, Toast.LENGTH_LONG).show();

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
                Toast.makeText(getApplicationContext(), "new" + e, Toast.LENGTH_LONG).show();
            }
            return routes;

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

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(6);
                    lineOptions.color(Color.rgb(255,140,38));
                }
                t.setText("Distance:" + distance + "\nDuration:" + duration);
                // Drawing polyline in the Google Map for the i-th route

                map.addPolyline(lineOptions);
            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();

            }
        }
    }

    /*
    class for autocomplete edittext for places
     */
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