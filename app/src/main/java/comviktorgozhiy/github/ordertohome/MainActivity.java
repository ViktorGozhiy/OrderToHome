package comviktorgozhiy.github.ordertohome;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import comviktorgozhiy.github.ordertohome.UI.MainMenu;
import comviktorgozhiy.github.ordertohome.UI.MyOrder;
import comviktorgozhiy.github.ordertohome.UI.Profile.Authentication;
import comviktorgozhiy.github.ordertohome.UI.Profile.Profile;
import comviktorgozhiy.github.ordertohome.UI.PromoList;
import comviktorgozhiy.github.ordertohome.Utils.UserUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvCurrentUser;
    public static final int LOGIN_REQUEST_CODE = 150;
    public static final int ORDER_REQEST_CODE = 160;
    private boolean showSplash = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_menu);
        View headerView = navigationView.getHeaderView(0);
        tvCurrentUser = headerView.findViewById(R.id.tvCurrentUser);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    tvCurrentUser.setText(firebaseAuth.getCurrentUser().getDisplayName());
                } else {
                    tvCurrentUser.setText("Anonymous"); // TODO FOR DEBUG ONLY
                }
            }
        });
        if (auth.getCurrentUser() == null) {
            String uid = UserUtils.getNewUserId(this);
            UserUtils.addNewClientIfNotExist(uid);
        }
        this.getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment current = getCurrentFragment();
                if (current instanceof MainMenu) navigationView.setCheckedItem(R.id.nav_menu);
                if (current instanceof PromoList) navigationView.setCheckedItem(R.id.nav_promotions);
                if (current instanceof MyOrder) navigationView.setCheckedItem(R.id.nav_order);
                if (current instanceof Authentication || current instanceof Profile) navigationView.setCheckedItem(R.id.nav_profile);
                //if (current instanceof History) navigationView.setCheckedItem(R.id.nav_menu);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putBoolean("showSplash", showSplash);
        MainMenu mainMenu = new MainMenu();
        mainMenu.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.container_main, mainMenu).commit();
        showSplash = false;
    }

    public Fragment getCurrentFragment() {
        return this.getFragmentManager().findFragmentById(R.id.container_main);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_menu:
                getFragmentManager().beginTransaction().replace(R.id.container_main, new MainMenu()).commit();
               // item.setChecked(true);
                break;
            case R.id.nav_promotions:
                getFragmentManager().beginTransaction().replace(R.id.container_main, new PromoList()).commit();
               // item.setChecked(true);
                break;
            case R.id.nav_help:
                debug();
                break;
            case R.id.nav_profile:
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    getFragmentManager().beginTransaction().replace(R.id.container_main, new Authentication()).commit();
                } else {
                    getFragmentManager().beginTransaction().replace(R.id.container_main, new Profile()).commit();
                }
                break;
            case R.id.nav_order:
                getFragmentManager().beginTransaction().replace(R.id.container_main, new MyOrder()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void debug() { //TODO Will be removed after debugging
      FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "LogOut", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
