package frc.robot;

import java.util.ArrayList;

public class AutoRoutine {
    private String locAutoDescription = "";
    private ArrayList<AutoStep> locAutoSteps = new ArrayList<AutoStep>();

    public AutoRoutine(String autoDescription, AutoStep[] steps){
        locAutoDescription = autoDescription;

        for (AutoStep x : steps) {
            locAutoSteps.add(x);
        }
    }

    //amount of steps, 
    public int getStepAmount(){
        return locAutoSteps.size();
    }

    public AutoStep getStep(int index){
        return locAutoSteps.get(index);
    }

    // TODO: Add a getter for the description of this routine.

}
