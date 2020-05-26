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

import java.util.List;

import co.edu.javeriana.enrutados.model.Route;
import co.edu.javeriana.enrutados.model.User;
import co.edu.javeriana.enrutados.services.EnrutadosApi;
import co.edu.javeriana.enrutados.ui.home.HomeFragment;
import co.edu.javeriana.enrutados.ui.home.PointAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String ENRUTADOS_LOG = ">enrutados";
    private FirebaseAuth mAuth;

    EditText username, password;
    Button forgotPassword, login;
    SharedPreferences sharedPref;
    User currentApiUser;
    String firebaseEmail;
    private static final String ENRUTADOS_API_URL = "https://fast-forest-61371.herokuapp.com/";
    private static EnrutadosApi enrutadosApi;

    static {
        Retrofit restClient = new Retrofit.Builder()
                .baseUrl(ENRUTADOS_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        enrutadosApi = restClient.create(EnrutadosApi.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        currentApiUser = null;
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

    public boolean isFormValid() {
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

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null /*&& user != null && !user.isEmpty()*/) {
            firebaseEmail = currentUser.getEmail();
            fetchUserInfo();
        } else {
            username.setText("");
            password.setText("");
        }
    }


    private void saveEmailInSharedPreferences() {
        String email = username.getText().toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.preference_user_key), email);

        editor.apply();
    }

    private void fetchUserInfo() {
        enrutadosApi.getUsers().enqueue(new MainActivity.GetUsersHandler());
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(ENRUTADOS_LOG, "signInWithEmail:success");
                    saveEmailInSharedPreferences();
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

    private void launchIntent () {
        if (currentApiUser == null) {
            Toast.makeText(this, "Algo salió mal :(", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentApiUser.getRole().equals("COORDINATOR")) {
            Intent intent = new Intent(this, CoordinatorActivity.class);
            intent.putExtra("user", currentApiUser.getEmail());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DrawerActivity.class);
            intent.putExtra("user", currentApiUser.getEmail());
            startActivity(intent);
        }
    }

    class GetUsersHandler implements Callback<List<User>> {
        @Override
        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
            if (!response.isSuccessful()) {
                Log.d(ENRUTADOS_LOG, String.valueOf(response.code()));
                return;
            }

            List <User> users = response.body();
            currentApiUser = getUser(users);
            launchIntent();
        }

        @Override
        public void onFailure(Call<List<User>> call, Throwable t) {
            Log.d(ENRUTADOS_LOG, t.getMessage());
        }

        private User getUser(List <User> users) {
            for (User u: users) {
                if (u.getEmail().equals(firebaseEmail)) {
                    return u;
                }
            }
            return null;
        }
    }
}
