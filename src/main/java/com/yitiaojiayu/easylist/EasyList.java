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

    private final int id;

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
        for (E element : this) {
            if (Objects.equals(element, o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new EasyIterator();
    }

    private class EasyIterator implements Iterator<E> {
        int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < size();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException("EasyList: iterator(): No more elements");
            }
            E nextElement = EasyList.this.get(cursor);
            cursor++;
            return nextElement;
        }
    }

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
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size(); i++) {
            sb.append(get(i));
            if (size() > i + 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List<?> other = (List<?>) o;
        if (size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            E thisElement = get(i);
            Object otherElement = other.get(i);
            if (!Objects.equals(thisElement, otherElement)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < size(); i++) {
            E element = get(i);
            hashCode = 31 * hashCode + (element == null ? 0 : element.hashCode());
        }
        return hashCode;
    }
}
