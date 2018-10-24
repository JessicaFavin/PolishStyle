public class VectorStack {
  private final int MAX = 6;
  private int stackPointer;
  private StackableVector[] stack;

  public VectorStack() {
    stackPointer = 0;
    stack = new StackableVector[MAX];
  }

  public void push(StackableVector obj) {
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

  public StackableVector pop() {
    StackableVector x = null;
    if(!this.isEmpty()){
      x = stack[stackPointer-1];
      stack[--stackPointer] = null;
    }
    return x;
  }

  public void drop() throws NotEnoughOperandsException {
    if(!this.isEmpty()){
      stack[--stackPointer] = null;
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  public void swap() throws NotEnoughOperandsException {
    if(this.enoughOperand()){
      StackableVector x = this.pop();
      StackableVector y = this.pop();
      this.push(x);
      this.push(y);
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  public void clear() {
    stackPointer = 0;
    stack = new StackableVector[MAX];
  }

  public void add() throws NotEnoughOperandsException{
    if(this.enoughOperand()){
      StackableVector x = this.pop();
      StackableVector y = this.pop();
      StackableVector result = y.add(x);
      this.push(result);
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  public void sub() throws NotEnoughOperandsException{
    if(this.enoughOperand()){
      StackableVector x = this.pop();
      StackableVector y = this.pop();
      StackableVector result = y.sub(x);
      this.push(result);
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  public void mult() throws NotEnoughOperandsException{
    if(this.enoughOperand()){
      StackableVector x = this.pop();
      StackableVector y = this.pop();
      StackableVector result = y.mult(x);
      this.push(result);
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  public void div() throws NotEnoughOperandsException, ZeroDivisionException {
    if(this.enoughOperand()){
      StackableVector x = this.pop();
      StackableVector y = this.pop();
      if(x.isNull()) {
        this.push(y);
        this.push(x);
        throw new ZeroDivisionException();
      } else {
        StackableVector result = y.div(x);
        this.push(result);
      }
    } else {
      throw new NotEnoughOperandsException();
    }
  }

  @Override
  public String toString() {
    String res = "";
    StackableVector obj;
    res+= "+----------+\n";
    for(int i=MAX; i>0; i--) {
      res += ("| ");
      if((obj = stack[i-1])!=null) {
        res += String.format("%8s", obj.toString());
      } else {
        res += "        ";
      }
      res += (" |\n");
      res+= "+----------+\n";
    }
    return res;
  }

}
