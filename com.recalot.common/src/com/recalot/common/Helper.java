// Copyright (C) 2016 Matth�us Schmedding
//
// This file is part of recalot.com.
//
// recalot.com is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// recalot.com is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with recalot.com. If not, see <http://www.gnu.org/licenses/>.

package com.recalot.common;

import com.recalot.common.communication.RecommendedItem;

import java.io.*;
import java.util.*;

/**
 * This class contains all kind of helper methods that are used by several cases.
 *
 * @author Matth�us Schmedding (info@recalot.com)
 */
public class Helper {
    /**
     * Copies the bytes of an InputStream to a PrintWriter.
     *
     * @param out    a PrintWriter to whom the bytes of the InputStream should be copied
     * @param stream a InputStream that contains the bytes that should be copied
     * @throws IOException is thrown when the InputStream can not be read
     * @see java.io.PrintWriter
     * @see java.io.InputStream
     */
    public static void copy(PrintWriter out, InputStream stream) throws IOException {
        InputStreamReader reader = new InputStreamReader(stream);

        char[] buffer = new char[1024];

        int len = reader.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = reader.read(buffer);
        }

        reader.close();
    }

    /**
     * Sorts a Map by value in descending order.
     *
     * @param map a map that should be sorted.
     * @return a sorted map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(
            Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(
                map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static void showMap(Map<String, Map<String, Double>> map) {
        System.out.println("[");
        for (Map.Entry<String, Map<String, Double>> e : map.entrySet()) {
            System.out.println("Outer Key: " + e.getKey());
            for (Map.Entry<String, Double> inner : e.getValue().entrySet()) {
                System.out.println("... " + inner.getKey() + ": " + inner.getValue());
            }
        }
        System.out.println("]");
    }

    public static void showMap(Map<String, Map<String, Double>> map, int innercap) {
        int outercap = map.size();
        showMap(map, outercap, innercap);
    }

    public static void showMap(Map<String, Map<String, Double>> map, int outercap, int innercap) {
        int counterouter = 0;
        int counterinner = 0;

        System.out.println("[");
        for (Map.Entry<String, Map<String, Double>> e : map.entrySet()) {
            counterouter += 1;
            System.out.println("Outer Key: " + e.getKey());
            for (Map.Entry<String, Double> inner : e.getValue().entrySet()) {
                counterinner += 1;
                System.out.println("... " + inner.getKey() + ": " + inner.getValue());
                if (counterinner >= innercap) {
                    counterinner = 0;
                    break;
                }
            }
            if (counterouter >= outercap) {
                counterouter = 0;
                break;
            }
        }
        System.out.println("]");
    }

    public static void showItems(List<RecommendedItem> itemlist) {
        System.out.println("[");
        for (RecommendedItem item : itemlist) {
            System.out.print(item.getItemId() + ", ");
        }
        System.out.println("\n]");
    }


    /**
     * Swap key/values of a Map
     *
     * @param map a map that should be swapped.
     * @return a swapped map
     */
    // see http://stackoverflow.com/questions/4436999/how-to-swap-keys-and-values-in-a-map-elegantly
    public static Map<String, Map<String, Double>> swapMap(Map<String, Map<String, Double>> map) {
        Map<String, Map<String, Double>> result = new LinkedHashMap();


        if (map != null) {
            for (Map.Entry<String, Map<String, Double>> e : map.entrySet()) {


                String innerkey = e.getKey();
                for (Map.Entry<String, Double> inner : e.getValue().entrySet()) {

                    String outerkey = inner.getKey();
                    if (!result.containsKey(outerkey)) {
                        // add new empty map
                        result.put(outerkey, new LinkedHashMap());
                    }
                    result.get(outerkey).put(innerkey, inner.getValue());
                }
            }


        }
        return result;
    }

    /**
     * A method that increments the counter value in a map. If no value exists,
     * it adds 1. Otherwise we increment the value
     *
     * @param map the container map
     * @param key the key for the entry that should be increment
     */
    public static <K> void incrementMapValue(Map<K, Integer> map, K key) {
        Integer existingValue = map.get(key);
        if (existingValue == null) {
            map.put(key, 1);
        } else {
            map.put(key, existingValue + 1);
        }
    }

    /**
     * A method that increments the counter value in a map. If no value exists,
     * it sets the given value. Otherwise we increment the existing value by the given value
     *
     * @param map   the container map
     * @param key   the key for the entry that should be increment
     * @param value the value that should be added
     */
    public static <K> void incrementMapValue(Map<K, Integer> map, K key, Integer value) {
        Integer existingValue = map.get(key);
        if (existingValue == null) {
            map.put(key, value);
        } else {
            map.put(key, existingValue + value);
        }
    }

    /**
     * A method that increments the counter value in a map. If no value exists,
     * it sets the given value. Otherwise we increment the existing value by the given value
     *
     * @param map   the container map
     * @param key   the key for the entry that should be increment
     * @param value the value that should be added
     */
    public static <K> void incrementMapValue(Map<K, Double> map, K key, Double value) {
        Double existingValue = map.get(key);
        if (existingValue == null) {
            map.put(key, value);
        } else {
            map.put(key, existingValue + value);
        }
    }


    /**
     * A method that sum the counter value in a map.
     *
     * @param map the map that contains the values
     */
    public static <K> Integer sum(Map<K, Integer> map) {
        Integer sum = 0;
        for (Integer v : map.values()) {
            sum += v;
        }

        return sum;
    }

    /**
     * Computes the Cosine similarity between two vectors.
     *
     * @param v1 the first integer vector
     * @param v2 the second integer vector
     * @return cosine similarity between the two given vectors. Returns 0 if the length of the vector is not equal.
     */
    public static Double computeCosSimilarity(List<Integer> v1, List<Integer> v2) {
        if (v1.size() != v2.size()) return 0.0;

        Double a = 0.0;
        Double b = 0.0;
        Double c = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            a += (v1.get(i) * v2.get(i));
            b += (v1.get(i) * v1.get(i));
            c += (v2.get(i) * v2.get(i));
        }

        if (b > 0.0 && c > 0.0) {
            return a / (Math.sqrt(b) * Math.sqrt(c));
        }

        return 0.0;
    }


    /**
     * Computes the Pearson similarity between two vectors.
     *
     * @param v1 the first integer vector
     * @param v2 the second integer vector
     * @return Pearson similarity between the two given vectors. Returns 0 if the length of the vector is not equal.
     */
    public static Double computePearsonSimilarity(List<Integer> v1, List<Integer> v2) {
        Double result = 0.0;
        int size = v1.size();

        if (v1.size() == v2.size()) {
            Double xy = 0.0;
            Double v1sum = 0.0;
            Double v2sum = 0.0;
            Double xx = 0.0;
            Double yy = 0.0;

            for (int i = 0; i < size; i++) {
                int xi = v1.get(i);
                int yi = v2.get(i);
                xy += xi * yi;
                v1sum += xi;
                v2sum += yi;
                xx += xi * xi;
                yy += yi * yi;
            }

            Double a = size * xy;
            Double b = v1sum * v2sum;
            Double c = size * xx - (v1sum * v1sum);
            Double d = size * yy - (v2sum * v2sum);

            if (c * d > 0.0) {
                result = (a - b) / (Math.sqrt(c) * Math.sqrt(d));
            }
        }

        return result;
    }

    /**
     * Computes the Adjusted Cosine similarity between two vectors.
     *
     * @param v1 the first integer vector
     * @param v2 the second integer vector
     * @return adjusted cosine similarity between the two given vectors using means of vectors for adjustment. Returns 0 if the length of the vector is not equal.
     */
    public static Double computeAdjustedCosineSimilarity(List<Integer> v1, List<Integer> v2) {
        Double result = 0.0;
        Double sumv1 = 0.0;
        Double sumv2 = 0.0;
        Double mean1 = 0.0;
        Double mean2 = 0.0;
        int size = v1.size();

        if (v1.size() == v2.size()) {
            for (int i = 0; i < size; i++) {
                sumv1 += v1.get(i);
                sumv2 += v2.get(i);
            }
            // mean
            mean1 = sumv1 / size;
            mean2 = sumv2 / size;

            result = computeAdjustedCosineSimilarity(v1, v2, mean1, mean2);
        }
        return result;
    }

    /**
     * Computes the Adjusted Cosine similarity between two vectors.
     *
     * @param v1 the first integer vector
     * @param v2 the second integer vector
     * @return adjusted cosine similarity between the two given vectors using means of vectors for adjustment. Returns 0 if the length of the vector is not equal.
     */
    public static Double computeAdjustedCosineSimilarityDoubles(List<Double> v1, List<Double> v2) {
        Double result = 0.0;
        Double sumv1 = 0.0;
        Double sumv2 = 0.0;
        Double mean1 = 0.0;
        Double mean2 = 0.0;
        int size = v1.size();

        if (v1.size() == v2.size()) {
            for (int i = 0; i < size; i++) {
                sumv1 += v1.get(i);
                sumv2 += v2.get(i);
            }
            // mean
            mean1 = sumv1 / size;
            mean2 = sumv2 / size;

            result = computeAdjustedCosineSimilarityDoubles(v1, v2, mean1, mean2);
        }
        return result;
    }

    /**
     * Computes the Adjusted Cosine similarity between two vectors.
     *
     * @param v1     the first integer vector
     * @param v2     the second integer vector
     * @param v1mean the mean of the first integer vector for adjustment
     * @param v2mean the mean of the second integer vector for adjustment
     * @return adjusted cosine similarity between the two given vectors using given means for adjustment. Returns 0 if the length of the vector is not equal.
     */
    public static Double computeAdjustedCosineSimilarity(List<Integer> v1, List<Integer> v2, Double v1mean, Double v2mean) {
        Double result = 0.0;
        int size = v1.size();

        if (v1.size() == v2.size()) {
            Double xy = 0.0;
            Double xx = 0.0;
            Double yy = 0.0;

            for (int i = 0; i < size; i++) {
                int xi = v1.get(i);
                int yi = v2.get(i);
                xy += (xi - v1mean) * (yi - v2mean);
                xx += (xi - v1mean) * (xi - v1mean);
                yy += (yi - v2mean) * (yi - v2mean);
            }

            if (xx > 0.0 && yy > 0.0) {
                result = xy / (Math.sqrt(xx) * Math.sqrt(yy));
            }
        }
        return result;
    }

    /**
     * Computes the Adjusted Cosine similarity between two vectors.
     *
     * @param v1     the first integer vector
     * @param v2     the second integer vector
     * @param v1mean the mean of the first integer vector for adjustment
     * @param v2mean the mean of the second integer vector for adjustment
     * @return adjusted cosine similarity between the two given vectors using given means for adjustment. Returns 0 if the length of the vector is not equal.
     */
    public static Double computeAdjustedCosineSimilarityDoubles(List<Double> v1, List<Double> v2, Double v1mean, Double v2mean) {
        Double result = 0.0;
        int size = v1.size();

        if (v1.size() == v2.size()) {
            Double xy = 0.0;
            Double xx = 0.0;
            Double yy = 0.0;

            for (int i = 0; i < size; i++) {
                Double xi = v1.get(i);
                Double yi = v2.get(i);
                xy += (xi - v1mean) * (yi - v2mean);
                xx += (xi - v1mean) * (xi - v1mean);
                yy += (yi - v2mean) * (yi - v2mean);
            }

            if (xx > 0.0 && yy > 0.0) {
                result = xy / (Math.sqrt(xx) * Math.sqrt(yy));
            }
        }
        return result;
    }

    /**
     * Computes the Adjusted Cosine similarity between two vectors.
     *
     * @param v1       the first integer vector
     * @param v2       the second integer vector
     * @param adjusted the flag indication if values are already adjusted. If false, adjustment is calculated with means of vectors
     * @return adjusted cosine similarity between the two given vectors using given means for adjustment. Returns 0 if the length of the vector is not equal.
     */
    public static Double computeAdjustedCosineSimilarity(List<Double> v1, List<Double> v2, boolean adjusted) {
        Double result = 0.0;
        if (!adjusted) {
            // for user based adjusted cosine, adjustment value can be calculated with mean of v1 and v2
            result = computeAdjustedCosineSimilarityDoubles(v1, v2);
        } else {
            // for item based adjusted cosine, the values of the vectores v1 and v2 has been adjusted already, so no further adjustment is necessary
            result = computeAdjustedCosineSimilarityDoubles(v1, v2, 0.0, 0.0);
        }

        return result;
    }

    /**
     * Compute the overlapping where both vectors have a value greater than 0.
     *
     * @param v1 the first integer vector
     * @param v2 the second integer vector
     * @return overlapping count.
     */
    public static Integer getOverlapping(List<Integer> v1, List<Integer> v2) {
        if (v1.size() != v2.size()) return 0;

        int overlapping = 0;
        for (int i = 0; i < v1.size(); i++) {
            if (v1.get(i) > 0 && v2.get(i) > 0) overlapping++;
        }

        return overlapping;
    }

    /**
     * Determinate whether a paging should be applied or not.
     * Applies the paging if necessary.
     *
     * @param array the array where the paging should be applied.
     * @param param a map with parameters.
     * @param <T>   Type of the array
     * @return the array with an apply paging.
     */
    public static <T> T[] applyPaging(T[] array, Map<String, String> param) {
        if (param != null && param.containsKey(Helper.Keys.Page)) {
            int page = 1;
            int pageSize = Keys.PageSize;

            String pageString = param.get(Helper.Keys.Page);
            if (Helper.isIntegerRegex(pageString)) {
                page = Integer.parseInt(pageString);
            }


            if (param.containsKey(Helper.Keys.PageSizeKey)) {
                String pageSizeString = param.get(Helper.Keys.PageSizeKey);
                if (Helper.isIntegerRegex(pageSizeString)) {
                    pageSize = Integer.parseInt(pageSizeString);
                }
            }

            int start = (page - 1) * pageSize;
            int end = page * pageSize;

            if (start >= 0 && start < array.length) {
                if (end <= array.length) {
                    return Arrays.copyOfRange(array, start, end);
                } else {
                    return Arrays.copyOfRange(array, start, array.length);
                }
            } else {
                return Arrays.copyOfRange(array, 0, 0);
            }

        } else {
            return array;
        }
    }

    /**
     * Trims a list if its size exceeds the topN or the count parameter in param.
     *
     * @param items a list that should be trimmed.
     * @param param a map with parameter that may contain a count parameter
     * @param topN  the fallback if no count parameter is given.
     * @param <T>   Type of the items containing in the list
     * @return the trimmed list.
     */
    public static <T> List<T> applySubList(List<T> items, Map<String, String> param, int topN) {
        if (param != null && param.containsKey(Keys.Count)) {
            int count = Integer.parseInt(param.get("count"));
            if (items.size() > count) items = items.subList(0, count);
        } else {
            if (items.size() > topN) items = items.subList(0, topN);
        }

        return items;
    }

    /**
     * Trims a list if its size exceeds the topN.
     *
     * @param items a list that should be trimmed.
     * @param topN  the fallback if no count parameter is given.
     * @param <T>   Type of the items containing in the list
     * @return the trimmed list.
     */
    public static <T> List<T> applySubList(List<T> items, int topN) {
        if (items.size() > topN) items = items.subList(0, topN);

        return items;
    }

    /**
     * Check whether the string is an positive integer value or not. Uses regex.
     *
     * @param str a string that should be checked.
     * @return a boolean whether the string is an integer or not.
     */
    public static boolean isIntegerRegex(String str) {
        return str != null && str.matches("^[0-9]+$");
    }

    /**
     * return directory of path and create it if necessary
     *
     * @param path path of the directory
     * @return directory
     */
    public static File createOrGetDir(String path) {
        File dir = new File(path);

// if the directory does not exist, create it
        if (!dir.exists()) {
            try {
                dir.mkdir();
            } catch (SecurityException se) {

            }
        }

        return dir;
    }

    /**
     * split a keyid string into a map
     * @param config
     * @return
     */
    public static Map<String, String> splitIdKeyConfig(String config) {
        //example confing: mp@rec-mp,blub,test-rec@test-rec-key


        //key , id pair
        HashMap<String, String> result = new HashMap<>();
        String[] split = config.split(",");

        for (String s : split) {
            //id should be something like mp@mp-test -> id@display-name
            if (s != null && !s.isEmpty()) {
                if (s.contains("@")) {
                    String[] idSplit = s.split("@");
                    if (idSplit.length > 1) {
                        result.put(idSplit[0], idSplit[1]);
                    }
                } else {
                    //if no @ is available the key equals the id
                    result.put(s, s);
                }
            }
        }

        return result;
    }


    /**
     * This class contains fixed keys that are used at several places.
     */
    public static class Keys {
        public static final String ExperimentId = "experiment-id".intern();
        public static final String OutputParam = "output".intern();
        public static final String ItemId = "item-id".intern();
        public static final String UserId = "user-id".intern();
        public static final String RelationId = "relation-id".intern();
        public static final String FromId = "from-id".intern();
        public static final String ToId = "to-id".intern();
        public static final String SourceId = "source-id".intern();
        public static final String DataBuilderId = "data-builder-id".intern();
        public static final String RecommenderId = "rec-id".intern();
        public static final String Type = "type".intern();
        public static final String DataSplitterId = "splitter-id".intern();
        public static final String State = "state".intern();
        public static final String Dir = "dir".intern();
        public static final String ID = "id".intern();
        public static final String TopN = "topN".intern();
        public static final String MetricIDs = "metric-ids".intern();
        public static final String Value = "value".intern();
        public static final String RecommenderBuilderId = "rec-builder-id".intern();
        public static final String RecommenderBuilderIdPrefix = "rec-builder-".intern();
        public static final String Page = "page".intern();
        public static final String PageSizeKey = "page-size".intern();
        public static final String Count = "count".intern();
        public static final String Key = "key".intern();
        public static final String TimeStamp = "timeStamp".intern();
        public static final String SplitType = "split-type".intern();

        public static final Integer PageSize = 10;
        public static final String Percentage = "percentage";

        public static class Context {
            public static final String LastConsumed = "last-consumed".intern();
            public static final String Params = "params".intern();
            public static final String DataSet = "dataSet".intern();
            public static final String Item = "item".intern();
        }
    }
}

