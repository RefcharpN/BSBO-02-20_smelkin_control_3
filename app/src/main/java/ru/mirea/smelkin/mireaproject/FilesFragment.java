package ru.mirea.smelkin.mireaproject;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ru.mirea.smelkin.mireaproject.databinding.FragmentFilesBinding;
import ru.mirea.smelkin.mireaproject.databinding.FragmentProfileBinding;


public class FilesFragment extends Fragment {

    private FragmentFilesBinding binding;
    private SecretKey key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentFilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialogFragment fragment = new MyDialogFragment();
                fragment.show(getChildFragmentManager(), "mirea");
            }
        });


        return root;
    }



    public void onOpenClicked() {
        if(isExternalStorageReadable())
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readFileFromExternalStorage();
                }
            }).start();
        }
        else
        {
            Toast.makeText(getContext(),"нет разрешения", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getContext(),"завершено", Toast.LENGTH_SHORT).show();
    }

    public void onSaveClicked() {
        if(isExternalStorageWritable()) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    writeFileToExternalStorage();
                }
            }).start();
        }
        else {
            Toast.makeText(getContext(),"нет разрешения", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getContext(), "завершено", Toast.LENGTH_SHORT).show();
    }

    public void readFileFromExternalStorage() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, String.format("%s.txt",binding.name.getText().toString()));
        try {
            FileInputStream fileInputStream = new FileInputStream(file.getAbsoluteFile());
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);

            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            String text = "";

            while (line != null) {
                text = text + line;
                line = reader.readLine();
            }

            SecretKey secretKey=generateKey("any data used as random seed1111");
            String strResult=decryptMsg(text,secretKey);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.text.setText(strResult);
                }
            });

        } catch (Exception e) {
            Log.w("ExternalStorage", String.format("Read from file %s failed", e.getMessage()));
        }
    }

    public void writeFileToExternalStorage() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, String.format("%s.txt",binding.name.getText().toString()));
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsoluteFile());
            OutputStreamWriter output = new OutputStreamWriter(fileOutputStream);

            SecretKey secretKey=generateKey("any data used as random seed1111");
            String strResult=encryptMsg(binding.text.getText().toString(),secretKey);

            output.write(strResult);
            output.close();

        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return Base64.encodeToString(cipherText, Base64.NO_WRAP);
    }
    public String decryptMsg(String cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        byte[] decode = Base64.decode(cipherText, Base64.NO_WRAP);
        String decryptString = new String(cipher.doFinal(decode), "UTF-8");
        return decryptString;
    }
    public static SecretKey generateKey(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        SecretKeySpec secret;
        secret = new SecretKeySpec(key.getBytes(), "AES");
        return  secret;
    }

    /* Проверяем хранилище на доступность чтения и записи*/
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    /* Проверяем внешнее хранилище на доступность чтения */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}