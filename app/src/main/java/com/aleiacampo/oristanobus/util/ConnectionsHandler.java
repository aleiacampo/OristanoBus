package com.aleiacampo.oristanobus.util;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.net.InetAddress;

/**
 * Created by Ale on 05/11/2015.
 */
public final class ConnectionsHandler {

    public static boolean isNetworkPresent(final Activity activity){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null)
            return true;
        else
            return false;
    }

    public static void connectionAlert(final Activity activity){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            alertDialog.setTitle("Oristano Bus");
            alertDialog.setMessage("Connessione internet non presente");
            alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
    }
}
