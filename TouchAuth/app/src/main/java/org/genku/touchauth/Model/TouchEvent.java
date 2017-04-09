package org.genku.touchauth.Model;

/**
 * Created by genku on 4/1/2017.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TouchEvent {

    public String[][] prevData;
    public String[][] postData;
    public String[][][] midData;

    public int start;
    public int end;


    public String[] strings;

    public static TouchEvent getTouchEventFromString(String raw) {

        Pattern rawPattern = Pattern.compile(rawPatternString);
        Matcher rawMatcher = rawPattern.matcher(raw);
        if (!rawMatcher.find()) return null;

        TouchEvent event = new TouchEvent();
        event.start = rawMatcher.start();
        event.end = rawMatcher.end();

        raw = raw.substring(event.start, event.end);

        String prev = raw.substring(0, 37 * 7);
        String post = raw.substring(raw.length() - 37 * 3);
        String[] mid = null;
        int numOfMid = (raw.length() / 37 - 10) / 6;
        if (numOfMid > 0) {
            mid = new String[numOfMid];
            int k = 37 * 7;
            for (int i = 0; i < numOfMid; ++i) {
                mid[i] = raw.substring(k + i * 37 * 6, k + (i + 1) * 37 * 6);
            }
        }


        event.prevData = new String[7][4];
        event.postData = new String[3][4];
        event.midData = new String[numOfMid][6][4];

        Pattern prevPattern = Pattern.compile(prevPatternString);
        Matcher prevMatcher = prevPattern.matcher(prev);
        if (prevMatcher.find()) {
            for (int i = 0; i < 7; ++i) {
                for (int j = 0; j < 4; ++j) {
                    event.prevData[i][j] = prevMatcher.group(i * 4 + j + 1);
                }
            }
        }

        Pattern postPattern = Pattern.compile(postPatternString);
        Matcher postMatcher = postPattern.matcher(post);
        if (postMatcher.find()) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 4; ++j) {
                    event.postData[i][j] = postMatcher.group(i * 4 + j + 1);
                }
            }
        }

        if (mid != null) {
            Pattern midPattern = Pattern.compile(midPatternString);
            for (int n = 0; n < numOfMid; ++n) {
                Matcher midMatcher = midPattern.matcher(mid[n]);
                if (midMatcher.find()) {
                    for (int i = 0; i < 6; ++i) {
                        for (int j = 0; j < 4; ++j) {
                            event.midData[n][i][j] = midMatcher.group(i * 4 + j + 1);
                        }
                    }
                }
            }
        }

        return event;
    }

    private final static String prevPatternString
            = "\\[\\s*(\\d+\\.\\d+)\\]\\s(0003)\\s(003a)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0003)\\s(0035)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0003)\\s(0036)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0003)\\s(0039)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0000)\\s(0002)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0001)\\s(014a)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0000)\\s(0000)\\s([0-9a-z]{8})\\s";
    private final static String midPatternString
            = "\\[\\s*(\\d+\\.\\d+)\\]\\s(0003)\\s(003a)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0003)\\s(0035)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0003)\\s(0036)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0003)\\s(0039)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0000)\\s(0002)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0000)\\s(0000)\\s([0-9a-z]{8})\\s";
    private final static String postPatternString
            = "\\[\\s*(\\d+\\.\\d+)\\]\\s(0000)\\s(0002)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0001)\\s(014a)\\s([0-9a-z]{8})\\s"
            + "\\[\\s*(\\d+\\.\\d+)\\]\\s(0000)\\s(0000)\\s([0-9a-z]{8})\\s";

    private final static String rawPatternString
            = "\\[\\s*\\d+\\.\\d+\\]\\s0003\\s003a\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0003\\s0035\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0003\\s0036\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0003\\s0039\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0000\\s0002\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0001\\s014a\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0000\\s0000\\s[0-9a-z]{8}\\s"
            + "(?:"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0003\\s003a\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0003\\s0035\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0003\\s0036\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0003\\s0039\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0000\\s0002\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0000\\s0000\\s[0-9a-z]{8}\\s"
            + ")*"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0000\\s0002\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0001\\s014a\\s[0-9a-z]{8}\\s"
            + "\\[\\s*\\d+\\.\\d+\\]\\s0000\\s0000\\s[0-9a-z]{8}\\s";
}
