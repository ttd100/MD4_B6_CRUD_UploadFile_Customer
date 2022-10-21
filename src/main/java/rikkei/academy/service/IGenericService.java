package rikkei.academy.service;

import java.util.List;

public interface IGenericService<T>{
    List<T> findAll();
    T findById(Long id);
    void save(T t);
    void remove(Long id);
}
