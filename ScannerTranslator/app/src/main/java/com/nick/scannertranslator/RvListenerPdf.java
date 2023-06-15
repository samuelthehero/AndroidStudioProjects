package com.nick.scannertranslator;

import com.nick.scannertranslator.adapters.AdapterPdf;
import com.nick.scannertranslator.models.ModelPdf;

public interface RvListenerPdf {

    void onPdfClick(ModelPdf modelPdf, int position);
    void onPdfMoreClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder);

}
