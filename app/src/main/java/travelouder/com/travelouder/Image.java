package travelouder.com.travelouder;

import android.os.Parcel;
import android.os.Parcelable;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Image implements Parcelable {

    public String userEmail;
    public String key;
    public String userId;
    public String downloadUrl;
    public String caption;
    public Double latitude;
    public Double longitude;
    public String placeName;


    // these properties will not be saved to the database
    public Image() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Image(String userEmail, String key, String userId, String downloadUrl, String caption, Double latitude, Double longitude, String placeName) {
        this.userEmail=userEmail;
        this.key = key;
        this.userId = userId;
        this.downloadUrl = downloadUrl;
        this.caption = caption;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.userId);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.caption);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeString(this.placeName);
    }

    protected Image(Parcel in) {
        this.key = in.readString();
        this.userId = in.readString();
        this.downloadUrl = in.readString();
        this.caption = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.placeName = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

}