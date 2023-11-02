package ru.mirea.smelkin.mireaproject;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import android.os.Bundle;

import ru.mirea.smelkin.mireaproject.databinding.ActivityMain2Binding;
import ru.mirea.smelkin.mireaproject.databinding.ActivityMainBinding;
import android.provider.Settings.Secure;
import android.os.Handler;



public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMain2Binding binding;
    // START declare_auth
    private FirebaseAuth mAuth;
    Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(binding.email.getText().toString(), binding.password.getText().toString());
            }
        });

        binding.signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        binding.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(binding.email.getText().toString(), binding.password.getText().toString());
            }
        });

        binding.verifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 200);
        }


        if(!checkRemote())
        {
            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            binding.textView9.setText(android_id);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
        else
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    finish();
                    System.exit(0);
                }
            }, 5000);

        }
    }


    private boolean checkRemote() {

        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

        for(int i=0;i < packs.size();i++) {
            PackageInfo p = packs.get(i);
            if(Objects.equals(p.packageName, "com.anydesk.anydeskandroid"))
            {
                DialogRemoteFind fragment = new DialogRemoteFind();
                fragment.show(getSupportFragmentManager(), "mirea");
                return true;
            }
        }
        return false;
    }

    // [END on_start_check_user]
    private void updateUI(FirebaseUser user) {
        if (user != null) {


            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);

//            binding.status.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
//
//            binding.detail.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            binding.create.setVisibility(View.GONE);
//            binding.email.setVisibility(View.GONE);
//            binding.password.setVisibility(View.GONE);
//            binding.signout.setVisibility(View.VISIBLE);
//            binding.signin.setVisibility(View.GONE);
//            binding.verifi.setVisibility(View.VISIBLE);
//            binding.verifi.setEnabled(!user.isEmailVerified());
        } else {
            binding.status.setText(R.string.signed_out);
            binding.detail.setText(null);
            binding.create.setVisibility(View.VISIBLE);
            binding.email.setVisibility(View.VISIBLE);
            binding.password.setVisibility(View.VISIBLE);
            binding.signin.setVisibility(View.VISIBLE);
            binding.signout.setVisibility(View.GONE);
            binding.verifi.setVisibility(View.GONE);
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        //тут конвертация в sha1

        String sha = this.encryptThisString(password);

        Log.d("sha_test", sha);

        mAuth.createUserWithEmailAndPassword(email, sha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity2.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public static String encryptThisString(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateForm() {
        if(binding.password.getText().toString().length() <6)
        {
            return false;
        }
        return !TextUtils.isEmpty(binding.email.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        String sha = this.encryptThisString(password);

        mAuth.signInWithEmailAndPassword(email, sha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                            startActivity(intent);
                        } else {

                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(MainActivity2.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        if (!task.isSuccessful()) {

                            binding.status.setText(R.string.auth_failed);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        binding.verifi.setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override

                    public void onComplete(@NonNull Task<Void> task) {

                        binding.verifi.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity2.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity2.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onContinueClicked() {
        handler.removeCallbacksAndMessages(null);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void onOkClicked() {
        finish();
        System.exit(0);
    }
}