package khanglna.com.networkingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TextContent extends AppCompatActivity {
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_content);
        Intent intent = this.getIntent();
        ArrayList<String> content = intent.getStringArrayListExtra("content");
        txtResult = findViewById(R.id.txtService);
        txtResult.setMovementMethod(new ScrollingMovementMethod());
        for (int i = 0; i < content.size(); i++) {
            txtResult.setText(txtResult.getText().toString() + content.get(i));
        }

    }
}
