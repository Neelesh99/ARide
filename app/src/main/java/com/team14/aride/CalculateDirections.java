package com.team14.aride;

public class CalculateDirections {
    private double[] Lat;
    private double[] Long;
    private int[] is;

    CalculateDirections(double[] Latitudes, double[] Longitudes, int[] indexs){
        Lat = Latitudes;
        Long = Longitudes;
        is = indexs;
    }
    public String CalcVectors(int i){
            if(is[i] >= 2) {
                double p1x = Lat[is[i] - 2];
                double p1y = Long[is[i] - 2];
                double p2x = Lat[is[i] - 1];
                double p2y = Long[is[i] - 1];
                p1x = p2x - p1x;
                p1y = p2y - p1y;
                double p3x = Lat[is[i]];
                double p3y = Long[is[i]];
                double p4x = Lat[is[i] + 1];
                double p4y = Long[is[i] + 1];
                p2x = p4x-p3x;
                p2y = p4y-p3y;
                p2x = p2x - p1x;
                p2y = p2y - p1y;
                p1x = 0;
                p1y = 1;
                double dot = p1x*p2x - p1y*p2y;
                double mod1 = Math.pow((p1x*p1x +p1y*p1y),0.5);
                double mod2 = Math.pow((p2x*p2x +p2y*p2y),0.5);
                double angle = Math.toDegrees(Math.acos((dot/(mod1*mod2))));
                if(angle < 180){
                    return "Left";
                }
                else{
                    return "Right";
                }
            }
        return "Null";
    }
}
