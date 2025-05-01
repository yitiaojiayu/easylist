package com.yitiaojiayu.easylist;

import com.yitiaojiayu.kryo.KryoSimple;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author yitiaojiayu
 * @date 2025/3/27
 */
public class EasyList<E> implements List<E> {

    private int id;

    public EasyList() {
        this.id = EasyListNative.new_object();
    }

    @Override
    public int size() {
        return EasyListNative.size(id);
    }

    @Override
    public boolean isEmpty() {
        return EasyListNative.is_empty(id);
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    // private class EasyIterator implements Iterator<E> {
    //     int cursor = 0;
    //
    //     @Override
    //     public boolean hasNext() {
    //         return cursor < count;
    //     }
    //
    //     @Override
    //     public E next() {
    //         if (!hasNext()) {
    //             throw new NoSuchElementException("EasyList: iterator(): No more elements");
    //         }
    //         E nextElement = EasyList.this.get(cursor);
    //         cursor++;
    //         return nextElement;
    //     }
    // }

    @Override
    public Object[] toArray() {
        return null;
    }

    @Override
    public <T> T[] toArray(T[] arr) {
        return null;
    }

    @Override
    public boolean add(E e) {
        return EasyListNative.add(id, size(), KryoSimple.asByteArray(e));
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public E get(int index) {
        return KryoSimple.asObject(EasyListNative.get(id, index));
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E e) {
        EasyListNative.add(id, index, KryoSimple.asByteArray(e));
    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return List.of();
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
