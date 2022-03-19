package com.example.smarthomeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity {
    TextView tvIdEdit;
    EditText etTipoEdit, etValorEdit;
    ImageView ivSaveEdit, ivCancelEdit;

    SharedPreferences sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        tvIdEdit = findViewById(R.id.tvIdEdit);
        etTipoEdit = findViewById(R.id.etTipoEdit);
        etValorEdit = findViewById(R.id.etValorEdit);
        ivSaveEdit = findViewById(R.id.ivSaveEdit);
        ivCancelEdit = findViewById(R.id.ivCancelEdit);

        sesion = getSharedPreferences("sesion",0);
        getSupportActionBar().setTitle("Modificar : "+sesion.getString("user",""));

        Bundle datos = this.getIntent().getExtras();
        String id = datos.getString("id");
        String tipo = datos.getString("tipo");
        String valor = datos.getString("valor");

        tvIdEdit.setText(id);
        etTipoEdit.setText(tipo);
        etValorEdit.setText(valor);

        ivSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });

        ivCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity3.this, MainActivity2.class));
            }
        });
    }

    private void guardar() {
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon()
                .appendQueryParameter("id", tvIdEdit.getText().toString())
                .appendQueryParameter("sensor", etTipoEdit.getText().toString())
                .appendQueryParameter("valor", etValorEdit.getText().toString())
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        respuestaGuardar(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity3.this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", sesion.getString("token", "Error"));
                return header;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void respuestaGuardar(JSONObject response) {
        try{
            if (response.getString("update").compareTo("y") == 0){
                Toast.makeText(this, "Datos modificados", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity3.this, MainActivity2.class));
            }else{
                Toast.makeText(this, "No se pueden guardar", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
    }
}