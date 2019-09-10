package util;

import java.util.ArrayList;

public class Convert {
    public static ArrayList<Float> toList(String string) {
        String[] array = string.split("_");
        ArrayList<Float> list = new ArrayList<Float>();

        try {
            for (String s : array) {
                list.add(Float.valueOf(s.trim()).floatValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
