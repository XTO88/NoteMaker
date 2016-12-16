package com.mrzhevskiy.android.notemaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ImageDialog extends DialogFragment {
    private ImageView imageView;
    private static String imagePath;
    private static Context con;

    public static void setImage(String path, Context context){
        imagePath = path;
        con = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,android.R.style.Theme_Black_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_image, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        Bitmap bitmap = PictureUtils.getScaledBitmap(imagePath, getActivity());
        imageView.setImageBitmap(bitmap);
        return view;
    }
}
