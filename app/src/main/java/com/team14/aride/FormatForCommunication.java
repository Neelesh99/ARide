package com.team14.aride;

import java.util.Calendar;
import java.util.Date;
//package com.team14.directionstest2;

public class FormatForCommunication {

    FormatForCommunication(){

    }

    public char[] DebugMode(){
        String temp = "cccEXECcccsssDBUGsss";
        return temp.toCharArray();
    }
    public char[] ResetSwitch(){
        String temp = "cccEXECcccsssRSETsss";
        return temp.toCharArray();
    }
    public char[] LowBattery(){
        String temp = "cccEXECcccsssBATRsss";
        return temp.toCharArray();
    }
    public char[] StoreNumber(String number){
        String temp = "cccEXECcccsssSTORsssddd1/" + number + "///";
        return temp.toCharArray();
    }
    public char[] GoToClock(){
        Date currentTime = Calendar.getInstance().getTime();
        String temp = "cccINSTcccsssCLKSssseeeNORMeeeN/A";
        return temp.toCharArray();
    }
    public char[] RecieveBluetooth(){
        String temp = "cccINSTcccsssBTRCssseeeNORMeee";
        return temp.toCharArray();
    }
    public char[] StartNav(String StreetName, String Distance, String NextStreet, String Direction){
        String temp = "cccINSTcccsssSTNVssseeeNORMeeeddd4/" + StreetName + "/" + Distance + "/" + Direction + "/" + NextStreet + "///";
        return temp.toCharArray();
    }
    public char[] SwitchToTurn(String StreetName, String Direction){
        String temp = "cccINSTcccsssTURNssseeeNORMeeeddd2/" + Direction + "/" + StreetName + "///";
        return temp.toCharArray();
    }
    public char[] ReturnToGeneral(String Streetname, String Distance, String NextStreet, String Direction){
        String temp = "cccINSTcccsssREGEssseeeNORMeeeddd4/" + Streetname + "/" + Distance + "/" + Direction + "/" + NextStreet + "///";
        return temp.toCharArray();
    }
    public char[] ArrivalScreen(String Streetname){
        String temp = "cccINSTcccsssFINIssseeeNORMeeeddd1/" + Streetname + "///";
        return temp.toCharArray();
    }
    public char[] StopNavigation(){
        Date currentTime = Calendar.getInstance().getTime();
        String temp = "cccINSTcccsssCLKSssseeeNVSTeeeddd1/" + currentTime.toString() + "///";
        return temp.toCharArray();
    }
    public char[] Reroute(){
        String temp = "cccINSTcccsssBTRCssseeeREROeee";
        return temp.toCharArray();
    }
}
