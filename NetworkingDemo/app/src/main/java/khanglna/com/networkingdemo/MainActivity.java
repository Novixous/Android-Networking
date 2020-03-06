package khanglna.com.networkingdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    private TextView txtMsg;
    private TextView txtInfo;
    private TextView txtIPInfo;
    private ServerSocket serverSocket;
    private String msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtMsg = findViewById(R.id.txtMsg);
        txtInfo = findViewById(R.id.txtInfo);
        txtIPInfo = findViewById(R.id.txtInfoIP);

        txtIPInfo.setText(getIpAddress());
        Thread serverSocketThread = new Thread(new SocketServerThread());
        serverSocketThread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerReplyThread extends Thread {
        private Socket hostThreadSocket;
        int cnt;

        public SocketServerReplyThread(Socket socket, int c) {
            this.hostThreadSocket = socket;
            this.cnt = c;
        }

        @Override
        public void run() {
            OutputStream os;
            String msgReply = "Networking, #" + cnt;
            try {
                os = hostThreadSocket.getOutputStream();
                PrintStream ps = new PrintStream(os);
                ps.print(msgReply);
                ps.close();
                msg += "Relayed: " + msgReply + "\n";
            } catch (IOException e) {
                e.printStackTrace();
                msg += "Something is wrong! " + e.toString() + "\n";
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtMsg.setText(msg);
                }
            });
        }
    }

    private class SocketServerThread extends Thread {
        public static final int SocketServerPORT = 8080;
        private int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket((SocketServerPORT));
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtInfo.setText("Waiting Port: " + serverSocket.getLocalPort());
                    }
                });
                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;
                    msg += "#" + count + " from " + socket.getInetAddress() + ":" + socket.getPort() + "\n";
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtMsg.setText(msg);
                        }
                    });
                    SocketServerReplyThread replyThread = new SocketServerReplyThread(socket, count);
                    replyThread.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNWInterface = NetworkInterface.getNetworkInterfaces();
            while (enumNWInterface.hasMoreElements()) {
                NetworkInterface nwInterface = enumNWInterface.nextElement();
                Enumeration<InetAddress> enumInetAddr = nwInterface.getInetAddresses();
                while (enumInetAddr.hasMoreElements()) {
                    InetAddress inet = enumInetAddr.nextElement();
                    if (inet.isSiteLocalAddress()) {
                        ip += "Site Local Address " + inet.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something is wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
