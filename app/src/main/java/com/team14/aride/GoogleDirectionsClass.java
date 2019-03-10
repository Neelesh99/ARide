package com.team14.aride;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

public class GoogleDirectionsClass implements DirectionCallback {
    public boolean End_Of_Instructions;
    ArrayList<LatLng> directionPosList;
    double[] Latitudes;
    double[] Longitudes;
    public String[] stockArr;
    int count;
    Geocache geo;
    String curr;
    double lat;
    double longi;
    public String begin;
    public Vector<Double> Turn_Lat = new Vector<>();
    public Vector<Double> Turn_Long = new Vector<>();
    public Vector<Double> Distances = new Vector<>();
    public Vector<String> NextStreet = new Vector<>();
    public int[] Turn_Index = new int[500];
    public GoogleDirectionsClass(Geocache g, double CurrentLat, double CurrentLong){
        geo = g;
        lat = CurrentLat;
        longi = CurrentLong;
    }
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

    public String Forward() throws IOException {
        String CurrentStreet = "";
        if(count < Latitudes.length) {
            End_Of_Instructions = false;
            String currentString = geo.snapToRoads(Latitudes[count], Longitudes[count]);
            count = count + 1;
            //count = count + 3;
            String[] col = currentString.split(",",5);
            int s = col.length;
            String g = Integer.toString(s);
            //view4.setText(g);
            if(s>3){
                CurrentStreet = col[1];
                if(CurrentStreet.equals(" South Kensington") || CurrentStreet.equals(" Knightsbridge")){
                    CurrentStreet = col[0];
                }
            }
            else{
                CurrentStreet = col[0];
            }

        }
        else{
            End_Of_Instructions = true;
        }
        curr = CurrentStreet;
        return CurrentStreet;
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

    public int getCount() {
        return count;
    }
    public void Calculate_Turns() throws IOException {
        curr = Forward();
        int star = 0;
        String hold = curr;
        begin = curr;
        for(int i = 0; !End_Of_Instructions ;i++){
            curr = Forward();
            if(curr.equals(hold)){

            }
            else{
                Turn_Lat.addElement(Latitudes[i]);
                Turn_Long.addElement(Longitudes[i]);
                NextStreet.addElement(curr);
                hold = curr;
                Turn_Index[star] = i;
                star++;
            }
        }
        count = 0;

    }
    public void Calulate_Distance(){
        double R = 6371e3;
        double Kale = lat;
        double Kales = longi;
        for(int i = 0; i < Turn_Lat.size();i++){
            double phi1 = Kale;
            double lambda1 = Kales;
            double phi2 = Turn_Lat.elementAt(i);
            double lambda2 = Turn_Long.elementAt(i);
            Kale = phi1;
            Kales = phi2;
            double delta_phi = ToRadians(phi1) - ToRadians(phi2);
            double delta_lambda = ToRadians(lambda1) - ToRadians(lambda2);
            double a = (Math.sin(delta_phi/2)*Math.sin(delta_phi/2)) + Math.cos(ToRadians(phi1))*Math.cos(ToRadians(phi2))*(Math.sin(delta_lambda/2)*Math.sin(delta_lambda/2));
            double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
            double d = R*c;
            Distances.addElement(d);
        }
    }
    public double ToRadians(double in){
        return in*(Math.PI/180);
    }

}
