package util;

import java.util.ArrayList;
import java.util.List;

public class Convert {
    public static ArrayList<Float> toList(String string) {
        String[] array = string.split("_");
        ArrayList<Float> list = new ArrayList<Float>();

        try {
            for (String s : array) {
                list.add(Float.parseFloat(s.trim()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static int[][] toArray(List coordinateList) {
        int[][] array = new int[coordinateList.size()][2];
        int index = 0;
        for (Object coordinates : coordinateList) {
            List values = (List) coordinates;
            int x = (Integer) values.get(0);
            int y = (Integer) values.get(1);
            array[index] = new int[]{x, y};
            ++index;
        }

        return array;
    }
}
