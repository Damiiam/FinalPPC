package abonnu.edu.ar.finalppc;

import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import abonnu.edu.ar.finalppc.DTO.Propiedad;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvDescripcionPropiedad, tvLatitud, tvLongitud, tvTipoPropiedad, tvDireccion, tvDetalles, tvDistancia, tvPrecio;
    Button btComprar;

    Propiedad propiedad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDescripcionPropiedad = findViewById(R.id.tvDescripcionPropiedad);
        tvLatitud = findViewById(R.id.tvLatitud);
        tvLongitud = findViewById(R.id.tvLongitud);
        tvTipoPropiedad = findViewById(R.id.tvTipoPropiedad);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvDetalles = findViewById(R.id.tvDetalles);
        tvDistancia = findViewById(R.id.tvDistancia);
        tvPrecio = findViewById(R.id.tvPrecio);

        if(getIntent().getExtras() != null){
            propiedad = (Propiedad) getIntent().getExtras().getSerializable("Propiedad");

            tvDescripcionPropiedad.setText(propiedad.getDescripcion());
            tvLatitud.setText(String.valueOf(propiedad.getLatitud()));
            tvLongitud.setText(String.valueOf(propiedad.getLongitud()));
            tvTipoPropiedad.setText(propiedad.getTipo());
            tvDireccion.setText(propiedad.getTelefono());
            tvDetalles.setText(propiedad.getDetalle());
            tvDistancia.setText(String.format("%.2f", propiedad.getDistancia()) + " m");
            tvPrecio.setText("$ " + propiedad.getValor());
        }

        btComprar = findViewById(R.id.btComprar);
        btComprar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(propiedad.getValor() < 80000){
            btComprar.setBackground(getResources().getDrawable(R.drawable.buttom_border_pink));
            btComprar.setText(getResources().getString(R.string.comprado));
        }else{
            Snackbar.make(view, getString(R.string.sinSaldo), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
