package core;

import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Algorithms {
    public static LocationWithNearbyPlaces KNN_WKNN_Algorithm(ArrayList<ReferencePoint> recordedRSSValue, ArrayList<Float> observedRSSValues, String parameter, boolean isWeighted) {

        ArrayList<AccessPoint> rssValues;
        float curResult = 0;
        ArrayList<LocDistance> locDistanceResultsList = new ArrayList<>();
        String myLocation = null;
        int K;

        try {
            K = Integer.parseInt(parameter);
        } catch (Exception e) {
            return null;
        }

        // Construct a list with locations-distances pairs for currently
        // observed RSS values
        for (ReferencePoint referencePoint : recordedRSSValue) {
            rssValues = referencePoint.getReadings();
            curResult = calculateEuclideanDistance(rssValues, observedRSSValues);

            if (curResult == Float.NEGATIVE_INFINITY)
                return null;

            locDistanceResultsList.add(0, new LocDistance(curResult, referencePoint.getLocId(), referencePoint.getName()));
        }

        // Sort locations-distances pairs based on minimum distances
        Collections.sort(locDistanceResultsList, new Comparator<LocDistance>() {
            public int compare(LocDistance gd1, LocDistance gd2) {
                return (gd1.getDistance() > gd2.getDistance() ? 1 : (gd1.getDistance() == gd2.getDistance() ? 0 : -1));
            }
        });

        if (!isWeighted) {
            myLocation = calculateAverageKDistanceLocations(locDistanceResultsList, K);
        } else {
            myLocation = calculateWeightedAverageKDistanceLocations(locDistanceResultsList, K);
        }
        LocationWithNearbyPlaces places = new LocationWithNearbyPlaces(myLocation, locDistanceResultsList);
        return places;

    }

    private static float calculateEuclideanDistance(ArrayList<AccessPoint> l1, ArrayList<Float> l2) {

        float finalResult = 0;
        float v1;
        float v2;
        float temp;

        for (int i = 0; i < l1.size(); ++i) {

            try {
                l1.get(i).getMeanRss();
                v1 = (float) l1.get(i).getMeanRss();
                v2 = l2.get(i);
            } catch (Exception e) {
                return Float.NEGATIVE_INFINITY;
            }

            // do the procedure
            temp = v1 - v2;
            temp *= temp;

            // do the procedure
            finalResult += temp;
        }
        return ((float) Math.sqrt(finalResult));
    }

    private static String calculateWeightedAverageKDistanceLocations(ArrayList<LocDistance> LocDistance_Results_List, int K) {
        double LocationWeight = 0.0f;
        double sumWeights = 0.0f;
        double WeightedSumX = 0.0f;
        double WeightedSumY = 0.0f;

        String[] LocationArray = new String[2];
        float x, y;

        int K_Min = K < LocDistance_Results_List.size() ? K : LocDistance_Results_List.size();

        // Calculate the weighted sum of X and Y
        for (int i = 0; i < K_Min; ++i) {
            if (LocDistance_Results_List.get(i).getDistance() != 0.0) {
                LocationWeight = 1 / LocDistance_Results_List.get(i).getDistance();
            } else {
                LocationWeight = 100;
            }
            LocationArray = LocDistance_Results_List.get(i).getLocation().split(" ");

            try {
                x = Float.valueOf(LocationArray[0].trim()).floatValue();
                y = Float.valueOf(LocationArray[1].trim()).floatValue();
            } catch (Exception e) {
                return null;
            }

            sumWeights += LocationWeight;
            WeightedSumX += LocationWeight * x;
            WeightedSumY += LocationWeight * y;

        }

        WeightedSumX /= sumWeights;
        WeightedSumY /= sumWeights;

        return WeightedSumX + " " + WeightedSumY;
    }

    private static String calculateAverageKDistanceLocations(ArrayList<LocDistance> LocDistance_Results_List, int K) {
        float sumX = 0.0f;
        float sumY = 0.0f;

        String[] LocationArray = new String[2];
        float x, y;

        int K_Min = K < LocDistance_Results_List.size() ? K : LocDistance_Results_List.size();

        // Calculate the sum of X and Y
        for (int i = 0; i < K_Min; ++i) {
            LocationArray = LocDistance_Results_List.get(i).getLocation().split(" ");

            try {
                x = Float.valueOf(LocationArray[0].trim()).floatValue();
                y = Float.valueOf(LocationArray[1].trim()).floatValue();
            } catch (Exception e) {
                return null;
            }

            sumX += x;
            sumY += y;
        }

        // Calculate the average
        sumX /= K_Min;
        sumY /= K_Min;

        return sumX + " " + sumY;
    }
}
