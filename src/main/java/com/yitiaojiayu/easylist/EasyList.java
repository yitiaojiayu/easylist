package com.yitiaojiayu.easylist;

import com.yitiaojiayu.kryo.KryoSimple;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author yitiaojiayu
 * @date 2025/3/27
 */
@SuppressWarnings("AlibabaConstantFieldShouldBeUpperCase")
public class EasyList<E> implements List<E> {

    private final ByteBuffer buffer;
    private int count;
    private final int[] index;
    private final int[] size;
    private int memoryUsed;

    public EasyList() {
        buffer = ByteBuffer.allocateDirect(1024);
        index = new int[10];
        size = new int[10];
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        byte[] data = KryoSimple.asByteArray(e);
        buffer.put(data);
        index[count] = memoryUsed;
        size[count] = data.length;
        memoryUsed += data.length;
        count++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
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
    public E get(int i) {
        if (i < 0 || i >= count) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + count);
        }
        byte[] data = EasyListNative.get(buffer, index[i], size[i]);
        return KryoSimple.asObject(data);
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {
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
}
