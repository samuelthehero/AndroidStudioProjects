package com.example.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PokedexAdapter extends BaseAdapter {

    Context context;
    String PokemonList[];
    int tipos[];
    LayoutInflater inflater;

    public PokedexAdapter(Context applicationContext, String[] PokemonList, int[] tipos) {
        this.context = context;
        this.PokemonList = PokemonList;
        this.tipos = tipos;
        inflater = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return PokemonList.length;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_list_view, null);
        TextView pokemon = (TextView) view.findViewById(R.id.textViewNombre);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView id = (TextView) view.findViewById(R.id.id);
        pokemon.setText(PokemonList[i]);
        icon.setImageResource(tipos[i]);
        id.setText("#" + (i+1)+"");

        return view;
    }
}
