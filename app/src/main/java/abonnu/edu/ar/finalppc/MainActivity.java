package abonnu.edu.ar.finalppc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    public static final String FILE_NAME = "credenciales";
    public static final String PASSWORD = "admin";

    TextInputLayout passwordWrapper;
    TextInputLayout nameWrapper;

    EditText etName, etPass;
    Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordWrapper = findViewById(R.id.passwordWrapper);
        nameWrapper = findViewById(R.id.usernameWrapper);

        etName = findViewById(R.id.etUserName);
        etName.setOnFocusChangeListener(this);

        etPass = findViewById(R.id.etPass);
        etPass.setOnFocusChangeListener(this);

        btLogin = findViewById(R.id.btLogin);
        btLogin.setOnClickListener(this);

        ComprobarCredenciales(getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE));
    }

    //Si el usuario ya esta registrado no se le muestra la actividad  de login
    private void ComprobarCredenciales(SharedPreferences preferencias){

        if(preferencias.getBoolean("autenticado", false)){
            //Toast.makeText(this, "Autenticado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ContentActivity.class));
        }

    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(this, "Presionando The Button", Toast.LENGTH_SHORT).show();

        if(etName.getText().toString().isEmpty()){
            nameWrapper.setError(getResources().getString(R.string.nameEmpty));
        }else{
            if (etPass.getText().toString().matches(PASSWORD)){

                SharedPreferences preferencias = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = preferencias.edit();

                editor.putString("userName", etName.getText().toString());
                editor.putBoolean("autenticado", !preferencias.getBoolean("autenticado", false));

                editor.commit();

                ComprobarCredenciales(preferencias);

            }else{
                etPass.setText("");
                passwordWrapper.setError(getResources().getString(R.string.passError));
            }
        }

        getCurrentFocus().clearFocus();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onFocusChange(View view, boolean isFocus) {
        if(isFocus){
            if (view.getId() == R.id.etUserName){
                nameWrapper.setErrorEnabled(false);
            }
            if (view.getId() == R.id.etPass){
                passwordWrapper.setErrorEnabled(false);
            }
        }
    }
}
