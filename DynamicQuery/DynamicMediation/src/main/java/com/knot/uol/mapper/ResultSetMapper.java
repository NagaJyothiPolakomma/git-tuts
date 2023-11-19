package com.knot.uol.mapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class ResultSetMapper<T> {
    private final Class<T> clazz;

    public ResultSetMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> mapResultSetToObjects(ResultSet resultSet) {
        List<T> objects = new ArrayList<>();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                T object = clazz.getDeclaredConstructor().newInstance();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(object, resultSet.getObject(i));
                }

                objects.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects;
    }
}
