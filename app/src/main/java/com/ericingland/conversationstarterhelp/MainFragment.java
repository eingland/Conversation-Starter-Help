package com.ericingland.conversationstarterhelp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams;

public class MainFragment extends Fragment {
    private static int mPosition = -1;
    private ShareActionProvider mShareActionProvider;
    private TextView mStarter;
    private ArrayList<String> mStarters;
    private Random mRandom = new Random();
    private int mCategory;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        NavigationDrawerFragment navigation = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (!navigation.isDrawerOpen()) {
            getActivity().getMenuInflater().inflate(R.menu.fragment_main, menu);
        }

        MenuItem actionItem = menu.findItem(R.id.menu_share);
        if (actionItem != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(actionItem);
            mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
            mCreateShareIntent();
        }
    }

    /**
     * Called when menu items are selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                if (mFavoriteExists()) {
                    mRemoveFavorite();
                } else {
                    mAddFavorite();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);


        // Update favorite Icon
        NavigationDrawerFragment navigation = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (!navigation.isDrawerOpen()) {
            MenuItem favoriteItem = menu.findItem(R.id.menu_favorite);
            if (mFavoriteExists()) {
                favoriteItem.setIcon(R.drawable.ic_action_favorite);
            } else {
                favoriteItem.setIcon(R.drawable.ic_action_favorite_outline);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout rootView = (RelativeLayout)inflater.inflate(R.layout.fragment_main, container, false);

        mStarter = (TextView) rootView.findViewById(R.id.starter);
        Button generate = (Button) rootView.findViewById(R.id.generate);
        generate.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mGenerate();
            }
        });

        Button previous = (Button) rootView.findViewById(R.id.previous);
        previous.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mPrevious();
            }
        });
        Button next = (Button) rootView.findViewById(R.id.next);
        next.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mNext();
            }
        });

        // set fragment state
        Bundle data = getArguments();
        mSetCategory(data.getInt("category"));

        //Restore Conversation Starter and category from previous instance
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        int tempPosition = settings.getInt("position", -1);
        if (tempPosition != -1) {
            mPosition = tempPosition;
            if (mPosition >= 0 && mPosition < mStarters.size()) {
                mUpdateText(mPosition);
            } else {
                mGenerate();
            }
        } else {
            mGenerate();
        }

        setHasOptionsMenu(true);

        MainActivity main = (MainActivity) getActivity();
        switch(mCategory) {
            case 0:
                main.mTitle = getResources().getString(R.string.app_name);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                main.mTitle = getResources().getStringArray(R.array.navigation_drawer_items)[mCategory];
                break;
            default:
                main.mTitle = getResources().getString(R.string.app_name);
                break;
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save current conversation starter and category to preferences storage
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("position", mPosition);

        // Commit the edits
        editor.apply();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /* Called on Activity resume */
    @Override
    public void onResume() {
        super.onResume();
        // Update share intent
        if (mShareActionProvider != null) //Prevent NullPointerException on screen rotation
        {
            mCreateShareIntent();
        }
    }

    /**
     * Generate a random conversation starter.
     */
    private void mGenerate() {
        // Randomly choose a conversation starter but not the same one
        int tempPosition = mPosition;
        while (mPosition == tempPosition) {
            mPosition = mRandom.nextInt(mStarters.size());
        }
        mUpdateText(mPosition);
    }

    /**
     * Update the text for the conversation starter
     */
    private void mUpdateText(int position) {
        mStarter.setText(mStarters.get(position));

        // Update share intent
        if (mShareActionProvider != null) //Prevent NullPointerException on screen rotation
        {
            mCreateShareIntent();
        }

        // Update icon on action bar
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.supportInvalidateOptionsMenu();
    }

    /**
     * View next Conversation Starter in order.
     */
    private void mNext() {
        if (mPosition + 1 > mStarters.size() - 1) {
            mPosition = 0;
        } else {
            mPosition += 1;
        }
        mUpdateText(mPosition);

    }

    /**
     * View previous Conversation Starter.
     */
    private void mPrevious() {
        if (mPosition - 1 < 0) {
            mPosition = mStarters.size() - 1;
        } else {
            mPosition -= 1;
        }
        mUpdateText(mPosition);
    }

    /**
     * Update the list of starters for the category
     */
    public void mSetCategory(int position) {
        Resources res = getResources();
        mStarters = new ArrayList<>();
        mCategory = position;

        switch (position) {
            case 0: // This contains the all category and thus all the conversation starters
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.thoughtfulStartersArray)));
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.educationStartersArray)));
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.factStartersArray)));
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.funnyStartersArray)));
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.generalStartersArray)));
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.partyStartersArray)));
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.relationshipStartersArray)));
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.travelStartersArray)));
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.workStartersArray)));
                break;
            case 2:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.thoughtfulStartersArray)));
                break;
            case 3:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.educationStartersArray)));
                break;
            case 4:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.factStartersArray)));
                break;
            case 5:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.funnyStartersArray)));
                break;
            case 6:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.generalStartersArray)));
                break;
            case 7:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.partyStartersArray)));
                break;
            case 8:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.relationshipStartersArray)));
                break;
            case 9:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.travelStartersArray)));
                break;
            case 10:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.workStartersArray)));
                break;
            default:
                mStarters.addAll(Arrays.asList(res.getStringArray(R.array.generalStartersArray)));
                break;
        }

        mGenerate();
    }

    /*
     * checks to see if the favorite is already in the database
     */
    private boolean mFavoriteExists() {
        String stringStarter = mStarter.getText().toString();
        DatabaseHelper db = new DatabaseHelper(getActivity());
        if (db.getFavoritesByString(stringStarter).size() > 0) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    /*
     * add favorite to database but only if it does not exist already
     */
    private void mAddFavorite() {
        String stringStarter = mStarter.getText().toString();
        DatabaseHelper db = new DatabaseHelper(getActivity());
        db.addFavorite(new Favorite("0", stringStarter));
        db.close();
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.supportInvalidateOptionsMenu();
    }

    /*
     * remove favorite from database
     */
    private void mRemoveFavorite() {
        String stringStarter = mStarter.getText().toString();
        DatabaseHelper db = new DatabaseHelper(getActivity());
        List<Favorite> favorite = db.getFavoritesByString(stringStarter);
        db.deleteFavorite(favorite.get(0));
        db.close();

        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.supportInvalidateOptionsMenu();
    }

    /**
     * Share Conversation Starter.
     */
    private void mCreateShareIntent() {
        // Create share intent
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        // Get the content
        String shareBody = mStarter.getText().toString();

        // Pass data to intent
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        mShareActionProvider.setShareIntent(sharingIntent);
    }

}
