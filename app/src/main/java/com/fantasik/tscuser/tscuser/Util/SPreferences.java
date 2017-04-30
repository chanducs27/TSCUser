package com.fantasik.tscuser.tscuser.Util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

/**
 * Created by a on 30-Apr-17.
 */

public class SPreferences {

    public static void ClearPreferences(Context cntt)
    {
        SharedPreferences.Editor editor = cntt.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
}
