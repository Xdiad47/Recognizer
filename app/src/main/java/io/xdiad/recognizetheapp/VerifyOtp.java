package io.xdiad.recognizetheapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyOtp extends AppCompatActivity {

    TextView textmobileshownumber,ResendOTP_TV;
    EditText editTextOTP1;
    EditText editTextOTP2;
    EditText editTextOTP3;
    EditText editTextOTP4;
    EditText editTextOTP5;
    EditText editTextOTP6;
    Button Verify_OTP;

    String getOtpBackend;

    ProgressBar progress_sendingOTP;

    FirebaseDatabase db;
    DatabaseReference reference;

    String phoneNumber, userName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp_activity);

        /*textmobileshownumber = findViewById(R.id.textmobileshownumber);
        ResendOTP_TV = findViewById(R.id.ResendOTP_TV);
        editTextOTP1 = findViewById(R.id.editTextOTP1);
        editTextOTP2 = findViewById(R.id.editTextOTP2);
        editTextOTP3 = findViewById(R.id.editTextOTP3);
        editTextOTP4 = findViewById(R.id.editTextOTP4);
        editTextOTP5 = findViewById(R.id.editTextOTP5);
        editTextOTP6 = findViewById(R.id.editTextOTP6);
        Verify_OTP = findViewById(R.id.Verify_OTP);
        progress_sendingOTP = findViewById(R.id.progress_sendingOTP);

        String PhoneNumber = getIntent().getStringExtra("phoneNumber");
        String User_Name = getIntent().getStringExtra("User_Name");

        textmobileshownumber.setText(String.format(
                "+91-%s", PhoneNumber
        ));

        getOtpBackend = getIntent().getStringExtra("backendOtp");



        Contacts_ contacts_ = new Contacts_(User_Name,PhoneNumber);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Contacts");
        reference.child(User_Name).setValue(contacts_).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(VerifyOtp.this, "Data Inserted", Toast.LENGTH_SHORT).show();
            }
        });


        Verify_OTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the text from each EditText
                String otp1 = editTextOTP1.getText().toString().trim();
                String otp2 = editTextOTP2.getText().toString().trim();
                String otp3 = editTextOTP3.getText().toString().trim();
                String otp4 = editTextOTP4.getText().toString().trim();
                String otp5 = editTextOTP5.getText().toString().trim();
                String otp6 = editTextOTP6.getText().toString().trim();

                // Check if any of the EditTexts is empty
                if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // If all fields are filled, proceed with your logic here
                    // For example, you might want to concatenate the OTP and verify it

                    String fullOTP = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
                    // Now you can use fullOTP for your verification logic

                    // Example: Navigate to another activity after verification
                    // Intent intent = new Intent(getApplicationContext(), YourNextActivity.class);
                    // startActivity(intent);

                    if (getOtpBackend!=null){

                        progress_sendingOTP.setVisibility(View.VISIBLE);

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                getOtpBackend,fullOTP
                        );
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progress_sendingOTP.setVisibility(View.GONE);

                                        if (task.isSuccessful()){
                                            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }else {
                                            Toast.makeText(VerifyOtp.this, "Enter correct OTP", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                    }else {
                        Toast.makeText(VerifyOtp.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    
                    Toast.makeText(getApplicationContext(), "OTP Verified", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupOtpInputs();

        ResendOTP_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("phoneNumber"),
                        60,
                        TimeUnit.SECONDS,
                        VerifyOtp.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                //progress_sendingOTP.setVisibility(View.GONE);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                //progress_sendingOTP.setVisibility(View.GONE);
                                Toast.makeText(VerifyOtp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String NewBackendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                //super.onCodeSent(s, forceResendingToken);

                                getOtpBackend = NewBackendOtp;
                                Toast.makeText(VerifyOtp.this, "Otp Sended Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                );

            }
        });*/


        initializeViews();

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        userName = getIntent().getStringExtra("User_Name");
        getOtpBackend = getIntent().getStringExtra("backendOtp");

        textmobileshownumber.setText(String.format("+91-%s", phoneNumber));

        Verify_OTP.setOnClickListener(v -> verifyOtp());

        setupOtpInputs();

        ResendOTP_TV.setOnClickListener(v -> resendOtp());


    }


    private void initializeViews() {
        textmobileshownumber = findViewById(R.id.textmobileshownumber);
        ResendOTP_TV = findViewById(R.id.ResendOTP_TV);
        editTextOTP1 = findViewById(R.id.editTextOTP1);
        editTextOTP2 = findViewById(R.id.editTextOTP2);
        editTextOTP3 = findViewById(R.id.editTextOTP3);
        editTextOTP4 = findViewById(R.id.editTextOTP4);
        editTextOTP5 = findViewById(R.id.editTextOTP5);
        editTextOTP6 = findViewById(R.id.editTextOTP6);
        Verify_OTP = findViewById(R.id.Verify_OTP);
        progress_sendingOTP = findViewById(R.id.progress_sendingOTP);
    }

    private void verifyOtp() {
        // Concatenate OTP parts
        String fullOTP = editTextOTP1.getText().toString().trim() +
                editTextOTP2.getText().toString().trim() +
                editTextOTP3.getText().toString().trim() +
                editTextOTP4.getText().toString().trim() +
                editTextOTP5.getText().toString().trim() +
                editTextOTP6.getText().toString().trim();

        // Retrieve the text from each EditText
        String otp1 = editTextOTP1.getText().toString().trim();
        String otp2 = editTextOTP2.getText().toString().trim();
        String otp3 = editTextOTP3.getText().toString().trim();
        String otp4 = editTextOTP4.getText().toString().trim();
        String otp5 = editTextOTP5.getText().toString().trim();
        String otp6 = editTextOTP6.getText().toString().trim();

        if (fullOTP.isEmpty() || fullOTP.length() < 6) {
            Toast.makeText(getApplicationContext(), "Please enter the valid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform OTP verification here...
        // For demonstration, we'll assume it's successful and proceed to store user data
        // Check if any of the EditTexts is empty
        if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            // If all fields are filled, proceed with your logic here
            // For example, you might want to concatenate the OTP and verify it

            //String fullOTP = otp1 + otp2 + otp3 + otp4 + otp5 + otp6;
            // Now you can use fullOTP for your verification logic

            // Example: Navigate to another activity after verification
            // Intent intent = new Intent(getApplicationContext(), YourNextActivity.class);
            // startActivity(intent);

            if (getOtpBackend!=null){

                progress_sendingOTP.setVisibility(View.VISIBLE);

                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        getOtpBackend,fullOTP
                );
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progress_sendingOTP.setVisibility(View.GONE);

                                if (task.isSuccessful()){
                                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(VerifyOtp.this, "Enter correct OTP", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }else {
                Toast.makeText(VerifyOtp.this, "Please check Internet Connection", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getApplicationContext(), "OTP Verified", Toast.LENGTH_SHORT).show();
        }



        saveUserData(userName, phoneNumber);
    }

    private void saveUserData(String userName, String phoneNumber) {
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Contacts");

        Map<String, Object> userData = new HashMap<>();
        userData.put("Name", userName);
        userData.put("phone_number", phoneNumber);

        reference.child(phoneNumber).setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(VerifyOtp.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                saveUserPhoneNumber(phoneNumber); // Save phone number to SharedPreferences
                navigateToHomeScreen();
            } else {
                Toast.makeText(VerifyOtp.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserPhoneNumber(String phoneNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppNamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userPhoneNumber", phoneNumber);
        editor.apply();
    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void resendOtp() {
        // Implement OTP resend logic

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + getIntent().getStringExtra("phoneNumber"),
                60,
                TimeUnit.SECONDS,
                VerifyOtp.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        //progress_sendingOTP.setVisibility(View.GONE);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        //progress_sendingOTP.setVisibility(View.GONE);
                        Toast.makeText(VerifyOtp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String NewBackendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        //super.onCodeSent(s, forceResendingToken);

                        getOtpBackend = NewBackendOtp;
                        Toast.makeText(VerifyOtp.this, "Otp Sended Successfully", Toast.LENGTH_SHORT).show();

                    }
                }
        );

    }



    private void setupOtpInputs() {
        // Assuming you have already initialized editTextOTP1 to editTextOTP6
        EditText[] otpInputs = new EditText[]{editTextOTP1, editTextOTP2, editTextOTP3, editTextOTP4, editTextOTP5, editTextOTP6};

        for(int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not used here
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Check if the current EditText has a digit and move focus to next EditText
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Optionally, move focus back if user deletes the digit
                    if (s.length() == 0 && index > 0) {
                        otpInputs[index - 1].requestFocus();
                    }
                }
            });
        }
    }

}