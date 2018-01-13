package com.example.k4170.slideritem;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by K4170 on 2018-01-09.
 */

public class InvoiceCardItem extends LinearLayout {

    private LinearLayout body;
    private final int MESSAGE_SCROLL_TO = 1;
    private int targetX;
    private int fraction;
    private ImageButton edit;
    private ImageButton delete;
    private ImageView image;
    private TextView name;
    private TextView taxNumber;
    CallMethod onclickMethod;
    private Context context;


    public InvoiceCardItem(Context context) {
        this(context, null);
    }

    public InvoiceCardItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InvoiceCardItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        View inflate = View.inflate(context, R.layout.invoice_card_item, this);
        initUI(inflate);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InvoiceCardItem);
        boolean enableSlide = typedArray.getBoolean(R.styleable.InvoiceCardItem_invoice_card_item_enable_slide, false);
        if (enableSlide) {
            body.setOnTouchListener(ontouchListener);
        }
        setOnclickEvent();
    }

    private void initUI(View inflate) {
        body = (LinearLayout) inflate.findViewById(R.id.ll_invoice_card_item_body);
        edit = (ImageButton) inflate.findViewById(R.id.ib_invoice_card_item_edit);
        delete = (ImageButton) inflate.findViewById(R.id.ib_invoice_card_item_delete);
        image = (ImageView) inflate.findViewById(R.id.iv_invoice_card_image);
        name = (TextView) inflate.findViewById(R.id.tv_invoice_card_item_name);
        taxNumber = (TextView) inflate.findViewById(R.id.tv_invoice_card_item_tax_number);
    }

    public void setImage(int resourceId){
        image.setImageResource(resourceId);
    }

    public void setName(String name){
        this.name.setText(name);
    }

    public void setTaxNumber(String taxNumber){
        this.taxNumber.setText(taxNumber);
    }

    public void setBodyOnclickListener(CallMethod callMethod){
        onclickMethod = callMethod;
    }
    private void setOnclickEvent() {
        edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "编辑", Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "删除", Toast.LENGTH_SHORT).show();
            }
        });
    }

    OnTouchListener ontouchListener = new OnTouchListener() {
        private int right;
        private int left;
        private float start;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float lastX = event.getRawX();
            WindowManager windowManager= (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            Display defaultDisplay = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            defaultDisplay.getMetrics(displayMetrics);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    start = lastX;
                    left = body.getLeft();
                    right = body.getRight();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    float offsetX = lastX - start;
                    if (left + offsetX > -200 * displayMetrics.density && left + offsetX <= 0) {
                        body.layout((int) (left + offsetX), body.getTop(),
                                (int) (right + offsetX), getBottom());
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if(Math.abs(start - lastX)< 10){
                        if(onclickMethod != null) {
                            onclickMethod.call();
                        }
                    }
                    start = 0;
                    int disX = getRight() - body.getRight();
                    fraction = disX / 30;
                    if (disX / (int) displayMetrics.density > 100) {
                        fraction = -fraction;
                        targetX = getMeasuredWidth() - (int) (200 * displayMetrics.density);
                    } else {
                        targetX = getMeasuredWidth();
                    }
                    Message message = new Message();
                    message.what = MESSAGE_SCROLL_TO;
                    handler.sendMessage(message);
                    break;
                }
            }
            return false;
        }
    };


    private int mCount = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_SCROLL_TO) {
                if ((fraction > 0 && body.getRight() <= targetX && mCount < 30) ||
                        (fraction < 0 && body.getRight() >= targetX && mCount < 30)) {
                    body.layout(body.getLeft() + fraction, body.getTop(),
                            body.getRight() + fraction, body.getBottom());
                    handler.sendEmptyMessageDelayed(MESSAGE_SCROLL_TO, 5);
                    mCount++;
                } else {
                    body.layout(targetX - getMeasuredWidth(), body.getTop(),
                            targetX, body.getTop() + getMeasuredWidth());
                    mCount = 0;
                }
            }
            super.handleMessage(msg);
        }
    };
}