public class Point {
      int x;
      int y;
    Point(int x, int y){
        this.x=x;
        this.y=y;
    }
      int getX(){
        return x;
    }
      int getY(){
        return y;
    }
String loc() {
    String str=getX()+","+getY();
    return str;

}
}
