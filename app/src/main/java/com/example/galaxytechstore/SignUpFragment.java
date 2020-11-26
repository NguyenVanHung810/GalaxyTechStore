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

import java.util.HashMap;
import java.util.Map;


public class SignUpFragment extends Fragment {


    private TextView AlreadyHaveAcc;
    private ProgressBar load;
    private EditText email, fullname, phonenumber, password, cfpassword;
    private ImageButton close;
    private Button signup;

    public SignUpFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        AlreadyHaveAcc = (TextView) view.findViewById(R.id.tv_have_account);
        load = (ProgressBar) view.findViewById(R.id.load);
        email = (EditText) view.findViewById(R.id.sign_in_email);
        fullname = (EditText) view.findViewById(R.id.sign_in_name);
        phonenumber = (EditText) view.findViewById(R.id.sign_in_name);
        password = (EditText) view.findViewById(R.id.sign_up_password);
        cfpassword = (EditText) view.findViewById(R.id.sign_up_confirm);

        close = (ImageButton) view.findViewById(R.id.btnclose);

        signup = (Button) view.findViewById(R.id.btn_sign_in);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        AlreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
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
        fullname.addTextChangedListener(new TextWatcher() {
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
        cfpassword.addTextChangedListener(new TextWatcher() {
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
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gửi dữ liệu người dùng mới đăng kí vô FireBase.
                checkEmailAndPassword();
            }
        });
    }

    private void checkEmailAndPassword() {
        // to do:
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_left);
        ft.replace(R.id.login_register_resetpassword_layout,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void checkInput() {
        // kiểm tra email có đang rỗng không
        if(!TextUtils.isEmpty(email.getText().toString())){
            if(!TextUtils.isEmpty(fullname.getText().toString())){
                if(!TextUtils.isEmpty(password.getText().toString()) && password.length() >= 8){
                    if(!TextUtils.isEmpty(cfpassword.getText().toString())){
                        signup.setEnabled(true);
                    }
                    else {
                        signup.setEnabled(false);
                    }
                }
                else {
                    signup.setEnabled(false);
                }
            }
            else {
                signup.setEnabled(false);
            }
        }
        else {
            signup.setEnabled(false);
        }
    }


    private void mainIntent(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}