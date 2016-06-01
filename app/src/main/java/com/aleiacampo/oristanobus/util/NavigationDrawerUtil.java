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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aleiacampo.oristanobus.R;
import com.aleiacampo.oristanobus.fragment.TimesFragment;


import java.util.ArrayList;

/**
 * This class is a helper for to
 * create a instance of navigationDrawer
 *
 * @author Pedro Paulo Amorim
 *
 */
public class NavigationDrawerUtil {


  /**
   *
   * @param appCompatActivity provide the instance of activity
   * @param drawerLayout provide the instance of drawerLayout
   * that's inflated at the activity
   * @param drawerCallback provide the callback of drawerLayout actions
   * @return a new instance of ActionBarDrawerToggle based on the params
   */
  public static ActionBarDrawerToggle configNavigationDrawer(
      final AppCompatActivity appCompatActivity,
      final DrawerLayout drawerLayout,
      final DrawerCallback drawerCallback) {

    if (appCompatActivity == null || drawerLayout == null) {
      return null;
    }

  //  drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
        appCompatActivity, drawerLayout, R.string.app_name, R.string.app_name) {


      @Override public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        if (drawerCallback != null) {
          drawerCallback.onDrawerOpened();
        }
      }

      @Override public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        if (drawerCallback != null) {
          drawerCallback.onDrawerClosed();
        }
      }

      @Override public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        if (drawerCallback != null) {
          drawerCallback.onDrawerSlide(drawerView, slideOffset);
        }
      }
    };
    drawerLayout.setDrawerListener(actionBarDrawerToggle);
    return actionBarDrawerToggle;
  }

  public interface DrawerCallback {
    void onDrawerOpened();
    void onDrawerClosed();
    void onDrawerSlide(View drawerView, float slideOffset);
  }


  public static void setFavourites(final AppCompatActivity appCompatActivity) {

      final Bundle bundle = new Bundle();
      final  ArrayList<Stop> stopsList;

      SQLiteHelper db = new SQLiteHelper(appCompatActivity);
      stopsList = db.getAllStops();
      ArrayList<String> stopsNameList = new ArrayList<>();

      for (Stop stop : stopsList) {
            stopsNameList.add(stop.nameStop);
        }

      if(stopsList.isEmpty())
          stopsNameList.add("Nessuna fermata salvata");

      ListView favourites = (ListView) appCompatActivity.findViewById(R.id.favourite_list);
      ArrayAdapter<String> adapter = new ArrayAdapter<>(appCompatActivity.getApplicationContext(), R.layout.text_view, stopsNameList);
      favourites.setAdapter(adapter);

      favourites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            bundle.putInt("id_stop", stopsList.get(position).idStop);
            bundle.putInt("id_line", stopsList.get(position).idLine);
            bundle.putString("name_stop", stopsList.get(position).nameStop);
            bundle.putString("name_line", stopsList.get(position).nameLine);

            DrawerLayout drawer = (DrawerLayout) appCompatActivity.findViewById(R.id.drawer_left);
            drawer.closeDrawer(Gravity.LEFT);

            FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
            TimesFragment timesFragment = new TimesFragment();
            timesFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.home_frag, timesFragment, "Times");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
      }
    });

    favourites.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> arg0, View view, final int pos, long id) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(appCompatActivity);
            alertDialog.setMessage("Rimuovere la fermata dai preferiti?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SQLiteHelper db2 = new SQLiteHelper(appCompatActivity);
                    db2.deleteStop(stopsList.get(pos).id);    // l'id della fermata caricato dal db
                    NavigationDrawerUtil.setFavourites(appCompatActivity);
                    dialog.cancel();
                }
                });
            alertDialog.setNegativeButton("Nope !", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
            return true;
      }
    });

    favourites.setLongClickable(true);
  }

}
