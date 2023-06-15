package com.nick.proyectoscannertranslator;

import com.nick.proyectoscannertranslator.adapters.AdapterPdf;
import com.nick.proyectoscannertranslator.models.ModelPdf;

public interface RvListenerPdf {

    void onPdfClick(ModelPdf modelPdf, int position);
    void onPdfMoreClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder);
}
