package com.fantasik.tscuser.tscuser.Util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by a on 17-Apr-17.
 */

public class Utils {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String Base_URL = "http://10.0.2.2:8076/Service1.svc";


    public static final VehicleTypeDetails VehType_Mini =new VehicleTypeDetails("Mini", 1, 2);
    public static final VehicleTypeDetails VehType_Micro =new VehicleTypeDetails("Mini", 2, 4);
    public static final VehicleTypeDetails VehType_Sedan =new VehicleTypeDetails("Sedan", 3, 6);

    public static String GetVehicleTypeName(String id)
    {
       if(id.equals("1"))
    {
        return VehType_Mini.vehtypename;
    }
        if(id.equals("2"))
        {
            return VehType_Micro.vehtypename;
        }
        if(id.equals("3"))
        {
            return VehType_Sedan.vehtypename;
        }
        return "";
    }
    // public static final String Base_URL = "http://chanducs27.cloudapp.net/Service1.svc";
    /* public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }*/
    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

}

