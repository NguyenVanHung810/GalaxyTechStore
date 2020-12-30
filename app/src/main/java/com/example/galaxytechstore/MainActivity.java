package com.example.galaxytechstore;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.galaxytechstore.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout frameLayout;
    public static Activity mainActivity;
    public static boolean resetMainActivity = false;
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
    private int scrollFlags;
    private AppBarLayout.LayoutParams layoutParams;
    private CircularImageView addProfileIcon;
    private CircleImageView profileView;
    private TextView fullname, email;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {
            if (DBqueries.email == null) {
                FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DBqueries.fullname = task.getResult().getString("name");
                            DBqueries.email = task.getResult().getString("email");
                            DBqueries.phone = task.getResult().getString("phonenumber");
                            DBqueries.profile = task.getResult().getString("profile");

                            fullname.setText(DBqueries.fullname);
                            email.setText(DBqueries.email);
                            if (DBqueries.profile.equals("")) {
                                addProfileIcon.setVisibility(View.VISIBLE);
                            } else {
                                addProfileIcon.setVisibility(View.INVISIBLE);
                                Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.user)).into(profileView);
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                fullname.setText(DBqueries.fullname);
                email.setText(DBqueries.email);
                if (DBqueries.profile.equals("")) {
                    profileView.setImageResource(R.drawable.placeholder);
                    addProfileIcon.setVisibility(View.VISIBLE);
                } else {
                    addProfileIcon.setVisibility(View.INVISIBLE);
                    Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.user)).into(profileView);
                }
            }
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }

        if (resetMainActivity) {
            resetMainActivity = false;
            actionbar_name.setVisibility(View.VISIBLE);
            navigationView.getMenu().getItem(0).setChecked(true);
            setFragment(new HomeFragment(), Home_Fragment);
        }
        invalidateOptionsMenu();
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

        layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = layoutParams.getScrollFlags();


        drawer = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        fullname = navigationView.getHeaderView(0).findViewById(R.id.main_name);
        email = navigationView.getHeaderView(0).findViewById(R.id.main_email);
        addProfileIcon = navigationView.getHeaderView(0).findViewById(R.id.add_profile_image_icon);
        profileView = navigationView.getHeaderView(0).findViewById(R.id.main_profile_pic);

        if (showcart) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button sign_in_dialog = signInDialog.findViewById(R.id.sign_in_btn);
        Button sign_up_dialog = signInDialog.findViewById(R.id.sign_up_btn);

        sign_in_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.diableCloseBtn = true;
                SignUpFragment.diableCloseBtn = true;
                signInDialog.dismiss();
                Login_Register_ResetPasswordActivity.setSignUpFragment = false;
                startActivity(new Intent(MainActivity.this, Login_Register_ResetPasswordActivity.class));
            }
        });

        sign_up_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.diableCloseBtn = true;
                SignUpFragment.diableCloseBtn = true;
                signInDialog.dismiss();
                Login_Register_ResetPasswordActivity.setSignUpFragment = true;
                startActivity(new Intent(MainActivity.this, Login_Register_ResetPasswordActivity.class));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void gotoFragment(String tt, Fragment fragment, int FragmentNo) {
        actionbar_name.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(tt);
        invalidateOptionsMenu();
        setFragment(fragment, FragmentNo);
        if (FragmentNo == Cart_Fragment || showcart) {
            navigationView.getMenu().getItem(3).setChecked(true);
            layoutParams.setScrollFlags(0);
        }
        else {
            layoutParams.setScrollFlags(scrollFlags);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentFragment == Home_Fragment) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem cartItem = menu.findItem(R.id.main_cart_ic);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badge_icon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badge_icon.setImageResource(R.drawable.cart_2);
            badge_count = cartItem.getActionView().findViewById(R.id.badge_count);

            if (currentUser != null) {
                if (DBqueries.cartLists.size() == 0) {
                    DBqueries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false, badge_count, new TextView(MainActivity.this));
                } else {
                    badge_count.setVisibility(View.VISIBLE);
                    if (DBqueries.cartLists.size() < 99) {
                        badge_count.setText(String.valueOf(DBqueries.cartLists.size()));
                    } else {
                        badge_count.setText("99");
                    }
                }

            }

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

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        gotoFragment("My Cart", new MyCartFragment(), Cart_Fragment);
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
                    mainActivity = null;
                    showcart = false;
                    gotoFragment("My Cart", new MyCartFragment(), Cart_Fragment);
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
            else {
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
                    mainActivity = null;
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

    MenuItem menuItem;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawers();
        menuItem = item;
        if (currentUser != null) {
            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    int id = item.getItemId();
                    if (id == R.id.my_home) {
                        actionbar_name.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                        setFragment(new HomeFragment(), Home_Fragment);
                    } else if (id == R.id.my_orders) {
                        gotoFragment("Đơn hàng của tôi", new MyOrdersFragment(), MyOrders_Fragment);
                    } else if (id == R.id.my_rewards) {
                        gotoFragment("Phần thưởng của tôi", new MyRewardsFragment(), MyRewards_Fragment);
                    } else if (id == R.id.my_carts) {
                        gotoFragment("Giỏ hàng", new MyCartFragment(), Cart_Fragment);
                    } else if (id == R.id.my_wishlist) {
                        gotoFragment("Sản phẩm yêu thích", new MyWishlistFragment(), MyWishlist_Fragment);
                    } else if (id == R.id.my_account) {
                        gotoFragment("Tài khoản cá nhân", new MyAccountFragment(), MyAccount_Fragment);
                    } else if (id == R.id.sign_out) {
                        FirebaseAuth.getInstance().signOut();
                        DBqueries.clearData();
                        DBqueries.email = null;
                        Intent registerIntent = new Intent(MainActivity.this, Login_Register_ResetPasswordActivity.class);
                        startActivity(registerIntent);
                        finish();
                    }
                    drawer.removeDrawerListener(this);
                }
            });
            return true;
        } else {
            signInDialog.show();
            return true;
        }
    }


}