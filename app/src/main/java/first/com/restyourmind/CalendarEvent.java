package first.com.restyourmind;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ASUS on 09/08/2015.
 */
public class CalendarEvent {
    int year;
    int month;
    int day;
    int hour;
    int minute;
    String FK_ID_Drawer;

    CalendarEvent(JSONObject jsonObject){
        try {
            this.FK_ID_Drawer = jsonObject.getString("FK_ID_Drawer");
            this.year = jsonObject.getInt("year");
            this.month = jsonObject.getInt("month");
            this.day = jsonObject.getInt("day");
            this.hour = jsonObject.getInt("hour");
            this.minute = jsonObject.getInt("minute");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String toString()
    {
        return ""+year+"."+month+"."+day+" "+hour+":"+minute;
    }

    public boolean isTheDay(int year, int month, int day) {
        if ((this.year == year) &&(this.month == month) &&(this.day == day)){
            return true;
        }
        else return false;
    }
}
