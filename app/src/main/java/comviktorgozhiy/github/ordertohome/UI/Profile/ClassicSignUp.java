package comviktorgozhiy.github.ordertohome.UI.Profile;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comviktorgozhiy.github.ordertohome.MainActivity;
import comviktorgozhiy.github.ordertohome.Models.ClientUser;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.Utils.FirebaseUtils;

public class ClassicSignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText edEmail, edPassword, edPasswordAgain;
    private Button btnSignUp;
    private TextView tvFailed;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_sign_up);
        setTitle(R.string.sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        auth = FirebaseAuth.getInstance();
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvFailed = findViewById(R.id.tvFailed);
        progressBar = findViewById(R.id.progressBar);
        edPasswordAgain = findViewById(R.id.edPasswordAgain);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        if (edPassword.getText().toString().equals(edPasswordAgain.getText().toString())) {
            if (!edEmail.getText().toString().isEmpty()) {
                setEnabledUI(false);
                progressBar.setVisibility(View.VISIBLE);
                tvFailed.setVisibility(View.GONE);
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    FirebaseUtils.createUser(new ClientUser(user.getUid()));
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    setEnabledUI(true);
                                    progressBar.setVisibility(View.GONE);
                                    tvFailed.setText(R.string.authentication_failed);
                                    tvFailed.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            } else { // email empty
                tvFailed.setText(R.string.empty_email);
                tvFailed.setVisibility(View.VISIBLE);
                setEnabledUI(true);
            }
        } else { // Passwords do not match
            tvFailed.setText("Passwords do not match!");
            tvFailed.setVisibility(View.VISIBLE);
            setEnabledUI(true);
        }
    }

    private void setEnabledUI(boolean flag) {
        edEmail.setEnabled(flag);
        edPassword.setEnabled(flag);
        btnSignUp.setEnabled(flag);
        edPasswordAgain.setEnabled(flag);

    }
}
