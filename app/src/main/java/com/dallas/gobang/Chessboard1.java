//package com.dallas.gobang;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.view.MotionEvent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Chessboard1 {
//    //线条数量
//    private static final int MAX_LINE = 15;
//
//    //线条的宽度
//    private int mPanelWidth;
//
//    //线条的高度
//    private float mLineHeight;
//
//    //黑棋子
//    private Bitmap mBlack;
//
//    //白棋子
//    private Bitmap mWhite;
//
//    //比例，棋子的大小是高的四分之三
//    private float rowSize = 3 * 1.0f / 4;
//
//    private Paint mPaint;
//
//    /**
//     * 测量
//     *
//     * @param widthMeasureSpec
//     * @param heightMeasureSpec
//     */
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        //获取高宽值
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//
//        int hightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int hightMode = MeasureSpec.getMode(heightMeasureSpec);
//
//        //拿到宽和高的最小值，也就是宽
//        int width = Math.min(widthSize, heightMeasureSpec);
//
//        //根据测量模式细节处理
//        if (widthMode == MeasureSpec.UNSPECIFIED) {
//            width = hightSize;
//        } else if (hightMode == MeasureSpec.UNSPECIFIED) {
//            width = widthSize;
//        }
//
//        //设置这样就是一个正方形了
//        setMeasuredDimension(width, width);
//
//    }
//
//    /**
//     * 绘制棋盘的方法
//     *
//     * @param canvas
//     */
//    private void drawLine(Canvas canvas) {
//        //获取高宽
//        int w = mPanelWidth;
//        float lineHeight = mLineHeight;
//
//        //遍历，绘制线条
//        for (int i = 0; i < MAX_LINE; i++) {
//            //横坐标
//            int startX = (int) (lineHeight / 2);
//            int endX = (int) (w - lineHeight / 2);
//
//            //纵坐标
//            int y = (int) ((0.5 + i) * lineHeight);
//
//            //绘制横
//            canvas.drawLine(startX, y, endX, y, mPaint);
//            //绘制纵
//            canvas.drawLine(y, startX, y, endX, mPaint);
//        }
//
//    }
//    private void initPaint() {
//        mPaint=new Paint();
//        //设置颜色
//        mPaint.setColor(0xff000000);
//        //抗锯齿
//        mPaint.setAntiAlias(true);
//        //设置防抖动
//        mPaint.setDither(true);
//        //设置Style
//        mPaint.setStyle(Paint.Style.STROKE);
//    }
//    private void initBitmap() {
//        //拿到图片资源
//        mBlack = BitmapFactory.decodeResource(getResources(), R.drawable.black_xfhy);
//        mWhite = BitmapFactory.decodeResource(getResources(), R.drawable.white_xfhy);
//        //棋子宽度
//        int mWhiteWidth = (int) (mLineHeight * rowSize);
//
//        //修改棋子大小
//        mWhite = Bitmap.createScaledBitmap(mWhite, mWhiteWidth, mWhiteWidth, false);
//        mBlack = Bitmap.createScaledBitmap(mBlack, mWhiteWidth, mWhiteWidth, false);
//    }
//    //存储用户点击的坐标
//    private List<Point> mWhiteArray = new ArrayList<>();
//    private List<Point> mBlackArray = new ArrayList<>();
//
//    //标记，是执黑子还是白子 ,白棋先手
//    private boolean mIsWhite = true;
//
//    /**
//     * 触摸事件
//     *
//     * @param event
//     * @return
//     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        switch (event.getAction()) {
//                //按下事件
//                case MotionEvent.ACTION_UP:
//
//                    int x = (int) event.getX();
//                    int y = (int) event.getY();
//
//                    //封装成一个Point
//                    Point p = getValidPoint(x, y);
//
//                    //判断当前这个点是否有棋子了
//                    if(mWhiteArray.contains(p) || mBlackArray.contains(p)){
//
//                        //点击不生效
//                        return false;
//                    }
//
//                    //判断如果是白子就存白棋集合，反之则黑棋集合
//                    if (mIsWhite) {
//                        mWhiteArray.add(p);
//                    } else {
//                        mBlackArray.add(p);
//                    }
//
//                    //刷新
//                    invalidate();
//
//                    //改变值
//                    mIsWhite = !mIsWhite;
//
//                    break;
//        }
//
//        return true;
//    }
//    /**
//     * 不能重复点击
//     *
//     * @param x
//     * @param y
//     * @return
//     */
//    private Point getValidPoint(int x, int y) {
//
//        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
//    }
//
//    /**
//     * 绘制棋子的方法
//     *
//     * @param canvas
//     */
//    private void drawPieces(Canvas canvas) {
//        for (int i = 0; i < mWhiteArray.size(); i++) {
//            //获取白棋子的坐标
//            Point whitePoint = mWhiteArray.get(i);
//            canvas.drawBitmap(mBlack, (whitePoint.getX() + (1 - rowSize) / 2) * mLineHeight, (whitePoint.getY() + (1 - rowSize) / 2) * mLineHeight, null);
//        }
//
//        for (int i = 0; i < mBlackArray.size(); i++) {
//            //获取黑棋子的坐标
//            Point blackPoint = mBlackArray.get(i);
//            canvas.drawBitmap(mWhite, (blackPoint.getX() + (1 - rowSize) / 2) * mLineHeight, (blackPoint.getY() + (1 - rowSize) / 2) * mLineHeight, null);
//        }
//    }
//
//}
