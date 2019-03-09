package com.team14.aride;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Set;
public class GoogleDirectionsClass implements DirectionCallback {
    public boolean End_Of_Instructions;
    ArrayList<LatLng> directionPosList;
    double[] Latitudes;
    double[] Longitudes;
    public String[] stockArr;
    public void requestDirection(LatLng origin, LatLng destination){
        String ServerKey = "AIzaSyAU-jSsThQo2f4Ne0ijd8qScR67JFaeHKY";
        GoogleDirection.withServerKey(ServerKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.BICYCLING)
                .execute(this);
    }
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody){
        if(direction.isOK()){
            End_Of_Instructions = false;
            Route route = direction.getRouteList().get(0);
            directionPosList = route.getLegList().get(0).getDirectionPoint();
            //String s = directionPosList.get(3).toString();
            //String b = directionPosList.get(6).toString();
            //String e = directionPosList.get(sif).toString();
            int a = directionPosList.size();
            String[] tt = new String[a];
            for(int i = 0 ;i < a; i++){
                String tmp = directionPosList.get(i).toString();
                tt[i] = tmp;
            }
            Latitudes = new double[a];
            Longitudes = new double[a];
            stockArr = tt;

            //view1.setText(b);
            //view2.setText(e);
            for(int i = 0; i < a;i++) {
                String[] data = tt[i].split(":", 2);
                String data2 = data[1].substring(2, (data[1].length() - 1));
                String[] latl = data2.split(",", 2);
                Latitudes[i] = Double.valueOf(latl[0]);
                Longitudes[i] = Double.valueOf(latl[1]);
            }
            //view2.setText(latl[0]);
            //view1.setText(latl[1]);
            //view2.setText(data2);
        }
        else{

        }
        //snapToRoads();
    }
    @Override
    public void onDirectionFailure(Throwable t) {

    }

    public boolean isEnd_Of_Instructions() {
        return End_Of_Instructions;
    }

    public ArrayList<LatLng> getDirectionPosList() {
        return directionPosList;
    }

    public double[] getLatitudes() {
        return Latitudes;
    }

    public double[] getLongitudes() {
        return Longitudes;
    }
}
