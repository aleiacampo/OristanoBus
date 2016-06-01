package com.aleiacampo.oristanobus.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.aleiacampo.oristanobus.R;
import com.aleiacampo.oristanobus.util.ConnectionsHandler;
import com.aleiacampo.oristanobus.util.NavigationDrawerUtil;
import com.aleiacampo.oristanobus.util.SQLiteHelper;
import com.aleiacampo.oristanobus.util.Stop;
import com.aleiacampo.oristanobus.util.WebServerHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import butterknife.ButterKnife;


public class TimesFragment extends Fragment {

    private SQLiteHelper db;
    private AlertDialog.Builder alertDialog;

    private View view;
    public Bundle bundle;

    private String url;

    public String[] array_orari;

    TextView textView_stopSelected;
    TextView textView_nextTime;
    ListView listView_stopTimes;
    Button button_saveInFavourites;

    private int id_stop;
    private int id_line;
    private String name_stop;
    private String name_line;

    ProgressBar progressBar;


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        bundle = getArguments();
        view = inflater.inflate(R.layout.fragment_times, container, false);
        ButterKnife.inject(this, view);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        id_line = bundle.getInt("id_line");
        name_line = bundle.getString("name_line");
        id_stop = bundle.getInt("id_stop");
        name_stop = bundle.getString("name_stop");
        url = "http://www.aleiacampo.com/stops.php?stop="+Integer.toString(id_stop);

        textView_stopSelected = (TextView) view.findViewById(R.id.textView_stopSelected);
        textView_stopSelected.setText(name_stop+", Linea "+ Integer.toString(id_line));
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBarTimes);
        textView_nextTime = (TextView) view.findViewById(R.id.textView_nextTime);
        listView_stopTimes = (ListView) view.findViewById(R.id.listView_stopTimes);
        button_saveInFavourites = (Button) view.findViewById(R.id.button_saveInFavourites);

        db = new SQLiteHelper(getActivity());
        String stringStopTimes = db.getTimes(id_stop);
        if (stringStopTimes == null) {
            new ParseJSONTask().execute();
            setButton();
        }
        else{
            Log.e("arrayStopTimes from db", stringStopTimes);
            String[] arrayStopTimes = stringStopTimes.replace("[", "").replace("]", "").split(", ");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.text_view, arrayStopTimes);
            listView_stopTimes.setAdapter(adapter);
            textView_nextTime.setText(Html.fromHtml("Prossimo bus: <font color=#E25F5F>"
                    + arrayStopTimes[getClosestTime(arrayStopTimes)] + "</font>"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 15);
            listView_stopTimes.setLayoutParams(params);
            button_saveInFavourites.setVisibility(View.GONE);
        }

    }

    private void setButton() {
        button_saveInFavourites.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String stringStopTimes = Arrays.toString(array_orari);
                Log.e("stopTimes before db", stringStopTimes);
                db = new SQLiteHelper(getActivity());
                db.addStop(new Stop(id_line, id_stop, name_line, name_stop));
                db.addTimes(id_stop, stringStopTimes);

                AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                NavigationDrawerUtil.setFavourites(appCompatActivity);
                alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Fermata salvata");
                alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        button_saveInFavourites.setVisibility(View.GONE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );
                        params.setMargins(0, 0, 0, 14);
                        listView_stopTimes.setLayoutParams(params);
                    }
                });
                alertDialog.show();
            }
        });
    }

    private int getClosestTime(String[] times){    // restituisce l'indice dell'orario pi prossimo all'ora attuale

        int hourToConfront;
        int minuteToConfront;
        int i = 0;
        Date currentDate = new Date();
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat minutesFormat = new SimpleDateFormat("mm");
        int thisHour = Integer.parseInt(hourFormat.format(currentDate));
        int thisMinute = Integer.parseInt(minutesFormat.format(currentDate));
        do{
            hourToConfront = Integer.parseInt(times[i].substring(0,2));
            minuteToConfront = Integer.parseInt(times[i].substring(3,5));
            if(hourToConfront == thisHour && minuteToConfront >= thisMinute)
                return i;
            if(hourToConfront > thisHour)   // es: ora attuale = 15.50, ultimo orario utile per le 15 = 15.49
                return i;
            i++;
        }while(i<times.length);
        return 0;

    }

    class ParseJSONTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!ConnectionsHandler.isNetworkPresent(getActivity())) {
                this.cancel(true);
                ConnectionsHandler.connectionAlert(getActivity());
            }
            else
                progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            WebServerHandler webServerHandler = new WebServerHandler();
            String jsonStr = webServerHandler.getJSONData(url);

            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray timesJSON = jsonObject.getJSONArray("bus_times");
                array_orari = new String[timesJSON.length()];

                for (int i = 0; i < timesJSON.length(); i++) {
                    JSONObject json_time = timesJSON.getJSONObject(i);
                    array_orari[i] = json_time.getString("time");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.text_view, array_orari);
            listView_stopTimes.setAdapter(adapter);
            textView_nextTime.setText(Html.fromHtml("Prossimo bus: <font color=#E25F5F>"
                    + array_orari[getClosestTime(array_orari)] + "</font>"));
        }
    }

}
