package com.team14.aride;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    public Button SearchDevices;
    public Button FindRoute;
    public Button Transmit_To_Device;
    public Button Markers;
    private final static int REQUEST_CODE_1 = 1;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothSocket mmSocket;
    private BluetoothComm Comm= new BluetoothComm();
    private BluetoothComm.ConnectedThread Con;
    GoogleDirectionsClass Dir;
    private LatLng origin;
    private LatLng destination;
    private FusedLocationProviderClient client;
    private double CurrentLat;
    private double CurrentLong;
    private GeoApiContext mContext;
    public EditText Dest;
    Geocache geod;
    CalculateDirections Cal;
    FormatForCommunication Formatter = new FormatForCommunication();
    private static Handler updateUIHandler = null;
    private final static int MESSAGE_ACTIVATE_MARKERS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        origin = new LatLng(51.059324, 0.163243);
        destination = new LatLng(51.058562, 0.163186);
        SearchDevices = findViewById(R.id.Search_BT);
        FindRoute = findViewById(R.id.Go_Button);
        Transmit_To_Device = findViewById(R.id.Go_Button_2);
        Markers = findViewById(R.id.button2);
        Markers.setEnabled(false);
        Transmit_To_Device.setEnabled(false);
        mContext = new GeoApiContext().setApiKey("AIzaSyAU-jSsThQo2f4Ne0ijd8qScR67JFaeHKY");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        geod = new Geocache(geocoder);
        Dest = findViewById(R.id.editText);
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
        updateUIElements();
        Markers.setVisibility(View.GONE);
        SearchDevices.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent in = new Intent(MainActivity.this, ListDevices.class);
                startActivityForResult(in,REQUEST_CODE_1);
            }
        });
        FindRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindRoute.setEnabled(false);
                mMap.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getLoc();
                        LatLng dest = null;
                        Dir = new GoogleDirectionsClass(geod,CurrentLat,CurrentLong);
                        try {
                            dest = LookUpAdress(Dest.getText().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(dest == null) {
                        }
                        else {
                            destination = dest;
                        }
                        origin = new LatLng(CurrentLat,CurrentLong);
                        Dir.requestDirection(origin,destination);
                        Delay(5000);
                        try {
                            Dir.Calculate_Turns();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Dir.Calulate_Distance();
                        Cal = new CalculateDirections(Dir.getLatitudes(),Dir.getLongitudes(),Dir.Turn_Index);
                        Message message = new Message();
                        message.what = MESSAGE_ACTIVATE_MARKERS;
                        updateUIHandler.sendMessage(message);
                    }
                }).start();
                //Markers.setEnabled(true);
            }
        });
        Markers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoc();
                moveToCurrentLocation(new LatLng(CurrentLat,CurrentLong));
                mMap.addCircle(new CircleOptions().center(new LatLng(CurrentLat,CurrentLong)).radius(7).fillColor(Color.GREEN));
            }
        });
        Transmit_To_Device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        char[] For_Transmit = Formatter.GoToClock();
                        String trans=new String(For_Transmit);
                        byte[] transmit=trans.getBytes();
                        Con.write(transmit);
                        Delay(5000);
                        int Turn_No = 0;
                        For_Transmit = Formatter.RecieveBluetooth();
                        trans=new String(For_Transmit);
                        transmit=trans.getBytes();
                        //BluetoothComm.ConnectedThread.write write = new BluetoothComm.ConnectedThread.write(transmit);
                        Con.write(transmit);
                        try {
                            Dir.Forward();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Delay(5000);
                        String Street = Dir.begin;
                        String Distance = Dir.Distances.elementAt(Turn_No).toString();
                        Distance = Distance.substring(0,2);
                        String Next_Street = Dir.NextStreet.elementAt(Turn_No);
                        String Direction = Cal.CalcVectors(Turn_No);
                        For_Transmit = Formatter.StartNav(Street,Distance,Next_Street,Direction);
                        trans=new String(For_Transmit);
                        transmit=trans.getBytes();
                        Con.write(transmit);
                        Turn_No++;
                        Delay(5000);
                        boolean arrived  = false;
                        while(!arrived){
                            if(Calculate_Instant_Distance(Dir.Turn_Lat.elementAt(Turn_No),Dir.Turn_Long.elementAt(Turn_No)) < 10 && Turn_No != (Dir.NextStreet.size()-2)){
                                boolean turned = false;
                                For_Transmit = Formatter.SwitchToTurn(Next_Street,Direction);

                                trans=new String(For_Transmit);
                                transmit=trans.getBytes();
                                Con.write(transmit);
                                Delay(5000);
                                /** Transmit For_Transmit**/

                                while(!turned){
                                    double dist = Calculate_Instant_Distance(Dir.Turn_Lat.elementAt(Turn_No),Dir.Turn_Long.elementAt(Turn_No));
                                    if(dist > 10){
                                        turned = true;
                                    }
                                }
                                Turn_No++;
                                //CurrentStreet = Next_Street;
                                Street = Next_Street;
                                Distance = Dir.Distances.elementAt(Turn_No).toString();
                                Distance = Distance.substring(0,2);
                                Next_Street = Dir.NextStreet.elementAt(Turn_No);
                                Direction = Cal.CalcVectors(Turn_No);
                                For_Transmit = Formatter.ReturnToGeneral(Street,Distance,Next_Street,Direction);
                                trans=new String(For_Transmit);
                                transmit=trans.getBytes();
                                Con.write(transmit);
                                //Delay(50000);
                                /** Transmit For_Transmit**/
                            }
                            else if(Turn_No != (Dir.NextStreet.size()-2)){
                                //wait(1000);
                                Distance = Calculate_Instant_Distance(Dir.Turn_Lat.elementAt(Turn_No),Dir.Turn_Long.elementAt(Turn_No)).toString();
                                Distance = Distance.substring(0,2);
                                For_Transmit = Formatter.ReturnToGeneral(Street,Distance,Next_Street,Direction);
                                trans=new String(For_Transmit);
                                transmit=trans.getBytes();
                                Con.write(transmit);
                                Delay(5000);
                                /** Transmit For_Transmit**/
                            }
                            else{
                                For_Transmit = Formatter.ArrivalScreen(Street);
                                trans=new String(For_Transmit);
                                transmit=trans.getBytes();
                                Con.write(transmit);
                                Delay(5000);
                                /** Transmit For_Transmit**/
                                arrived = true;
                            }
                        }
                    }
                }).start();
            }
        });


    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        moveToCurrentLocation(new LatLng(51.499114, -0.174825));


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (REQUEST_CODE_1) : {
                if (resultCode == Activity.RESULT_OK) {
                    String MACAdress = data.getStringExtra(EXTRA_DEVICE_ADDRESS);

                    BluetoothAdapter a=BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device=a.getRemoteDevice(MACAdress);
                    ConnectThread connect= new ConnectThread(device);
                    mmSocket=connect.socket();
                    Con = Comm.new ConnectedThread(mmSocket);
                }
                //break;
            }
        }
    }
    @Override
    public void onResume() {
        //might need to write this function because it is interrupted and can't go back to OnCreate？
        super.onResume();
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }
    private void getLoc(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double temp = location.getLatitude();
                    CurrentLat = temp;
                    double temp2 = location.getLongitude();
                    CurrentLong = temp2;
                }
            }
        });
    }
    public Double Calculate_Instant_Distance(double lat, double longi){
        double R = 6371e3;
        getLoc();
        double phi1 = CurrentLat;
        double lambda1 = CurrentLong;
        double phi2 = lat;
        double lambda2 = longi;
        double delta_phi = ToRadians(phi1) - ToRadians(phi2);
        double delta_lambda = ToRadians(lambda1) - ToRadians(lambda2);
        double a = (Math.sin(delta_phi/2)*Math.sin(delta_phi/2)) + Math.cos(ToRadians(phi1))*Math.cos(ToRadians(phi2))*(Math.sin(delta_lambda/2)*Math.sin(delta_lambda/2));
        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return R*c;

    }
    public double ToRadians(double in){
        return in*(Math.PI/180);
    }
    public void Delay(int Mil){
        double[] New = new double[Mil];
        for(int i = 0; i < Mil;i++){
            New[i] = Mil*Mil;
            getLoc();
        }
    }
    public String snapToRoads(double latitude, double longitude) throws IOException {

        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        //String temp = addresses.get(0).;

        return address;
        //view5.setText(temp);
        //view4.setText(knownName);

    }
    public void updateUIElements(){
        if(updateUIHandler == null)
        {
            updateUIHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    // Means the message is sent from child thread.
                    if(msg.what == MESSAGE_ACTIVATE_MARKERS)
                    {
                        // Update ui in main thread.
                        Markers.setEnabled(true);
                        Transmit_To_Device.setEnabled(true);
                        FindRoute.setEnabled(true);
                        mMap.addMarker(new MarkerOptions().position(origin).title("Start"));
                        mMap.addMarker(new MarkerOptions().position(destination).title("Finish"));
                        mMap.addPolyline(new PolylineOptions().add(origin,new LatLng(Dir.Turn_Lat.elementAt(0),Dir.Turn_Long.elementAt(0))).width(5).color(Color.BLUE));
                        for(int i = 0; i < Dir.Turn_Lat.size()-1;i++){
                            mMap.addPolyline(new PolylineOptions().add(new LatLng(Dir.Turn_Lat.elementAt(i),Dir.Turn_Long.elementAt(i)),new LatLng(Dir.Turn_Lat.elementAt(i+1),Dir.Turn_Long.elementAt(i+1))).width(5).color(Color.BLUE));
                        }
                        moveToCurrentLocation(origin);
                        Markers.setVisibility(View.VISIBLE);
                    }
                }
            };
        }
    }
    public LatLng LookUpAdress(String Location) throws IOException{
        double latitude = 0;
        double longitude = 0;
        List<Address> addresses1;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        addresses1 = geocoder.getFromLocationName(Location,1);
        //double latitude = 0;
        //double longitude = 0;
        if(addresses1.size() > 0) {
            latitude= addresses1.get(0).getLatitude();
            longitude= addresses1.get(0).getLongitude();
        }
        LatLng destination = new LatLng(latitude,longitude);
        return destination;
    }
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }
}
