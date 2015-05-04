package halverson.gregory.reverseimagesearch.searchpictureonphone.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import halverson.gregory.reverseimagesearch.searchpictureonphone.R;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;
import halverson.gregory.reverseimagesearch.searchpictureonphone.fragment.LiveDisplayGridFragment;
import halverson.gregory.reverseimagesearch.searchpictureonphone.fragment.LiveWaitingScreenFragment;
import halverson.gregory.reverseimagesearch.searchpictureonphone.thread.SearchJob;

public class LiveSearchImageOnPhoneActivity extends ActionBarActivity
{
    // Fragment
    LiveWaitingScreenFragment waitingScreenFragment;
    LiveDisplayGridFragment gridFragment;

    // Thread
    public SearchJob searchJob;

    // Data
    DeviceImagesIndex deviceImagesIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_search_image_on_phone);

        waitingScreenFragment = new LiveWaitingScreenFragment();
        gridFragment = new LiveDisplayGridFragment();

        if (savedInstanceState == null)
        {
            searchOnPhoneWaitingFragment();
        }
    }

    public DeviceImagesIndex openDatabase()
    {
        deviceImagesIndex = new DeviceImagesIndex(this);
        return deviceImagesIndex;
    }

    @Override
    public void onBackPressed()
    {
        searchJob.cancel(true);

        super.onBackPressed();
    }

    public void searchOnPhoneWaitingFragment()
    {
        /*
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new LiveWaitingScreenFragment())
                .commit();
        */

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, waitingScreenFragment)
                .commit();
    }

    public void displayGridFragment()
    {
        /*
        getSupportFragmentManager().beginTransaction()
                //.add(R.id.container, new DisplayGridFragment())
                .replace(R.id.container, new LiveDisplayGridFragment())
                .commit();
        */

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, gridFragment)
                .commit();
    }

    public void hideStatusBar()
    {
        if (gridFragment != null)
            gridFragment.hideStatusBar();
    }

    public void hideSpinner()
    {
        if (waitingScreenFragment != null)
            waitingScreenFragment.hideSpinner();
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
}
