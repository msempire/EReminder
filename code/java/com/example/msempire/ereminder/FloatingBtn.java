package com.example.msempire.ereminder;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by msempire on 16/7/4.
 */
public class FloatingBtn extends ViewGroup implements View.OnClickListener {

    //default res
    public static final int DEFAULT_SHOW_IMG_ID = R.drawable.ic_add_white_24dp;
    public static final String TAG_MAIN_BTN = "FLOATING_BTN_MAIN_TAG";

    public static final int STATE_CLOSE = 0;
    public static final int STATE_OPEN = 1;

    public static final int ANI_Y = 0;
    public static final int ANI_SCALE_X = 1;

    private FloatingActionButton m_mainBtn = null;
    private int m_state = STATE_CLOSE;
    private int m_extendAni = ANI_Y;
    private static final int m_padding = 20;
    View.OnClickListener m_listener = null;
    private ArrayList<View> m_viewList = new ArrayList<>();
    private int m_defaultResId = DEFAULT_SHOW_IMG_ID;
    private boolean m_alwaysShowMainBtn = false;

    private int m_BtnColor;
    private int m_mainBtnColor;


    //constructor
    public FloatingBtn(Context context){
        this(context, null);
    }

    public FloatingBtn(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public FloatingBtn(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        TypedArray arr = context.getTheme().obtainStyledAttributes(new int[]{
                R.attr.colorAccent,
        });

        m_BtnColor = arr.getColor(0, Color.WHITE);
        arr.recycle();

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, outValue, true);
        m_mainBtnColor = outValue.data;
//
    }

    //add item
    public void addItem(int resId, String desc, Object tag){
        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        ImageView view = new ImageView(getContext());
        view.setImageResource(resId);
//        view.setVisibility(INVISIBLE);
        view.setTag(tag);
        view.setOnClickListener(this);
        view.getDrawable().setColorFilter(m_BtnColor,
                PorterDuff.Mode.SRC_ATOP);

//        GradientDrawable d = new GradientDrawable();
////        d.setShape(GradientDrawable.OVAL);
//        int r = Math.max(view.getDrawable().getMinimumWidth(), view.getDrawable().getMinimumHeight());
//        d.setSize(r, r);
//        d.setColor(Color.GRAY);
//        view.setBackground(d);
//        int pad = 15;
//        view.setPadding(pad, pad, pad, pad);
        m_viewList.add(view);
        int count = m_viewList.size();

        //if only one view, then use the one
        if(count == 1){
            m_mainBtn = new FloatingActionButton(getContext(), null);
            m_mainBtn.setImageResource(resId);
            m_mainBtn.setTag(tag);
            m_mainBtn.setOnClickListener(this);
//            m_mainBtn.getDrawable().setColorFilter(m_mainBtnColor, PorterDuff.Mode.SRC_ATOP);
            this.addView(m_mainBtn, params);
        }
        //add two, then set main btn and add the first view
        else if(count == 2){
            m_mainBtn.setImageResource(m_defaultResId);
            m_mainBtn.setTag(m_defaultResId);
            m_mainBtn.getDrawable().setColorFilter(m_mainBtnColor, PorterDuff.Mode.SRC_ATOP);
            this.addView(m_viewList.get(0),0, params);
            this.addView(view, 0, params);
        }
        //add three, just add the view
        else
        {
            this.addView(view,0, params);
        }
    }

    public void addItem(View  view){
        if(view == null)
            return;
        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        m_viewList.add(view);
        this.addView(view,0, params);

        if(m_mainBtn == null){
            m_mainBtn = new FloatingActionButton(getContext(), null);
            m_mainBtn.setImageResource(m_defaultResId);
            m_mainBtn.setTag(m_defaultResId);
            m_mainBtn.setOnClickListener(this);
            m_viewList.add(m_mainBtn);
            this.addView(m_mainBtn, params);
        }

    }


    public void clearAll(){
        this.removeAllViews();
        m_viewList.clear();
        m_mainBtn = null;
    }

    //click event
    public void setClickListener(View.OnClickListener l){
        m_listener = l;
    }

    public void setExtendAni(int ani){
        m_extendAni = ani;
    }

