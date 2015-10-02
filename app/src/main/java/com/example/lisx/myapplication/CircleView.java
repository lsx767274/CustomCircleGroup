/*
 * @Title CircleView.java
 * @Copyright Copyright 2010-2014 Careland Software Co,.Ltd All Rights Reserved.
 * @author lisx
 * @date 2015-5-14 下午5:51:25
 * @version 1.0
 */
package com.example.lisx.myapplication;

import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Scroller;
import android.widget.TextView;



/**
 * @author lisx
 * @date 2015-5-14 下午5:51:25
 */
@SuppressLint("ResourceAsColor")
public class CircleView extends ViewGroup {
	    //返回判断模式
	    public static final boolean MIRROR = true;
	    public static final float PI = 3.1415926f;
	    //返回模式
	    private static final int SCALE = MIRROR ? -1 : 1;
	    //位置角度
	    private static final float OFFSET_ANGEL = 0;
	    //显示item数量
	    private static final int SHOW_COUNT = 5;
	    //旋转一下角度
	    private static final float STEP_ANGLE = 2*PI / SHOW_COUNT;
	    //边缘角度
	    private static final float EDGE_ANGLE = (STEP_ANGLE - PI) / 2;
	    private static final boolean IS_DRAWABLE_ABORT = true;
	    //当前item数量
	    private int currentItem = 0;
	    //当前角度
	    private float currentAngle = 0;
	    //item下标
	    private int itemCount = 0;
	    
        //设置同步旋转圆Drawable对象
	    private Drawable rotateDrawable;
	    private BaseAdapter adapter;
	    private OnItemClickListener itemClickListener;
	    //触摸移动距离
	    private int touchSlop;

	    private float[] dAngle = new float[3];
	    private int[] dTime = new int[3];
	    //最后时间
	    private long lastTime = 0;
	    //最后角度
	    private int lastAngle;
	    //滑动
	    private Scroller scroller;
	    //动画是否完成
	    private boolean isAnimation;

	    public CircleView(Context context) {
	        super(context);
	        init();
	    }

	    public CircleView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        init();
	    }

	    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
	        init();
	    }

	    
