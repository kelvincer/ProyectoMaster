package com.upv.magicplace.detail.activity;

import android.util.Log;

import com.upv.magicplace.ConstantsHelper;
import com.upv.magicplace.detail.activity.api.DetailPlaceApiService;
import com.upv.magicplace.detail.activity.events.DetailActivityEvent;
import com.upv.magicplace.detail.entities.Result;
import com.upv.magicplace.lib.EventBus;
import com.upv.magicplace.response.DetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivityRepositoryImpl implements DetailActivityRepository {

    String TAG = DetailActivityRepositoryImpl.class.getSimpleName();
    EventBus eventBus;
    DetailPlaceApiService detailPlaceApiService;

    public DetailActivityRepositoryImpl(EventBus eventBus, DetailPlaceApiService detailPlaceApiService) {
        this.eventBus = eventBus;
        this.detailPlaceApiService = detailPlaceApiService;
    }

    @Override
    public void fetchDetail(String placeId) {

        Call<DetailResponse> call = detailPlaceApiService.getDetail(placeId, ConstantsHelper.GOOGLE_PLACE_API_KEY);
        call.enqueue(new Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, Response<DetailResponse> response) {
                if (response.isSuccessful()) {
                    DetailResponse detailResponse = response.body();
                    if (detailResponse.getStatus().equals("OK")) {
                        post(DetailActivityEvent.GET_DETAIL, detailResponse.getResult());
                    } else {
                        post(DetailActivityEvent.GET_DETAIL_ERROR, detailResponse.getStatus());
                    }
                }
                Log.d(TAG, response.raw().toString());
            }

            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                post(DetailActivityEvent.GET_DETAIL_ERROR, t.getLocalizedMessage());
            }
        });
    }

    private void post(int type, String message) {
        DetailActivityEvent event = new DetailActivityEvent();
        event.setType(type);
        event.setMessage(message);
        eventBus.post(event);
    }

    private void post(int type, Result data) {
        DetailActivityEvent event = new DetailActivityEvent();
        event.setData(data);
        event.setType(type);
        eventBus.post(event);
    }
}
