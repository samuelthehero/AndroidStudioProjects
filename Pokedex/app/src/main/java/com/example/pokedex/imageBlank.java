package com.example.pokedex;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link imageBlank#newInstance} factory method to
 * create an instance of this fragment.
 */
public class imageBlank extends Fragment {

    public imageBlank() {
        // Required empty public constructor
    }

    public static imageBlank newInstance(String param1, String param2) {
        imageBlank fragment = new imageBlank();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_blank, container, false);
    }
}