package autoclick;

import java.util.Comparator;
import java.util.Map;

public class EntryComparator<K> implements Comparator {

  public int compare(Object o1, Object o2) {
    Map.Entry<K, Integer> e1 = (Map.Entry<K, Integer>) o1;
    Map.Entry<K, Integer> e2 = (Map.Entry<K, Integer>) o2;

    return e2.getValue().compareTo(e1.getValue());
  }

}
