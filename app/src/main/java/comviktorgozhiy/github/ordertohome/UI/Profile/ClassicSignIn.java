package comviktorgozhiy.github.ordertohome.UI.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import comviktorgozhiy.github.ordertohome.MainActivity;
import comviktorgozhiy.github.ordertohome.R;

public class ClassicSignIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private EditText edEmail, edPassword;
    private Button btnSignIn, btnSignUp;
    private TextView tvForgotPass, tvFailed;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_sign_in);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(R.string.classic_sign_in);
        auth = FirebaseAuth.getInstance();
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvFailed = findViewById(R.id.tvFailed);
        progressBar = findViewById(R.id.progressBar);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void signIn() {
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            setEnabledUI(false);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                tvFailed.setVisibility(View.VISIBLE);
                                setEnabledUI(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void signUp() {
        Intent intent = new Intent(this, ClassicSignUp.class);
        startActivityForResult(intent, MainActivity.LOGIN_REQUEST_CODE);
    }

    private void setEnabledUI(boolean flag) {
        edEmail.setEnabled(flag);
        edPassword.setEnabled(flag);
        btnSignUp.setEnabled(flag);
        btnSignIn.setEnabled(flag);
        tvForgotPass.setEnabled(flag);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSignIn) signIn();
        if (id == R.id.btnSignUp) signUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
