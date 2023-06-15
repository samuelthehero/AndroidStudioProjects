package com.example.monitorterremotos.databaseFile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TerremotoSQLiteHelper extends SQLiteOpenHelper {

    //Sentencia SQL para crear la tabla de Terremotos
    String sqlCreate = "CREATE TABLE terremoto (id TEXT, place TEXT, magnitude TEXT, time INTEGER, latitude INTEGER, longitude INTEGER)";

    public TerremotoSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

//    public TerremotoSQLiteHelper(Context contexto, String place,
//                                 SQLiteDatabase.CursorFactory factory, int version) {
//        super(contexto, place, factory, version);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS terremoto");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
    }
}
