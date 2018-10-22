public class Stackable {
  private int value;

  public Stackable(int value) {
    this.value = value;
  }

  public Stackable add(Stackable obj) {
    return new Stackable(this.value + obj.getValue());
  }

  public Stackable sub(Stackable obj) {
    return new Stackable(this.value - obj.getValue());
  }

  public Stackable mult(Stackable obj) {
    return new Stackable(this.value * obj.getValue());
  }

  public Stackable div(Stackable obj) throws ZeroDivisionException {
    if(obj.getValue()==0){
      System.out.println("dic zero stackable");
      throw new ZeroDivisionException();
    }
    return new Stackable(this.value / obj.getValue());
  }

  private int getValue() {
    return this.value;
  }

  public boolean isNull(){
    return this.value==0;
  }

  @Override
  public String toString() {
    return String.valueOf(this.value);
  }

}
