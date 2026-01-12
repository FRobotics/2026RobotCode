package frc.robot;

import frc.test.TestEdgeChange;
import frc.test.TestEdgeOn;
import frc.test.TestEdgeOn2;

public class Main {


  /**
   *   Main routine.   put your test code here....
   */
  public static void main(String... args) {
    System.out.println("Testing function: Lib4150XXXXXX");

    TestEdgeChange.executeTest();
    TestEdgeOn.exexuteTest();
    TestEdgeOn2.executeTest();
    
    System.out.println("Done testing function: Lib4150XXXXXX");
  }
}
