package com.example.lisx.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    static List<String> testList = new ArrayList<String>();
    // 模拟测试数据
    private static final String str1 = "一开始觉得挺简单的,";
    private static final String str2 = "设置完成,";
    private static final String str3 = "我也不知道是大家在平时碰到这种效果机会少呢还是这个效果就是菜鸟级别,入门就懂的.不管是哪种,我都把我实现的方法给记录下来吧.。";
    private static final String str4 = "他告诉我一个方法,一语惊醒梦中人";
    private static final String str5 = "这样就造成了两个文字才自动换行.但是限定宽度之后,一行只能显示一个";
    private static final String str6 = "我的正常，贴出代码，我看看，可能布局或者代码里面没设置好吧";

    private static final String str7 = "但是设置颜色后，背景会变成矩形的，难道你没发现吗";
    private static final String str8 = "没有代码呀";
    private static final String str9 = "我能看见呀，郁闷";
    private static final String str10 = "没看到有什么特殊意义上吧，直接xml写个圆背景设置给TextView就可以了吧";
    private static final String str11 = "学习啊！多谢分享啊";
    private static final String str12 = "剛子須要 做圓形的 textview ,太好了";

    private static final String str13 = "恐高，不要去的aaa";
    private static final String str14 = "看来不错！很好玩的样子幺！很期待去玩！一起来吧！bbb";
    private static final String str15 = "空气真心很好，感觉人都舒服了，真的很不错哦，大家一起来吧！快快来吧。ccc";
    private static final String str16 = "西海大峡谷必须走一遍啊，真心333";
    private static final String str17 = "游人不多，可以尽情的玩耍，景色很美虽然有些累，不过很值得啊，呼应大家一起来的，happy_ddd";
    private static final String str18 = "很美eee";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> list = getTestList();
        setContentView(R.layout.activity_main);
        CircleView circleView = (CircleView)findViewById(R.id.id_menulayout);
        ImageView circleBig = (ImageView)findViewById(R.id.circle_center_big);
        CircleAdapter adapter = new CircleAdapter(this,list);
        circleView.setAdapter(adapter);
        circleView.setRotateDrawable(getResources().getDrawable(R.drawable.pl_open_big_bg));
        circleView.startAnimation(circleView.getShowAnimation());
        circleBig.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.show));







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    public static List<String> getTestList() {

        testList.add(str1);
        testList.add(str2);
        testList.add(str3);
        testList.add(str4);
        testList.add(str5);
        testList.add(str6);

        testList.add(str7);
        testList.add(str8);
        testList.add(str9);
        testList.add(str10);
        testList.add(str11);
        testList.add(str12);

        testList.add(str13);
        testList.add(str14);
        testList.add(str15);
        testList.add(str16);
        testList.add(str17);
        testList.add(str18);

        if (testList.size() > 0 && null != testList) {
            return testList;
        }
        return null;
    }
}
