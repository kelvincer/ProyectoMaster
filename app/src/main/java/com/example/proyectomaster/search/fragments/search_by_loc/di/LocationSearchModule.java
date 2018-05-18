package com.example.proyectomaster.search.fragments.search_by_loc.di;

import com.example.proyectomaster.lib.EventBus;
import com.example.proyectomaster.lib.di.LibsModule;
import com.example.proyectomaster.search.fragments.search_by_loc.LocationSearchInteractor;
import com.example.proyectomaster.search.fragments.search_by_loc.LocationSearchInteractorImpl;
import com.example.proyectomaster.search.fragments.search_by_loc.LocationSearchPresenter;
import com.example.proyectomaster.search.fragments.search_by_loc.LocationSearchPresenterImpl;
import com.example.proyectomaster.search.fragments.search_by_loc.LocationSearchRepository;
import com.example.proyectomaster.search.fragments.search_by_loc.LocationSearchRepositoryImpl;
import com.example.proyectomaster.search.fragments.search_by_loc.api.GooglePlaceLocationApiService;
import com.example.proyectomaster.search.fragments.search_by_loc.ui.LocationSearchView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {LibsModule.class, PlaceApiModule.class})
public class LocationSearchModule {

    LocationSearchView locationSearchView;

    public LocationSearchModule(LocationSearchView locationSearchView) {
        this.locationSearchView = locationSearchView;
    }

    @Provides
    @Singleton
    public LocationSearchView provideLocationSearchView() {
        return locationSearchView;
    }

    @Provides
    @Singleton
    public LocationSearchPresenter provideLocationSearchPresenterImpl(EventBus eventBus, LocationSearchView locationSearchView, LocationSearchInteractor locationSearchInteractor) {

        return new LocationSearchPresenterImpl(eventBus, locationSearchView, locationSearchInteractor);
    }

    @Provides
    @Singleton
    public LocationSearchInteractor provideLocationSearchInteractorImpl(LocationSearchRepository repository) {
        return new LocationSearchInteractorImpl(repository);
    }

    @Provides
    @Singleton
    public LocationSearchRepository LocationSearchRepositoryImpl(EventBus eventBus, GooglePlaceLocationApiService service) {
        return new LocationSearchRepositoryImpl(eventBus, service);
    }
}
