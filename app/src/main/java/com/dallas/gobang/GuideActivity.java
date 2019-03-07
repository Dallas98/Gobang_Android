package com.dallas.gobang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity {

    /**
     * 定义全局变量ViewPager
     */
    private ViewPager mViewPager;

    /**
     * 底部圆点   2张图片,一张是当前,一张不是当前
     */
    private ImageView pager1,pager2,pager3,pager4,pager5;
    /**
     * 当前页是哪一页
     */
    private int currIndex = 0;
    /**
     * 是否是第一次进该app
     */
    private boolean isFirstCome = false;
    /**
     * SharedPreferences文件名称
     */
    private final static String SHAREDFILENAME = "myShared";
    /**
     * 开始按钮
     */
    private Button start_app = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        /**
         * 实例化ViewPager
         */
        mViewPager = (ViewPager) findViewById(R.id.whatsnew);

        /**
         * 设置滑动页变化的接口
         */
        mViewPager.setOnPageChangeListener(new MyPager());


        pager1 = (ImageView) findViewById(R.id.pager1);
        pager2 = (ImageView) findViewById(R.id.pager2);
        pager3 = (ImageView) findViewById(R.id.pager3);
        pager4 = (ImageView) findViewById(R.id.pager4);
        pager5 = (ImageView) findViewById(R.id.pager5);


        //加载布局
        LayoutInflater lif = LayoutInflater.from(GuideActivity.this);
        //调用它的inflate()方法来加载view1这个布局
        //参数:id,父布局
        View view1 = lif.inflate(R.layout.start_one, null);
        View view2 = lif.inflate(R.layout.start_tow, null);
        View view3 = lif.inflate(R.layout.start_three, null);
        View view4 = lif.inflate(R.layout.start_four, null);
        View view5 = lif.inflate(R.layout.start_five, null);

        //获取view5中的按钮
        start_app = (Button)view5.findViewById(R.id.start_app);
        start_app.setOnClickListener(new StartAppListener());

        /**
         * 每个页面的view数据
         */
        final ArrayList<View> array = new ArrayList<>();
        array.add(view1);
        array.add(view2);
        array.add(view3);
        array.add(view4);
        array.add(view5);

        /**
         * 填充ViewPager的数据适配器
         */
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return array.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager)container).removeView(array.get(position));
            }



            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager)container).addView(array.get(position));
                return array.get(position);
            }
        };
        //设置适配器
        mViewPager.setAdapter(mPagerAdapter);
    }

    /**
     * 如果是第一次进app,则放置数据
     */
    private void putIsFirstData(){
        SharedPreferences.Editor editor = getSharedPreferences(SHAREDFILENAME
                ,MODE_PRIVATE).edit();
        editor.putBoolean("isfirst",false);
        editor.commit();
    }

    /**
     * 获取当前是否是第一次进
     * @return
     */
    private Boolean getIsFirstCome(){
        boolean isFirst;
        SharedPreferences pref = getSharedPreferences(SHAREDFILENAME,MODE_PRIVATE);
        isFirst = pref.getBoolean("isfirst",true);
        return isFirst;
    }

    public class MyPager implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    pager1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    pager2.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;

                case 1:
                    pager2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    pager1.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    pager3.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;

                case 2:
                    pager3.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    pager2.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    pager4.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;

                case 3:
                    pager4.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    pager3.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    pager5.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;

                case 4:
                    pager5.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
                    pager4.setImageDrawable(getResources().getDrawable(R.drawable.page));
                    break;

            }
            currIndex = arg0;
        }
    }

    //开始按钮监听器
    class StartAppListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(GuideActivity.this,StartScreenActivity.class);
            startActivity(intent);
            GuideActivity.this.finish();
        }
    }

}
