package hk.edu.cuhk.ie.iems5722.group21.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import hk.edu.cuhk.ie.iems5722.group21.R;

public class AboutusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        TextView textView = findViewById(R.id.content_text_view);
        textView.setText(R.string.copyright);
    }
}
