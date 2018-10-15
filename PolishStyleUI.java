import java.util.Scanner;
import java.io.*;

public class PolishStyleUI {

  private int mode;

  public PolishStyleUI() {
    this(1);
  }

  public PolishStyleUI(int mode) {
    this.mode = mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public void run(){
    switch(mode){
      case 1:
        //interpet mode
        this.runInterpretor();
        break;
      case 2:
        //interpet + log mode
        this.runLogging();
        break;
      case 3:
        //replay log mode
        this.runLogFile();
        break;
      default:
        break;
    }
  }

  public void runInterpretor() {
    Stack stack = new Stack();
    boolean stop = false;
    String input;

    System.out.println("-------------------Interpretor mode-------------------");
    System.out.println("Press q to quit");
    System.out.println("Anything other than number or operand will be ignored");
    while (!stop) {
      //readInstruction
      System.out.println(stack);
      input = readInstruction();
      if(input.equals("q")) {
        stop = true;
        break;
      }
      executeInstruction(stack, input);
    }
  }

  public void runLogging() {
    Stack stack = new Stack();
    boolean stop = false;
    String input;
    String validInput;
    File file = new File("polish_style.log");
    FileOutputStream fos;
    try{
      file.createNewFile();
      fos = new FileOutputStream(file, false);
      System.out.println("-------------------Log mode-------------------");
      System.out.println("Press q to quit");
      System.out.println("Anything other than number or operand will be ignored");
      while (!stop) {
        System.out.println(stack);
        input = readInstruction();
        if(input.equals("q")) {
          stop = true;
          break;
        }
        validInput = executeInstruction(stack, input);
        fos.write(validInput.getBytes());
      }
      fos.write("\n".getBytes());
      fos.close();
    } catch(FileNotFoundException fnfe) {
      //this should not happen
    }catch(IOException ioe) {
      System.out.println("IO exception");
      System.exit(1);
    }
  }

  public void runLogFile() {
    System.out.println("-------------------File mode-------------------");
    //to do
    //for each line read print import junit.framework.TestCase;
    //execute it
    //print stack
  }

  public String readInstruction(){
    Scanner sc = new Scanner(System.in);
    String input = sc.nextLine();
    return input;
  }

  public String executeInstruction(Stack stack, String input) {
    String validInput = "";
    String[] parts = input.split(" ");
    for(String s : parts) {
      try{
        int value = Integer.parseInt(s);
        Stackable obj = new Stackable(value);
        stack.push(obj);
        validInput += s+" ";
      } catch(NumberFormatException nfe) {
        String operator = s;
        try{
          switch(operator) {
            case "+":
              stack.add();
              validInput += s+" ";
              break;
            case "-":
              stack.sub();
              validInput += s+" ";
              break;
            case "*":
              stack.mult();
              validInput += s+" ";
              break;
            case "/":
              stack.div();
              validInput += s+" ";
              break;
            default:
              break;
          }
        } catch(NotEnoughOperandsException neoe) {
          System.out.println("Not enough operand in the stack.");
        }
      }
    }
    return validInput;
  }
}
