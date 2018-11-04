import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;
import java.io.*;
import java.net.*;
import static java.net.URLDecoder.decode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PolishStyle {

  private int mode;
  private int remote;
  private Socket socket;
  private InputStream userInput;
  private PrintStream userOutput;
  private Scanner sc;
  private File logFile;
  private FileOutputStream fos;
  private static HashMap<String, String> request;

  public PolishStyle() {
    //launch locally by default
    initPolishStyle(0, false);
  }


  public PolishStyle(String[] args) {
    if(args.length == 1 && (args[0].equals("--remote") || args[0].equals("-r"))){
      initPolishStyle(1, false);
    } else if(args.length == 1 && (args[0].equals("--http-server") || args[0].equals("-s"))){
      initPolishStyle(0, true);
    } else if(args.length == 0){
      initPolishStyle(0, false);
    } else {
      System.out.println("java PolishStyle [-r|--remote|-s|--http-server]");
    }
  }

  public void initPolishStyle(int remote, boolean httpServer) {
    this.mode = 1;
    this.remote = remote;

    sc = new Scanner(System.in);

    if(!httpServer){
      this.initRemote();
      this.setMode();
      this.mainLoop();
    } else {
      this.httpServer();
    }
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
    Stack stack = new Stack();
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

  public String sendForm(Stack stack) {
    String res = "";
    Date dNow = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.US);
    dNow = new Date();
    res = "";
    res += "HTTP/1.1 200 OK\r\n"
            + "Date: " + ft.format(dNow) + " GMT\r\n"
            + "Server: Mine\r\n"
            + "Content-Type: text/html; charset=UTF-8\r\n"
            + "Connection: keep-alive\r\n";
    //sent file content
    //----------------------> content length
    String content = "";

    content += "<!DOCTYPE html>\r\n<html>\r\n<body>\r\n<h2>Reverse Polish calculator</h2>\r\n";
    content += stack.toHTML();
    content += "<form method=\"get\" action=\"\">\r\nGive me reverse polish calculation:<br>\r\n";
    content += "<input type=\"textarea\" name=\"calc\" autofocus>\r\n<br><br>\r\n<input type=\"submit\" value=\"Submit\">\r\n</form>\r\n";
    content += "<p>Click Submit to sent.</p>\r\n</body>\r\n</html>\r\n";

    res += "Content-Length: "+content.length();
    res += ("\r\n\r\n");
    res += content;

    res += ("\r\n");
    return res;
  }

  public String send404() {
    Date dNow = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.US);
    String res = "";
    res += "HTTP/1.1 404 Not Found\r\n"
            + "Date: " + ft.format(dNow) + " GMT\r\n"
            + "Server: Mine\r\n"
            + "Content-Type: text/html; charset=UTF-8\r\n"
            + "Connection: close\r\n";
    res += ("\r\n");
    return res;
  }

  public void httpServer() {
    try{
      //init Server connexion
      ServerSocket serversocket = new ServerSocket(8080);
      System.out.println("Waiting for client to connect.");
      String input;
      String output;
      Stack stack = new Stack();
      socket = serversocket.accept();
      System.out.println("Browser connected !");
      userInput = socket.getInputStream();
      sc = new Scanner(userInput);
      userOutput = new PrintStream(socket.getOutputStream());
      request = new HashMap<String, String>();
      //---------------------read index page request----------------------------
      while ((input = sc.nextLine())!=null&&!input.equals("")) {
        String[] res  = input.split(" ", 2);
        String key = res[0].replaceFirst(":","").trim();
        String value = res[1].trim();
        request.put(key, value);
      }
      //------------------------send index page --------------------------------
      if(request.get("GET")!=null){

        String filename = request.get("GET").split(" ")[0];
        filename = decode(filename, "UTF-8");
        if(filename.equals("/")){
          userOutput.print(sendForm(stack));
        } else {
          userOutput.print(send404());
        }
      }

      while(true) {
        request = new HashMap<String, String>();

        while ((input = sc.nextLine())!=null&&!input.equals("")) {
          String[] res  = input.split(" ", 2);
          String key = res[0].replaceFirst(":","").trim();
          String value = res[1].trim();
          request.put(key, value);
        }
        //---------------------send calculation result----------------------------
        if(request.get("GET")!=null){

          String filename = request.get("GET").split(" ")[0];
          if(filename.startsWith("/?calc=")){
            String calc = java.net.URLDecoder.decode(filename, "UTF-8");
            calc = calc.replace("/?calc=", "");
            executeInstruction(stack, calc);
            userOutput.print(sendForm(stack));
          } else {
            userOutput.print(send404());
          }
        }

      }

    } catch (IOException ioe) {
      System.out.println("Problem while connecting to client. Bye.");
      System.exit(3);
    }
  }

  public static void main(String[] args) {
    new PolishStyle(args);
  }

}
