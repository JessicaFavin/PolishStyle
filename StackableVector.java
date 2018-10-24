public class StackableVector {
  private int x;
  private int y;

  public StackableVector(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public StackableVector add(StackableVector obj) {
    return new StackableVector(this.x + obj.getX(), this.y + obj.getY());
  }

  public StackableVector sub(StackableVector obj) {
    return new StackableVector(this.x - obj.getX(), this.y - obj.getY());
  }

  public StackableVector mult(StackableVector obj) {
    return new StackableVector(this.x * obj.getX(), this.y * obj.getY());
  }

  public StackableVector div(StackableVector obj) throws ZeroDivisionException {
    if(obj.getX()==0 || obj.getY()==0){
      throw new ZeroDivisionException();
    }
    return new StackableVector(this.x / obj.getX(), this.y / obj.getY());
  }

  private int getX() {
    return this.x;
  }

  private int getY() {
    return this.y;
  }

  public boolean isNull(){
    return this.x==0;
  }

  @Override
  public String toString() {
    return "v("+String.valueOf(this.x)+","+String.valueOf(this.y)+")";
  }
}
