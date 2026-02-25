package frc.robot;

public class MatchSystem {
  
    private static boolean areRed;
    private static boolean areBlue;

  
    private MatchSystem(){}

    public static void setRed(boolean paramIsRed) {
        areRed = paramIsRed;
        areBlue = !paramIsRed;

    }
    public static void setBlue(boolean paramIsBlue)
    {
        areBlue = paramIsBlue;
        areRed = !paramIsBlue;

    }
    public static boolean isRed() {
        return areRed;
    }
    public static boolean isBlue() {
        return areBlue;
    }
    //TODO: what needs to go in here?
    //currently jsut returning if we are blue or not
    public static boolean disableExec() {
        return areBlue;
    }

    
}
