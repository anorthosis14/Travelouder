package travelouder.com.travelouder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class PostData implements Parcelable {

    public static final String PARCEL_TAG = "parceltagpost";
    public static final String POST_INDEX = "postindex";

    @SerializedName("Caption")
    @Expose
    private String caption;
    @SerializedName("URL")
    @Expose
    private String url;
    @SerializedName("Date")
    @Expose
    private String timestamp;
    @SerializedName("Coordinates")
    @Expose
    private String coordinates;

    public PostData(String url, String caption, String timestamp, String coordinates) {
        this.url = url;
        this.caption = caption;
        this.timestamp = timestamp;
        this.coordinates = coordinates;
    }

    public String getCaption() {
        return caption;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }

    public String getCoordinates() {
        return coordinates;
    }

    // Parcelling part
    public PostData(Parcel in){
        String[] data = new String[4];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.caption = data[0];
        this.url = data[1];
        this.timestamp = data[2];
        this.coordinates = data[3];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.caption,
                this.url,
                this.timestamp,
                this.coordinates});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PostData createFromParcel(Parcel in) {
            return new PostData(in);
        }

        public PostData[] newArray(int size) {
            return new PostData[size];
        }
    };


}