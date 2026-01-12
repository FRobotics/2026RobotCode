
package frc.test;

import frc.robot.Lib4150DigitalEdgeChange;

public class TestEdgeChange {

  // --------constructor -- cant be called.
  private TestEdgeChange() {
  }

  /**
   *   Main routine.   put your test code here....
   */
  public static void executeTest() {

    System.out.println("Testing function: Lib4150XXXXXX");

    Lib4150DigitalEdgeChange testEdgeChange= new Lib4150DigitalEdgeChange();

    boolean result1 = testEdgeChange.EdgeChangeExec(false);
    boolean result2 = testEdgeChange.EdgeChangeExec(true);
    boolean result3 = testEdgeChange.EdgeChangeExec(false);
    boolean result4 = testEdgeChange.EdgeChangeExec(true);
    System.out.println(result1 +""+ result2 +""+ result3 +""+ result4);
    System.out.println("Done testing function: Lib4150XXXXXX");
  }
}
