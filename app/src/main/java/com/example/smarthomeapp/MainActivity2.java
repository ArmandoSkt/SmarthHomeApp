package com.example.smarthomeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    EditText etTipo, etValor;
    Button bAdd, bRefresh;
    RecyclerView rvMsg;
    SharedPreferences sesion;
    String lista [][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        etTipo = findViewById(R.id.etTipo);
        etValor = findViewById(R.id.etValor);
        bAdd = findViewById(R.id.bAdd);
        bRefresh = findViewById(R.id.bRefresh);
        rvMsg = findViewById(R.id.rvMsg);
        sesion = getSharedPreferences("sesion", 0);
        getSupportActionBar().setTitle("Mensajes -" +
                sesion.getString("user", ""));
        rvMsg.setHasFixedSize(true);
        rvMsg.setItemAnimator(new DefaultItemAnimator());
        rvMsg.setLayoutManager(new LinearLayoutManager(this));

        llenar();

        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llenar();
            }
        });
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregar();
            }
        });
    }

    private void agregar() {
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon().build().toString();
        StringRequest peticion = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        agregarRespuesta(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, "Error de conexión", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", sesion.getString("token", "Error"));
                return header;
            }

            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("tipo", etTipo.getText().toString());
                params.put("valor", etValor.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void agregarRespuesta(String response) {
    }

    private void llenar() {
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon().build().toString();
        JsonArrayRequest peticion = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        llenarRespuesta(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, "Error de conexión", Toast.LENGTH_SHORT).show();

            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", sesion.getString("token", "Error"));
                return header;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void llenarRespuesta(JSONArray response) {
        try {
            Log.d("DEPURAR","llenarRespuesta: Si responde");
            lista = new String[response.length()][5];
            for (int i = 0; i < response.length(); i++){
                lista[i][0] = response.getJSONObject(i).getString("id");
                lista[i][1] = response.getJSONObject(i).getString("user");
                lista[i][2] = response.getJSONObject(i).getString("sensor");
                lista[i][3] = response.getJSONObject(i).getString("valor");
                lista[i][4] = response.getJSONObject(i).getString("fecha");
            }
            rvMsg.setAdapter(new MyAdapter(lista));
        }catch (Exception e){
        }
    }
}