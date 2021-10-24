package dev.zenqrt.utils.collection;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionUtils {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K,V> unsortedMap) {
        var sortedMap = new LinkedHashMap<K,V>();
        unsortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(entry -> sortedMap.put(entry.getKey(), entry.getValue()));

        return sortedMap;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K,V> unsortedMap, Comparator<V> comparator) {
        var sortedMap = new LinkedHashMap<K,V>();
        unsortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(comparator))
                .forEachOrdered(entry -> sortedMap.put(entry.getKey(), entry.getValue()));

        return sortedMap;
    }

}
