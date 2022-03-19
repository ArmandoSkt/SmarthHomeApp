package com.example.smarthomeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    EditText etTipo, etValor;
    Button bAdd, bRefresh;
    RecyclerView rvMsg;
    SharedPreferences sesion;
    String lista[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        etTipo = findViewById(R.id.etTipo);
        etValor = findViewById(R.id.etValor);
        bAdd = findViewById(R.id.bAdd);
        bRefresh = findViewById(R.id.bRefresh);
        rvMsg = findViewById(R.id.rvMsg);
        sesion = getSharedPreferences("sesion",0);
        getSupportActionBar().setTitle("Mensajes - " +
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
        StringRequest peticion  = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        agregarRespuesta(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("Error", error.getMessage());
                Toast.makeText(MainActivity2.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization",
                        sesion.getString("token", "Error"));
                return header;
            }
            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("sensor", etTipo.getText().toString());
                params.put("valor", etValor.getText().toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void agregarRespuesta(String response) {
        try{
            JSONObject r = new JSONObject(response);
            if(r.getString("add").compareTo("y")==0){
                Toast.makeText(MainActivity2.this, "Almacenado correctamente " + r.getString("id"), Toast.LENGTH_SHORT).show();
                llenar();
            }else{
                Toast.makeText(MainActivity2.this, "Error no se pudo agregar", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
    }

    private void llenar() {
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon().build().toString();
        JsonArrayRequest peticion  = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        llenarRespuesta(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity2.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            }
        }){
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization",
                        sesion.getString("token", "Error"));
                return header;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }

    private void llenarRespuesta(JSONArray response) {
        try {
            Log.d("DEPURAR", "llenarRespuesta:" + response.toString());
            lista = new String[response.length()][5];
            for (int i = 0; i < response.length(); i++) {
                lista[i][0] = response.getJSONObject(i).getString("id");
                lista[i][1] = response.getJSONObject(i).getString("user");
                lista[i][2] = response.getJSONObject(i).getString("sensor");
                lista[i][3] = response.getJSONObject(i).getString("valor");
                lista[i][4] = response.getJSONObject(i).getString("fecha");
            }
            Log.d("DEPURAR", lista.toString());
            rvMsg.setAdapter(new MyAdapter(lista, new RecyclerViewOnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    Toast.makeText(MainActivity2.this, "Clicl al elemento " + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onClickEdit(View v, int position) {
                    Bundle extras = new Bundle();
                    extras.putString("id",lista[position][0]);
                    extras.putString("tipo",lista[position][2]);
                    extras.putString("valor",lista[position][3]);

                    Intent i = new Intent(MainActivity2.this, MainActivity3.class);
                    i.putExtras(extras);
                    startActivity(i);
                }

                @Override
                public void onClickDel(View v, int position) {
                    new AlertDialog.Builder(MainActivity2.this)
                            .setTitle("Eliminar")
                            .setMessage("Quieres eliminar el mensaje id=" +  lista[position][0] + "?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    eliminar(lista[position][0]);
                                }
                            })
                            .setNegativeButton("No",null)
                            .create().show();
                }
            }));
            Toast.makeText(this, "Lista Actualizada", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.d("Error", e.getMessage());
        }
    }

    private void eliminar(String id) {
        String url = Uri.parse(Config.URL + "registro.php")
                .buildUpon()
                .appendQueryParameter("id", id)
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        respuestaEliminar(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, "Error de red", Toast.LENGTH_SHORT).show();
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

    private void respuestaEliminar(JSONObject response) {
        try{
            if (response.getString("delete").compareTo("y") == 0){
                Toast.makeText(this, "Datos eliminados", Toast.LENGTH_SHORT).show();
                llenar();
            }else{
                Toast.makeText(this, "No se puede eliminar", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){}
    }
}