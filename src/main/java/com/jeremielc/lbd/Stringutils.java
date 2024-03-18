package com.jeremielc.lbd;

public class Stringutils {
    public static String center(String value, int totalLength) {
        return center(value, totalLength, ' ');
    }

    public static String center(String value, int totalLength, char paddingChar) {
        StringBuilder sb = new StringBuilder(value);
        boolean leftFirst = false;

        while(sb.length() < totalLength) {
            leftFirst = !leftFirst;

            if(leftFirst) {
                sb.insert(0, paddingChar);
            } else {
                sb.append(paddingChar);
            }
        }

        return sb.toString();
    }
}
