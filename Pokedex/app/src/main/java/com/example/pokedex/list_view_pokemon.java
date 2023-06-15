package com.example.pokedex;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class list_view_pokemon extends Fragment {
    private ListView mListView;
    String[] pokemon = {
            "Bulbasaur",
            "Charizard",
            "Charmander",
            "Charmeleon",
            "Pansage",
            "Pikachu",
            "Jigglypuff",
            "Squirtle"
    };
    int tipos[] = {
            R.drawable.agua,
            R.drawable.tipo_fuego,
            R.drawable.tipo_fuego,
            R.drawable.tipo_fuego,
            R.drawable.normal,
            R.drawable.electro,
            R.drawable.normal,
            R.drawable.agua
    };
    int fotos[] = {
            R.drawable.bulbasaur,
            R.drawable.charizard,
            R.drawable.charmander,
            R.drawable.charmeleon,
            R.drawable.pansage,
            R.drawable.pikachu,
            R.drawable.jigglypuff,
            R.drawable.squirtle
    };
    ImageView icon;

    public list_view_pokemon() {
        // Required empty public constructor
    }

    public static list_view_pokemon newInstance(String param1,String param2){
        list_view_pokemon fragment = new list_view_pokemon();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view_pokemon,container,false);


        mListView = (ListView) rootView.findViewById(R.id.listView);


        PokedexAdapter customAdapter = new PokedexAdapter(getActivity(), pokemon, tipos);
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