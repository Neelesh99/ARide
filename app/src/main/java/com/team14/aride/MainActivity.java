package com.team14.aride;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    public Button SearchDevices;
    public Button FindRoute;
    public Button Transmit_To_Device;
    private final static int REQUEST_CODE_1 = 1;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothSocket mmSocket;
    private BluetoothComm Comm= new BluetoothComm();
    private BluetoothComm.ConnectedThread Con;
    GoogleDirectionsClass Dir;
    private LatLng origin;
    private LatLng destination;
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
        SearchDevices.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent in = new Intent(MainActivity.this, ListDevices.class);
                startActivityForResult(in,REQUEST_CODE_1);
            }
        });
        FindRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Dir.requestDirection(origin,destination);
                    }
                }).start();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
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
}
