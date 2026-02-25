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

    public AutoRoutine(String autoDescription, ArrayList<AutoStep> routine){
        locAutoDescription = autoDescription;
        locAutoSteps.addAll(locAutoSteps.size(), routine);
    }

    //amount of steps, 
    public int getStepAmount(){
        return locAutoSteps.size();
    }

    public AutoStep getStep(int index){
        return locAutoSteps.get(index);
    }

    public String getAutoDescription(){
        return locAutoDescription;
    }


}
