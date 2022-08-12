package SweeperChat;

public class MyTuple<T> {
    private T x;
    private T y;
    public MyTuple(T nX, T nY){
        x = nX;
        y = nY;
    }
    public T getX(){
        return this.x;
    }
    public T getY(){
        return this.y;
    }
    public void setX(T nX){
        this.x = nX;
    }
    public void setY(T nY){
        this.y = nY;
    }
    public String toString(){
        return "(" + x + "," + y + ")";
    }
    public boolean equals(Object other){
        if (other instanceof MyTuple<?>){
            MyTuple<?> o = (MyTuple<?>) other;
            return o.x.equals(this.x) && o.y.equals(this.y);
        }
        return false;
    }

}
