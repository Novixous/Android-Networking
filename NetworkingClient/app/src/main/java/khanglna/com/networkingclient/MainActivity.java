package khanglna.com.networkingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
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
        img = findViewById(R.id.i)
    }

    public void clickToSend(View view) {
    }
}
