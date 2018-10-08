package travelouder.com.travelouder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Image> currentDataList;
    private List<Image> allPostDataList = new ArrayList<>();
    private List<Image> userDataList = new ArrayList<>();
    private Context context;
    private int width;
    private boolean isHomeMode;
    private ClickHandler clickHandler;

    public PostAdapter(Context context, int width, ClickHandler clickHandler,boolean isHomeMode){
        this.context = context;
        this.width = width;
        this.clickHandler = clickHandler;
        this.isHomeMode=isHomeMode;
        currentDataList=isHomeMode?allPostDataList:userDataList;
    }
    public interface ClickHandler {
        void onClick(Image imagePost);
    }

    public void addImage(Image image, boolean isCurrentUser) {
        if (allPostDataList == null) allPostDataList = new ArrayList<>();
        allPostDataList.add(image);
        if (userDataList == null) userDataList = new ArrayList<>();
        if (isCurrentUser) {
            userDataList.add(image);
        }
        notifyDataSetChanged();
    }

    public void setMode(boolean isHome){
        this.isHomeMode=isHome;
        currentDataList=isHome?allPostDataList:userDataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        ImageViewHolder viewHolder = new ImageViewHolder(view);
        setupClickableViewHolder(view, viewHolder);
        return viewHolder;
    }

    public void setupClickableViewHolder(final View v, final RecyclerView.ViewHolder viewHolder) {

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentDataList != null && !currentDataList.isEmpty()) {
                    int index = viewHolder.getAdapterPosition();
                    Image image = currentDataList.get(index);
                    clickHandler.onClick(image);
                }
            }

        });

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Image image = currentDataList.get(position);
        String url = image.downloadUrl;
        String userEmail= image.userEmail;
        final ImageViewHolder ivh = (ImageViewHolder) holder;
        //String[] parts = userEmail.split("@");
        //String first = parts[0];
        ivh.caption.setText(image.caption != null ? userEmail +"  "+  image.caption : "Caption not available");
        ivh.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(url)
                .resize(width, 0)
                .into(ivh.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivh.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        ivh.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return currentDataList != null ? currentDataList.size() : 0;
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView caption;
        public ProgressBar progressBar;
        public ImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageViewPost);
            caption = (TextView) itemView.findViewById(R.id.tvCaption);
            progressBar=itemView.findViewById(R.id.pbLoadingImage);
        }

    }

}
