package com.tgenie.common.util.comparator;

import com.google.common.collect.Lists;

import java.util.*;

import com.tgenie.common.util.vo.*;


/**
 * @author dzt
 * @date 17/8/24
 * Hope you know what you have done
 *
 * 多字段排序器
 */
public class MultiColComparator implements Comparator<Map<String, Object>> {

    private List<Order> compareCols;

    private MultiColComparator(List<Order> compareCols) {
        this.compareCols = compareCols;
    }

    @Override
    public int compare(Map<String, Object> vo1, Map<String, Object> vo2) {

        int result;
        Order order = compareCols.get(0);
        String colName = order.getColName();
        boolean isASC = order.isASC();
        Quantifiable dict = order.getDict();

        if (vo1.get(colName) == null & vo2.get(colName) == null) {
            result = 0;
        } else if (vo1.get(colName) == null & vo2.get(colName) != null) {
            result = -1;
        } else if (vo1.get(colName) != null & vo2.get(colName) == null) {
            result = 1;
        } else {
            boolean comparableInOrder = dict != null && (Integer.MAX_VALUE != dict.orderOf(vo1.get(colName) + "") || Integer.MAX_VALUE != dict.orderOf(vo2.get(colName) + ""));
            if (comparableInOrder) {
                result = (dict.orderOf(vo1.get(colName) + "")).compareTo(dict.orderOf(vo2.get(colName) + ""));
            } else {
                result = (vo1.get(colName) + "").compareTo(vo2.get(colName) + "");
            }
        }

        if (result == 0 && compareCols.size() > 1) {
            return new MultiColComparator(compareCols.subList(1, compareCols.size())).compare(vo1, vo2);
        } else {
            return result * (isASC ? 1 : -1);
        }
    }

    public static void main(String[] args) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map14 = new HashMap<>(10);
        map14.put("color", "黑色");
        map14.put("size", "4XL");
        list.add(map14);
        Map<String, Object> map1 = new HashMap<>(10);
        map1.put("color", "黑色");
        map1.put("size", "2XL");
        list.add(map1);
        Map<String, Object> map3 = new HashMap<>(10);
        map3.put("color", "黑色");
        map3.put("size", "L");
        list.add(map3);
        Map<String, Object> map2 = new HashMap<>(10);
        map2.put("color", "黑色");
        map2.put("size", "XL");
        list.add(map2);
        Map<String, Object> map7 = new HashMap<>(10);
        map7.put("color", "白色");
        map7.put("size", "XL");
        list.add(map7);
        Map<String, Object> map6 = new HashMap<>(10);
        map6.put("color", "白色");
        map6.put("size", "2XL");
        list.add(map6);
        Map<String, Object> map8 = new HashMap<>(10);
        map8.put("color", "白色");
        map8.put("size", "L");
        list.add(map8);
        Map<String, Object> map12 = new HashMap<>(10);
        map12.put("color", "白色");
        map12.put("size", "3XL");
        list.add(map12);
        Map<String, Object> map4 = new HashMap<>(10);
        map4.put("color", "黑色");
        map4.put("size", "M");
        list.add(map4);
        Map<String, Object> map9 = new HashMap<>(10);
        map9.put("color", "白色");
        map9.put("size", "M");
        list.add(map9);
        Map<String, Object> map5 = new HashMap<>(10);
        map5.put("color", "黑色");
        map5.put("size", "X");
        list.add(map5);
        Map<String, Object> map10 = new HashMap<>(10);
        map10.put("color", "白色");
        map10.put("size", "X");
        list.add(map10);
        Map<String, Object> map11 = new HashMap<>(10);
        map11.put("color", "黑色");
        map11.put("size", "3XL");
        list.add(map11);
        Map<String, Object> map13 = new HashMap<>(10);
        map13.put("color", "白色");
        map13.put("size", "4XL");
        list.add(map13);
        Map<String, Object> map15 = new HashMap<>(10);
        map15.put("color", "白色");
        map15.put("size", "A");
        list.add(map15);
        System.out.println(list.toString());
        list.sort(new MultiColComparator(Lists.newArrayList(new Order("color"), new Order("size", Size.DICT))));
        System.out.println(list.toString());
    }
}
