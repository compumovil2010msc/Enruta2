package co.edu.javeriana.enrutados;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button forgotPassword, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inflate
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        forgotPassword = (Button) findViewById(R.id.forgot_password);
        login = (Button) findViewById(R.id.login);

        //Add events
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PasswordRecoveryActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                if (user.isEmpty()) {
                    username.setError(getString(R.string.error_empty_field));
                } else {
                    validateUser(user, pass);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void validateUser (String user, String pass) {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (user.equals("coord")) {
            editor.putString(getString(R.string.preference_user_key), user);
            editor.putString(getString(R.string.preference_acc_key), getString(R.string.preference_acc_coord));
            editor.apply();
            Intent intent = new Intent(this, DrawerActivity.class);
            startActivity(intent);
        } else if (user.equals("tech")) {
            editor.putString(getString(R.string.preference_user_key), user);
            editor.putString(getString(R.string.preference_acc_key), getString(R.string.preference_acc_tech));
            editor.apply();
            Intent intent = new Intent(this, DrawerActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.error_incorrect_login, Toast.LENGTH_SHORT).show();
        }
    }
}
