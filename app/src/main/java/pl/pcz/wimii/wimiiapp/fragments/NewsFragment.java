package pl.pcz.wimii.wimiiapp.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.pcz.wimii.wimiiapp.Adapters.NewsAdapter;
import pl.pcz.wimii.wimiiapp.R;
import pl.pcz.wimii.wimiiapp.SettingsActivity;
import pl.pcz.wimii.wimiiapp.Utils.Utils;
import pl.pcz.wimii.wimiiapp.data.ReaderContract;
import pl.pcz.wimii.wimiiapp.service.NewsService;
import pl.pcz.wimii.wimiiapp.service.NewsServiceResultReceiver;

public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, NewsServiceResultReceiver.Receiver {

    private final static int RSS_LOADER = 0;

    RecyclerView newsList;
    NewsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout mySwipeRefreshLayout;
    public NewsServiceResultReceiver mReceiver;

    public NewsFragment() { }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(RSS_LOADER, null, this);
        mReceiver = new NewsServiceResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_news, container, false);

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_news_feed);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("Refresh news feed", "onRefresh called from SwipeRefreshLayout");
                        updateRSS();
                    }
                }
        );
        newsList = (RecyclerView) view.findViewById(R.id.rv_fn);
        layoutManager = new LinearLayoutManager(getActivity());
        newsList.setLayoutManager(layoutManager);
        adapter = new NewsAdapter();
        newsList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateRSS();
    }

    public void updateRSS() {
        Log.d("NewsFragment", "UpdatingRSS");
        mySwipeRefreshLayout.setRefreshing(true);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String channelPref = sharedPrefs.getString(SettingsActivity.PREF_CHANNEL, getResources().getString(R.string.channel_default));
        Log.v("channelPref", channelPref);

        Intent intent = new Intent(getActivity(), NewsService.class);
        intent.putExtra(NewsService.ADDRESS_QUERY_EXTRA, "http://wimii.pcz.czest.pl/pl/" + channelPref);
        intent.putExtra("receiverTag", mReceiver);
        getActivity().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        mySwipeRefreshLayout.setRefreshing(false);
        Utils.showUpdateSnackbar(getContext(), getView(), (Utils.FetchRssResponse) resultData.getSerializable("result"));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri rssUri = ReaderContract.NewsEntry.buildRssUri();
        String[] projection = {
                ReaderContract.NewsEntry._ID,
                ReaderContract.NewsEntry.COLUMN_NAME_GUID,
                ReaderContract.NewsEntry.COLUMN_NAME_TITLE,
                ReaderContract.NewsEntry.COLUMN_NAME_LINK,
                ReaderContract.NewsEntry.COLUMN_NAME_DATE,
                ReaderContract.NewsEntry.COLUMN_NAME_DESC
        };
        String sortOrder = ReaderContract.NewsEntry.COLUMN_NAME_DATE + " DESC";
        return new CursorLoader(getActivity(), rssUri, projection, null, null, sortOrder);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        Utils.showUpdateSnackbar(getActivity(), getView(), Utils.FetchRssResponse.UPDATETIME);
        mySwipeRefreshLayout.setRefreshing(false);
    }
}
