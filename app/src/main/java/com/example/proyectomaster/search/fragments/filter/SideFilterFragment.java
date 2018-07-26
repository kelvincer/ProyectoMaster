package com.example.proyectomaster.search.fragments.filter;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.proyectomaster.CommonHelper;
import com.example.proyectomaster.R;
import com.example.proyectomaster.search.activity.ui.SearchActivity;
import com.example.proyectomaster.search.fragments.LocOptionalParamFragment;
import com.example.proyectomaster.search.fragments.TextOptionalParamFragment;
import com.example.proyectomaster.search.fragments.search_by_loc.NearbySearchFragment;
import com.example.proyectomaster.search.fragments.search_by_text.TextSearchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SideFilterFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.rdg_sortby)
    RadioGroup rdgSortby;

    public SideFilterFragment() {
        // Required empty public constructor
    }

    public static SideFilterFragment newInstance() {
        SideFilterFragment fragment = new SideFilterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_side_filter, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        addOptionaParametersFragment(TextOptionalParamFragment.newInstance());
        resetTextSearchParam();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupViews() {

        rdgSortby.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == -1)
                    return;

                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.rdb_text:
                            addSearchBarFragment(TextSearchFragment.newInstance());
                            resetTextSearchParam();
                            clearRecycleview();
                            CommonHelper.SEARCH_MODE = 1;
                            addOptionaParametersFragment(TextOptionalParamFragment.newInstance());
                            break;
                        case R.id.rdb_location:
                            addSearchBarFragment(NearbySearchFragment.newInstance());
                            clearRecycleview();
                            resetLocSearchParam();
                            CommonHelper.SEARCH_MODE = 2;
                            addOptionaParametersFragment(LocOptionalParamFragment.newInstance());
                            break;
                        default:
                            throw new RuntimeException("Incorrect radio button id");
                    }
                }
            }
        });

        rdgSortby.check(R.id.rdb_text);
    }

    private void addOptionaParametersFragment(Fragment fragment) {

        getFragmentManager().beginTransaction()
                .replace(R.id.optional_parameters_container, fragment)
                .commit();
    }

    private void addSearchBarFragment(Fragment f) {
        getFragmentManager().beginTransaction()
                .replace(R.id.search_head_container, f)
                .commit();
    }

    private void resetTextSearchParam() {

        CommonHelper.NEXT_PAGE_TOKEN = null;
        CommonHelper.QUERY = null;

        CommonHelper.location = null;
        CommonHelper.radius = null;
        CommonHelper.opennow = null;
        CommonHelper.minprice = null;
    }

    private void resetLocSearchParam() {

        CommonHelper.NEXT_PAGE_TOKEN = null;
        CommonHelper.location = null;
        CommonHelper.radius = "1000";
        CommonHelper.KEYWORD = null;
        CommonHelper.opennow = null;
        CommonHelper.RANKYBY = "prominence";

    }

    private void clearRecycleview() {
        ((SearchActivity) getActivity()).clearData();
    }
}
