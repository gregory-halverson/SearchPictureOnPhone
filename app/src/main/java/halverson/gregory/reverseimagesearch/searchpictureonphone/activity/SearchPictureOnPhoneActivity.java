package halverson.gregory.reverseimagesearch.searchpictureonphone.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import halverson.gregory.reverseimagesearch.searchpictureonphone.R;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;
import halverson.gregory.reverseimagesearch.searchpictureonphone.fragment.DisplayGridFragment;
import halverson.gregory.reverseimagesearch.searchpictureonphone.fragment.SearchOnPhoneWaitingFragment;
import halverson.gregory.reverseimagesearch.searchpictureonphone.thread.SearchJob;

public class SearchPictureOnPhoneActivity extends ActionBarActivity implements SearchOnPhoneWaitingFragment.OnFragmentInteractionListener
{
    public SearchJob hashJob;

    // Data
    DeviceImagesIndex deviceImagesIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_picture_on_phone);

        if (savedInstanceState == null)
        {
            searchOnPhoneWaitingFragment();
        }
    }

    public DeviceImagesIndex openDatabase(SearchOnPhoneWaitingFragment waitingFragment)
    {
        deviceImagesIndex = new DeviceImagesIndex(this, waitingFragment);
        return deviceImagesIndex;
    }

    public void onFragmentInteraction(Uri uri)
    {

    }

    @Override
    public void onBackPressed()
    {
        hashJob.cancel(true);

        super.onBackPressed();
    }

    public void searchOnPhoneWaitingFragment()
    {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new SearchOnPhoneWaitingFragment())
                .commit();
    }

    public void displayGridFragment()
    {
        getSupportFragmentManager().beginTransaction()
                //.add(R.id.container, new DisplayGridFragment())
                .replace(R.id.container, new DisplayGridFragment())
                .commit();
    }

    public DeviceImagesIndex getDeviceImagesIndex()
    {
        return deviceImagesIndex;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_picture_on_phone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {

        public PlaceholderFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_search_picture_on_phone, container, false);
            return rootView;
        }
    }
}
