package comviktorgozhiy.github.ordertohome.UI.Profile;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import comviktorgozhiy.github.ordertohome.Models.ClientUser;
import comviktorgozhiy.github.ordertohome.R;
import comviktorgozhiy.github.ordertohome.UI.MainMenu;


public class Profile extends Fragment implements View.OnClickListener {

    private FirebaseAuth auth;
    private TextView tvBonus, tvHowTo;
    private Button btnEdit, btnSignOut;
    private FirebaseDatabase fb;
    private ClientUser clientUser;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle(R.string.profile);
        tvBonus = v.findViewById(R.id.tvBonus);
        tvHowTo = v.findViewById(R.id.tvHowTo);
        btnEdit = v.findViewById(R.id.btnEdit);
        btnSignOut = v.findViewById(R.id.btnSignOut);
        progressBar = v.findViewById(R.id.progressBar);
        btnSignOut.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        fb = FirebaseDatabase.getInstance();
        setEnabledUI(false);
        getInfo();
        return v;
    }

    private void getInfo() {
        Query query = fb.getReference("clients").child(auth.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clientUser = dataSnapshot.getValue(ClientUser.class);
                if (clientUser == null) {
                 fb.getReference("clients").child(auth.getUid()).setValue(new ClientUser(auth.getUid()));
                 getInfo();
                } else {
                    setupUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupUI() {
        setEnabledUI(true);
        progressBar.setVisibility(View.GONE);
        tvBonus.setText(clientUser.getBonus() + " " + getResources().getString(R.string.bonuses));
    }

    private void signOut() {
        auth.signOut();
        getActivity().getFragmentManager().beginTransaction().replace(R.id.container_main, new Authentication()).commit();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSignOut:
                signOut();
                break;
        }
    }

    private void setEnabledUI(boolean flag) {
        btnSignOut.setEnabled(flag);
        btnEdit.setEnabled(flag);
    }
}
