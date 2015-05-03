package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

public class ImageIndexEntry
{
    private long id;
    private String imageFilePath;
    private int averageHashUpper;
    private int averageHashLower;
    private long fileModifiedDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public int getAverageHashUpper()
    {
        return averageHashUpper;
    }

    public void setAverageHashUpper(int averageHashUpper)
    {
        this.averageHashUpper = averageHashUpper;
    }

    public int getAverageHashLower()
    {
        return averageHashLower;
    }

    public void setAverageHashLower(int averageHashLower)
    {
        this.averageHashLower = averageHashLower;
    }

    public Long getFileModifiedDate()
    {
        return fileModifiedDate;
    }

    public void setFileModifiedDate(Long fileModifiedDate)
    {
        this.fileModifiedDate = fileModifiedDate;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return imageFilePath;
    }
}