package com.example.monitorterremotos;

import com.example.monitorterremotos.adapter.TerremotoAdapter;
import com.example.monitorterremotos.model.Terremoto;

public interface ItemClickInterface {

    void terremotoItemClickListener(TerremotoAdapter.TerremotoAdapterHoler holder, Terremoto terremoto);
}
