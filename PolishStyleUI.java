import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;
import java.io.*;
import java.net.*;

public class PolishStyleUI {

  private int mode;
  private int remote;
  private Socket socket;
  private InputStream userInput;
  private PrintStream userOutput;
  private Scanner sc;
  private File file;
  private FileOutputStream fos;

  public PolishStyleUI() {
    this.mode = 1;
    this.remote = 0;

    sc = new Scanner(System.in);

    this.setRemote();
    this.setMode();
    this.run();

  }

  public void setRemote() {
    menuRemote();
    initRemote();
  }

  private void menuRemote() {
    System.out.println("Run 0.locally or 1.remotely ?");
    boolean wrongInput = true;
    do {
      try {
        this.remote = sc.nextInt();
        if(this.remote==0 || this.remote==1) {
          wrongInput = false;
        }
      } catch(InputMismatchException ime) {
        //flush scanner content
        sc.nextLine();
      }
    } while(wrongInput);
  }

  private void initRemote() {
    if(this.remote==0) {
      userInput = System.in;
      userOutput = System.out;
    } else {
      //remote TCP conenxion
      try{
        ServerSocket serversocket = new ServerSocket(1111);
        System.out.println("Waiting for client to connect.");
        socket = serversocket.accept();
        System.out.println("Client connected !");
        userInput = socket.getInputStream();
        userOutput = new PrintStream(socket.getOutputStream());
      } catch (IOException ioe) {
        System.out.println("Problem while connecting to client. Bye.");
        System.exit(3);
      }
    }
    sc = new Scanner(userInput);
  }

  public void setMode() {
    menuMode();
    initMode();
  }

  private void menuMode() {
    boolean wrongInput = true;
    userOutput.println("Select a mode :");
    userOutput.println("1. Run interpretor");
    userOutput.println("2. Run interpretor + log input");
    userOutput.println("3. Run from log file");
    do {
      try {
        this.mode = sc.nextInt();
        if(this.mode>0 && this.mode<4){
          wrongInput = false;
        }
      } catch(InputMismatchException ime) {
        //flush scanner content
        sc.nextLine();
      }
    } while(wrongInput);
  }

  private void initMode() {
    if(this.mode==3) {
      try{
        file = new File("polish_style.log");
        if(file.exists()){
          sc = new Scanner(file);
        } else {
          userOutput.println("No polish_style.log log file found.");
          System.exit(2);
        }
      } catch (FileNotFoundException fnfe) {
        userOutput.println("No polish_style.log log file found.");
        System.exit(2);
      }
    } else if (this.mode==2){
      try {
        file = new File("polish_style.log");
        //create file only if it doesn't exist
        file.createNewFile();
        fos = new FileOutputStream(file, false);
      } catch (FileNotFoundException fnfe) {
        userOutput.println("No polish_style.log log file found.");
        System.exit(2);
      } catch (IOException ioe) {
        userOutput.println("I/O exception.");
        System.exit(1);
      }
      sc = new Scanner(userInput);
    } else {
      sc = new Scanner(userInput);
    }
  }

  public void run(){
    Stack stack = new Stack();
    boolean stop = false;
    String input = "";
    String validInput;
    try{
      userOutput.println("Press q to quit");
      userOutput.println("Anything other than number or operand will be ignored");
      while (!stop) {
        userOutput.println(stack);
        if(sc.hasNextLine()){
          input = readInstruction();
        } else {
          //No more input to read (EOF or Ctrl+D)
          System.exit(4);
        }
        if(input.equals("q")) {
          stop = true;
          if(this.isRemote()){
            socket.close();
          }
          break;
        }
        validInput = executeInstruction(stack, input);
        if(!validInput.trim().equals("")){
          userOutput.println("Running : "+validInput);
          if(this.logMode()){
            validInput += "\n";
            fos.write(validInput.getBytes());
          }
        }
      }
      if(this.logMode()){
        fos.close();
      }
      if(this.isRemote()){
        socket.close();
      }
    } catch(IOException ioe) {
      userOutput.println("IO exception");
      System.exit(1);
    }
  }

  private boolean logMode() {
    return this.mode ==2;
  }

  private boolean isRemote() {
    return this.remote==1;
  }

  private String readInstruction(){
    String input = sc.nextLine();
    return input;
  }

  private String executeInstruction(Stack stack, String input) {
    String validInput = "";
    String[] parts = input.split(" ");
    for(String s : parts) {
      try{
        int value = Integer.parseInt(s);
        Stackable obj = new Stackable(value);
        //default operation
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
            /*
            case "pop":
              Stackable obj = stack.pop();
              userOutput.println("Object popped : "+obj);
              validInput += s+" ";
              break;
            */
            case "drop":
              stack.drop();
              validInput += s+" ";
              break;
            case "swap":
              stack.swap();
              validInput += s+" ";
              break;
            case "clear":
              stack.clear();
              validInput += s+" ";
              break;
            default:
            break;
          }
        } catch(NotEnoughOperandsException neoe) {
          userOutput.println("Not enough operand in the stack.");
        } catch (ZeroDivisionException zde) {
          userOutput.println("Division by 0 not allowed.");
        }
      }
    }
    return validInput;
  }
}
