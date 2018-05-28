package com.example.proyectomaster.search.activity.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.proyectomaster.CommonHelper;
import com.example.proyectomaster.R;
import com.example.proyectomaster.detail.activity.ui.DetailActivity;
import com.example.proyectomaster.search.activity.adapters.OnItemClickListener;
import com.example.proyectomaster.search.activity.di.DaggerSearchActivityComponent;
import com.example.proyectomaster.search.fragments.filter.SideFilterFragment;
import com.example.proyectomaster.lib.di.LibsModule;
import com.example.proyectomaster.location.ApiLocationManager;
import com.example.proyectomaster.location.LocationCallback;
import com.example.proyectomaster.search.entities.Result;
import com.example.proyectomaster.search.activity.SearchActivityPresenter;
import com.example.proyectomaster.search.activity.adapters.PlacesApiAdapter;
import com.example.proyectomaster.search.activity.di.SearchActivityModule;
import com.example.proyectomaster.search.fragments.search_by_loc.ui.LocalizationSearchFragment;
import com.example.proyectomaster.search.fragments.search_by_text.ui.SearchByTextFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements
        SearchActivityView, LocationCallback, OnItemClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName();
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.places_recycler)
    RecyclerView placesRecyclers;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @Inject
    SearchActivityPresenter presenter;
    @Inject
    PlacesApiAdapter adapter;

    private List<String> listaPermisos = new ArrayList<>();
    private int SOLICITUD_PERMISO = 100;
    ApiLocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_demo);
        ButterKnife.bind(this);

        setupInjection();
        addFragments();
        setupRecyclerView();
        presenter.onCreate();
        setupPermission();
        initVariables();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SOLICITUD_PERMISO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpLocationManager();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void setupPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (withPermissions()) {
                setUpLocationManager();
            } else {
                requestPermissions(SOLICITUD_PERMISO, this);
            }
        } else {
            setUpLocationManager();
        }
    }

    public boolean withPermissions() {

        listaPermisos.clear();

        int accessFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (accessFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listaPermisos.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        return listaPermisos.isEmpty();
    }

    private void setUpLocationManager() {

        locationManager = new ApiLocationManager(this);
        locationManager.setLocationCallback(this);
        locationManager.onInit();
    }

    public void requestPermissions(int requestCode, Activity activity) {
        ActivityCompat.requestPermissions(activity, listaPermisos.toArray((new String[listaPermisos.size()])), requestCode);
    }

    private void setupInjection() {

        DaggerSearchActivityComponent.builder()
                .libsModule(new LibsModule(this))
                .searchActivityModule(new SearchActivityModule(this, this))
                .build()
                .inject(this);
    }

    private void setupRecyclerView() {

        placesRecyclers.setLayoutManager(new LinearLayoutManager(this));
        placesRecyclers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        placesRecyclers.setAdapter(adapter);
        placesRecyclers.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {

                if (CommonHelper.NEXT_PAGE_TOKEN != null) {
                    searchAgain();
                } else {
                    Toast.makeText(SearchActivity.this, "TOKEN == NULL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchAgain() {

        Fragment fragment = getFragmentManager().findFragmentById(R.id.search_head_container);

        if (CommonHelper.SEARCH_MODE == 1)
            ((SearchByTextFragment) fragment).findPlaces(CommonHelper.QUERY);
        else if (CommonHelper.SEARCH_MODE == 2)
            ((LocalizationSearchFragment) fragment).findPlaces(CommonHelper.QUERY);
    }

    private void addFragments() {

        Fragment fragment = SearchByTextFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.search_head_container, fragment).commit();

        Fragment f = SideFilterFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.side_filter_container, f).commit();
    }

    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.END);
    }

    @Override
    public void setPlaces(List<Result> data) {
        adapter.updateData(data);
    }

    @Override
    public void showProgressBar() {
        progressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLocationChanged(Location location) {
        CommonHelper.MY_LOCATION = location;
        Log.d(TAG, location.toString());
    }

    public void clearData() {
        adapter.clearData();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void initVariables() {

        CommonHelper.MY_LOCATION = null;
        CommonHelper.NEXT_PAGE_TOKEN = null;
        CommonHelper.QUERY = null;
        CommonHelper.SOURCE = 1;
        CommonHelper.SEARCH_MODE = 1;
    }

    @Override
    public void onItemClick(String placeId) {

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(CommonHelper.PLACE_ID, placeId);
        startActivity(intent);
    }
}
