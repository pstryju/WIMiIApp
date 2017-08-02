package pl.pcz.wimii.wimiiapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

import pl.pcz.wimii.wimiiapp.R;

public class Utils {
    public enum FetchRssResponse { UPDATETIME, OFFLINE, REFRESHERROR }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showUpdateSnackbar(Context c, View v, FetchRssResponse option) {
        Snackbar snackbar;
        switch (option) {
            case UPDATETIME:
                SharedPreferences sharedPref = c.getSharedPreferences("prefs", Context.MODE_PRIVATE);
                String updateTime = sharedPref.getString("updatetime", "");
                snackbar = Snackbar.make(v, c.getResources().getString(R.string.snackbar_actual_for) + updateTime, Snackbar.LENGTH_LONG);
                break;
            case OFFLINE:
                snackbar = Snackbar.make(v, c.getResources().getString(R.string.snackbar_offline), Snackbar.LENGTH_LONG);
                break;
            case REFRESHERROR:
                snackbar = Snackbar.make(v, c.getResources().getString(R.string.snackbar_error_refresh), Snackbar.LENGTH_LONG);
                break;
            default:
                snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
        }
        snackbar.setAction(c.getResources().getString(R.string.snackbar_close), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.setActionTextColor(c.getResources().getColor(android.R.color.holo_red_light ));
        snackbar.setDuration(5000);
        snackbar.show();


    }

}
