package main;

/**
 * Owner: Kaloyan
 */

public class Time {

    private int Year = 0;
    private int Month = 0;
    private int Day = 0;
    private int Hour = 0;
    private int Minutes = 0;
    private int Sec = 1;

    public void setElapsedTime(double speed) {
        Sec++;
        Sec *= speed;
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

    @Override
    public String toString() {
        /*
        if (Minutes < 10 && Sec < 10 && Hour < 10) {
            return "0" + Hour + ":0" + Minutes + ":0" + Sec + ", " + Day + " Day(s)" + ", " + Month + " Month(s)" + ", " + Year + " Year(s)";
        } else if (Sec < 10 & Minutes < 10) {
            return Hour + ":0" + Minutes + ":0" + Sec + ", " + Day + " Day(s)" + ", " + Month + " Month(s)" + ", " + Year + " Year(s)";
        } else if (Sec < 10 && Hour < 10) {
            return "0" + Hour + ":" + Minutes + ":0" + Sec + ", " + Day + " Day(s)" + ", " + Month + " Month(s)" + ", " + Year + " Year(s)";
        } else if (Minutes < 10 && Hour < 10) {
            return "0" + Hour + ":0" + Minutes + ":" + Sec + ", " + Day + " Day(s)" + ", " + Month + " Month(s)" + ", " + Year + " Year(s)";
        } else if (Sec < 10) {
            return Hour + ":" + Minutes + ":0" + Sec + ", " + Day + " Day(s)" + ", " + Month + " Month(s)" + ", " + Year + " Year(s)";
        } else if (Minutes < 10) {
            return Hour + ":0" + Minutes + ":" + Sec + ", " + Day + " Day(s)" + ", " + Month + " Month(s)" + ", " + Year + " Year(s)";
        } else if (Hour < 10) {
            return "0" + Hour + ":" + Minutes + ":" + Sec + ", " + Day + " Day(s)" + ", " + Month + " Month(s)" + ", " + Year + " Year(s)";
        } else
            return Hour + ":" + Minutes + ":" + Sec + ", " + Day + " Day(s)" + ", " + Month + " Month(s)" + ", " + Year + " Year(s)";
        */
        return String.format("%02d:%02d:%02d, %02d Day(s), %02d Month(s), %02d Year(s)", Hour, Minutes, Sec, Day, Month, Year);

    }
}
