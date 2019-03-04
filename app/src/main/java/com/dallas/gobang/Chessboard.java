package com.dallas.gobang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Chessboard extends View {

    //线条数量
    private static final int MAX_LINE = 15;

    //线条的宽度
    private int mPanelWidth;

    //线条的高度
    private float mLineHeight;
    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //获取高宽值
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int hightSize = MeasureSpec.getSize(heightMeasureSpec);
        int hightMode = MeasureSpec.getMode(heightMeasureSpec);

        //拿到宽和高的最小值，也就是宽
        int width = Math.min(widthSize, heightMeasureSpec);

        //根据测量模式细节处理
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = hightSize;
        } else if (hightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        //设置这样就是一个正方形了
        setMeasuredDimension(width, width);

    }

/************************************************************************/
    /**
     * 棋盘右下角的坐标值，即最大坐标值,横竖线数量
     */
    private static int maxX;
    private static int maxY;

    //第一点偏离左上角从像数，为了棋盘居中
    private static int xOffset;
    private static int yOffset;

    /**
     * 画笔对象
     */
    private Paint globalPaint;

    /**
     * 每一个格子的大小
     */
    int chequerSize = 75;

    /**
     * 用来显示信息的TextView
     */
    private TextView showInfo = null;

    private Bitmap whiteChessBitmap = null;  //白棋图片
    private Bitmap blackChessBitmap = null;  //黑棋图片
    /**
     * 是否该黑子下棋     默认是黑棋先手(用户先手)
     */
    private boolean isBlack = true;
    /**
     * 游戏状态:开始
     */
    private final static int START = 1;
    /**
     * 游戏状态:结束
     */
    private final static int END = 2;
    /**
     * 游戏当前状态
     */
    private int gameState = START;
    /**
     * 白棋子数据
     */
    private ArrayList<Point> allWhiteChessList = new ArrayList<>();
    /**
     * 黑棋子数据
     */
    private ArrayList<Point> allBlackChessList = new ArrayList<>();

    /**
     * 所有棋盘里面棋子的坐标
     */
    private Point[][] allChessCoord;

    //实例化AudioManager对象，控制声音
    private AudioManager audioManager = null;
    //最大音量
    float audioMaxVolumn;
    //当前音量
    float audioCurrentVolumn;
    float volumnRatio;
    //音效播放池
    private SoundPool playSound = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    //存放音效的HashMap
    private Map<Integer, Integer> map = new HashMap<Integer, Integer>();

    //当前游戏模式    人机或者人人
    private static GamePattern gamePattern = GamePattern.MANANDMAN;

    /**
     * 重新开始按钮
     */
    private Button btn_restart = null;
    /**
     * 悔棋按钮
     */
    private Button btn_undo = null;




    /**
     * 棋盘的线(类)
     */
    class Line {
        float xStart, yStart, xStop, yStop;

        public Line(float xStart, float yStart, float xStop, float yStop) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xStop = xStop;
            this.yStop = yStop;
        }
    }

    //棋盘上面的线
    private List<Line> lines = new ArrayList<>();

    //构造函数
    public Chessboard(Context context) {
        super(context);
    }

    //这个构造方法必须实现,否则会加载出错    我的手机android4.4.4 默认调用的这个构造方法
    public Chessboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0xfff1d38d);//设置背景色
        initPaintAndBitmap();
    }

    public Chessboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0xfff1d38d);//设置背景色
        initPaintAndBitmap();
    }

    public static void setGamePattern(GamePattern gamePattern) {
        Chessboard.gamePattern = gamePattern;
    }

    /**
     * 初始化画笔以及黑白棋图片
     */
    private void initPaintAndBitmap() {
        globalPaint = new Paint();
        globalPaint.setColor(0xff000000); //画笔颜色
        globalPaint.setAntiAlias(true);   //设置抗锯齿
        globalPaint.setDither(true);      //设置防抖动
        globalPaint.setStyle(Paint.Style.STROKE);  //设置图形是空心的
        //初始化黑白棋 棋子 图片
        whiteChessBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_xfhy);
        blackChessBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_xfhy);
    }

    /**
     * 初始化游戏音效
     */
    private void initPlaySound() {
        //实例化AudioManager对象，控制声音
        audioManager = (AudioManager) MyApplication.getContext().
                getSystemService(MyApplication.getContext().AUDIO_SERVICE);

//最大音量
        audioMaxVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//当前音量
        audioCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        map.put(0, playSound.load(MyApplication.getContext(), R.raw.chess_sound, 1));
        map.put(1, playSound.load(MyApplication.getContext(), R.raw.chess_sound, 1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        showInfo = MainActivity.locationInfo;
        btn_restart = MainActivity.btn_restart;
        btn_undo = MainActivity.btn_undo;
        btn_restart.setOnClickListener(new OnClickRestartListener());
        btn_undo.setOnClickListener(new OnClickUndoListener());
        //画棋盘上的线
        drawChessboardLines(canvas);

        //画棋子
        drawChesses(canvas);

        //初始化声音
        initPlaySound();
    }

    /**
     * 画棋盘
     *
     * @param canvas
     */
    public void drawChessboardLines(Canvas canvas) {
        for (Line line : lines) {
            canvas.drawLine(line.xStart, line.yStart, line.xStop, line.yStop, globalPaint);
        }
    }

    /**
     * 当屏幕大小改变时
     * 初始横线和竖线的数目
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */

    private static float rowSize=3*1.0f/4;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        //拿到宽
        mPanelWidth = w;
        //分割
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;
        int mWhiteWidth = (int) (mLineHeight * rowSize);

        maxX = (int) Math.floor(w / chequerSize);         //屏幕宽度除以点的大小
        maxY = (int) Math.floor(h / chequerSize);   //棋盘
        Log.d("Dallas", "maxX:--------------->" + Integer.toString(maxX));
        Log.d("Dallas", "maxY:--------------->" + Integer.toString(maxY));

        //初始化所有棋子,注意,一个横排有maxY+1个
        allChessCoord = new Point[maxY + 1][maxX + 1];

        //设置X,Y坐标微调值,目的整个框居中
        xOffset = ((w - (chequerSize * maxX)) / 2);
        yOffset = ((h - (chequerSize * maxY)) / 2);
        Log.d("Dallas", "xOffset:------------->" + String.valueOf(xOffset) + "dp");
        Log.d("Dallas", "yOffset:------------->" + String.valueOf(yOffset) + "dp");

        whiteChessBitmap = Bitmap.createScaledBitmap(whiteChessBitmap, 65, 65, false);
        blackChessBitmap = Bitmap.createScaledBitmap(blackChessBitmap, 65, 65, false);

        //创建棋盘上的线条
        createLines();
        //初始化所有棋子
        initAllChesses();
    }

    /**
     * 绘制棋子
     *
     * @param canvas
     */
    private void drawChesses(Canvas canvas) {
        Log.d("Dallas", "绘制棋子");
        int wSize = allWhiteChessList.size();
        for (int i = 0; i < wSize; i++) {
            Point wp = allWhiteChessList.get(i);
            canvas.drawBitmap(whiteChessBitmap, wp.getX() -xOffset-15, wp.getY() - yOffset-15, null);

            //canvas.drawBitmap(whiteChessBitmap, wp.getX() - xOffset, wp.getY() - yOffset, null);
        }
        int bSize = allBlackChessList.size();
        for (int i = 0; i < bSize; i++) {
            Point bp = allBlackChessList.get(i);
            canvas.drawBitmap(blackChessBitmap, bp.getX()-xOffset-15, bp.getY()-yOffset-15, null);
            //canvas.drawBitmap(blackChessBitmap, bp.getX() - xOffset, bp.getY() - yOffset, null);
        }
    }

    /**
     * 创建棋盘上的线条
     */
    private void createLines() {

        float xStart = 0, yStart = 0, xStop = 0, yStop = 0;
        //竖线
        for (int i = 0; i <= maxX; i++) {
            xStart = xOffset + i * chequerSize;
            yStart = yOffset;
            xStop = xOffset + i * chequerSize;
            yStop = yOffset + maxY * chequerSize;
            lines.add(new Line(xStart, yStart, xStop, yStop));
        }
        //横线
        for (int i = 0; i <= maxY; i++) {
            xStart = xOffset;
            yStart = yOffset + i * chequerSize;
            xStop = xOffset + maxX * chequerSize;
            yStop = yOffset + i * chequerSize;
            lines.add(new Line(xStart, yStart, xStop, yStop));
        }
    }

    /**
     * 初始化所有的棋子
     */
    private void initAllChesses() {
        for (int i = 0; i <= maxY; i++) {
            for (int j = 0; j <= maxX; j++) {
                allChessCoord[i][j] = new Point(xOffset + j * chequerSize, yOffset + i * chequerSize);
            }
        }
    }

    /**
     * 获取用户点击事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        //判断当前游戏状态
        if (gameState == END) {
            return false;
        }

        //如果是点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            //当前用户点击的位置
            Point currentLocation = new Point(x, y);

            //判断之前是否已经下了该位置的棋
            if (allBlackChessList.contains(currentLocation) ||
                    allWhiteChessList.contains(currentLocation)) {
                return false;
            }

            /*------------------获取当前用户点击位置与哪个棋子位置最近----------------------------*/
            int distance = (int) Math.hypot((currentLocation.getX() - allChessCoord[0][0].getX()),
                    (currentLocation.getY() - allChessCoord[0][0].getY()));
            Point tempPointCoord = null;
            Point correctPoint = null;   //正确的点
            int row = 0, col = 0;   //需要添加的那个棋子在二维数组中的行列值
            for (int i = 0; i <= maxY; i++) {
                for (int j = 0; j <= maxX; j++) {
                    tempPointCoord = new Point(allChessCoord[i][j].getX(),
                            allChessCoord[i][j].getY());
                    int distance2 = (int) Math.hypot((currentLocation.getX() -
                                    tempPointCoord.getX()),
                            (currentLocation.getY() - tempPointCoord.getY()));
                    if (distance2 <= distance) {
                        row = i;
                        col = j;
                        distance = distance2;
                        correctPoint = tempPointCoord;
                    }
                }
            }
            //判断之前是否已经下了该位置的棋
            if (allBlackChessList.contains(correctPoint) ||
                    allWhiteChessList.contains(correctPoint)) {
                return false;
            }
            if(allChessCoord[row][col].getChessType() != ChessType.NOCHESS){
                return false;
            }

            //没有找到正确的点
            if (correctPoint == null) {
                return false;
            }


            //判断当前是否是人人对战
            if(gamePattern == GamePattern.MANANDMAN){
                //如果当前是黑子下棋,则添加到黑棋数据里面
                if (isBlack) {
                    allChessCoord[row][col].setChessType(ChessType.BLACK);
                    correctPoint.setChessType(ChessType.BLACK);  //设置棋子类型是黑子
                    playSound.play(
                            map.get(0),//声音资源
                            volumnRatio,//左声道
                            volumnRatio,//右声道
                            1,//优先级
                            0,//循环次数，0是不循环，-1是一直循环
                            1);//回放速度，0.5~2.0之间，1为正常速度
                    allBlackChessList.add(correctPoint);
                } else {
                    allChessCoord[row][col].setChessType(ChessType.WHITE);
                    //设置棋子类型是白子
                    correctPoint.setChessType(ChessType.WHITE);
                    playSound.play(
                            map.get(0),//声音资源
                            volumnRatio,//左声道
                            volumnRatio,//右声道
                            1,//优先级
                            0,//循环次数，0是不循环，-1是一直循环
                            1);//回放速度，0.5~2.0之间，1为正常速度
                    allWhiteChessList.add(correctPoint);
                }
                invalidate();         //View界面重绘
                isBlack = !isBlack;   //下了棋之后,下一个下棋的人不是自己.
                judgeWhoWin(correctPoint, row, col);   //判断输赢
            } else if(gamePattern == GamePattern.MANANDMACHINE){

                /*---------------人是黑子,先添加黑棋--------------------*/
                allChessCoord[row][col].setChessType(ChessType.BLACK);
                correctPoint.setChessType(ChessType.BLACK);  //设置棋子类型是黑子
                playSound.play(
                        map.get(0),//声音资源
                        volumnRatio,//左声道
                        volumnRatio,//右声道
                        1,//优先级
                        0,//循环次数，0是不循环，-1是一直循环
                        1);//回放速度，0.5~2.0之间，1为正常速度
                allBlackChessList.add(correctPoint);
                judgeWhoWin(correctPoint, row, col);   //判断输赢
                invalidate();         //View界面重绘

                //电脑下棋
                Point point = getAIChess();    //该点在棋盘中的横纵坐标保存在point中
                int tempX = point.getX();
                int tempY = point.getY();
                allChessCoord[point.getX()][point.getY()].setChessType(ChessType.WHITE);
                point.setChessType(ChessType.WHITE);
                getCoordinateByRowCol(point,point.getX(),point.getY());
                allWhiteChessList.add(point);
                Log.d("Dallas","电脑下棋-------->"+point.toString());
                invalidate();         //View界面重绘
                judgeWhoWin(point, tempX, tempY);   //判断输赢
            }

            //Log.d("Dallas", "添加" + correctPoint.toString());
            //invalidate();         //View界面重绘
            //judgeWhoWin(correctPoint, row, col);   //判断输赢

        }
        return super.onTouchEvent(event);
    }









    /**
     * 判断输赢
     *
     * @param point 当前下的棋子
     * @param row   该棋子在所有棋子二维数组中的横坐标   <= maxY
     * @param col   该棋子在所有棋子二维数组中的纵坐标   <= maxX
     */
    private void judgeWhoWin(Point point, int row, int col) {
        int chessCount = 0;  //连着的棋子数量
        /*------------------------判断横向--------------------------*/
        //判断棋子右边
        for (int j = col + 1; j <= maxX; j++) {
            //从该棋子的右边一个开始
            if (allChessCoord[row][j].getChessType() == point.getChessType()) {
                chessCount++;
            } else {
                break;
            }
        }
        showWin(point, chessCount);    //显示当前是谁(黑棋白棋)赢了

        //判断左边
        for (int j = col - 1; j >= 0; j--) {
            if (allChessCoord[row][j].getChessType() == point.getChessType()) {
                chessCount++;
            } else {
                break;
            }
        }
        showWin(point, chessCount);    //显示当前是谁(黑棋白棋)赢了
        chessCount = 0;

        /*------------------------判断竖向--------------------------*/
        //判断正上方
        for (int i = row - 1; i >= 0; i--) {
            if (allChessCoord[i][col].getChessType() == point.getChessType()) {
                chessCount++;
            } else {
                break;
            }
        }
        showWin(point, chessCount);    //显示当前是谁(黑棋白棋)赢了

        //判断正下方
        for (int i = row + 1; i <= maxY; i++) {
            if (allChessCoord[i][col].getChessType() == point.getChessType()) {
                chessCount++;
            } else {
                break;
            }
        }
        showWin(point, chessCount);    //显示当前是谁(黑棋白棋)赢了
        chessCount = 0;

        /*------------------------判断左斜方向--------------------------*/
        //判断西北方向 row-1,col-1
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (allChessCoord[i][j].getChessType() == point.getChessType()) {
                chessCount++;
            } else {
                break;
            }
        }
        showWin(point, chessCount);    //显示当前是谁(黑棋白棋)赢了

        //判断东南方向
        for (int i = row + 1, j = col + 1; i <= maxY && j <= maxX; i++, j++) {
            if (allChessCoord[i][j].getChessType() == point.getChessType()) {
                chessCount++;
            } else {
                break;
            }
        }
        showWin(point, chessCount);    //显示当前是谁(黑棋白棋)赢了
        chessCount = 0;

        /*------------------------判断右斜方向--------------------------*/
        //判断东北方向
        for (int i = row - 1, j = col + 1; i >= 0 && j <= maxX; i--, j++) {
            if (allChessCoord[i][j].getChessType() == point.getChessType()) {
                chessCount++;
            } else {
                break;
            }
        }
        showWin(point, chessCount);    //显示当前是谁(黑棋白棋)赢了

        //判断西南方向
        for (int i = row + 1, j = col - 1; i <= maxY && j >= 0; i++, j--) {
            if (allChessCoord[i][j].getChessType() == point.getChessType()) {
                chessCount++;
            } else {
                break;
            }
        }
        showWin(point, chessCount);    //显示当前是谁(黑棋白棋)赢了
    }

    /**
     * 显示当前是谁(黑棋白棋)赢了
     *
     * @param point //当判断已经有一方赢了的时候,最后下的那个点
     */
    private void showWin(Point point, int chessCount) {
        if (chessCount > 3) {
            //胜负已分
            if (point.getChessType() == ChessType.BLACK) {
                showInfo.setText("黑棋胜利!");
                Toast.makeText(MyApplication.getContext(),
                        "黑棋胜利!",Toast.LENGTH_SHORT).show();
                gameState = END;   //游戏结束
            } else if (point.getChessType() == ChessType.WHITE) {
                showInfo.setText("白棋胜利!");
                Toast.makeText(MyApplication.getContext(),
                        "白棋胜利!",Toast.LENGTH_SHORT).show();
                gameState = END;   //游戏结束
            }
        }
    }

    //重新开始 按钮功能
    class OnClickRestartListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showInfo.setText("欢迎使用终极五子棋!");
            //移除所有棋子
            allWhiteChessList.clear();
            allBlackChessList.clear();
            //设置棋盘上所有棋子为NOCHESS
            for (int i = 0; i < maxY + 1; i++) {
                for (int j = 0; j < maxX + 1; j++) {
                    allChessCoord[i][j].setChessType(ChessType.NOCHESS);
                }
            }
            gameState = START;  //游戏状态设为START
            isBlack = true;   //黑棋先开始
            invalidate();         //View界面重绘
        }
    }

    //设置那个位置为无棋子
    void setNoChess(Point tempPoint){
        for (int i = 0; i <= maxY; i++) {
            for (int j = 0; j <= maxX; j++) {
                if (allChessCoord[i][j].equals(tempPoint)) {
                    allChessCoord[i][j].setChessType(ChessType.NOCHESS);
                    break;
                }
            }
        }
    }

    //悔棋按钮功能
    class OnClickUndoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //首先判断是否棋盘上有棋子
            if (allWhiteChessList.size() == 0 && allBlackChessList.size() == 0) {
                Toast.makeText(MyApplication.getContext(), "亲,现在棋盘上没有棋子哦",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if(gamePattern == GamePattern.MANANDMAN){
                Point tempPoint = null;
                //如果当前是黑子下棋,则说明上一步是白子下的棋
                if (isBlack) {
                    if (allWhiteChessList.size() > 0 && gameState == START) {
                        tempPoint = allWhiteChessList.remove(allWhiteChessList.size() - 1);
                    }
                } else {
                    if (allBlackChessList.size() > 0 && gameState == START) {
                        tempPoint = allBlackChessList.remove(allBlackChessList.size() - 1);
                    }
                }

                //设置那个棋盘位置为无棋子
                setNoChess(tempPoint);
                isBlack = !isBlack;   //下棋人调换
            } else if(gamePattern == GamePattern.MANANDMACHINE){
                //人机对战的话,则悔棋一次,需要去掉2颗棋子
                Point tempPoint = null;
                if (allWhiteChessList.size() > 0 && gameState == START) {
                    tempPoint = allWhiteChessList.remove(allWhiteChessList.size() - 1);
                    //设置那个棋盘位置为无棋子
                    setNoChess(tempPoint);
                }
                if (allBlackChessList.size() > 0 && gameState == START) {
                    tempPoint = allBlackChessList.remove(allBlackChessList.size() - 1);
                    //设置那个棋盘位置为无棋子
                    setNoChess(tempPoint);
                }
            }

            invalidate();         //View界面重绘
        }
    }

    //已知棋子在棋盘的行和列获取棋子坐标
    void getCoordinateByRowCol(Point point,int row,int col){
        point.setX(xOffset+col*chequerSize);
        point.setY(yOffset+row*chequerSize);
    }

    //得到一个当前最好的棋子
    Point getAIChess(){
        //自己是白子
        //首先判断对手的处境
        //历遍棋盘，遇到空点则计算价值，取最大价值点下子
        //写个函数去计算那个空点的价值
        int score;            //当前空点的分数
        int maxScore = 0;     //最高分
        int currentX = 0;
        int currentY = 0;
        for (int i = 0; i <= maxY; i++) {
            for(int j = 0; j <= maxX; j++){
                if(allChessCoord[i][j].getChessType() == ChessType.NOCHESS){
                    //判断当前空点,  对于用户的棋子能得多少分
                    score = getPointScore(i,j,ChessType.BLACK);
                    if(score > maxScore){
                        currentX = i;
                        currentY = j;
                        maxScore = score;
                    }
                }

            }
        }


        //其次判断自己的处境
        int score2;            //当前空点的分数
        int maxScore2 = 0;     //对于白子的最高分
        int currentX2 = 0;
        int currentY2 = 0;
        for (int i = 0; i <= maxY; i++) {
            for(int j = 0; j <= maxX; j++){
                if(allChessCoord[i][j].getChessType() == ChessType.NOCHESS){
                    score2 = getPointScore(i,j,ChessType.WHITE);
                    if(score2 > maxScore2){
                        currentX2 = i;
                        currentY2 = j;
                        maxScore2 = score2;
                    }
                }

            }
        }

        Point point;
        //如果需要防守的分数比需要进攻的分数高
        if(maxScore > maxScore2){
            point = new Point(currentX,currentY);
        } else {
            point = new Point(currentX2,currentY2);
        }
        Log.d("Dallas","最高分----------------------->"+maxScore+"      点"+point.toString());

        return point;
    }

    //获取当前空点在棋盘中的得分
    int getPointScore(int row,int col,ChessType chessType){
        int score = 0;
        int chessCount = 0;  //连着的棋子数量
        /*------------------------判断横向--------------------------*/
        //判断棋子右边
        for (int j = col + 1; j <= maxX; j++) {
            //从该棋子的右边一个开始   //右边如果等于空格退出循环
            if (allChessCoord[row][j].getChessType() == chessType) {
                //如果右边有一颗白子(即用户下的棋),则白子数量+1
                chessCount++;
            } else {
                break;
            }
        }
        //根据当前棋子数目计算改点的分数,加到分数中
        score += calculateScoreByCount(chessCount);
        chessCount = 0;

        //判断左边
        for (int j = col - 1; j >= 0; j--) {
            if (allChessCoord[row][j].getChessType() == chessType) {
                chessCount++;
            }else {
                break;
            }
        }
        //根据当前棋子数目计算改点的分数,加到分数中
        score += calculateScoreByCount(chessCount);
        chessCount = 0;

        /*------------------------判断竖向--------------------------*/
        //判断正上方
        for (int i = row - 1; i >= 0; i--) {
            if (allChessCoord[i][col].getChessType() == chessType) {
                chessCount++;
            } else {
                break;
            }
        }
        //根据当前棋子数目计算改点的分数,加到分数中
        score += calculateScoreByCount(chessCount);
        chessCount = 0;

        //判断正下方
        for (int i = row + 1; i <= maxY; i++) {
            if (allChessCoord[i][col].getChessType() == chessType) {
                chessCount++;
            } else {
                break;
            }
        }
        //根据当前棋子数目计算改点的分数,加到分数中
        score += calculateScoreByCount(chessCount);
        chessCount = 0;

        /*------------------------判断左斜方向--------------------------*/
        //判断西北方向 row-1,col-1
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (allChessCoord[i][j].getChessType() == chessType) {
                chessCount++;
            } else {
                break;
            }
        }
        //根据当前棋子数目计算改点的分数,加到分数中
        score += calculateScoreByCount(chessCount);
        chessCount = 0;

        //判断东南方向
        for (int i = row + 1, j = col + 1; i <= maxY && j <= maxX; i++, j++) {
            if (allChessCoord[i][j].getChessType() == chessType) {
                chessCount++;
            } else {
                break;
            }
        }
        //根据当前棋子数目计算改点的分数,加到分数中
        score += calculateScoreByCount(chessCount);
        chessCount = 0;

        /*------------------------判断右斜方向--------------------------*/
        //判断东北方向
        for (int i = row - 1, j = col + 1; i >= 0 && j <= maxX; i--, j++) {
            if (allChessCoord[i][j].getChessType() == chessType) {
                chessCount++;
            } else {
                break;
            }
        }
        //根据当前棋子数目计算改点的分数,加到分数中
        score += calculateScoreByCount(chessCount);
        chessCount = 0;

        //判断西南方向
        for (int i = row + 1, j = col - 1; i <= maxY && j >= 0; i++, j--) {
            if (allChessCoord[i][j].getChessType() == chessType) {
                chessCount++;
            } else {
                break;
            }
        }
        //根据当前棋子数目计算改点的分数,加到分数中
        score += calculateScoreByCount(chessCount);
        return score;
    }

    //通过白子数量计算改点的分数
    int calculateScoreByCount(int count){
        switch(count){
            case 0:
                return PatternScore.NOSERIAL;
            case 1:
                return PatternScore.ONESERIAL;
            case 2:
                return PatternScore.TWOSERIAL;
            case 3:
                return PatternScore.THREESERIAL;
            case 4:
                return PatternScore.FOURSERIAL;
            default:
                return 0;
        }
    }

}
