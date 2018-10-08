package travelouder.com.travelouder;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ImageDetailActivity extends AppCompatActivity {
    public static final String TAGPOST = "ImagePost";
    ImageView ivFetchedPost;
    TextView tvCaption;
    Image image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        image = getIntent().getParcelableExtra(TAGPOST);
        ivFetchedPost = findViewById(R.id.ivImagePost);
        //tvCaption=findViewById(R.id.tvPostDetailCaption);
        //tvCaption.setText(image.caption);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        Picasso.with(getApplicationContext())
                .load(image.downloadUrl)
                .resize(width, 0)
                .into(ivFetchedPost);
    }


    public void openMaps(View view) {

        String query = "geo:0,0?q="+image.latitude + ","+image.longitude + "(" + image.placeName + ")";
        Uri gmmIntentUri = Uri.parse(query);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
