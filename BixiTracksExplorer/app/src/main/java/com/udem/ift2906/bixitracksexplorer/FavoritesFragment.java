package com.udem.ift2906.bixitracksexplorer;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.udem.ift2906.bixitracksexplorer.DBHelper.DBHelper;

public class FavoritesFragment extends Fragment  {
    private Context mContext;
    private OnFragmentInteractionListener mListener;
    private View mFavoritesView;
    private TextView mNoFavoritesView;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private LocationManager mLocationManager;
    private LatLng mCurrentUserLatLng;
    private FavoritesListViewAdapter mFavoritesStationListViewAdapter;
    private boolean hasFavorite;
    private ListView mFavoritesList;
    private StationsNetwork mStationsNetworkFavorites;

    public interface OnFragmentInteractionListener {
        public void onFavoritesFragmentInteraction();
    }

    public static FavoritesFragment newInstance(int sectionNumber) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            ((MainActivity) getActivity()).onSectionHiddenChanged(
                    getArguments().getInt(ARG_SECTION_NUMBER));
            setUpUI();
        }
    }

    @Override
    public void onAttach(Activity activity) {   //Only happening on orientation change now
        super.onAttach(activity);
        mContext = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = layoutInflater.inflate(R.layout.fragment_favoris, container, false);
        mFavoritesView = inflatedView.findViewById(R.id.favoritesListView_Holder);
        mNoFavoritesView = (TextView) inflatedView.findViewById(R.id.noFavorite_holder);
        mFavoritesList = (ListView) inflatedView.findViewById(R.id.favorites_listView);
        setUpUI();
        return inflatedView;
    }

    public void setUpUI(){
        // TODO get favorites station list directly from DB
        StationsNetwork stationsNetwork = DBHelper.getStationsNetwork();
        mStationsNetworkFavorites = new StationsNetwork();
        for(StationItem stationItem: stationsNetwork.stations)
            if(stationItem.isFavorite())
                mStationsNetworkFavorites.stations.add(stationItem);
        mFavoritesStationListViewAdapter = new FavoritesListViewAdapter(mContext,mStationsNetworkFavorites,mCurrentUserLatLng);
        mFavoritesList.setAdapter(mFavoritesStationListViewAdapter);
        setCurrentLocation();
        hasFavorite = !mStationsNetworkFavorites.stations.isEmpty();
        // Show the correct view if user has favorites or not
        if (!hasFavorite) {
            mNoFavoritesView.setVisibility(View.VISIBLE);
            mFavoritesView.setVisibility(View.GONE);
        } else {
            mNoFavoritesView.setVisibility(View.GONE);
            mFavoritesView.setVisibility(View.VISIBLE);
        }
    }

    public void setCurrentLocation() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            mCurrentUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }
}



