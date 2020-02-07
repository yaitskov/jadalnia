package org.dan.jadalnia.app.order;

import kotlin.Pair;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.order.pojo.OrderLabel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;

public class KotlinSucks {
    public static <E, K, V> Map<K, LinkedList<V>> group(
            Stream<E> s,
            Function<E, K> kF,
            Function<E, V> vF) {
        return s.collect(groupingBy(kF, mapping(vF, toCollection(LinkedList::new))));
    }

    public static Map<DishName, LinkedList<OrderLabel>> group(
            Stream<Pair<DishName, OrderLabel>> s) {
        return group(s, Pair::getFirst, Pair::getSecond);
    }
}
