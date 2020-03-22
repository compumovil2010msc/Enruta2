package co.edu.javeriana.enrutados;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class PasswordRecoveryActivity extends AppCompatActivity {

    WebView webRecoverPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        webRecoverPassword = (WebView) findViewById(R.id.web_recover_password);
        webRecoverPassword.loadUrl("https://www.javeriana.edu.co/cambio/");
    }
}
