package com.example.ejercicio_2_3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ejercicio_2_3.modelo.Foto;
import com.example.ejercicio_2_3.photos.SQLiteConexion;
import com.example.ejercicio_2_3.photos.Transacciones;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class Listado extends AppCompatActivity {

    SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
    Button btnVolverAL;
    ListView list;
    ArrayList<Foto> listaFotos = new ArrayList<Foto>();
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        SQLiteDatabase db = conexion.getWritableDatabase();
        String sql = "SELECT * FROM fotografia";
        Cursor cursor = db.rawQuery(sql, new String[] {});

        btnVolverAL = (Button) findViewById(R.id.btnVolverAL);
        list = findViewById(R.id.listaAL);

        while (cursor.moveToNext()) {
            listaFotos.add(new Foto(cursor.getInt(0), cursor.getString(1), cursor.getBlob(2)));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        AdaptadorFotografia adaptador = new AdaptadorFotografia(this);

        list.setAdapter(adaptador);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CapturarFoto(i);
            }
        });

        btnVolverAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityFotografia.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CapturarFoto(int id) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Foto fotoItem = null;
        listaFotos = new ArrayList<Foto>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tblFotografias, null);

        while (cursor.moveToNext()) {
            fotoItem = new Foto();
            fotoItem.setId(cursor.getInt(0));
            fotoItem.setDescrip(cursor.getString(1));
            listaFotos.add(fotoItem);
        }
        cursor.close();
        Foto foto = listaFotos.get(id);
    }

    class AdaptadorFotografia extends ArrayAdapter<Foto> {

        AppCompatActivity appCompatActivity;

        AdaptadorFotografia(AppCompatActivity context) {
            super(context, R.layout.foto, listaFotos);
            appCompatActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.foto, null);

            imageView = item.findViewById(R.id.imageView);

            SQLiteDatabase db = conexion.getWritableDatabase();

            String sql = "SELECT * FROM fotografia";

            Cursor cursor = db.rawQuery(sql, new String[] {});
            Bitmap bitmap = null;
            TextView textView1 = item.findViewById(R.id.textView);

            if (cursor.moveToNext()) {
                textView1.setText(listaFotos.get(position).getDescrip());
                byte[] blob = listaFotos.get(position).getFoto();
                ByteArrayInputStream bais = new ByteArrayInputStream(blob);
                bitmap = BitmapFactory.decodeStream(bais);
                imageView.setImageBitmap(bitmap);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
            return (item);
        }
    }
}