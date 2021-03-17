package com.example.project;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, NavigationView.OnNavigationItemSelectedListener {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    private GoogleMap mMap;
    private Marker marker1;
    private LinearLayout lin;
    Button direction;
    Polyline polyline;
    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 101;
    private FusedLocationProviderClient fusedLocationClient;
    Location userlocation;
    LatLng latLng1,latLng2;
    private GeoApiContext geoApiContext = null;
    private double lat;
    private double lon;
    MarkerOptions park_place_mark,userMarkerOption;
    DrawerLayout drawerLayout;
    ImageView imageView;
    SearchView searchView;
    TextView Name;
    TextView Address1;
    TextView Address2;
    TextView HourPrice;
    String longit;
    String latit;
    NavigationView navigationView;
    TextView namesider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        Name = (TextView) findViewById(R.id.Name);
        namesider= (TextView) findViewById(R.id.nameUser);
        Address1 = (TextView) findViewById(R.id.Address1);
        HourPrice= (TextView) findViewById(R.id.HourPrice);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        imageView = (ImageView)findViewById(R.id.menu);
        //firebaseAuth = FirebaseAuth.getInstance();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        searchView = findViewById(R.id.search_location);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        ReadfromDataBase();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Park mark
        latLng1 = new LatLng(30.627545, 32.278150);
        park_place_mark = new MarkerOptions().position(latLng1).icon(bMDVector(getApplicationContext(),R.drawable.parkp));
        marker1 = mMap.addMarker(park_place_mark);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
        //user Location mark
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getPosition().equals(latLng1)) {
                    lin = (LinearLayout) findViewById(R.id.lin);
                    lin.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
       /* mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                lin.setVisibility(View.GONE);
            }
        });

        */
        //draw polyline with direction
        direction = (Button)findViewById(R.id.direction);
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String url = getUrl(userMarkerOption.getPosition(),park_place_mark.getPosition(),"driving");
                //new FetchURL(MapsActivity.this).execute(url,"driving");
                if(polyline != null )
                {
                    polyline.remove();
                }
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(latLng1,latLng2)
                        .width(15)
                        .color(Color.BLACK)
                        .geodesic(true));
            }
        });

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        //origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        //destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //mode
        String mode = "mode=" + directionMode;
        //building parameters in web services
        String parameters = str_origin + "&" + str_dest + "&" +mode;
        //output format
        String output = "json";
        //building the url to web services
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key" +getString(R.string.google_maps_key);
        return url;

    }

    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        return false;

    }

    private void fetchLocation() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this).setTitle("Required Location Permission").setMessage("You Have To Give This Permission.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                        );
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Task<Location> task = fusedLocationClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Logic to handle location object
                        userlocation = location;
                        lat = userlocation.getLatitude();
                        lon = userlocation.getLongitude();
                        latLng2 = new LatLng(lat, lon);
                        userMarkerOption = new MarkerOptions().position(latLng2);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng2).zoom(18).bearing(location.getBearing()).tilt(70).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        mMap.animateCamera(cameraUpdate);
                    }
                }
            });
        }


    }


    @Override
    public void onTaskDone(Object... values) {
        if(polyline != null )
        {
            polyline.remove();
        }
        polyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


    private BitmapDescriptor bMDVector(Context context, int VectorId)
    {
        Drawable VectorDrawable = ContextCompat.getDrawable(context,VectorId);
        VectorDrawable.setBounds(0,0,VectorDrawable.getIntrinsicWidth(),VectorDrawable.getIntrinsicHeight());
        Bitmap bitmap =Bitmap.createBitmap(VectorDrawable.getIntrinsicWidth(),VectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        VectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void book(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    public void ReadfromDataBase()
    {
        //read from firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //this method is called once with the initial value and again
                //whenwver data at this location is updated
                String garage_name = dataSnapshot.child("ParkirApp").child("GARAGE").child("FCI_Garage").child("Name").getValue().toString();
                String garage_add = dataSnapshot.child("ParkirApp").child("GARAGE").child("FCI_Garage").child("Address1").getValue().toString();
                String garage_price= dataSnapshot.child("ParkirApp").child("GARAGE").child("FCI_Garage").child("HourPrice").getValue().toString();
                longit = dataSnapshot.child("ParkirApp").child("GARAGE").child("FCI_Garage").child("Address2").child("longitude").getValue().toString();
                latit = dataSnapshot.child("ParkirApp").child("GARAGE").child("FCI_Garage").child("Address2").child("longitude").getValue().toString();
                Name.setText(garage_name);
                Address1.setText(garage_add);
                HourPrice.setText(garage_price);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //failed to read value
                Log.w("read","Failed to read value.",databaseError.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
       if(drawerLayout.isDrawerOpen(GravityCompat.START))
       {
           drawerLayout.closeDrawer(GravityCompat.START);
       }
       else
       {
           super.onBackPressed();
       }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
       int id = menuItem.getItemId();
       if(id == R.id.feedItem) {
           Intent intent = new Intent(this, complain.class);
           startActivity(intent);
       }
       else if(id == R.id.barrier)
       {
           Intent intent = new Intent(this, Barrier.class);
           startActivity(intent);
       }
       else if (id == R.id.fqa)
       {
           Intent intent = new Intent(this,Expandable_View.class);
           startActivity(intent);
       }
       else if (id == R.id.setting)
       {

           openDialog();
       }
       else if (id == R.id.logout)
       {
           Intent intent = new Intent(this,signin.class);
           startActivity(intent);
           finish();
       }
       drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openDialog() {
        TroubleshootingDialog troubleshootingDialog = new TroubleshootingDialog();
        troubleshootingDialog.show(getSupportFragmentManager(),"");
    }

}


