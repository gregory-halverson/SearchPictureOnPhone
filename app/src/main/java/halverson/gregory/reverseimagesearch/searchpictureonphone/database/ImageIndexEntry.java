package halverson.gregory.reverseimagesearch.searchpictureonphone.database;

public class ImageIndexEntry
{
    private long id;
    private String imageFilePath;
    private String averageHashHex;
    private Long fileModifiedDate;

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

    public String getAverageHashHex()
    {
        return averageHashHex;
    }

    public void setAverageHashHex(String averageHashHex)
    {
        this.averageHashHex = averageHashHex;
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