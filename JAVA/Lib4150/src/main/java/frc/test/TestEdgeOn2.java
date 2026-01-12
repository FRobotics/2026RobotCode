
package frc.test;

import frc.robot.Lib4150DigitalEdgeOn;

public class TestEdgeOn2 {

  // --------constructor -- cant be called.
  private TestEdgeOn2() {
  }

  /**
   *   Main routine.   put your test code here....
   */
  public static void executeTest() {
    System.out.println("Testing function: Lib4150DigitalEdgeOn");
    Lib4150DigitalEdgeOn testEdgeOn = new Lib4150DigitalEdgeOn();

    boolean result1 = testEdgeOn.EdgeOnExec(true);
    boolean result2 = testEdgeOn.EdgeOnExec(false);
    boolean result3 = testEdgeOn.EdgeOnExec(false);
    boolean result4 = testEdgeOn.EdgeOnExec(true);
    System.out.println(result1 + " "+result2 +" "+ result3 +" "+ result4);
    System.out.println("Done testing function: Lib4150DigitalEdgeOn");

    System.out.println("Testing function: Lib4150DigitalRateOfChange");
    Lib4150RateOfChange testRateOfChange = new Lib4150RateOfChange();

    double result5 = testRateOfChange.RateOfChangeCalc(4, 1);
    double result6 = testRateOfChange.RateOfChangeCalc(32, 5);
    double result7 = testRateOfChange.RateOfChangeCalc(7, 0);
    double result8 = testRateOfChange.RateOfChangeCalc(13, 2);
    System.out.println(result5 + " "+result6 +" "+ result7 +" "+ result8);
    System.out.println("Done testing function: Lib4150DigitalRateOfChange");
  }
}
