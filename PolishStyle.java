public class PolishStyle {
  public static void main(String[] args) {
    Stack stack = new Stack();
    Stackable obj1 = new Stackable(6);
    Stackable obj2 = new Stackable(3);
    stack.push(obj1);
    System.out.println(stack);
    stack.push(obj2);
    System.out.println(stack);
    stack.push(obj1);
    System.out.println(stack);
    stack.push(obj2);
    System.out.println(stack);
    stack.div();
    System.out.println(stack);
  }
}
