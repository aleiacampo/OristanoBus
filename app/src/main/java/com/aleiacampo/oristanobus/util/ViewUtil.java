/*
* Copyright (C) 2015 Pedro Paulo de Amorim
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.aleiacampo.oristanobus.util;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aleiacampo.oristanobus.R;

import com.aleiacampo.oristanobus.fragment.TimesFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is a helper to configure
 * some view on your application
 *
 * @author Pedro Paulo Amorim
 *
 */
public class ViewUtil {

  /**
   *
   * @param appCompatActivity provide the instance of activity
   * @param toolbar provide the isntance of the base toolbar of application (can be CultView)
   */
    public static void configToolbar(AppCompatActivity appCompatActivity, Toolbar toolbar) {
        if (toolbar == null || appCompatActivity == null) {
          throw new IllegalArgumentException("toolbar or appCompatActivity is null");
        }
        appCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar == null) {
          return;
        }
        actionBar.setTitle(appCompatActivity.getResources().getString(R.string.search_default));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }



    public static void loadMostSearched(final AppCompatActivity appCompatActivity){

        final ArrayList<Stop> stopsList = new ArrayList<>();
        final ArrayList<String> stopsNameList = new ArrayList<>();

      new AsyncTask<Void, Void, Void>() {

        @Override
        protected void onPreExecute() {
          super.onPreExecute();
          if (!ConnectionsHandler.isNetworkPresent(appCompatActivity)) {
            this.cancel(true);
            stopsNameList.add("Connessione dati non presente");
            ListView listView_searched = (ListView) appCompatActivity.findViewById(R.id.textView_mostSerched);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(appCompatActivity.getApplicationContext(), R.layout.text_view, stopsNameList);
            listView_searched.setAdapter(adapter);
          }
        }

        @Override
        protected Void doInBackground(Void... params) {

          Stop stop;
          String url = "http://www.aleiacampo.com/stops.php?clicked=10";
          WebServerHandler webServerHandler = new WebServerHandler();
          String jsonStr = webServerHandler.getJSONData(url);
          try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray stopsJSON = jsonObject.getJSONArray("bus_stops");
            for (int i = 0; i < stopsJSON.length(); i++) {
              JSONObject bus_stop = stopsJSON.getJSONObject(i);
              stop = new Stop(bus_stop.getInt("id_line"), bus_stop.getInt("id_stop"),bus_stop.getString("name_line"),bus_stop.getString("name_stop"));
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

          for(Stop stop : stopsList){
            stopsNameList.add("Linea "+stop.idLine+" - "+stop.nameStop);
          }

          ListView listView_searched = (ListView) appCompatActivity.findViewById(R.id.textView_mostSerched);
          ArrayAdapter<String> adapter = new ArrayAdapter<>(appCompatActivity.getApplicationContext(), R.layout.text_view, stopsNameList);
          listView_searched.setAdapter(adapter);
          listView_searched.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              Bundle bundle = new Bundle();
              bundle.putInt("id_stop", stopsList.get(position).idStop);
              bundle.putInt("id_line", stopsList.get(position).idLine);
              bundle.putString("name_line", stopsList.get(position).nameLine);
              bundle.putString("name_stop", stopsList.get(position).nameStop);

              FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
              TimesFragment timesFragment = new TimesFragment();
              timesFragment.setArguments(bundle);
              appCompatActivity.onBackPressed();
              FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
              fragmentTransaction.replace(R.id.home_frag, timesFragment, "Times");
              fragmentTransaction.addToBackStack(null);
              fragmentTransaction.commit();

            }
          });
        }
      }.execute();
    }

}
