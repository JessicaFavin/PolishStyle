public class Stack {
  private final int MAX = 6;
  private int stackPointer;
  private Stackable[] stack;

  public Stack() {
    stackPointer = 0;
    stack = new Stackable[MAX];
  }

  public void push(Stackable obj) {
    if(stackPointer != MAX){
      stack[stackPointer++] = obj;
    }
  }
  public boolean isEmpty() {
    return stackPointer<=0;
  }

  public boolean enoughOperand() {
    return stackPointer>=2;
  }

  public Stackable pop() {
    Stackable x = null;
    if(!this.isEmpty()){
      x = stack[stackPointer-1];
      stack[--stackPointer] = null;
    }
    return x;
  }

  public void add() throws NotEnoughOperandsException{
    if(this.enoughOperand()){
      Stackable x = this.pop();
      Stackable y = this.pop();
      Stackable result = y.add(x);
      this.push(result);
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  public void sub() throws NotEnoughOperandsException{
    if(this.enoughOperand()){
      Stackable x = this.pop();
      Stackable y = this.pop();
      Stackable result = y.sub(x);
      this.push(result);
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  public void mult() throws NotEnoughOperandsException{
    if(this.enoughOperand()){
      Stackable x = this.pop();
      Stackable y = this.pop();
      Stackable result = y.mult(x);
      this.push(result);
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  public void div() throws NotEnoughOperandsException{
    if(this.enoughOperand()){
      Stackable x = this.pop();
      Stackable y = this.pop();
      Stackable result = y.div(x);
      this.push(result);
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  @Override
  public String toString() {
    String res = "";
    Stackable obj;
    res+= "+------+\n";
    for(int i=MAX; i>0; i--) {
      res += ("| ");
      if((obj = stack[i-1])!=null) {
        res += String.format("%4s", obj.toString());
      } else {
        res += "    ";
      }
      res += (" |\n");
      res+= "+------+\n";
    }
    return res;
  }

}
