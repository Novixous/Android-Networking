package khanglna.com.networkingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    private ImageView img;
    private TextView txt;
    private EditText edtWord;
    private TextView txtResult;
    private TextView txtJson;
    private static TextView txtMsgReceived;
    private EditText edtMsg;
    public static final String KNIGHT_IMG = "";
    public static final String BLADE_IMG = "";
    public static final String TEXT_URL = "";
    public static final String WORD_DEFINITION = "";
    public static final String JSON_URL = "";
    public static final String IP_ADDRESS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.img);
        txt = (TextView) findViewById(R.id.txtText);
        edtWord = (EditText) findViewById(R.id.edtWord);
        txtResult = (TextView) findViewById(R.id.txtService);
        txtJson = (TextView) findViewById(R.id.txtJson);
        edtMsg = (EditText) findViewById(R.id.editMessage);
        txtMsgReceived = (TextView) findViewById(R.id.txtMsgReceived);
    }

    //Download Image
    public void clickToViewImg(View view) {
    }

    //Download Text
    public void clickToViewText(View view) {
    }

    //Consume XML Web Services
    public void clickToConsumeXml(View view) {
    }

    //Consume JSON Web Services
    public void clickToConsumeJson(View view) {
    }

    //Socket Programing
    public void clickToSend(View view) {
        MyClientTask client = new MyClientTask(IP_ADDRESS, Integer.parseInt(edtMsg.getText().toString()));
        client.execute();
    }

    private class MyClientTask extends AsyncTask<Void, Void, Void> {
        private String dstAddress;
        private int dstPort;
        private String response = "";

        public MyClientTask(String dstAddress, int dstPort) {
            this.dstAddress = dstAddress;
            this.dstPort = dstPort;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Socket socket = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int byteRead;
                InputStream inputStream = socket.getInputStream();
                while ((byteRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, byteRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknowHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
