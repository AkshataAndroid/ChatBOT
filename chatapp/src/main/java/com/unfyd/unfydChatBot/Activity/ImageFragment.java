package com.unfyd.unfydChatBot.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.unfyd.unfydChatBot.R;

import java.io.ByteArrayInputStream;

public class ImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_image, container, false);
        ImageView previewThumbnail = (ImageView)view.findViewById(R.id.myImage);
     // ByteArrayInputStream obj= getArguments().getByteArray("byteArray");

            Bitmap b = BitmapFactory.decodeByteArray(
                    getArguments().getByteArray("byteArray"),0, getArguments().getByteArray("byteArray").length);
            previewThumbnail.setImageBitmap(b);

        return view;
    }
}
