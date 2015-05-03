package halverson.gregory.reverseimagesearch.searchpictureonphone.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import halverson.gregory.reverseimagesearch.searchpictureonphone.R;
import halverson.gregory.reverseimagesearch.searchpictureonphone.database.DeviceImagesIndex;

/**
 * Created by Gregory on 4/20/2015.
 */
// Adapter class for image grid
public class SearchResultsAdapter extends BaseAdapter
{
    private Activity activity;
    private DeviceImagesIndex deviceImagesIndex;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public SearchResultsAdapter(Activity activity,
                                ImageLoader imageLoader,
                                DisplayImageOptions options,
                                DeviceImagesIndex deviceImagesIndex)
    {
        super();

        this.activity = activity;
        this.imageLoader = imageLoader;
        this.options = options;
        this.deviceImagesIndex = deviceImagesIndex;
    }

    // Get number of items to show in list
    @Override
    public int getCount() {
        //return mThumbIds.length;
        return (int)this.deviceImagesIndex.getImageCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        View view = convertView;
        final ViewHolder gridViewImageHolder;
//            check to see if we have a view
        if (convertView == null) {
//                no view - so create a new one
            view = activity.getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
            gridViewImageHolder = new ViewHolder();
            gridViewImageHolder.imageView = (ImageView) view.findViewById(R.id.image);
            gridViewImageHolder.imageView.setMaxHeight(80);
            gridViewImageHolder.imageView.setMaxWidth(80);
            view.setTag(gridViewImageHolder);
        } else {
//                we've got a view
            gridViewImageHolder = (ViewHolder) view.getTag();
        }

        imageLoader.displayImage(deviceImagesIndex.getImageUriAtPosition(position), gridViewImageHolder.imageView, options);

        return view;
    }

    private static class ViewHolder
    {
        ImageView imageView;
    }
}