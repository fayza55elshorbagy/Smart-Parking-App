package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LinearLayout mdotsLayout;
    private SliderActivity sliderAdapter;
    private TextView[] mdots;
    TextView skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mdotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        sliderAdapter = new SliderActivity(this);
        viewPager.setAdapter(sliderAdapter);
        addDotsindicator(0);
        viewPager.addOnPageChangeListener(viewListener);
        skip = (TextView) findViewById(R.id.skip);
    }
    public void addDotsindicator(int position)
    {
        mdots = new TextView[4];
        mdotsLayout.removeAllViews();
        for(int i =0; i< mdots.length;i++)
        {
            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextColor(getResources().getColor(R.color.gray));
            mdots[i].setTextSize(35);
            mdotsLayout.addView(mdots[i]);

        }
        if(mdots.length > 0)
        {
            mdots[position].setTextColor(getResources().getColor(R.color.projectColor));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsindicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void onClick(View view) {
        Intent MyIntent = new Intent(this,signin.class);
        startActivity(MyIntent);
    }



}

