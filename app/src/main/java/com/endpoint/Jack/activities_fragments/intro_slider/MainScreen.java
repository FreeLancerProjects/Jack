package com.endpoint.Jack.activities_fragments.intro_slider;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.text.Html;
import android.widget.Button;
import android.os.Bundle;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.endpoint.Jack.R;
import com.endpoint.Jack.activities_fragments.activity_home.client_home.activity.ClientHomeActivity;
import com.endpoint.Jack.activities_fragments.activity_sign_in.activity.SignInActivity;
import com.endpoint.Jack.language.Language_Helper;
import com.endpoint.Jack.models.UserModel;
import com.endpoint.Jack.preferences.Preferences;
import com.endpoint.Jack.singletone.UserSingleTone;
import com.endpoint.Jack.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;


public class MainScreen extends AppCompatActivity {

    PreferenceManager preferenceManager;
    LinearLayout Layout_bars;
    TextView[] bottomBars;
    int[] screens;
    Button  Next;
    ViewPager vp;
    TextView Skip;
    MyViewPagerAdapter myvpAdapter;
    private String lang;

    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Language_Helper.updateResources(base,Language_Helper.getLanguage(base)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        preferences=Preferences.getInstance();
        vp =  findViewById(R.id.view_pager);
        Layout_bars =  findViewById(R.id.layoutBars);
        Skip =  findViewById(R.id.skip);
        Next =  findViewById(R.id.next);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) Skip.getLayoutParams();

        if(lang.equals("ar")){
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)   ;

params.setMarginStart(20);
        }
        else {
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)   ;
            params.setMarginEnd(20);


        }
        screens = new int[]{
                R.layout.intro_screen1,
                R.layout.intro_screen2,
                R.layout.intro_screen3,
        };
        myvpAdapter = new MyViewPagerAdapter();
        vp.setId(0x1000);

        vp.setAdapter(myvpAdapter);
        preferenceManager = new PreferenceManager(this);
        vp.addOnPageChangeListener(viewPagerPageChangeListener);
        if (!preferenceManager.FirstLaunch()) {
            launchMain();
            finish();
        }
        ColoredBars(0);
    }

    public void next(View v) {
        int i = getItem(+1);
        if (i < screens.length) {
            vp.setCurrentItem(i);
        } else {
            launchMain();
        }
    }

    public void skip(View view) {
        launchMain();
    }

    private void ColoredBars(int thisScreen) {
        int[] colorsInactive = getResources().getIntArray(R.array.dot_on_page_not_active);
        int[] colorsActive = getResources().getIntArray(R.array.dot_on_page_active);
        bottomBars = new TextView[screens.length];

        Layout_bars.removeAllViews();
        for (int i = 0; i < bottomBars.length; i++) {
            bottomBars[i] = new TextView(this);
            bottomBars[i].setTextSize(100);
            bottomBars[i].setText(Html.fromHtml("&#175"));
            Layout_bars.addView(bottomBars[i]);
            bottomBars[i].setTextColor(colorsInactive[thisScreen]);
        }
        if (bottomBars.length > 0)
            bottomBars[thisScreen].setTextColor(colorsActive[thisScreen]);
    }

    private int getItem(int i) {
        return vp.getCurrentItem() + i;
    }

    private void launchMain() {
        preferenceManager.setFirstTimeLaunch(false);
        String session = preferences.getSession(MainScreen.this);

        if (session.equals(Tags.session_login))
        {
            UserModel userModel = preferences.getUserData(MainScreen.this);
            UserSingleTone userSingleTone = UserSingleTone.getInstance();
            userSingleTone.setUserModel(userModel);

            Intent intent = new Intent(MainScreen.this, ClientHomeActivity.class);
            startActivity(intent);
            finish();
        }else
        {
            Intent intent = new Intent(MainScreen.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            ColoredBars(position);
            if (position == screens.length - 1) {
                Next.setText(R.string.start);
                Skip.setVisibility(View.GONE);
            } else {
                Next.setText(getString(R.string.next));
                Skip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
           inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(screens[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return screens.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }

        @Override
        public boolean isViewFromObject(View v, Object object) {
            return v == object;
        }
    }
}