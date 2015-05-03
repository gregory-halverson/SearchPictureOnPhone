package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

import halverson.gregory.image.hash.Hash;

/**
 * Created by Gregory on 5/3/2015.
 */
public class ImageProfile
{
    public long id;
    public long modifiedDate;
    public Hash averageHash;

    public ImageProfile(long id, Hash averageHash, long modifiedDate)
    {
        this.id = id;
        this.modifiedDate = modifiedDate;
        this.averageHash = averageHash;
    }
}
