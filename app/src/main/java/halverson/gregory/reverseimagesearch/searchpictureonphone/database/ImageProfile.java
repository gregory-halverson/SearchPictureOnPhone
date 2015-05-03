package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

/**
 * Created by Gregory on 5/3/2015.
 */
public class ImageProfile
{
    public long modifiedDate;
    public String averageHashStringHex;

    public ImageProfile(long modifiedDate, String averageHashStringHex)
    {
        this.modifiedDate = modifiedDate;
        this.averageHashStringHex = averageHashStringHex;
    }
}
