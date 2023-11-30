package ru.mirea.smelkin.mireaproject;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
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

import java.io.File;
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
    private static final int REQUEST_READ_PHONE_STATE = 200;
    private ActivityMain2Binding binding;
    // START declare_auth
    private FirebaseAuth mAuth;
    Handler handler = new Handler();

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final double CENTER_LATITUDE = 37.7749;  // Широта центра радиуса
    private static final double CENTER_LONGITUDE = -122.4194;  // Долгота центра радиуса
    private static final double RADIUS_METERS = 1000;  // Радиус в метрах
    private LocationManager locationManager;
    private boolean locationChecked = false;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.AuthenticationCallback authenticationCallback;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.fng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFingerprintAuthAvailable()) {
                    startFingerprintAuth();
                } else {
                    Toast.makeText(MainActivity2.this, "Fingerprint authentication is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(MainActivity2.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(MainActivity2.this, "Authentication succeeded!", Toast.LENGTH_SHORT).show();

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    updateUI(currentUser);


                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(MainActivity2.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            };
        }

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

    private boolean isFingerprintAuthAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricManager biometricManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                biometricManager = getSystemService(BiometricManager.class);
            }
            if (biometricManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
                }
            }
        } else {
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(this);
            return fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }

    private void startFingerprintAuth() {
        CancellationSignal cancellationSignal = new CancellationSignal();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPrompt = new BiometricPrompt.Builder(this)
                    .setTitle("Fingerprint authentication")
                    .setSubtitle("Place your finger on the fingerprint sensor")
                    .setDescription("Touch the fingerprint sensor to verify your identity.")
                    .setNegativeButton("Cancel", getMainExecutor(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity2.this, "Authentication cancelled", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            biometricPrompt.authenticate(cancellationSignal, getMainExecutor(), authenticationCallback);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 200);
        }

        ConnectionChecker connectionChecker = new ConnectionChecker("10.66.66.1", 4013, new ConnectionChecker.ConnectionListener() {
            @Override
            public void onConnectionChecked(boolean isConnected) {
                if (isConnected) {
                    // Подключение успешно
                    System.out.println("Подключено к серверу");
                } else {
                    // Не удалось подключиться
                    System.out.println("Не удалось подключиться к серверу");
                }
            }
        });
        connectionChecker.execute();



        if (!isDeviceRooted()) {
            if (!checkRemote()) {
                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                binding.textView9.setText(android_id);

//                FirebaseUser currentUser = mAuth.getCurrentUser();
//                updateUI(currentUser);
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        finish();
                        System.exit(0);
                    }
                }, 5000);

            }
        } else {
            Toast.makeText(this, "есть root права. завершение через 5 сек.", Toast.LENGTH_SHORT).show();

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


    public boolean isDeviceRooted() {
        String suPath = "/system/bin/su";
        String suPathX = "/system/xbin/su";

        if (new File(suPath).exists() || new File(suPathX).exists()) {
            return true;
        } else {
            return false;
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

        //тут конвертация в sha2

        sha2 sha = new sha2(password);

        mAuth.createUserWithEmailAndPassword(email, sha.finalhash)
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



    private boolean validateForm() {
        if(binding.password.getText().toString().length() <6)
        {
            return false;
        }
        return !TextUtils.isEmpty(binding.email.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        //тут конвертация в sha2
        sha2 sha = new sha2(password);

        mAuth.signInWithEmailAndPassword(email, sha.finalhash)
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