package com.example.galaxytechstore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.galaxytechstore.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    private static final int Home_Fragment = 0;
    private TextView actionbar_name;
    private static final int Cart_Fragment = 1;
    private static final int MyOrders_Fragment = 2;
    private static final int MyRewards_Fragment = 4;
    private static final int MyWishlist_Fragment = 3;
    private static final int MyAccount_Fragment = 5;
    private int currentFragment = -1;
    public static boolean showcart = false;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Window window;
    private Dialog signInDialog;
    public static DrawerLayout drawer;
    private FirebaseUser currentUser;
    private TextView badge_count;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        actionbar_name = (TextView) findViewById(R.id.actionbar_name);
        setSupportActionBar(toolbar);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        if (showcart) {
            drawer.setDrawerLockMode(1);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            gotoFragment("My Cart", new MyCartFragment(), -2);
        } else {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new HomeFragment(), Home_Fragment);
        }

        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.setCanceledOnTouchOutside(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button sign_in_dialog = signInDialog.findViewById(R.id.sign_in_btn);
        Button sign_up_dialog = signInDialog.findViewById(R.id.sign_up_btn);
        Intent registerIntent = new Intent(MainActivity.this, Login_Register_ResetPasswordActivity.class);


        sign_in_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.diableCloseBtn = true;
                SignUpFragment.diableCloseBtn = true;
                signInDialog.dismiss();
                Login_Register_ResetPasswordActivity.setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        sign_up_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.diableCloseBtn = true;
                SignUpFragment.diableCloseBtn = true;
                signInDialog.dismiss();
                Login_Register_ResetPasswordActivity.setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void gotoFragment(String tt, Fragment fragment, int FragmentNo) {
        actionbar_name.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(tt);
        invalidateOptionsMenu();
        setFragment(fragment, FragmentNo);
        if (FragmentNo == Cart_Fragment) {
            navigationView.getMenu().getItem(3).setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentFragment == Home_Fragment) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            //
            MenuItem cartItem = menu.findItem(R.id.main_cart_ic);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badge_icon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badge_icon.setImageResource(R.drawable.cart_2);
            badge_count = cartItem.getActionView().findViewById(R.id.badge_count);
            //

            //
            MenuItem notificationItem = menu.findItem(R.id.main_notification_ic);
            notificationItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = notificationItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.notification);
            TextView notifyCount = notificationItem.getActionView().findViewById(R.id.badge_count);

            if (currentUser != null) {
                DBqueries.checkNotifications(false, notifyCount);
            }

            notificationItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                    }
                }
            });
            //

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                    }
                }
            });
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.main_search_ic:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;
            case R.id.main_notification_ic:
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                return true;
            case R.id.main_cart_ic:
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    gotoFragment("My Cart", new MyCartFragment(), Cart_Fragment);
                }
                return true;
            case android.R.id.home:
                if (showcart) {
                    showcart = false;
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setFragment(Fragment fragment, int FragmentNo) {
        if (FragmentNo != currentFragment) {
            if (FragmentNo == MyRewards_Fragment) {
                window.setStatusBarColor(Color.parseColor("#5b04b1"));
                toolbar.setBackgroundColor(Color.parseColor("#5b04b1"));
            }
            {
                window.setStatusBarColor(Color.parseColor("#81d4fa"));
                toolbar.setBackgroundColor(Color.parseColor("#81d4fa"));
            }
            currentFragment = FragmentNo;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.main_layout, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == Home_Fragment) {
                currentFragment = -1;
                super.onBackPressed();
            } else {
                if (showcart) {
                    showcart = false;
                    finish();
                } else {
                    actionbar_name.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), Home_Fragment);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (currentUser != null) {
            int id = item.getItemId();
            if (id == R.id.my_home) {
                actionbar_name.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                setFragment(new HomeFragment(), Home_Fragment);
            } else if (id == R.id.my_orders) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    gotoFragment("My Orders", new MyOrdersFragment(), MyOrders_Fragment);
                }
            } else if (id == R.id.my_rewards) {
                if(currentUser == null){
                    signInDialog.show();
                }
                else {
                    gotoFragment("My Rewards", new MyRewardsFragment(), MyRewards_Fragment);
                }
            } else if (id == R.id.my_carts) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    gotoFragment("My Cart", new MyCartFragment(), Cart_Fragment);
                }
            } else if (id == R.id.my_wishlist) {
                if(currentUser == null){
                    signInDialog.show();
                }
                else {
                    gotoFragment("My Wishlist", new MyWishlistFragment(), MyWishlist_Fragment);
                }
            } else if (id == R.id.my_account) {
                if(currentUser == null){
                    signInDialog.show();
                }
                else {
                    gotoFragment("My Account", new MyAccountFragment(), MyAccount_Fragment);
                }
            } else if (id == R.id.sign_out) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                Intent registerIntent = new Intent(MainActivity.this, Login_Register_ResetPasswordActivity.class);
                startActivity(registerIntent);
                finish();
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            drawer.closeDrawer(GravityCompat.START);
            signInDialog.show();
            return true;
        }
    }


}