    public void setShowMainBtn(boolean flag){
        m_alwaysShowMainBtn = flag;
        clearAll();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int horMid = getWidth() / 2;
        int verMid = getHeight() / 2;

        int viewW = 0;
        int viewH = 0;

        int count = getChildCount();
        int nextH = 0;

        //layout
        if(m_mainBtn != null && m_extendAni == ANI_Y){

            for(int i = 0; i < count; ++i){
                nextH = getHeight() - m_mainBtn.getMeasuredHeight() / 2;
                View view = getChildAt(i);
                viewH = view.getMeasuredHeight();
                viewW = view.getMeasuredWidth();
                view.layout(horMid - viewW/2, nextH - viewH/2, horMid + viewW/2, nextH+viewH/2);
                if(m_state == STATE_CLOSE){
                    view.setY(nextH -viewH/2);
                }
            }
        }
        //set y
        nextH = 0;
        if(m_state == STATE_OPEN || m_extendAni == ANI_SCALE_X){
            for(int i = 0; i < count-1; ++i){
                View view = getChildAt(i);
                final LayoutParams lp = (LayoutParams) view.getLayoutParams();
                viewH = view.getMeasuredHeight();
//                viewW = view.getMeasuredWidth();
                view.setY(nextH);
//                view.layout(horMid - viewW/2, nextH, horMid + viewW/2, nextH+viewH);
                nextH += viewH + m_padding;
                if(m_extendAni == ANI_SCALE_X && view != m_mainBtn){
                    view.setScaleX(m_state == STATE_OPEN ? 1 : 0);
                }
            }
        }

    }
//
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxW = 0;
        int maxH = 0;

        int count = getChildCount();
        for (int i = 0;i < count; ++i) {
            View view = getChildAt(i);
            measureChildWithMargins(view, widthMeasureSpec, 0, heightMeasureSpec, 0);
            maxW = Math.max(maxW, view.getMeasuredWidth());
            maxH += view.getMeasuredHeight() + m_padding;
        }

//        Log.d(Constants.LOG, "onMeasure" + maxW + "-" + maxH);
        setMeasuredDimension(maxW, maxH);

    }

    public void setState(int st, boolean shoAni){
        if(m_state != st){
            if(shoAni)
                switchState();
            else {
                m_state = st;
                requestLayout();
            }
        }
    }

    public boolean getStateOpen(){
        return m_state == STATE_OPEN;
    }

    private void switchState(){
        if(m_state == STATE_OPEN){
            m_state = STATE_CLOSE;
            extendLayout(false);
        }
        else {
            m_state = STATE_OPEN;
            extendLayout(true);
        }

    }

    private void extendLayout(boolean flag){
        //m_mainbtn
        if(flag){
            m_mainBtn.animate().rotation(45.0f).setDuration(300);
        }else {
            m_mainBtn.animate().rotation(0.0f).setDuration(300);
        }

        int count = getChildCount() - 1;

        float nextH = 0;
        int viewH = 0;
        float horH = 0;
        if(!flag){
            horH = m_mainBtn.getY() + m_mainBtn.getMeasuredHeight()/2;
        }

        if(m_extendAni == ANI_Y) {
            for (int i = 0; i < count; ++i) {
                View view = getChildAt(i);
                view.setVisibility(VISIBLE);
                viewH = view.getMeasuredHeight();
                if (!flag)
                    nextH = horH - viewH / 2;
                view.animate().y(nextH).setDuration(300).setInterpolator(new AccelerateInterpolator());
                if (flag) {
                    nextH += viewH + m_padding;
                }
            }
        }else if(m_extendAni == ANI_SCALE_X){
            for(int i = 0; i < count; ++i){
                View view = getChildAt(i);
                view.animate().scaleX(flag ? 1 : 0).setDuration(300).setInterpolator(new AccelerateInterpolator());
            }
        }
    }

    public void onClick(View view){
        int tag = (int) view.getTag();
        if(tag == m_defaultResId){
            switchState();
            return;
        }

        if(m_listener != null) {
            m_listener.onClick(view);
        }
        setState(STATE_CLOSE, true);
    }
}
