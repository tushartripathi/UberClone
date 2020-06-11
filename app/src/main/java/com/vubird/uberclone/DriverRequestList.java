package com.vubird.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DriverRequestList extends AppCompatActivity {


    LocationManager locationManager;
    LocationListener locationListener;
    ListView listView;

    //inflate Lisview In UI
    ArrayAdapter adapters;
    //add values to ArrayAdapter using listview
    ArrayList<String> nearByDriveRequests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {///On Create is called in start of Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_list);



        listView = findViewById(R.id.requestlistview);
        nearByDriveRequests = new ArrayList<>();
        //AdapterView
        adapters = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nearByDriveRequests);
        listView.setAdapter(adapters);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String strarry = adapters.getItem(i).toString();
                String arr[] = strarry.split(" ");
                String UserId = arr[arr.length -1];
                Toast.makeText(DriverRequestList.this,UserId, Toast.LENGTH_LONG).show();


                ParseQuery<ParseObject> requestCarQuery = ParseQuery.getQuery("RequestCar");
                requestCarQuery.whereEqualTo("username" , UserId);
                requestCarQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e)
                    {
                            if (objects.size() > 0 && e == null)
                            {
                                Location driverCurretLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                for (ParseObject obj : objects)
                                {
                                    obj.put("status","accepted");
                                    obj.put("driverName", ParseUser.getCurrentUser().getUsername());
                                    obj.put("driverLat",driverCurretLocation.getLatitude());
                                    obj.put("driverLong",driverCurretLocation.getLongitude());

                                    obj.saveInBackground(new SaveCallback()
                                    {
                                            @Override
                                            public void done(ParseException e)
                                            {
                                                if(e==null)
                                                {
                                                    Toast.makeText(DriverRequestList.this,"Done ", Toast.LENGTH_SHORT ).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(DriverRequestList.this,e.toString(), Toast.LENGTH_LONG ).show();
                                                }
                                            }
                                    });
                                }
                            }
                            else
                            {
                                Toast.makeText(DriverRequestList.this   , "No Result", Toast.LENGTH_LONG).show();
                            }

                    }
                });




            }
        });
    }

    // Inflate Menu in the activity directy
    // menu layout in res >> menu falder
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //called when menu is clicked
    //swtich case to check which menu item is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.logOutUser:
                ParseUser.logOut();
                finish();
                Intent i = new Intent(this, login.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // button click to do main operation in the app
    // finding location of driver and displying listview with closest passenfer requests
    public void refreshList(View view)
    {
        // LocationManager starts the service
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        // Listner helps in getting your coordinats
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                updateRequestListView(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        // chekc for persmissoin
        // for sdk below 23 no need for user permission
        if(Build.VERSION.SDK_INT < 23)
        {
            // call for current locaiton
            // using GPS
            // loop of 0 sec

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            
        }

        // // chekc for persmissoin
        // for sdk 23 and above
        // user permision is needed
        else {

            if (ContextCompat.checkSelfPermission(DriverRequestList.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(DriverRequestList.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                }
            else
                {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location currentPassengerLoction = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(currentPassengerLoction != null) {
                    updateRequestListView(currentPassengerLoction);
                }
            }
        }
    }


    // check the reply of user from userPermission box
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1000 && grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(DriverRequestList.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(currentDriverLocation != null)
                {
                     updateRequestListView(currentDriverLocation);
                }
            }
        }
    }

    private void updateRequestListView(Location pLocation)
    {


        if(pLocation!=null)
        {

                //give driver current location
            final ParseGeoPoint driverCurrentLocation = new ParseGeoPoint(pLocation.getLatitude(),pLocation.getLongitude());
            // create query from All the requests
            ParseQuery<ParseObject> requestCarQuery = ParseQuery.getQuery("RequestCar");
            //condition the query with "near to driver location"
            requestCarQuery.whereNear("passengerLocation" , driverCurrentLocation);
            requestCarQuery.whereNotEqualTo("status","accepted");
            //run the query
            requestCarQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e)
                {
                    // when query is done
                    if(nearByDriveRequests.size() != objects.size())
                    {
                        if (objects.size() > 0 && e == null)
                        {
                            nearByDriveRequests.clear();
                            //run loop on all the returned reuslts
                            for (ParseObject nearRequest : objects)
                            {
                                //get data from object with column name Passenger Location - contatin the location of passenger request location
                                //Convert it into ParseGeoPoint as the data passengerLocation is a lat and long data
                                //get distance between two geo points from "Driver Current Location" - " Distant in mils"
                                Double milesDistanceToPassenger = driverCurrentLocation.distanceInMilesTo((ParseGeoPoint) nearRequest.get("passengerLocation"));
                                //round of the value for better use
                                float roundedValue = Math.round(milesDistanceToPassenger * 10) / 10;
                                //add values in the adapter
                                nearByDriveRequests.add("There are " + roundedValue + " miles to " + nearRequest.get("username"));
                            }
                            //refresh the adapter
                            adapters.notifyDataSetChanged();
                        }
                        else
                            {
                                Toast.makeText(DriverRequestList.this   , "No Result", Toast.LENGTH_LONG).show();
                            }
                        adapters.notifyDataSetChanged();
                    }
                }
            });
        }

    }
}


