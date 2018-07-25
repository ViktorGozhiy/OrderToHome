package comviktorgozhiy.github.ordertohome.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import comviktorgozhiy.github.ordertohome.Models.ClientUser;

import static android.content.Context.MODE_PRIVATE;

public class UserUtils {

    public static String getUid(Context context, FirebaseUser user) {
        String uid;
        if (user != null) uid = user.getUid();
        else {
            SharedPreferences sPref = context.getSharedPreferences("user", MODE_PRIVATE);
            uid = sPref.getString("uid", "");
        }
        return uid;
    }

    public static String getNewUserId(Context context) {
        SharedPreferences sPref = context.getSharedPreferences("user", MODE_PRIVATE);
        String uid = sPref.getString("uid", "");
        if (uid.equals("")) {
            uid = getRandomUid();
            SharedPreferences.Editor editor = sPref.edit();
            editor.putString("uid", uid);
            editor.apply();
        }
        return uid;
    }

    private static String getRandomUid() {
        String uid = "anonymous-";
        Random rnd = new Random();
        uid = uid + Integer.toString(rnd.nextInt(1000000000) + 1000000000);
        return uid;
    }

    public static void addNewClientIfNotExist(final String uid) {
        Query query = FirebaseDatabase.getInstance().getReference("clients").child(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) addNewClient(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void addNewClient(String uid) {
        ClientUser clientUser = new ClientUser(uid);
        FirebaseDatabase.getInstance().getReference("clients").child(uid).setValue(clientUser);
    }
}
