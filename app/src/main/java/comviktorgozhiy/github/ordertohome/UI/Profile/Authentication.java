package comviktorgozhiy.github.ordertohome.UI.Profile;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import comviktorgozhiy.github.ordertohome.R;


public class Authentication extends Fragment implements View.OnClickListener {

    private TextView tvTerms, tvLogin;
    private Button btnPhone;
    private ImageView ivFacebook, ivTwitter, ivGoogle;
    public static final int AUTH_OK = 1;
    private FirebaseAuth auth;
    private FirebaseAuthSettings authSettings;
    private String phoneNum = "+380111111111";
    private String testVerifCode = "123456";
    private AlertDialog alertDialog;
    private ProgressBar progressBar;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_authentication, container, false);
        getActivity().setTitle(R.string.profile);
        tvTerms = v.findViewById(R.id.tvTerms);
        tvLogin = v.findViewById(R.id.tvLogin);
        btnPhone = v.findViewById(R.id.btnPhone);
        ivFacebook = v.findViewById(R.id.ivFacebook);
        ivTwitter = v.findViewById(R.id.ivTwitter);
        ivGoogle = v.findViewById(R.id.ivGoogle);
        progressBar = v.findViewById(R.id.progressBar);
        tvTerms.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        ivFacebook.setOnClickListener(this);
        ivTwitter.setOnClickListener(this);
        ivGoogle.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        auth.useAppLanguage();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        return v;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tvTerms:
                showTermsOfUse();
                break;
            case R.id.tvLogin:
                if (classicLogin() == AUTH_OK) showProfile(null);
                break;
            case R.id.btnPhone:
                showPhoneNumDialog();
                break;
            case R.id.ivFacebook:
                if (facebookLogin() == AUTH_OK) showProfile(null);
                break;
            case R.id.ivTwitter:
                if (twitterLogin() == AUTH_OK) showProfile(null);
                break;
            case R.id.ivGoogle:
                googleLogin();
                break;
        }
    }

    private void showTermsOfUse() {

    }

    private int classicLogin() {
        return 0;
    }

    private void phoneLogin() {
        progressBar.setVisibility(View.VISIBLE);
        if (alertDialog != null) alertDialog.dismiss(); // TODO Call when phone auth successful only
    }

    private int facebookLogin() {
        return 0;
    }

    private int twitterLogin() {
        return 0;
    }

    private void googleLogin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("GOOGLE SIGN IN", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("GOOGLE SIGN IN", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("GOOGLE SIGN IN", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            showProfile(user);
                        } else {
                            Log.d("GOOGLE SIGN IN", "signInWithCredential:failure");
                            Snackbar.make(null, "AuthFailed", Snackbar.LENGTH_SHORT).show();
                            showProfile(null);
                        }
                    }
                });
    }

    private void showProfile(FirebaseUser user) {
        Toast.makeText(getActivity(), "USer: " + user.getDisplayName() + " - " + user.getUid(), Toast.LENGTH_SHORT).show();
    }
    private void showPhoneNumDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialog = inflater.inflate(R.layout.phone_input_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialog);
        final EditText edPhoneNum = dialog.findViewById(R.id.edPhoneNum);
        final TextView tvInvalidNum = dialog.findViewById(R.id.tvInvalidNum);
        edPhoneNum.setSelection(edPhoneNum.getText().length());
        builder.setCancelable(false)
                .setPositiveButton(R.string.send_code, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edPhoneNum.getText().length() != 13) tvInvalidNum.setVisibility(View.VISIBLE);
                else {
                    view.setEnabled(false);
                    phoneNum = edPhoneNum.getText().toString();
                    phoneLogin();
                }
            }
        });
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPriceText));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPriceText));
    }
}
