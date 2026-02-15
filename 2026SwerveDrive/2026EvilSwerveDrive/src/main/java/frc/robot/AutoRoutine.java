package frc.robot;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException; 
import java.util.Scanner;         


public class AutoRoutine {
    private String locAutoDescription = "";
    private ArrayList<AutoStep> locAutoSteps = new ArrayList<AutoStep>();
    

    public AutoRoutine(String autoDescription, AutoStep[] steps){
  
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

    public String getAutoDescription(){
        return locAutoDescription;
    }


}
