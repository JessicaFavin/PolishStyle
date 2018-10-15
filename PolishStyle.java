import java.util.Scanner;
import java.util.InputMismatchException;

public class PolishStyle {
  public static void main(String[] args) {
    PolishStyleUI psui = new PolishStyleUI();
    //menu
    int mode = 1;
    System.out.println("Select a mode :");
    System.out.println("1. Run interpretor");
    System.out.println("2. Run interpretor + log input");
    System.out.println("3. Run from log file");
    Scanner sc = new Scanner(System.in);
    boolean wrong = true;
    askMode: do {
      try {
        mode = sc.nextInt();
        System.out.println(mode);
        if(mode>0 && mode<4){
          wrong = false;
        }
      } catch(InputMismatchException ime) {
        //flush scanner content
        sc.nextLine();
      }
    } while(wrong);

    psui.setMode(mode);
    psui.run();
  }
}
