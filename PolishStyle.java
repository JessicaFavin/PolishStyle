import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;

public class PolishStyle {

  private int mode;
  private int remote;
  private Socket socket;
  private InputStream userInput;
  private PrintStream userOutput;
  private Scanner sc;
  private File logFile;
  private FileOutputStream fos;

  public PolishStyle() {
    //launch locally by default
    initPolishStyle(0);
  }


  public PolishStyle(String[] args) {
    if(args.length == 1 && (args[0].equals("--remote") || args[0].equals("-r"))){
      initPolishStyle(1);
    } else if(args.length == 0){
      initPolishStyle(0);
    } else {
      System.out.println("java PolishStyle [-r|--remote]");
    }
  }

  public void initPolishStyle(int remote) {
    this.mode = 1;
    this.remote = remote;

    sc = new Scanner(System.in);

    this.initRemote();
    this.setMode();
    this.mainLoop();
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
    userOutput.println("Select a mode :\n"+
    "1. Run interpretor\n"+
    "2. Run interpretor and log input\n"+
    "3. Run from log file");
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
        logFile = new File("polish_style.log");
        if(logFile.exists()){
          sc = new Scanner(logFile);
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
        logFile = new File("polish_style.log");
        //create logFile only if it doesn't exist
        logFile.createNewFile();
        fos = new FileOutputStream(logFile, false);
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

  public void mainLoop() {
    VectorStack stack = new VectorStack();
    boolean stop = false;
    String input = "";
    String validInput;
    try{
      userOutput.println("-------------------------------------------------------\n"+
      "Press q to quit\n"+
      "Default operation is push\n"+
      "Operators are : + - * / drop swap clear\n"+
      "Anything other than operand or operator will be ignored\n"+
      "-------------------------------------------------------");
      while (!stop) {
        userOutput.println(stack);
        if(!sc.hasNextLine()){
          //No more input to read (EOF or Ctrl+D)
          System.exit(4);
        }
        input = readInstruction();
        if(input.equals("q")) {
          stop = true;
          break;
        }
        validInput = executeInstruction(stack, input);
        //non empty instruction
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
      userOutput.println("I/O exception");
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

  private String executeInstruction(VectorStack stack, String input) {
    String validInput = "";
    //validInput += readStackableInt();
    validInput += readStackableVector(stack, input);
    return validInput;
  }
/*
  private String readStackableInt(Stack stack, String input) {
    String[] parts = input.split(" ");
    for(String part : parts) {
      try{
        int value = Integer.parseInt(s);
        //default operation
        stack.push(new StackableInt(value));
        validInput += part+" ";
      } catch(NumberFormatException nfe) {
        String operator = part;
        try{
          switch(operator) {
            case "+":
              stack.add();
              validInput += part+" ";
              break;
            case "-":
              stack.sub();
              validInput += part+" ";
              break;
            case "*":
              stack.mult();
              validInput += part+" ";
              break;
            case "/":
              stack.div();
              validInput += part+" ";
              break;
            case "drop":
              stack.drop();
              validInput += part+" ";
              break;
            case "swap":
              stack.swap();
              validInput += part+" ";
              break;
            case "clear":
              stack.clear();
              validInput += part+" ";
              break;
            default:
              //ignore unrecognized instruction
              break;
          }
        } catch(NotEnoughOperandsException neoe) {
          userOutput.println("Not enough operand in the stack.");
        } catch (ZeroDivisionException zde) {
          userOutput.println("Division by 0 not allowed.");
        }
      }
    }
  }
*/
  private String readStackableVector(VectorStack stack, String input) {
    String validInput = "";
    String[] parts = input.split(" ");
    for(String part : parts) {

      int x =0, y =0;
      Pattern pattern = Pattern.compile("\\(?(\\d+),(\\d+)\\)?");
      Matcher matcher = pattern.matcher(part);
      if(matcher.find()) {
          try {
            x = Integer.parseInt(matcher.group(1));
            y = Integer.parseInt(matcher.group(2));
            //default operation
            stack.push(new StackableVector(x,y));
            validInput += part+" ";
          } catch(NumberFormatException nfe) {
            userOutput.println("What have you done now ? Plz stahp T.T");
          }

      } else {
        String operator = part;
        try{
          switch(operator) {
            case "+":
              stack.add();
              validInput += part+" ";
              break;
            case "-":
              stack.sub();
              validInput += part+" ";
              break;
            case "*":
              stack.mult();
              validInput += part+" ";
              break;
            case "/":
              stack.div();
              validInput += part+" ";
              break;
            case "drop":
              stack.drop();
              validInput += part+" ";
              break;
            case "swap":
              stack.swap();
              validInput += part+" ";
              break;
            case "clear":
              stack.clear();
              validInput += part+" ";
              break;
            default:
              //ignore unrecognized instruction
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

  public static void main(String[] args) {
    new PolishStyle(args);
  }

}
