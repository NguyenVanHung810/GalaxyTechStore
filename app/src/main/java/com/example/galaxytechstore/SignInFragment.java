package com.example.galaxytechstore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignInFragment extends Fragment {

    private TextView noAccount, forgotpassword;
    private EditText email, password;
    private Button signin;
    private ProgressBar load;
    private ImageButton close;

    private FirebaseAuth firebaseAuth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static boolean diableCloseBtn = false;

    public SignInFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        noAccount = (TextView) view.findViewById(R.id.tv_have_account);
        forgotpassword = (TextView) view.findViewById(R.id.sign_in_forgotpw);
        email = (EditText) view.findViewById(R.id.sign_in_email);
        password = (EditText) view.findViewById(R.id.sign_in_pw);
        signin = (Button) view.findViewById(R.id.btn_sign_in);
        load = (ProgressBar) view.findViewById(R.id.load_sign_in);
        close = (ImageButton) view.findViewById(R.id.btnclose);
        firebaseAuth = FirebaseAuth.getInstance();

        if (diableCloseBtn) {
            close.setVisibility(View.GONE);
        } else {
            close.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new ResetPasswordFragment());
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void checkEmailAndPassword() {
        if (email.getText().toString().matches(emailPattern)) {
            if (password.length() >= 8) {
                load.setVisibility(View.VISIBLE);
                signin.setEnabled(false);
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mainIntent();
                        } else {
                            load.setVisibility(View.INVISIBLE);
                            signin.setEnabled(false);
                            toastInfo(task.getException().getMessage());
                        }
                    }
                });
            } else {
                toastInfo("Incorrect Email of Password !!!");
            }
        } else {
            toastInfo("Incorrect Email of Password !!!");
        }
    }

    private void checkInput() {
        if (!TextUtils.isEmpty(email.getText().toString())) {
            if (!TextUtils.isEmpty(password.getText().toString())) {
                signin.setEnabled(true);
            } else {
                signin.setEnabled(false);
            }
        } else {
            signin.setEnabled(false);
        }
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_right);
        fragmentTransaction.replace(R.id.login_register_resetpassword_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void toastInfo(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
    }

    private void mainIntent() {
        if (diableCloseBtn) {
            diableCloseBtn = false;
        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
        getActivity().finish();
    }
}