package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class help_tutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_tutorial);
    }
    int index = 0;
    int[] imgarray = new int[]{R.drawable.i2,R.drawable.i1,R.drawable.back2};
    public void NextBtn(View view) {
        ImageView imageView = (ImageView) findViewById(R.id.im);
        imageView.setImageResource(imgarray[index]);
        index++;
        if (index >2)
            index = 0;

    }
}
