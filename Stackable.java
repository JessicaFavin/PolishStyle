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

  public Stackable div(Stackable obj) {
    return new Stackable(this.value / obj.getValue());
  }

  public int getValue() {
    return this.value;
  }

}
