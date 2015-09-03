package com.recalot.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * This class contains all kind of helper methods that are used by several cases.
 *
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Helper {
    /**
     * Copies the bytes of an InputStream to a PrintWriter.
     *
     * @param out a PrintWriter to whom the bytes of the InputStream should be copied
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
     * @param map the container map
     * @param key the key for the entry that should be increment
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
     * @param map the container map
     * @param key the key for the entry that should be increment
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

    /***
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
     * @param <T> Type of the array
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
     * @param topN the fallback if no count parameter is given.
     * @param <T> Type of the items containing in the list
     *
     * @return the trimmed list.
     */
    public static <T> List<T> applySubList(List<T> items, Map<String, String> param, int topN) {
        if(param != null && param.containsKey(Keys.Count)) {
            int count = Integer.parseInt(param.get("count"));
            if(items.size() > count) items = items.subList(0, count);
        } else {
            if(items.size() > topN) items = items.subList(0, topN);
        }

        return items;
    }

    /**
     * Trims a list if its size exceeds the topN.
     *
     * @param items a list that should be trimmed.
     * @param topN the fallback if no count parameter is given.
     * @param <T> Type of the items containing in the list
     *
     * @return the trimmed list.
     */
    public static <T> List<T> applySubList(List<T> items, int topN) {
        if(items.size() > topN) items = items.subList(0, topN);

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
    }
}
