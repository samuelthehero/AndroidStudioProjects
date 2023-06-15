package com.example.pokedexproyecto;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    Context context;
    String PokemonList[];
    int tipos[];
    int numero[];
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, String[] PokemonList, int[] tipos) {
        this.context = context;
        this.PokemonList = PokemonList;
        this.tipos = tipos;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return PokemonList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.layout_elemento_listado, null);
        TextView pokemon = (TextView) view.findViewById(R.id.textView);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView number = (TextView) view.findViewById(R.id.textView2);
        pokemon.setText(PokemonList[i]);
        icon.setImageResource(tipos[i]);
            number.setText("#" + (i+1)+"");


        return view;
    }
}