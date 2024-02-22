package io.xdiad.recognizetheapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EnterPhoneNumber extends AppCompatActivity {

    EditText EnterNumberET, Enter_NameET;
    Button SendOTP;
    ProgressBar progress_sendingOTP;

    RelativeLayout SignInGoogle;

    GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_phone_number_activity);

        EnterNumberET = findViewById(R.id.EnterNumberET);
        SendOTP = findViewById(R.id.SendOTP);
        progress_sendingOTP = findViewById(R.id.progress_sendingOTP);
        Enter_NameET = findViewById(R.id.Enter_NameET);
        SignInGoogle = findViewById(R.id.SignInGoogle);


        if (isUserDetailsSaved()) {
            navigateToHomeScreen();
        } else {

            SendOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = EnterNumberET.getText().toString().trim();
                    String User_Name = Enter_NameET.getText().toString().trim();

                    // Check if the phone number field is empty
                    if (phoneNumber.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter a phone number", Toast.LENGTH_SHORT).show();
                    }
                    // Check if the phone number is less than 10 digits
                    else if (phoneNumber.length() < 10) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                    }
                    // If the input is a valid 10-digit number, proceed with navigating to VerifyOTP activity
                    else {

                        progress_sendingOTP.setVisibility(View.VISIBLE);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + phoneNumber,
                                60,
                                TimeUnit.SECONDS,
                                EnterPhoneNumber.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                        progress_sendingOTP.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progress_sendingOTP.setVisibility(View.GONE);
                                        Toast.makeText(EnterPhoneNumber.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String backendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        //super.onCodeSent(s, forceResendingToken);
                                        progress_sendingOTP.setVisibility(View.GONE);
                                        Intent intent = new Intent(EnterPhoneNumber.this, VerifyOtp.class);
                                        //Optionally, pass the phone number to the VerifyOTP activity
                                        intent.putExtra("User_Name", User_Name);
                                        intent.putExtra("phoneNumber", phoneNumber);
                                        intent.putExtra("backendOtp", backendOtp);

                                        startActivity(intent);
                                    }
                                }
                        );


                    }
                }
            });


        }


        //Google sign in code start

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this,options);

        SignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = client.getSignInIntent();
                startActivityForResult(i,1234);
            }
        });

        //Google sign in code end




    }


    //google sign in code start

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(getApplicationContext(),HomeScreen.class);
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(EnterPhoneNumber.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null){
            Intent intent = new Intent(this,HomeScreen.class);
            startActivity(intent);
        }
    }


    //google sign in code end



    private boolean isUserDetailsSaved() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppNamePrefs", MODE_PRIVATE);
        // Check if the user phone number exists or any other flag indicating the user has completed the verification
        return sharedPreferences.contains("userPhoneNumber");
    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
        finish(); // Close the current activity
    }


}