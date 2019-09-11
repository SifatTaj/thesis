package core;

import model.*;

import java.util.ArrayList;
import java.util.Comparator;

public class Algorithms {
    public static LocationWithNearbyPlaces KNN_WKNN_Algorithm(ArrayList<ReferencePoint> recordedRSSValue, ArrayList<Float> observedRSSValues, int k, boolean isWeighted) {

        ArrayList<AccessPoint> rssValues;
        float curResult;
        ArrayList<LocDistance> locDistanceResultsList = new ArrayList<>();
        String myLocation;

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
        locDistanceResultsList.sort(new Comparator<LocDistance>() {
            public int compare(LocDistance gd1, LocDistance gd2) {
                return (Double.compare(gd1.getDistance(), gd2.getDistance()));
            }
        });

        if (!isWeighted) {
            myLocation = calculateAverageKDistanceLocations(locDistanceResultsList, k);
        } else {
            myLocation = calculateWeightedAverageKDistanceLocations(locDistanceResultsList, k);
        }

        return new LocationWithNearbyPlaces(myLocation, locDistanceResultsList);

    }

    private static float calculateEuclideanDistance(ArrayList<AccessPoint> l1, ArrayList<Float> l2) {

        float finalResult = 0;
        float v1;
        float v2;
        float temp;

        for (int i = 0; i < l1.size(); ++i) {

            try {
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
        double LocationWeight;
        double sumWeights = 0.0f;
        double WeightedSumX = 0.0f;
        double WeightedSumY = 0.0f;

        String[] LocationArray;
        float x, y;

        int K_Min = Math.min(K, LocDistance_Results_List.size());

        // Calculate the weighted sum of X and Y
        for (int i = 0; i < K_Min; ++i) {
            if (LocDistance_Results_List.get(i).getDistance() != 0.0) {
                LocationWeight = 1 / LocDistance_Results_List.get(i).getDistance();
            } else {
                LocationWeight = 100;
            }
            LocationArray = LocDistance_Results_List.get(i).getLocation().split(" ");

            try {
                x = Float.parseFloat(LocationArray[0].trim());
                y = Float.parseFloat(LocationArray[1].trim());
            } catch (Exception e) {
                return null;
            }

            sumWeights += LocationWeight;
            WeightedSumX += LocationWeight * x;
            WeightedSumY += LocationWeight * y;

        }

        WeightedSumX /= sumWeights;
        WeightedSumY /= sumWeights;

        return (int) WeightedSumX + " " + (int) WeightedSumY;
    }

    private static String calculateAverageKDistanceLocations(ArrayList<LocDistance> LocDistance_Results_List, int K) {
        float sumX = 0.0f;
        float sumY = 0.0f;

        String[] LocationArray;
        float x, y;

        int K_Min = Math.min(K, LocDistance_Results_List.size());

        // Calculate the sum of X and Y
        for (int i = 0; i < K_Min; ++i) {
            LocationArray = LocDistance_Results_List.get(i).getLocation().split(" ");

            try {
                x = Float.parseFloat(LocationArray[0].trim());
                y = Float.parseFloat(LocationArray[1].trim());
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
