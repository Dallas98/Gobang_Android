package com.dallas.gobang;



public class Point {
    private int x;   //横坐标
    private int y;   //竖坐标
    private final int PRIMEX = 15;
    private final int PRIMEY = 15;
    private ChessType chessType;    //当前坐标点的棋子类型
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        chessType = ChessType.NOCHESS;  //默认是没有棋子
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ChessType getChessType() {
        return chessType;
    }

    public void setChessType(ChessType chessType) {
        this.chessType = chessType;
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }

    @Override
    public int hashCode() {
        int result = PRIMEX * x;
        result += PRIMEY * y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(obj instanceof Point){
            Point other = (Point) obj;
            if(x != other.x){
                return false;
            }
            if(y != other.y){
                return false;
            }
        }
        return true;
    }

}
