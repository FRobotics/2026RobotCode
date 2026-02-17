package frc.robot;

import java.util.ArrayList;
// TODO: Remove unused imports.
import java.io.File;
import java.io.FileNotFoundException; 
import java.util.Scanner;         


public class AutoRoutine {
    private String locAutoDescription = "";
    private ArrayList<AutoStep> locAutoSteps = new ArrayList<AutoStep>();
    

    public AutoRoutine(String autoDescription, AutoStep[] steps){
  
        // TODO: set class variable with value of autoDescription parameter..
        for (AutoStep x : steps) {
            locAutoSteps.add(x);
        }
    }

    public AutoRoutine(String autoDescription, ArrayList<AutoStep> routine){
        // TODO:  set class variable with value of autoDescription parameter..
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
