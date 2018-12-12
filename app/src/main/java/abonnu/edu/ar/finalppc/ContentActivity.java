package abonnu.edu.ar.finalppc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import abonnu.edu.ar.finalppc.Adapter.PropiedadesAdapter;
import abonnu.edu.ar.finalppc.DTO.Propiedad;

public class ContentActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, Response.Listener<JSONArray>, Response.ErrorListener{

    //public static final String FILE_NAME = "coordenadas";

    TextView tvHello, tvRecalculando, tvAyuda;
    ImageView imgLostMan;
    FloatingActionButton fbtExit;
    RecyclerView rvContenedorPropiedades;

    ArrayList<Propiedad> listaPropiedades;

    LocationManager mLocationManager;
    static Location lastLocation;

    RequestQueue request;
    JsonArrayRequest jsonArrayRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        listaPropiedades = new ArrayList<>();

        tvHello = findViewById(R.id.tvHello);
        tvRecalculando = findViewById(R.id.tvRecalculando);
        tvAyuda = findViewById(R.id.tvAyuda);

        imgLostMan = findViewById(R.id.imgLostMan);

        fbtExit = findViewById(R.id.fbtExit);
        fbtExit.setOnClickListener(this);

        rvContenedorPropiedades = findViewById(R.id.rvContenedorPropiedades);
        rvContenedorPropiedades.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        MostrarSaludo();

        request = Volley.newRequestQueue(getApplicationContext());

        ObtenerCoordenadas();

        checkLastLocation();

        updateView();
    }


    //Consulta las SharedPreferences para extrar el nombre del usuario registrado
    private void MostrarSaludo(){
        SharedPreferences preferencias = getSharedPreferences(MainActivity.FILE_NAME, Context.MODE_PRIVATE);

        String saludo = getString(R.string.hello) + " " + preferencias.getString("userName", "");
        tvHello.setText(saludo);
    }

    private void ObtenerCoordenadas() {
        try{
            //mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                //mLocationManager.removeUpdates(this);
                mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 1000, 50, this);
                Log.d("ServiceTrack", "Updates Iniciados");
            }
        }catch (NullPointerException e){
            Log.e("ServiceTrack", "Error de Localizacion " + e.toString());

        }
    }

    private void checkLastLocation(){
        if (mLocationManager.isProviderEnabled(mLocationManager.GPS_PROVIDER)){
            if(LlamarWebService(lastLocation)){
                Log.d("ServiceTrack", "Requiriendo Web service con la ultima ubicacion conocida");
            }else{
                Log.e("ServiceTrack", "No hay informacion sobre la ultima ubicacion");
            }
        }
    }

    private boolean LlamarWebService(Location location) {

        if(location != null){
            String url = "http://ppc.edit.com.ar:8080/ppc/resources/datos/propiedades/" +
                    String.valueOf(location.getLatitude()) +
                    "/" +
                    String.valueOf(location.getLongitude());

            Log.d("ServiceTrack", "Url a Consultar: " + url);

            jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
            request.add(jsonArrayRequest);

            return true;

        }else{
            Log.e("ServiceTrack", "Localizacion Nula");
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fbtExit){
            Snackbar.make(view, getString(R.string.msjSalida), Snackbar.LENGTH_LONG).setAction(getString(R.string.confirmSalida), this).show();
        }else{
            SharedPreferences preferencias = getSharedPreferences(MainActivity.FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();

            editor.remove("userName");
            editor.putBoolean("autenticado", !preferencias.getBoolean("autenticado", false));

            editor.commit();

            startActivity(new Intent(this, MainActivity.class));
        }

    }

    @Override
    public void onBackPressed() {
    }


    private void SwchitViews(int viewLista, int viewError){
        rvContenedorPropiedades.setVisibility(viewLista);
        tvRecalculando.setVisibility(viewError);
        tvAyuda.setVisibility(viewError);
        imgLostMan.setVisibility(viewError);
        updateView();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("ServiceTrack", "Provider Change Location");
        if (!location.equals(lastLocation)){
            LlamarWebService(location);
        }

        lastLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("ServiceTrack", "Provider Change Status");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("ServiceTrack", "Provider Enabled");
        mLocationManager.removeUpdates(this);
        ObtenerCoordenadas();

        checkLastLocation();

        updateView();
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("ServiceTrack", "Provider Disabled");
        //Cambia al lost man
        SwchitViews(View.INVISIBLE, View.VISIBLE);
    }

    private void updateView(){
        if(mLocationManager.isProviderEnabled(mLocationManager.GPS_PROVIDER)){
            tvAyuda.setText(getString(R.string.msjInfo));
        }else{
            tvAyuda.setText(getString(R.string.msjAyuda));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ServiceTrack", "Destroy ContentActivity");
        mLocationManager.removeUpdates(this);
    }

    //Disparado cuando la respuesta del Web Service falla :(
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error en la Respuesta del Servicio", Toast.LENGTH_SHORT).show();
        Log.d("ServiceTrack", "Error " + error.toString());
    }

    //Disparado cuando la respuesta del Web Service se realiza correctamente :)
    @Override
    public void onResponse(final JSONArray response) {

        try {
            listaPropiedades.clear();
            for (int i = 0; i < response.length(); i++){
                //Log.d("ServiceTrack", "Propiedad " + i + ": " + response.getJSONObject(i).toString());
                listaPropiedades.add(new Gson().fromJson(response.getJSONObject(i).toString(), Propiedad.class));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        PropiedadesAdapter adaptadorPropiedades = new PropiedadesAdapter(listaPropiedades);
        rvContenedorPropiedades.setAdapter(adaptadorPropiedades);

        //Cambia a la lista de propiedades
        SwchitViews(View.VISIBLE, View.INVISIBLE);

        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });


        rvContenedorPropiedades.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recycler, MotionEvent event) {
                try {
                    View child = recycler.findChildViewUnder(event.getX(), event.getY());

                    if (child != null && mGestureDetector.onTouchEvent(event)) {

                        int position = recycler.getChildAdapterPosition(child);

                        //Toast.makeText(ContentActivity.this,"The Item Clicked is: "+ position ,Toast.LENGTH_SHORT).show();
                        //Log.d("ServiceTrack", "Propiedad " + position + ": " + response.getJSONObject(position).toString());

                        try{
                            Propiedad propiedad = new Gson().fromJson(response.getJSONObject(position).toString(), Propiedad.class);

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Propiedad", propiedad);

                            Intent intent = new Intent(ContentActivity.this, DetailActivity.class);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
}
