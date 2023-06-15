package com.example.pokedexproyecto;

import static com.example.pokedexproyecto.R.layout.activity_main;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class BlankFragment2 extends Fragment {
    private ListView mListView;
    String[] pokemon = {"Charmander", "Charmeleon", "Charizard", "Squirtle", "Wartortle", "Blastoise", "Bulbasaur", "Ivysaur", "Venusaur","Breloom"};
    int tipos[] = {R.drawable.fuego, R.drawable.fuego, R.drawable.fuego, R.drawable.agua, R.drawable.agua, R.drawable.agua,R.drawable.planta,R.drawable.planta,R.drawable.planta,R.drawable.planta};
    int fotos[] = {R.drawable.charmander, R.drawable.charmeleon, R.drawable.charizard, R.drawable.squirtle, R.drawable.wartortle, R.drawable.blastoise,R.drawable.bulbasaur,R.drawable.ivysaur,R.drawable.venasaur,R.drawable.breloom};
    ImageView icon;

    public BlankFragment2() {
        // Required empty public constructor
    }

    public static BlankFragment2 newInstance(String param1,String param2){
        BlankFragment2 fragment = new BlankFragment2();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_blank,container,false);


    mListView = (ListView) rootView.findViewById(R.id.listView);


        CustomAdapter customAdapter = new CustomAdapter(getActivity(), pokemon, tipos);
        mListView.setAdapter(customAdapter);


        mListView.setClickable(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Log.i("Click", "click en el elemento " + position + " de mi ListView");
                icon = (ImageView) getActivity().findViewById(R.id.imageView3);
               icon.setImageResource(fotos[position]);
            }
        });


        return rootView;



    }




}