package co.edu.javeriana.enrutados;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String ENRUTADOS_LOG = ">enrutados";
    private FirebaseAuth mAuth;

    EditText username, password;
    Button forgotPassword, login;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();

        if (intent.hasExtra("logout")) {
            mAuth.signOut();
        }

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
                String email = username.getText().toString();
                String pass = password.getText().toString();

                if (isFormValid()) {
                    signInUser(email, pass);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public boolean isFormValid()
    {
        String frmEmail = username.getText().toString();
        String frmPassword = password.getText().toString();
        boolean isValid = true;

        if (TextUtils.isEmpty(frmEmail)) {
            username.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (!frmEmail.contains("@") || !frmEmail.contains(".") || frmEmail.length() < 5) {
            username.setError("Invalid email format");
            isValid = false;
        } else {
            username.setError(null);
        }

        if (TextUtils.isEmpty(frmPassword)) {
            password.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else {
            password.setError(null);
        }

        return isValid;
    }

    private void updateUI(FirebaseUser currentUser)
    {
        //String user = sharedPref.getString(getString(R.string.preference_user_key), null);

        if (currentUser != null /*&& user != null && !user.isEmpty()*/) {
            Intent intent = new Intent(this, DrawerActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            username.setText("");
            password.setText("");
        }
    }

    /*
    private void validateUser (String user, String pass) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_user_key), user);

        if (user.equals("coord")) {
            editor.putString(getString(R.string.preference_acc_key), getString(R.string.preference_acc_coord));
        } else {
            editor.putString(getString(R.string.preference_acc_key), getString(R.string.preference_acc_tech));
        }

        editor.commit();
        Intent intent = new Intent(this, DrawerActivity.class);
        startActivity(intent);
    }*/

    private void signInUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(ENRUTADOS_LOG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Log.w(ENRUTADOS_LOG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

}
