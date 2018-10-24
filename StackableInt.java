public class StackableInt {
  private int value;

  public StackableInt(int value) {
    this.value = value;
  }

  public StackableInt add(StackableInt obj) {
    return new StackableInt(this.value + obj.getValue());
  }

  public StackableInt sub(StackableInt obj) {
    return new StackableInt(this.value - obj.getValue());
  }

  public StackableInt mult(StackableInt obj) {
    return new StackableInt(this.value * obj.getValue());
  }

  public StackableInt div(StackableInt obj) throws ZeroDivisionException {
    if(obj.getValue()==0){
      throw new ZeroDivisionException();
    }
    return new StackableInt(this.value / obj.getValue());
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
