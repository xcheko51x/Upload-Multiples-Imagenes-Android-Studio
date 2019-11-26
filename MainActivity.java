package com.xcheko51x.seleccionarmultipleimagenes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    int PICK_IMAGE = 100;
    Uri imageUri;
    Button btnGaleria, btnSubirImagenes;
    GridView gvImagenes;

    List<Uri> listaImagenes = new ArrayList<>();
    List<String> listaBase64Imagenes = new ArrayList<>();

    GridViewAdapter baseAdapter;

    String URL_UPLOAD_IMAGENES = "http://192.168.0.11/PruebasCanal/UploadImagenes.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gvImagenes = findViewById(R.id.gvImagenes);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnSubirImagenes = findViewById(R.id.btnSubirImagenes);

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnSubirImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subirImagenes();
            }
        });
    }

    public void subirImagenes() {

        listaBase64Imagenes.clear();

        for(int i = 0 ; i < listaImagenes.size() ; i++) {
            try {
                InputStream is = getContentResolver().openInputStream(listaImagenes.get(i));
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                String cadena = convertirUriToBase64(bitmap);

                enviarImagenes("nomIma"+i, cadena);

                bitmap.recycle();

            } catch (IOException e) { }

        }
    }

    public void enviarImagenes(final String nombre, final String cadena) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD_IMAGENES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("nom", nombre);
                params.put("imagenes", cadena);

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public String convertirUriToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String encode = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encode;
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECCIONA LAS IMAGENES"), PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ClipData clipData = data.getClipData();

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {

            if(clipData == null) {
                imageUri = data.getData();
                listaImagenes.add(imageUri);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    listaImagenes.add(clipData.getItemAt(i).getUri());
                }
            }
        }

        baseAdapter = new GridViewAdapter(MainActivity.this, listaImagenes);
        gvImagenes.setAdapter(baseAdapter);

    }
}