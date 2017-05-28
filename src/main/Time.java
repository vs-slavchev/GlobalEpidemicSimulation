package main;

import java.io.Serializable;

/**
 * Owner: Kaloyan
 */

public class Time implements Serializable {

    private int Year = 0;
    private int Month = 0;
    private int Day = 0;
    private int Hour = 0;
    private int Minutes = 0;
    private int Sec = 0;
    private int RunSpeed = 0;
    private int SaveRunSpeed = 0;
    private int LastHour = 0;
    private boolean InitialStart = true;

    /**
     * Calculating the elapsed time after the simulation is stared
     */
    public void setElapsedTime() {
        Sec++;
        Sec *= RunSpeed;
        if (RunSpeed > 59) {
            Minutes++;
            Sec = Sec % 59;
        }
        if (Sec >= 60) {
            Sec = 0;
            Minutes++;
        }
        if (Minutes >= 60) {
            Minutes = 0;
            Hour++;
        }
        if (Hour >= 24) {
            Hour = 0;
            Day++;
        }
        if (Day >= 30) {
            Month++;
            Day = 0;
        }
        if (Month >= 12) {
            Month = 0;
            Year++;
        }
    }

    /**
     *  checking the elapsed time in the simulation, foreach
     *  time Hour increments the method returns a boolean value
     *  (Used to time the AlgorithmThread so it is fired every  application hour)
     * @return a boolean value
     */
    public boolean checkHour(){
        if(Hour == 0 && LastHour >= 23){
            LastHour = Hour;
            return true;
        }
        else if(Hour> LastHour){
            LastHour = Hour;
            return true;
        }
        else if(InitialStart){
            InitialStart = false;
            return  true;
        }
        else return false;
    }

    /**
     * returns  RunSpeed
     * @return
     */
    public int getRunSpeed() {
        return RunSpeed;
    }

    /**
     * Method used to set the value of the RunSpeed variable
     * @param speed used to assign  its value to RunSpeed
     */
    public void setRunSpeed(int speed) {
        this.RunSpeed = speed;
    }

    /**
     * incrementing RunSpeed value depending on what its value is at
     * the moment
     * for instance if RunSpeed<20 is incremented with 5 and etc.
     */
    public void addRunSpeed() {
        if (RunSpeed == 0) {
            RunSpeed++;
        } else if (RunSpeed == 1) {
            RunSpeed = 5;
        } else if (RunSpeed < 20) {
            RunSpeed += 5;
        } else RunSpeed += 10;
    }
    /**
     * decrementing(subtracting) RunSpeed value depending on what its
     * value is at the moment.
     * for instance if RunSpeed<20 is decremented with 10 and etc.
     */
    public void subtract() {
        if (RunSpeed > 20) {
            RunSpeed -= 10;
        } else if (RunSpeed == 0) {
            RunSpeed = 0;
        } else
            RunSpeed -= 5;
    }

    /**
     * Gets the SaveRunSpeed variable which is used for saving
     * the RunSpeed variable
     * @return the SaveRunSpeed value
     */
    public int getSavedRunSpeed() {
        return SaveRunSpeed;
    }

    /**
     *  Takes the value of RunSpeed and assigns it to SaveRunSpeed variable
     *  for later use
     */
    public void saveRunSpeed() {
        this.SaveRunSpeed = RunSpeed;
    }

    /**
     * Used to set the current time in the application when necessary
     * @param hour assigning the Hour variable
     * @param min assigning the Minutes variable
     * @param sec assigning the Sec variable
     * @param day assigning the Day variable
     * @param month assigning the Month variable
     * @param year assigning the Year variable
     */
    public void setTime(int hour, int min, int sec, int day, int month, int year) {
        this.Sec = sec;
        this.Minutes = min;
        this.Hour = hour;
        this.Day = day;
        this.Month = month;
        this.Year = year;
    }

    /**
     * Used to get the current time in the application
     * @return A string containing the current time if format sec,min,hour-d/m/y
     */
    public String getTime() {
        return Sec + "," + Minutes + "," + Hour + "-" + Day + "/" + Month + "/" + Year;
    }

    /**
     * The method is used to check what is the value of RunSpeed
     * ( the method is created mainly for the purpose of timing the
     * Thread responsible for the Timer in the application)
     * @return a value in the range of 1000-10 depending on the RunSpeed value
     */
    public int timerSleepTime() {
        if (RunSpeed >= 0 && RunSpeed <= 5) {
            return 1000;
        } else if (RunSpeed >= 5 && RunSpeed <= 10) {
            return 750;
        } else if (RunSpeed >= 10 && RunSpeed <= 20) {
            return 500;
        } else if (RunSpeed >= 20 && RunSpeed <= 40) {
            return 250;
        } else
            return 10;
    }

    /**
     * Overriding the toString method
     * @return the current time in string format hh:mm:ss, dd/mm/yy
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d, %02d Day(s), %02d Month(s), %02d Year(s)",
                Hour, Minutes, Sec, Day, Month, Year);

    }
}
