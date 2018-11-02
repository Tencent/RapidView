package com.tencent.rapidview.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.framework.RapidConfig;
import com.tencent.rapidview.framework.RapidObject;
import com.tencent.rapidview.param.RelativeLayoutParams;
import com.tencent.rapidviewdemo.MainActivity;

import java.util.Random;

/**
 * @Class RapidXmlViewer
 * @Desc 光子XML开发预览生成器
 *
 * @author arlozhang
 * @date 2018.09.14
 */
public class RapidXmlViewer {

    private static RapidXmlViewer msInstance = null;

    private final BroadcastReceiver mDebugImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String xml    = intent.getStringExtra("debug_xml");
            Bitmap bitmap = null;

            if( xml == null || xml.compareTo("") == 0 ){
                return;
            }

            bitmap = get(MainActivity.getInstance(), xml);

            if( bitmap != null){
                FileUtil.write2File(FileUtil.compressBitmap(bitmap, Bitmap.CompressFormat.PNG, 100), FileUtil.getRapidDebugDir() + "xml_snapshot" + ".png");
            }
        }
    };

    private RapidXmlViewer(){}

    public static RapidXmlViewer getInstance(){

        if( msInstance == null ){
            msInstance = new RapidXmlViewer();
        }

        return msInstance;
    }

    public void initialize(Context context){
        IntentFilter filter;

        if( !RapidConfig.DEBUG_MODE ){
            return;
        }

        filter = new IntentFilter();
        filter.addAction("android.intent.action.BROADCAST_FORM_ADB");
        filter.addAction("com.tencent.android.rapidview");

        context.registerReceiver(mDebugImageReceiver, filter);
    }

    public Bitmap get(Activity context, String xml){
        IRapidView rapidView    = null;
        RapidObject rapidObject = new RapidObject();
        DisplayMetrics metric   = new DisplayMetrics();
        int    measuredWidth    = 0;
        int    measuredHeight   = 0;
        Bitmap bitmap           = null;
        Canvas canvas           = null;

        rapidObject.initialize(context, "", null, false, xml, null, null, false);

        rapidView = rapidObject.load(HandlerUtils.getMainHandler(), context, RelativeLayoutParams.class, null, null);
        if( rapidView == null ){
            return null;
        }

        try{

            rapidView.getView().setLayoutParams(rapidView.getParser().getParams().getLayoutParams());

            recursiveSetBackground(rapidView);

            context.getWindowManager().getDefaultDisplay().getMetrics(metric);

            rapidView.getView().layout(0, 0, metric.widthPixels, metric.heightPixels);

            measuredWidth = View.MeasureSpec.makeMeasureSpec(metric.widthPixels, View.MeasureSpec.EXACTLY);

            measuredHeight = View.MeasureSpec.makeMeasureSpec(metric.heightPixels, View.MeasureSpec.AT_MOST);

            rapidView.getView().measure(measuredWidth, measuredHeight);

            rapidView.getView().layout(0, 0, rapidView.getView().getMeasuredWidth(), rapidView.getView().getMeasuredHeight());

            bitmap = Bitmap.createBitmap(rapidView.getView().getMeasuredWidth(), rapidView.getView().getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            canvas = new Canvas(bitmap);

            rapidView.getView().draw(canvas);

            recursiveSetAnnotation(rapidView, bitmap, 0,0);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }

    private void recursiveSetBackground(IRapidView rapidView){

        View view = rapidView.getView();

        view.setBackgroundColor(getRandomBgColor());

        if( rapidView.getParser().mArrayChild == null || rapidView.getParser().mArrayChild.length == 0 ){
            return;
        }

        for( int i = 0; i < rapidView.getParser().mArrayChild.length; i++ ){
            recursiveSetBackground(rapidView.getParser().mArrayChild[i]);
        }
    }

    private void recursiveSetAnnotation(IRapidView rapidView, Bitmap bitmap, int parentLeft, int parentTop){
        Random random = new Random();
        int left = rapidView.getView().getLeft();
        int top  = rapidView.getView().getTop();
        int randomColor = getRandomTipColor();

        int randomWidth = 0;
        int randomHeight = 0;

        try{
            randomWidth = random.nextInt(rapidView.getView().getMeasuredWidth() / 2);
            randomHeight = random.nextInt( rapidView.getView().getMeasuredHeight() / 2);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String text = rapidView.getParser().getID() + ":" +
                "{width:" + ViewUtils.px2dip(rapidView.getView().getContext(), rapidView.getView().getMeasuredWidth()) +
                ",height:" + ViewUtils.px2dip(rapidView.getView().getContext(),  rapidView.getView().getMeasuredHeight()) + "}";

        setBitmapText(rapidView.getView().getContext(), bitmap, text, Color.RED, left + parentLeft, top + parentTop + randomHeight);

        text = "[" + rapidView.getParser().getID() + ":L" + "]" + Integer.toString(ViewUtils.px2dip(rapidView.getView().getContext(),  rapidView.getView().getMeasuredWidth())) + "dp";
        setBitmapText(rapidView.getView().getContext(), bitmap, text,
                randomColor, (left + parentLeft) / 2, top + parentTop + rapidView.getView().getMeasuredHeight() / 2 + randomHeight);

        text = "[" + rapidView.getParser().getID() + ":T" + "]" + Integer.toString(ViewUtils.px2dip(rapidView.getView().getContext(),  rapidView.getView().getMeasuredHeight())) + "dp";
        setBitmapText(rapidView.getView().getContext(), bitmap, text, randomColor, left + parentLeft + rapidView.getView().getMeasuredWidth() / 2, (top + parentTop) / 2 + randomHeight);

        setBitmapCtrlLine(rapidView, bitmap,randomWidth, randomHeight, randomColor, left + parentLeft, top + parentTop);

        if( rapidView.getParser().mArrayChild == null || rapidView.getParser().mArrayChild.length == 0 ){
            return;
        }

        for( int i = 0; i < rapidView.getParser().mArrayChild.length; i++ ){
            recursiveSetAnnotation(rapidView.getParser().mArrayChild[i], bitmap, left + parentLeft, top + parentTop);
        }
    }

    private int getRandomBgColor(){
        Random random = new Random();

        return Color.rgb(0, random.nextInt(155) + 100, random.nextInt(255));
    }


    private int getRandomTipColor(){
        Random random = new Random();

        return Color.rgb( random.nextInt(155) + 100, random.nextInt(150), 0);
    }

    private void setBitmapCtrlLine(IRapidView rapidView, Bitmap bmp, int randomWidth, int randomHeight, int color, int left, int top) {

        Canvas canvas = new Canvas(bmp);
        Paint  paint  = new Paint();

        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);

        canvas.drawRect(0, top + rapidView.getView().getMeasuredHeight() / 2 + randomHeight, left, top + rapidView.getView().getMeasuredHeight() + randomHeight, paint);
        canvas.drawRect(left + rapidView.getView().getMeasuredWidth() / 2 + randomWidth, 0, left + rapidView.getView().getMeasuredWidth() / 2 + randomWidth, top, paint);

        canvas.drawRect(left, top, left + rapidView.getView().getMeasuredWidth(), top + rapidView.getView().getMeasuredHeight(), paint);

        paint.setStrokeWidth(2);
    }

    private void setBitmapText(Context context, Bitmap bmp, String text,int color, int left, int top){

        Canvas canvas = new Canvas(bmp);
        Paint  paint  = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);

        paint.setTextSize( ViewUtils.dip2px(context, 7));
        paint.setColor(color);
        paint.setFakeBoldText(false);
        paint.setTypeface(Typeface.create("宋体", Typeface.NORMAL));

        canvas.drawText(text, left, top, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
    }
}
