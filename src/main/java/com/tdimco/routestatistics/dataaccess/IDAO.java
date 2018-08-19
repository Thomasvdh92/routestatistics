package com.tdimco.routestatistics.dataaccess;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Thomas on 6-4-2018.
 */
public interface IDAO<T> {

    public T create(T t);

    public T read(T t);

    public T update(T t);

    public void delete(T t);

    public List<T> getAll();

}
