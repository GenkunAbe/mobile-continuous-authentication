package org.genku.touchauth.Model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtomTouchEvent {

    /*
     * Synchronization events.
     */
    public static final int SYN_REPORT           = 0;
    public static final int SYN_CONFIG           = 1;
    public static final int SYN_MT_REPORT        = 2;

    /*
     * Absolute axes
     */
    public static final int ABS_MT_SLOT          = 0x2f;    /* MT slot being modified */
    public static final int ABS_MT_TOUCH_MAJOR	 = 0x30;	/* Major axis of touching ellipse */
    public static final int ABS_MT_TOUCH_MINOR	 = 0x31;	/* Minor axis (omit if circular) */
    public static final int ABS_MT_WIDTH_MAJOR	 = 0x32;	/* Major axis of approaching ellipse */
    public static final int ABS_MT_WIDTH_MINOR	 = 0x33;	/* Minor axis (omit if circular) */
    public static final int ABS_MT_ORIENTATION	 = 0x34;	/* Ellipse orientation */
    public static final int ABS_MT_POSITION_X	 = 0x35;	/* Center X ellipse position */
    public static final int ABS_MT_POSITION_Y	 = 0x36;	/* Center Y ellipse position */
    public static final int ABS_MT_TOOL_TYPE	 = 0x37;	/* Type of touching device */
    public static final int ABS_MT_BLOB_ID		 = 0x38;	/* Group a set of packets as a blob */
    public static final int ABS_MT_TRACKING_ID   = 0x39;    /* Unique ID of initiated contact */
    public static final int ABS_MT_PRESSURE      = 0x3a;    /* Pressure on contact area */
    public static final int ABS_MT_DISTANCE      = 0x3b;    /* Contact hover distance */

    public static final int BTN_TOUCH            = 0x14a;


    private String mTime;
    private String mType;
    private String mCode;
    private String mValue;

    public AtomTouchEvent(String s) {
        Pattern p = Pattern.compile("\\[\\s*(\\d+\\.\\d+)\\]\\s([0-9a-z]{4})\\s([0-9a-z]{4})\\s([0-9a-z]{8})\\s");
        Matcher m = p.matcher(s);
        if (m.matches()) {
            mTime = m.group(1);
            mType = m.group(2);
            mCode = m.group(3);
            mValue = m.group(4);
        }
    }

    public String getmTime() {
        return mTime;
    }

    public String getmType() {
        return mType;
    }

    public String getmCode() {
        return mCode;
    }

    public String getmValue() {
        return mValue;
    }
}
