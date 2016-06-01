
package com.aleiacampo.oristanobus.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import com.aleiacampo.cult.CultView;
import com.aleiacampo.oristanobus.fragment.SearchFragment;
import com.aleiacampo.oristanobus.util.NavigationDrawerUtil;
import com.aleiacampo.oristanobus.util.ViewUtil;
import com.aleiacampo.oristanobus.view.SearchView;
import com.aleiacampo.oristanobus.R;
import com.aleiacampo.oristanobus.fragment.AboutFragment;
import com.aleiacampo.oristanobus.fragment.HomeFragment;


/**
 * This is the base activity of the application, here
 * are injected the view and configured the drawerLayout
 *
 * @author Pedro Paulo Amorim
 *
 */
public class BaseActivity extends AppCompatActivity {

  private ActionBarDrawerToggle mDrawerToggle;
  private SearchView searchView;
  private FragmentPagerItemAdapter adapter;

  public Bundle bundle;


  @InjectView(R.id.cult_view) CultView cultView;
  @Optional @InjectView(R.id.drawer_left) DrawerLayout drawerLayout;
  @Optional @InjectView(R.id.smart_tab_layout) SmartTabLayout smartTabLayout;
  @Optional @InjectView(R.id.view_pager) ViewPager viewPager; // schede scorrevoli


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base);
    ButterKnife.inject(this);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    ViewUtil.configToolbar(this, cultView.getInnerToolbar());
    mDrawerToggle = NavigationDrawerUtil.configNavigationDrawer(this, drawerLayout, null);
    initializeViewPager();
    configCultView();
    startSearchFragment();

    NavigationDrawerUtil.setFavourites(this);
    ViewUtil.loadMostSearched(this);

  }

  @Override protected void onResume() {
    super.onResume();
    if (mDrawerToggle != null) {
      mDrawerToggle.syncState();
    }
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (mDrawerToggle != null) {
      mDrawerToggle.onConfigurationChanged(newConfig);
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.search, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home: // bottone in alto a sx che mostra i preferiti
        return mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item);
      case R.id.action_search:
        cultView.showSlide(); // bottone in alto a dx che gestisce la ricerca
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onBackPressed() {
    if (cultView.isSecondViewAdded()) {
      cultView.hideSlideTop();
      return;
    }
    super.onBackPressed();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override //messa io
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK ) {
      DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_left);
      if (drawer.isDrawerOpen(Gravity.LEFT)){
        drawer.closeDrawer(Gravity.LEFT);
        return Boolean.parseBoolean(null);
      }
    }
    if (keyCode == KeyEvent.KEYCODE_MENU ) {
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  private void initializeViewPager() {
    adapter = new FragmentPagerItemAdapter(
            getSupportFragmentManager(), FragmentPagerItems.with(this)
            .add(R.string.home, HomeFragment.class)
            .add(R.string.more, AboutFragment.class)
            .create());
    if (viewPager == null || smartTabLayout == null) {
      return;
    }
    viewPager.setAdapter(adapter);
    smartTabLayout.setViewPager(viewPager);
  }

  private void configCultView() {
    searchView = new SearchView();
    cultView.setOutToolbarLayout(searchView.getView(
            LayoutInflater.from(this).inflate(R.layout.layout_search, null), searchViewCallback));
    cultView.setOutContentLayout(R.layout.layout_most_searched);
  }

  private SearchView.SearchViewCallback searchViewCallback =
          new SearchView.SearchViewCallback() {
            @Override
            public void onCancelClick() {
              hideKeyboard();
              onBackPressed();
            }
          };

  private void hideKeyboard() {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            if (getCurrentFocus() != null) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    });
  }

  private void startSearchFragment(){
    bundle = new Bundle();
    final EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
    searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          hideKeyboard();
          bundle.putString("wordToSearch", searchEditText.getText().toString());
          FragmentManager fragmentManager = getSupportFragmentManager();
          FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
          SearchFragment searchFragment = new SearchFragment();
          searchFragment.setArguments(bundle);
          fragmentTransaction.replace(R.id.most_searched, searchFragment);
          fragmentTransaction.commit();
          return true;
        }
        return false;
      }
    });
  }
}
