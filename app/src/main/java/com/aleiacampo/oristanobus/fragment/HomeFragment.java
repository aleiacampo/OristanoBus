package com.aleiacampo.oristanobus.fragment;

import java.util.ArrayList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aleiacampo.oristanobus.util.NoDefaultSpinner;
import com.aleiacampo.oristanobus.util.ConnectionsHandler;
import com.aleiacampo.oristanobus.util.Stop;
import com.aleiacampo.oristanobus.util.WebServerHandler;
import com.aleiacampo.oristanobus.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class HomeFragment extends Fragment {

    public Bundle bundle = new Bundle();
    private NoDefaultSpinner linesSpinner;
    private View view;
    private NoDefaultSpinner stopsSpinner;
    private String url;
    private Button goButton;
    private TextView textViewOristanoBus;
    private Typeface oasisFont;
    ProgressBar progressBar;
    LayoutInflater layoutInflater;

    private ArrayList<String> stopsNameList;
    private ArrayList<Stop> stopsList;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, view);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        bundle.putString("url", "http://www.aleiacampo.com/stops.php?stop=");

        textViewOristanoBus = (TextView) view.findViewById(R.id.textView_oristanoBus);
        oasisFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/oasis.ttf");
        textViewOristanoBus.setTypeface(oasisFont);

        linesSpinner = (NoDefaultSpinner) view.findViewById(R.id.lines_spinner);
        stopsSpinner = (NoDefaultSpinner) view.findViewById(R.id.stops_spinner);
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBarHome);
        goButton = (Button) view.findViewById(R.id.go_button);

        layoutInflater = LayoutInflater.from(getActivity().getApplicationContext());

        String[] arrayLines = getActivity().getResources().getStringArray(R.array.lines_array);

        ArrayAdapter<String>adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.text_view, arrayLines) {

            @Override
            public boolean isEnabled(int position) {
                if(position == 9 || position == 10 || position == 11)
                    return false;
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if(position == 0)
                    return new View(getContext());
                if(position == 9 || position == 10 || position == 11) {
                    TextView mTextView = (TextView) super.getDropDownView(position, convertView, parent);
                    mTextView.setTextColor(Color.GRAY);
                    return mTextView;
                }
                return super.getDropDownView(position, null, parent);
            }

            @Override
            public final View getView(int position, View convertView, ViewGroup parent) {
                if(position == 0) {
                    TextView mTextView = (TextView) super.getView(position, convertView, parent);
                    mTextView.setTextColor(Color.GRAY);
                    return mTextView;
                }
                return super.getView(position, convertView, parent);
            }
        };

        linesSpinner.setAdapter(adapter1);
        linesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int lineSelected, long id) {
                url = "http://www.aleiacampo.com/stops.php?line=" + Integer.toString(lineSelected);
                stopsList = new ArrayList<>();
                stopsNameList = new ArrayList<>();
                if (lineSelected != 0)
                    new ParseJSONTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bundle.putInt("id_stop", stopsList.get((int) stopsSpinner.getSelectedItemId()).idStop-1);
                bundle.putString("name_stop", stopsSpinner.getSelectedItem().toString());
                bundle.putInt("id_line", ((int) linesSpinner.getSelectedItemId()));
                bundle.putString("name_line", linesSpinner.getSelectedItem().toString());
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    TimesFragment timesFragment = new TimesFragment();
                    timesFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.home_frag, timesFragment, "Times");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
        goButton.setClickable(false);

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
            Stop stop;
            WebServerHandler webServerHandler = new WebServerHandler();
            String jsonStr = webServerHandler.getJSONData(url);
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray stopsJSON = jsonObject.getJSONArray("bus_stops");
                for (int i = 0; i < stopsJSON.length(); i++) {
                    JSONObject bus_stop = stopsJSON.getJSONObject(i);
                    stop = new Stop(bus_stop.getInt("id"), bus_stop.getString("stop"));
                    stopsList.add(stop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            stopsNameList.add("Seleziona una fermata..");
            for(Stop stop : stopsList){
                stopsNameList.add(stop.nameStop);
            }
            goButton.setClickable(false);
            progressBar.setVisibility(View.GONE);
            stopsSpinner.setVisibility(View.VISIBLE);

            ArrayAdapter<String>adapterLines = new ArrayAdapter<String>(getActivity(), R.layout.text_view, stopsNameList) {

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    if(position == 0)
                        return new View(getContext());
                    return super.getDropDownView(position, null, parent);
                }

                @Override
                public final View getView(int position, View convertView, ViewGroup parent) {
                    if(position == 0) {
                        TextView mTextView = (TextView) super.getView(position, convertView, parent);
                        mTextView.setTextColor(Color.GRAY);
                        return mTextView;
                    }
                    return super.getView(position, convertView, parent);
                }
            };

            stopsSpinner.setAdapter(adapterLines);

            stopsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    goButton.setClickable(true);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

}
