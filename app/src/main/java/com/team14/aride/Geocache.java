package com.team14.aride;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Geocache {
    private GeoApiContext mContext;
    Geocoder geocoder;
    public Geocache(Geocoder g){
        mContext = new GeoApiContext().setApiKey("AIzaSyAU-jSsThQo2f4Ne0ijd8qScR67JFaeHKY");
        geocoder = g;
    }
    public String snapToRoads(double latitude, double longitude) throws IOException {

        List<Address> addresses;
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
    public LatLng LookUpAdress(String Location) throws IOException{
        double latitude = 0;
        double longitude = 0;
        List<Address> addresses1;
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
}
