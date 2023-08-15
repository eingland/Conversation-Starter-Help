package com.ericingland.conversationstarterhelp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.ShareActionProvider;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends ListFragment implements ActionMode.Callback {
    private ShareActionProvider mShareActionProvider;
    private boolean mActionModeStarted = false;
    private ActionMode mActionMode;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.fragment_favorites, container, false);

        setHasOptionsMenu(true);

        MainActivity main = (MainActivity) getActivity();
        main.mTitle = getResources().getString(R.string.favorites);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mListView = getListView();
        mRefreshList();
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Set up list view listener
        mListView.setOnItemClickListener(new OnItemClickListener() {
                                      public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                                          if (mActionModeStarted) {
                                              //Check for nothing checked
                                              if (mGetCheckedFavorites().size() == 0) {
                                                  mActionMode.finish();
                                              }
                                          } else {
                                              mActionModeStarted = true;
                                              AppCompatActivity activity=(AppCompatActivity)getActivity();
                                              activity.startSupportActionMode(FavoritesFragment.this);
                                          }
                                          mShareActionProvider.setShareIntent(mShare());
                                      }
                                  }
        );

        // Restart Contextual Action Bar if Orientation changed
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (settings.getBoolean("action_mode_started", false)) {
            mActionModeStarted = true;
            AppCompatActivity activity = (AppCompatActivity)getActivity();
            activity.startSupportActionMode(this);
            mShareActionProvider.setShareIntent(mShare());
        }

    }

    private void mRefreshList() {
        // Put all Favorite strings in ListView
        DatabaseHelper db = new DatabaseHelper(getActivity());
        String[] favorites = db.getAllFavoriteStrings();
        db.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.favorites_single_list_item, favorites);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        NavigationDrawerFragment navigation = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (!navigation.isDrawerOpen()) {
            getActivity().getMenuInflater().inflate(R.menu.fragment_favorites, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private List<Favorite> mGetCheckedFavorites() {
        DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());
        List<Favorite> favorites = db.getAllFavorites();
        SparseBooleanArray checked = mListView.getCheckedItemPositions();
        List<Favorite> checkedFavorites = new ArrayList<>();
        for (int i = 0; i < favorites.size(); i++) {
            if (checked.get(i)) {
                checkedFavorites.add(favorites.get(i));
            }
        }
        db.close();
        return checkedFavorites;
    }

    private void mDelete() {
        List<Favorite> favorites = mGetCheckedFavorites();
        DatabaseHelper db = new DatabaseHelper(getActivity());
        for (Favorite i : favorites) {
            Favorite favorite = db.getFavorite(i.getID());
            db.deleteFavorite(favorite);
        }
        db.close();
    }

    private Intent mShare() {
        List<Favorite> favorites = mGetCheckedFavorites();
        String shareBody = "";

        // Add all favorites to share message
        for (Favorite fav : favorites) {
            shareBody += fav.getString() + "\n";
        }

        // Create share intent
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        // Pass data to intent
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "What do you think of this Conversation Starter?");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        return sharingIntent;
    }

    @Override
    public void onPause() {
        super.onPause();

        //Persist action bar state on orientation change
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("action_mode_started", mActionModeStarted);

        // Commit the edits
        editor.apply();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.fragment_favorites_context, menu);
        mActionMode = mode;

        MenuItem actionItem = menu.findItem(R.id.menu_multishare);
        mShareActionProvider = new ShareActionProvider(getActivity());
        MenuItemCompat.setActionProvider(actionItem, mShareActionProvider);
        mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionModeStarted = false;
        mRefreshList();
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_multishare:
                mShare();
                return true;
            case R.id.menu_delete:
                mDelete();
                mode.finish();
                return true;
            default:
                mode.finish();
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        return false;
    }

}