//	  //设置该View本身地大小 
//	    @Override
//	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
//	        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),  
//	                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));  
//	    } 

		/**
		 * 初始化布局，图盘，启动动画
		 */
		private void init(){
	    	//设置同布局画view,画back bacckground ，或者设置背景透明，	    	
	    	// viewgroup需要有画的东西才会执行ondraw方法
//	    	setWillNotDraw(false);
	    	setBackgroundColor(getResources().getColor(R.color.transparent));
	        ViewConfiguration configuration = ViewConfiguration.get(getContext());
	        touchSlop = configuration.getScaledTouchSlop();
	        scroller = new Scroller(getContext(), new DecelerateInterpolator());
	        startAnimation(getShowAnimation());
	        isAnimation = true;
	        currentItem = -5;
	    }

	    /**
	     * 显示动画
	     * @return
	     */
	    public Animation getShowAnimation(){
	        AnimationSet set = new AnimationSet(false);
	        ScaleAnimation scaleIn = new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f,
	                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
	        scaleIn.setDuration(200);
	        set.addAnimation(scaleIn);
	        ScaleAnimation scaleOut = new ScaleAnimation(1f, 0.83f, 1f, 0.83f,
	                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
//	        scaleOut.setDuration(150);
	        scaleOut.setStartOffset(200);
	        set.addAnimation(scaleOut);
	        set.setAnimationListener(new Animation.AnimationListener() {
	            @Override
	            public void onAnimationStart(Animation animation) {

	            }
	            @Override
	            public void onAnimationEnd(Animation animation) {
	            	//完成时设置位置
	                scroll(0, -5*STEP_ANGLE);
	            }

	            @Override
	            public void onAnimationRepeat(Animation animation) {

	            }
	        });
	        return set;
	    }

	    @Override
	    protected void onLayout(boolean changed, int l, int t, int r, int b) {
	        reset();
	    }
        //添加Item项布局
	    private void reset(){
	        itemCount = adapter==null ? 0 : adapter.getCount();
	        removeAll();
	        addAll(!isAnimation);
	        layout();
	    }

	    /**
	     *确定Item项位置 
	     */
	    private void layout(){
	        int childCount = getChildCount();
	        float firstAngle = EDGE_ANGLE + currentAngle;
	        int radius = (int) (Math.min(getWidth()-getPaddingLeft()-getPaddingRight(),
	                        getHeight() - getPaddingTop() - getPaddingBottom()) / 3f);
	        int count = Math.min(childCount, SHOW_COUNT);

	        int start = currentItem >=0 ? 0 : -currentItem;
	        for(int i = 0; i<count; i++){
	            float currAngle = firstAngle + STEP_ANGLE * (i+start);
	            int cx = (int) (SCALE*radius * Math.cos(currAngle)) + getWidth() / 2;
	            int cy = (int) (radius * Math.sin(currAngle)) + getHeight() / 2;
	            View child = getChildAt(i);
	            int cw = child.getMeasuredWidth();
	            int ch = child.getMeasuredHeight();
	            int cl = cx - cw / 2;
	            int ct = cy - ch / 2;	   
	            child.layout(cl, ct, cl+cw, ct+ch);
	            float angleRange = PI/2 + EDGE_ANGLE;
	            if(currAngle < EDGE_ANGLE+OFFSET_ANGEL) {
	                setAlpha(child, (PI / 2 + currAngle - OFFSET_ANGEL) / (angleRange - OFFSET_ANGEL));
	            } else if(currAngle > 3*PI/2 - angleRange-OFFSET_ANGEL){
	                setAlpha(child,
	                        (3 * PI / 2 - currAngle - OFFSET_ANGEL) / (angleRange - OFFSET_ANGEL));
	            } else {
	                setAlpha(child, 1);
	            }
	        }
	        invalidate();
	    }

//	    @Override
//	    protected void dispatchDraw(Canvas canvas) {
//	        if(!IS_DRAWABLE_ABORT){
//	            drawRotateDrawable(canvas);
//	        }
//	        super.dispatchDraw(canvas);
//	        if(IS_DRAWABLE_ABORT){
//	            drawRotateDrawable(canvas);
//	        }
//	    }
	    //画同步圆
	    @Override
		protected void onDraw(Canvas canvas) {
	    	 if(!IS_DRAWABLE_ABORT){
		            drawRotateDrawable(canvas);
		        }
		        super.dispatchDraw(canvas);
		        if(IS_DRAWABLE_ABORT){
		            drawRotateDrawable(canvas);
		        }	
		        super.onDraw(canvas);
		}

		/**
		 * 画同步圆
		 * @param canvas
		 */
		protected void drawRotateDrawable(Canvas canvas) {
	        if(rotateDrawable!=null) {
	            int radius = (int) (Math.min(getWidth()-getPaddingLeft()-getPaddingRight(),
	                    getHeight() - getPaddingTop() - getPaddingBottom()) / 3f);
	            int cx = getWidth() / 2;
	            int cy = getHeight() / 2;
	            int key = canvas.save();
	            float degree = (currentAngle - currentItem * STEP_ANGLE) * SCALE / PI * 180;
	            canvas.rotate(degree, getWidth() / 2, getHeight() / 2);
	            rotateDrawable.setBounds(cx - radius, cy - radius, cx + radius, cy + radius);
	            rotateDrawable.draw(canvas);
	            canvas.restoreToCount(key);
	        }
	    }
        //设置旋转Item影藏
	    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	    private void setAlpha(View child, float a){
	        float alpha = a*a*a;
	        if(Build.VERSION.SDK_INT >= 11) {
	            child.setAlpha(alpha);
	        } else {
	            if(alpha <0.5f){
	                child.setVisibility(View.INVISIBLE);
	            } else {
	                child.setVisibility(View.VISIBLE);
	            }
	        }
	    }

	    private int mode;
	    private float motionX, motionY;

	    @Override
	    public boolean onInterceptTouchEvent(MotionEvent event) {
	        if(isAnimation){
	            return false;
	        }
	        float ex = event.getX();
	        float ey = event.getY();
	        switch (event.getAction()){
	        case MotionEvent.ACTION_DOWN:
	            motionX = ex;
	            motionY = ey;
	            mode = 0;
	            stop();
	            lastTime = SystemClock.uptimeMillis();
	            for (int i = 0; i < 3; i++) {
	                dAngle[i] = 0f;
	                dTime[i] = 0;
	            }
	            break;
	        case MotionEvent.ACTION_MOVE:
	            if(mode==0){
	                float diffX = Math.abs(ex - motionX);
	                float diffY = Math.abs(ey - motionY);
	                if(diffX > touchSlop || diffY > touchSlop){
	                    mode = 1;
	                }
	            }
	        }
	        return mode!=0;
	    }

	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        if(isAnimation){
	            return false;
	        }
	        float ex = event.getX();
	        float ey = event.getY();
	        switch (event.getAction()){
	        case MotionEvent.ACTION_DOWN:
	            motionX = ex;
	            motionY = ey;
	            mode = 0;
	            lastTime = SystemClock.uptimeMillis();
	            for (int i = 0; i < 3; i++) {
	                dAngle[i] = 0f;
	                dTime[i] = 0;
	            }
	            break;
	        case MotionEvent.ACTION_MOVE:
	            if(mode==0){
	                float diffX = Math.abs(ex - motionX);
	                float diffY = Math.abs(ey - motionY);
	                if(diffX > touchSlop || diffY > touchSlop){
	                    mode = 1;
	                } else {
	                    break;
	                }
	            }
	            prepareMove(ex, ey);
	            motionX = ex;
	            motionY = ey;
	            break;
	        case MotionEvent.ACTION_UP:
	            if(mode==0){
	                for(int i=0; i<getChildCount(); i++){
	                    View child = getChildAt(i);
	                    if(child.getLeft() <= ex && ex < child.getRight()
	                            && child.getTop() <= ey && ey < child.getBottom()){
	                        if(itemClickListener!=null){
	                            itemClickListener.onItemClick(i+currentItem, child, this);
	                        }
	                        break;
	                    }
	                }
	                performClick();
	                break;
	            }
	        case MotionEvent.ACTION_CANCEL:
	            prepareMove(ex, ey);
	            float tAngle = dAngle[0] * 500f + dAngle[1] * 300f + dAngle[2] * 200f;
	            int tTime = dTime[0] + dTime[1] + dTime[2];
	            if(tTime==0){
	                break;
	            }
	            float vAngle = tAngle / tTime;
	            fling(vAngle);
	            break;
	        }
	        return true;
	    }

	    /**
	     * 旋转移动
	     * @param ex 按下x坐标
	     * @param ey 按下y坐标
	     */
	    private void prepareMove(float ex, float ey){
	        float cx = getWidth() / 2;
	        float cy = getHeight() / 2;
	        float dx = 0;
	        if(ey < cy && motionY < cy){
	            if(ex < cx && motionX < cx){
	                dx = ex - motionX + motionY - ey;
	            } else if(ex > cx && motionX > cx){
	                dx = ex - motionX + ey - motionY;
	            } else {
	                dx = ex - motionX;
	            }
	        } else if(ey > cy && motionY > cy){
	            if(ex < cx && motionX < cx){
	                dx = motionX - ex + motionY - ey;
	            } else if(ex > cx && motionX > cx){
	                dx = motionX - ex + ey - motionY;
	            } else {
	                dx = motionX - ex;
	            }
	        } else {
	            if(ex < cx && motionX < cx){
	                dx = motionY - ey;
	            } else if(ex > cx && motionX > cx){
	                dx = ey - motionY;
	            }
	        }
	        move(SCALE * dx * 2 / getWidth(), true);
	    }

	    /**
	     * 模拟惯性滑动
	     * @param start 启动位置
	     * @param end 结束位置
	     */
	    public void scroll(float start, float end){
	        stop();
	        removeCallbacks(runnable);
	        lastAngle = (int) (start*1000);
	        int distance = (int)(end * 1000) - lastAngle;
	        scroller.startScroll(0, 0, distance, 0, 2000);
	        post(runnable);
	    }

	    /**
	     * 惯性滑动
	     * @param v 惯性滑动角度
	     */
	    public void fling(float v){
	        stop();
	        removeCallbacks(runnable);
	        v = Math.min(PI*4, Math.max(-PI*4, v));
	        lastAngle = Integer.MAX_VALUE / 2;
	        scroller.fling(Integer.MAX_VALUE / 2, 0, (int) (v * 1000), 0,
	                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
	        post(runnable);
	    }

	    private void stop(){
	        if(!scroller.isFinished()){
	            scroller.abortAnimation();
	        }
	    }

	    /**
	     * 移动位置并重新请求布局
	     * @param da
	     * @param edge
	     */
	    private void move(float da, boolean edge){
	        long time = SystemClock.uptimeMillis();
	        if(time >= lastTime + 50){
	            dAngle[2] = dAngle[1];
	            dAngle[1] = dAngle[0];
	            dAngle[0] = da;
	            dTime[2] = dTime[1];
	            dTime[1] = dTime[0];
	            dTime[0] = (int) (time - lastTime);
	            lastTime = time;
	        } else {
	            dAngle[0] += da;
	            dTime[0] = (int) (time - lastTime);
	        }
	        int newItem = currentItem;
	        float newAngle = currentAngle + da;
	        while(newAngle < -PI/2-EDGE_ANGLE){
	            newItem++;
	            newAngle += STEP_ANGLE;
	        }
	        while(newAngle >= -PI/2-EDGE_ANGLE + STEP_ANGLE) {
	            newItem--;
	            newAngle -= STEP_ANGLE;
	        }

	        if(edge) {
	            if (newItem > itemCount - SHOW_COUNT) {
	                newItem = itemCount - SHOW_COUNT;
	                newAngle = 0;
	            } else if (newItem == itemCount - SHOW_COUNT && newAngle < 0) {
	                newAngle = 0;
	            }
	            if (newItem < 0) {
	                newItem = 0;
	                newAngle = 0;
	            } else if (newItem == 0 && newAngle > 0) {
	                newAngle = 0;
	            }
	        }

	        if(newItem != currentItem){
	            currentItem = newItem;
	            removeAll();
	            addAll(edge);
	        }
	        currentAngle = newAngle;
	        layout();
	    }

	    /**
	     * 设置旋转对象
	     * @param rotateDrawable
	     */
	    public void setRotateDrawable(Drawable rotateDrawable){
	        if(this.rotateDrawable!=null){
	            this.rotateDrawable.setCallback(null);
	            unscheduleDrawable(this.rotateDrawable);
	        }
	        this.rotateDrawable = rotateDrawable;
	        if(rotateDrawable!=null){
	            rotateDrawable.setCallback(this);
	        }
	        postInvalidate();
	    }

	    @Override
	    protected boolean verifyDrawable(Drawable who) {
	        return who == this.rotateDrawable || super.verifyDrawable(who);
	    }

	    /**
	     * 设置adapter,并注册观察者
	     * @param adapter
	     */
	    public void setAdapter(BaseAdapter adapter){
	        if(this.adapter!=null){
	            this.adapter.unregisterDataSetObserver(dataSetObserver);
	        }
	        if(adapter!=null){
	            adapter.registerDataSetObserver(dataSetObserver);
	        }
	        this.adapter = adapter;
	        requestLayout();
	    }

	    public void setOnItemClickListener(OnItemClickListener itemClickListener){
	        this.itemClickListener = itemClickListener;
	    }

	    private ArrayList<View> recycleView = new ArrayList<View>();
	    
	    private ArrayList<Map<String,Integer>> recycleViewSize = new ArrayList<Map<String,Integer>>();

	    /**
	     * 移除所有Item，并保存
	     */
	    private void removeAll(){
	        int childCount = getChildCount();
	        for(int i=0; i<childCount; i++){
	            recycleView.add(getChildAt(i));
	        }
	        detachAllViewsFromParent();
	    }

	    /**
	     * 添加所有Item
	     * @param edge
	     */
	    private void addAll(boolean edge){
	        int count = Math.min(itemCount, SHOW_COUNT);
	        if(edge && currentItem + count > itemCount){
	            currentItem = Math.max(itemCount - count, 0);
	            currentAngle = 0;
	        }
	        for(int i=0; i<count; i++){
	            getOrMakeView(i+currentItem);
	        }
	        recycleView.clear();
	    }

	    /**
	     * 添加Item在Viewgroup，并确定Item的宽高
	     * @param pos
	     */
	    private void getOrMakeView(int pos){
	        if(pos<0){
	            return;
	        }
	        if(recycleView.size()>0){
	            View oldView = recycleView.remove(0);
	            View newView = adapter.getView(pos, oldView, this);
//	            LinearLayout layout = (LinearLayout)view;
	            TextView textView = (TextView)newView.findViewById(R.id.id_circle_menu_item_text);
	            //0 == pos ? getWidth() / 3+300 :getWidth() / 3
	            TextPaint paint = textView.getPaint();
	            int width = (int) Layout.getDesiredWidth(textView.getText(), 0, textView.getText().length(), paint);
	            newView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
	                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	            attachViewToParent(newView, -1, newView.getLayoutParams());
	            
	        } else {
	            View newView = adapter.getView(pos, null, this);
	            TextView textView = (TextView)newView.findViewById(R.id.id_circle_menu_item_text);
	            //0 == pos ? getWidth() / 3+300 :getWidth() / 3
	            TextPaint paint = textView.getPaint();
	            int width = (int) Layout.getDesiredWidth(textView.getText(), 0, textView.getText().length(), paint);
	            newView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
	                    MeasureSpec.makeMeasureSpec(textView.getHeight(), MeasureSpec.AT_MOST));
	            addViewInLayout(newView, -1, generateDefaultLayoutParams(), true);
	            
	        }
	    }

	    public interface OnItemClickListener{
	        void onItemClick(int pos, View view, ViewGroup parent);
	    }

	    private DataSetObserver dataSetObserver = new DataSetObserver() {
	        @Override
	        public void onChanged() {
	            requestLayout();
	        }

	        @Override
	        public void onInvalidated() {
	            requestLayout();
	        }
	    };
        // 使用线程控制惯性停止
	    private final Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	            if(scroller.computeScrollOffset()){
	                int angle = scroller.getCurrX();
	                float da = (angle - lastAngle) / 1000f;
	                lastAngle = angle;
	                move(da, !isAnimation);
	                post(this);
	            } else {
	                isAnimation = false;
	            }
	        }
	    };
	    /**
	     * 重置
	     */
	    public void resetInitialize() {
			currentItem = 0;
			currentAngle = 0;
			itemCount = 0;
			lastTime = 0;
			lastAngle = 0;
			Scroller scroller;
			isAnimation = false;
		}
}
