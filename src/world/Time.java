package world;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Owner: Kaloyan
 */

public class Time implements Serializable {

    private long seconds = 0;
    private int runSpeed = 0;
    private int saveRunSpeed = 0;
    private long lastHour = 0;
    private boolean initialStart = true;

    /**
     * Calculating the elapsed time after the simulation is stared
     */
    public void tickTime() {
        seconds += runSpeed;
    }

    /**
     * checking the elapsed time in the simulation, foreach
     * time Hour increments the method returns a boolean value
     * (Used to time the AlgorithmThread so it is fired every  application hour)
     *
     * @return a boolean value
     */
    public boolean checkHour() {
        long Hour = seconds/3600;
        if (Hour == 0 && lastHour >= 23) {
            lastHour = Hour;
            return true;
        } else if (Hour > lastHour) {
            lastHour = Hour;
            return true;
        } else if (initialStart) {
            initialStart = false;
            return true;
        } else return false;
    }

    public int getRunSpeed() {
        return runSpeed;
    }

    public void setRunSpeed(int speed) {
        this.runSpeed = speed;
    }

    /**
     * incrementing runSpeed value depending on what its value is at
     * the moment
     */
    public void addRunSpeed() {
        if (runSpeed == 0) {
            runSpeed++;
        } else if (runSpeed == 1) {
            runSpeed = 5;
        } else if (runSpeed < 20) {
            runSpeed += 5;
        } else {
            runSpeed += 10;
        }
    }

    /**
     * subtracting runSpeed value depending on what its
     * value is at the moment.
     */
    public void subtractRunSpeed() {
        if (runSpeed > 20) {
            runSpeed -= 10;
        } else if (runSpeed == 0) {
            runSpeed = 0;
        } else {
            runSpeed -= 5;
        }
    }
    public int getSavedRunSpeed() {
        return saveRunSpeed;
    }

    public void saveRunSpeed() {
        this.saveRunSpeed = runSpeed;
    }

    /**
     * The method is used to check what is the value of runSpeed
     * ( the method is created mainly for the purpose of timing the
     * Thread responsible for the Timer in the application)
     *
     * @return a value in the range of 1000-10 depending on the runSpeed value
     */
    public int timerSleepTime() {
        if (runSpeed >= 0 && runSpeed <= 5) {
            return 1000;
        } else if (runSpeed >= 5 && runSpeed <= 10) {
            return 750;
        } else if (runSpeed >= 10 && runSpeed <= 20) {
            return 500;
        } else if (runSpeed >= 20 && runSpeed <= 40) {
            return 250;
        } else
            return 10;
    }

    @Override
    public String toString() {
        Date date = new Date(seconds * 1000);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss, dd MMMMM");
        return dateFormat.format(date) + String.format(" %02d Years", seconds/31_536_000);

    }
}
