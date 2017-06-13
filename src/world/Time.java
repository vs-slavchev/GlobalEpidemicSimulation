package world;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Owner: Kaloyan
 */

public class Time implements Serializable {

    private final int[] speedValues = {1, 2, 5, 10, 25, 50, 75, 100, 200};
    private long seconds = 0;
    private long lastHour = -1;
    private int speedValueIndex = 0;

    /**
     * Calculating the elapsed time after the simulation is stared
     */
    public void tickTime() {
        seconds += getTimeSpeed();
    }

    /**
     * checking the elapsed time in the simulation, foreach
     * time Hour increments the method returns a boolean value
     * (Used to time the AlgorithmThread so it is fired every  application hour)
     *
     * @return a boolean value
     */
    public boolean checkHourHasPassed() {
        long currentHour = seconds / 3600;
        if (currentHour != lastHour) {
            lastHour = currentHour;
            return true;
        }
        return false;
    }

    public int getTimeSpeed() {
        try {
            return speedValues[speedValueIndex];
        } catch (IndexOutOfBoundsException ioobe) {
            ioobe.printStackTrace();
            return 0;
        }
    }

    public void increaseSpeed() {
        speedValueIndex = Math.min(speedValueIndex + 1, speedValues.length - 1);
    }

    public void decreaseSpeed() {
        speedValueIndex = Math.max(speedValueIndex - 1, 0);
    }

    /**
     * The method is created mainly for the purpose of timing the
     * Thread responsible for the Timer in the application.
     *
     * @return the milliseconds the timer thread is supposed to sleep for
     */
    public int calculateTimerSleepTime() {
        return 1000 / getTimeSpeed();
    }

    public boolean isAtMaxSpeed() {
        return speedValueIndex == speedValues.length - 1;
    }

    public boolean isAtMinSpeed() {
        return speedValueIndex == 0;
    }

    @Override
    public String toString() {
        Date date = new Date(seconds * 1000);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss, dd MMMMM");
        // that is how many seconds there are in one year
        return dateFormat.format(date) + String.format(" %02d Years", seconds / 31_536_000);
    }
}