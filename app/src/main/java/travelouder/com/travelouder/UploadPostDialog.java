package travelouder.com.travelouder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class UploadPostDialog extends DialogFragment{

    private OnCaptionReveive onCaptionReveive;


    public UploadPostDialog() {}

    public static UploadPostDialog getInstance(MainActivity mainActivity, OnCaptionReveive onCaptionReveive) {
        UploadPostDialog uploadPostDialog = new UploadPostDialog();
        uploadPostDialog.setOnCaptionReveive(onCaptionReveive);
        return uploadPostDialog;
    }

    public void setOnCaptionReveive(OnCaptionReveive onCaptionReveive) {
        this.onCaptionReveive = onCaptionReveive;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_upload_post, null);

        final EditText editTextCaption = (EditText) v.findViewById(R.id.editTextUploadPhotoCaption);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                .setTitle("Set caption")
                // Add action buttons
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(editTextCaption != null && onCaptionReveive != null) {
                            String caption = editTextCaption.getText().toString();
                            if(caption.isEmpty()) {
                                caption = "Default caption";
                            }
                            onCaptionReveive.onCaptionSubmitted(caption);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UploadPostDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}