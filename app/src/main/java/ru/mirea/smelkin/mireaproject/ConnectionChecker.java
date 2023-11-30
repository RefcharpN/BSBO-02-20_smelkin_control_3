package ru.mirea.smelkin.mireaproject;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionChecker extends AsyncTask<Void, Void, Boolean> {

    private String ip;
    private int port;
    private ConnectionListener listener;

    public ConnectionChecker(String ip, int port, ConnectionListener listener) {
        this.ip = ip;
        this.port = port;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 1000);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        if (listener != null) {
            listener.onConnectionChecked(isConnected);
        }
    }

    public interface ConnectionListener {
        void onConnectionChecked(boolean isConnected);
    }
}