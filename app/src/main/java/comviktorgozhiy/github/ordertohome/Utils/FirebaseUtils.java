package comviktorgozhiy.github.ordertohome.Utils;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import comviktorgozhiy.github.ordertohome.Models.ClientUser;
import comviktorgozhiy.github.ordertohome.Models.Order;
import comviktorgozhiy.github.ordertohome.Models.Order.Item;
import comviktorgozhiy.github.ordertohome.Models.Product;

public class FirebaseUtils {


    public static void createUser(ClientUser clientUser) {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        fb.getReference("clients").child(clientUser.getUid()).setValue(clientUser);
    }

    public static void addProductToOrder(Product product, String uid, String categoryName, int count) {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        Order.Item item = new Order.Item(product.getTitle(), count, product.getPrice(), categoryName);
        fb.getReference("clients")
                .child(uid)
                .child("currentOrder")
                .child(product.getTitle())
                .setValue(item);
    }

    public static void addOneProductToOrder(final Product product, final String uid, final String categoryName) {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        Query query = fb.getReference("clients").child(uid).child("currentOrder");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasAlreadyItem = false;
                if (dataSnapshot.getValue() != null) { // If order is not empty
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order.Item item = snapshot.getValue(Order.Item.class);
                        if (item.getTitle().equals(product.getTitle())) { // If item already exists
                            addProductToOrder(product, uid, categoryName, item.getQuantity() + 1);
                            hasAlreadyItem = true;
                        }
                    }
                    if (!hasAlreadyItem) addProductToOrder(product, uid, categoryName, 1); // If no needed position in order
                } else {
                    addProductToOrder(product, uid, categoryName, 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void replaceItemInOrder(Order.Item item, String uid) {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        fb.getReference("clients")
                .child(uid)
                .child("currentOrder")
                .child(item.getTitle())
                .setValue(item);
    }

    public static void removeItemFromOrder(Order.Item item, String uid) {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        fb.getReference("clients")
                .child(uid)
                .child("currentOrder")
                .child(item.getTitle())
                .removeValue();
    }

    public static void clearCurrentOrder(String uid) {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        fb.getReference("clients").child(uid).child("currentOrder").removeValue();
    }
}
