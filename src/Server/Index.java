package Server;

import java.util.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

class Index {
    private static ConcurrentHashMap<String, Collection<String>> index = null;

    public Index(ConcurrentHashMap<String, Collection<String>> index) {
        this.index = index;
    }

    public Collection<String> findInvertedIndex(String word) {
        if (index == null) {
            return null;
        }
        Collection<String> files = index.getOrDefault(word, null);
        return files;
    }
}
