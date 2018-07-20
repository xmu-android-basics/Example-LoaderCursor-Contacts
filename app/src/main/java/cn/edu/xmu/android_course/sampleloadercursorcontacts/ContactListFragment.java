package cn.edu.xmu.android_course.sampleloadercursorcontacts;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

public class ContactListFragment extends ListFragment implements
        SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data.
    SimpleCursorAdapter mAdapter;

    // If non-null, this is the current filter the user has provided.
    String mSearchString;

    // These are the Contacts rows that we will retrieve.
    static final String[] CONTACTS_PROJECTION = new String[] {
            Contacts._ID,
            Contacts.DISPLAY_NAME,
            Contacts.CONTACT_STATUS,
            Contacts.CONTACT_PRESENCE,
            Contacts.PHOTO_ID,
            Contacts.LOOKUP_KEY,
    };

    // This is the select criteria
    static final String CONTACTS_SELECTION = "((" +
            Contacts.DISPLAY_NAME + " NOTNULL) AND (" +
            Contacts.DISPLAY_NAME + " != '' ) AND (" +
            Contacts.HAS_PHONE_NUMBER + "=1))";

    static final String CONTACTS_ORDER = Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("No phone numbers");

        // We have a menu item to show in action bar.
        setHasOptionsMenu(true);

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = { Contacts.DISPLAY_NAME, Contacts.CONTACT_PRESENCE };
        int[] toViews = { android.R.id.text1, android.R.id.text2 }; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        SearchView searchView = new SearchView(getActivity());
        searchView.setOnQueryTextListener(this);
        item.setActionView(searchView);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchString = !TextUtils.isEmpty(newText) ? newText : null;

        getLoaderManager().restartLoader(0, null, this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri baseUri;
        if (mSearchString != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(mSearchString));
        } else {
            baseUri = Contacts.CONTENT_URI;
        }

        return new CursorLoader(
                getActivity(),
                baseUri,
                CONTACTS_PROJECTION,
                CONTACTS_SELECTION,
                null,
                CONTACTS_ORDER);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
