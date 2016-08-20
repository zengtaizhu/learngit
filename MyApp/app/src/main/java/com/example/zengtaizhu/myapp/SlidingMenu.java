package com.example.zengtaizhu.myapp;

/**
 * Created by hp on 2016/4/27.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.nineoldandroids.view.ViewHelper;

public class SlidingMenu extends HorizontalScrollView{
    //摆放left_menu和content的水平布局
    private LinearLayout mWrapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    //获取屏幕的宽度
    private int mScreenWidth;
    private int mMenuWidth;
    //left_menu拉出时menu右侧到屏幕最右侧的padding,单位为dp
    private int mMenuRightPadding = 50;
    //检测是否已经测量过子View的宽和高
    private boolean onceMessage;
    //检测菜单是否已经弹出
    private boolean isOpen;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    /**
     * 未使用自定义属性时调用两个参数的构造方法
     *
     * @param context
     * @param attrs
     */
    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当使用了自定义属性时，会调用此构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取我们定义的属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
            }
        }
        a.recycle();
        //得到屏幕的宽和高，保存在outMetrics变量中
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        //把dp转化为px，第一个参数为将要被转换的单位，第二个为默认值
        //这里已经显示确定了50这个默认值。所以在自定义属性的时候可以直接取后面（int）部分作为默认值
        //mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    }


    /**
     * 设置子View的宽和高，以及自己的宽和高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //不用多次重复获取子View的宽和高
        if (!onceMessage) {
            //因为在HorizontalScrollView下只有一个子View
            mWrapper = (LinearLayout)getChildAt(0);

            mMenu = (ViewGroup) mWrapper.getChildAt(0);
            mContent = (ViewGroup) mWrapper.getChildAt(1);

            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            //因为mWrapper在LineaLayout下会自动match_parent，所以只要显式设置了mMenu与mContent就不用自己设置
            // mWrapper.getLayoutParams().width=
            // 同理自己的宽和高都与上面一样
            onceMessage = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * 决定子View的摆放的位置,通过设置偏移量，将menu隐藏
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            //实现scrollTo后getScrollX为mMenuWidth,在未实现scrollTo时getScrollX为0
            this.scrollTo(mMenuWidth, 0);
        }

    }

    /**
     * 控制内部View的移动效果
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                //隐藏菜单
                if (scrollX >= mMenuWidth / 2) {
                    //不用ScrollTo()是因为要给用户一个滑动动画效果
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen=false;
                }
               // if(isOpen==true&&scrollX==0&&getX()>(mScreenWidth-mMenuWidth)){
               //     this.smoothScrollTo(mMenuWidth, 0);
               //     isOpen=false;
               //     return false;
               //}
                //将菜单完全显示，就是将内容区域完全显示
                else {
                    this.smoothScrollTo(0, 0);
                    isOpen=true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }
    public void openMenu(){
        if(isOpen)
            return;
        this.smoothScrollTo(0,0);
        isOpen=true;
    }
    public void closeMenu() {
        if(!isOpen)
            return;
        this.smoothScrollTo(mMenuWidth,0);
        isOpen=false;
    }

    /**
     * 切换菜单
     */
    public void toggle() {
        if(isOpen){
            closeMenu();
        }
        else {
            openMenu();
        }
    }

    /**
     * 滚动发生时调用
     * @param l：getScrollX的值
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //调用属性动画,将mMenu瞬间移动到getScrollX的位置上，达到视觉效果一直被拉出来
        //获取一个scale的梯度值，从左向右拉范围为1~0
        float scale=l*1.0f/mMenuWidth;
        ViewHelper.setTranslationX(mMenu,mMenuWidth*scale*0.8f);
        //可以写成ViewHelper.setTranslationX(mMenu,l);
        /**
         * scale:1.0~0
         * content区域：实现1.0~0.7缩放效果
         *                0.7 + scale*0.3
         *             实现1.0~0.6透明度变化
         *                0.6+scale*0.4
         *
         * menu区域：实现0.7~1.0缩放效果
         *              1.0-scale*0.3
         *          实现0.6~1.0透明度变化
         *              1.0-scale*0.4
         */
        //设置缩放
        float rightScale=  0.7f + scale*0.3f;
        float leftScale= 1.0f-scale*0.3f;
        float rightAlpha=0.6f+scale*0.4f;
        float leftAlpha=1.0f-scale*0.4f;
        //设置缩放的中心点
        ViewHelper.setPivotX(mContent,0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);
        ViewHelper.setAlpha(mContent,rightAlpha);
        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu,leftAlpha);
    }
}
