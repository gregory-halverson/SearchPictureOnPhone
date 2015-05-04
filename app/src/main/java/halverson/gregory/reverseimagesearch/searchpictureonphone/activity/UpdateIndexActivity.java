package halverson.gregory.reverseimagesearch.searchpictureonphone.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import halverson.gregory.image.AndroidCodec;
import halverson.gregory.reverseimagesearch.searchpictureonphone.R;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;
import halverson.gregory.reverseimagesearch.searchpictureonphone.thread.IndexJob;

public class UpdateIndexActivity extends ActionBarActivity
{
    // Data
    DeviceImagesIndex deviceImagesIndex;

    // Thread
    IndexJob indexJob;

    // View
    TextView statusText;
    ImageView imageView;
    ProgressBar spinner;

    public UpdateIndexActivity() { }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_index);

        statusText = (TextView) findViewById(R.id.updateIndexStatusText);
        imageView = (ImageView) findViewById(R.id.updateIndexImageView);
        spinner = (ProgressBar) findViewById(R.id.updateIndexSpinner);

        // Hash images on phone
        indexJob = new IndexJob(this, statusText, imageView, spinner);
        indexJob.execute();
    }

    public DeviceImagesIndex openDatabase()
    {
        deviceImagesIndex = new DeviceImagesIndex(this);
        return deviceImagesIndex;
    }

    public void setStatusText(String text)
    {
        statusText.setText(text);
    }

    public void setImageFromFilePathString(String filePathString)
    {
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(AndroidCodec.decodedUriStringFromFilePathString(filePathString));
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed()
    {
        // Cancel indexing engine
        indexJob.cancel(true);

        super.onBackPressed();

        // Make sure activity closes on back button
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_index, menu);
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
