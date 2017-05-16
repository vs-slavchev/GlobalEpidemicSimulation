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
    private int Sec = 0;
    private int RunSpeed = 0;
    private int SaveRunSpeed= 0;

    public void setElapsedTime() {
        Sec++;
        Sec *= RunSpeed;
        if(RunSpeed>59){
            Minutes++;
            Sec=Sec%59;
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
    public int getRunSpeed(){
        return RunSpeed;
    }
    public void addRunSpeed(){
        if(RunSpeed==0){
            RunSpeed++;
        }
        else if(RunSpeed==1){
            RunSpeed=5;
        }
        else if(RunSpeed<20){
            RunSpeed+=5;
        }
        else RunSpeed+=10;
    }
    public void substract(){
        if(RunSpeed>20){
            RunSpeed-=10;
        }
        else if(RunSpeed==0){
            RunSpeed = 0;
        }
        else
            RunSpeed-=5;
    }
    public void setRunSpeed(int speed){
        this.RunSpeed = speed;
    }
    public int getSavedRunSpeed(){
        return SaveRunSpeed;
    }
    public void saveRunSpeed(){
        this.SaveRunSpeed = RunSpeed;
    }

    public int timerSleepTime(){
        if(RunSpeed>=0 && RunSpeed<=5){
            return 1000;
        }
        else if(RunSpeed>=5 && RunSpeed<=10){
            return 750;
        }
        else if(RunSpeed>=10 && RunSpeed<=20){
            return 500;
        }
        else if(RunSpeed>=20 && RunSpeed<=40){
            return 250;
        }
        else
            return 10;
    }
    public int algorithmSleepTime(){
        if(timerSleepTime() == 10){
            return 1000;
        }
        else if(timerSleepTime() == 250){
            return 1500;
        }
        else if(timerSleepTime() == 500){
            return 2000;
        }
        else if(timerSleepTime() == 750){
            return 2500;
        }
        else {
            return 3000;
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
