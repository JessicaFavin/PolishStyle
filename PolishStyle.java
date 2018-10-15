import java.util.Scanner;

public class PolishStyle {
  public static void main(String[] args) {
    Stack stack = new Stack();
    Scanner sc = new Scanner(System.in);
    String input;
    boolean stop = false;
    System.out.println("Press q to quit");
    System.out.println("Anything other than number or operand will be ignored");
    ask: while (!stop) {
      input = sc.nextLine();
      if(input.equals("q")) {
        stop = true;
        break ask;
      }
      String[] parts = input.split(" ");
      for(String s : parts) {
        try{
          int value = Integer.parseInt(s);
          Stackable obj = new Stackable(value);
          stack.push(obj);
        } catch(NumberFormatException nfe) {
          //nfe.printStackTrace();
          String operator = s;
          switch(operator) {
            case "+":
              stack.add();
              break;
            case "-":
              stack.sub();
              break;
            case "*":
              stack.mult();
              break;
            case "/":
              stack.div();
              break;
            default:
              break;
          }
        }
      }
      System.out.println(stack);
    }
  }
}
