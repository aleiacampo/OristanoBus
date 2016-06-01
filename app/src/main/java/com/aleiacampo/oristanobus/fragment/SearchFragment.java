package com.aleiacampo.oristanobus.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aleiacampo.cult.CultView;
import com.aleiacampo.oristanobus.util.ConnectionsHandler;
import com.aleiacampo.oristanobus.util.Stop;
import com.aleiacampo.oristanobus.R;
import com.aleiacampo.oristanobus.util.WebServerHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Ale on 02/11/2015.
 */
public class SearchFragment extends Fragment {


    ProgressDialog dialog;

    private ArrayList<String> stopsNameList;
    private ArrayList<Stop> stopsList;
    ProgressBar progressBar;

    private String wordToSearch;
    private View view;

    TextView appCompatTextView_searched;

    private ListView listView_search;
    private Bundle bundle;

    @InjectView(R.id.cult_view) CultView cultView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ButterKnife.inject(getActivity());

        dialog = new ProgressDialog(getActivity());
        bundle = getArguments();
        wordToSearch = bundle.getString("wordToSearch");
        wordToSearch = wordToSearch.trim();
        appCompatTextView_searched = (TextView) view.findViewById(R.id.appCompatTextView_searched);
        appCompatTextView_searched.setText("Risultati per '"+wordToSearch+"'");
        progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBarSearch);

        try {
            wordToSearch = java.net.URLEncoder.encode(wordToSearch, "UTF-8");
            Log.e("conversione", "CONVERSIONE UTF-8 eseguita");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("conversione", "CONVERSIONE UTF-8 non eseguita");
        }
        stopsList = new ArrayList<>();
        stopsNameList = new ArrayList<>();
        new ParseJSONTask().execute();

    }

    private void setSearched() {

        for(Stop stop : stopsList){
            stopsNameList.add("Linea "+stop.idLine+" - "+stop.nameStop);
        }
        if(stopsList.isEmpty() && ConnectionsHandler.isNetworkPresent(getActivity()))
            stopsNameList.add("Nessun risultato trovato");

        listView_search = (ListView) getActivity().findViewById(R.id.search_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.text_view, stopsNameList);
        listView_search.setAdapter(adapter);
        listView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                bundle = new Bundle();
                bundle.putInt("id_stop", stopsList.get(position).idStop);
                bundle.putInt("id_line", stopsList.get(position).idLine);
                bundle.putString("name_stop", stopsList.get(position).nameStop);
                bundle.putString("name_line", stopsList.get(position).nameLine);

                getActivity().onBackPressed();

                new AddToMostSearched(stopsList.get(position).idStop).execute();

                FragmentManager fragmentManager = getFragmentManager();
                TimesFragment timesFragment = new TimesFragment();
                timesFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.home_frag, timesFragment, "Times");    // il layout che andrà a sostituire
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }

    class ParseJSONTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!ConnectionsHandler.isNetworkPresent(getActivity())) {
                this.cancel(true);
                stopsNameList.add("Connessione dati non presente");
                ListView listView_searched = (ListView) getActivity().findViewById(R.id.search_list);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.text_view, stopsNameList);
                listView_searched.setAdapter(adapter);
            }
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Stop stop;
            String url = "http://www.aleiacampo.com/stops.php?search=" + wordToSearch;
            WebServerHandler webServerHandler = new WebServerHandler();
            String jsonStr = webServerHandler.getJSONData(url);
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray stopsJSON = jsonObject.getJSONArray("bus_stops");
                for (int i = 0; i < stopsJSON.length(); i++) {
                    JSONObject bus_stop = stopsJSON.getJSONObject(i);
                    stop = new Stop(bus_stop.getInt("id_line"), bus_stop.getInt("id_stop"), bus_stop.getString("name_line"), bus_stop.getString("name_stop"));
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
            progressBar.setVisibility(View.GONE);
            setSearched();
        }
    }

    class AddToMostSearched extends AsyncTask<Void, Void, Void> {

        String url;

        AddToMostSearched(int stop_id){
            url = "http://www.aleiacampo.com/stops.php?AddToMostSearched="+stop_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            try {
                HttpResponse response = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}