package models;

import com.owlike.genson.annotation.JsonDateFormat;
import com.owlike.genson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by arielpollack on 12/31/14.
 */

@XmlRootElement
public class TimeBlock {

    Integer duration; // duration in minutes
    @JsonIgnore
    Integer day, hour, minute;

    @JsonDateFormat(asTimeInMillis = true)
    Date date;

    private Calendar calendar;

    public TimeBlock() { }

    public TimeBlock(Integer day, Integer duration, long date) {
        this.duration = duration;
        setDate(new Date(date));
    }

    public Integer getDay() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public Integer getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        this.calendar = new GregorianCalendar();
        this.calendar.setTimeInMillis(this.date.getTime());
    }
}
