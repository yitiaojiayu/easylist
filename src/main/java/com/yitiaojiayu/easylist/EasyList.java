package com.yitiaojiayu.easylist;

import com.yitiaojiayu.kryo.KryoSimple;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author yitiaojiayu
 * @date 2025/3/27
 */
@SuppressWarnings({"AlibabaConstantFieldShouldBeUpperCase", "PatternVariableCanBeUsed", "unused", "java:S106"})
public class EasyList<E> implements List<E> {

    private static final int DEFAULT_BUFFER_CAPACITY = 1024;
    private static final int DEFAULT_LIMIT_SIZE = 10;

    private ByteBuffer buffer;
    private int limit;
    private int count;
    private int[] index;
    private int[] size;
    private int memoryUsed;

    public EasyList() {
        buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_CAPACITY);
        limit = DEFAULT_LIMIT_SIZE;
        this.count = 0;
        index = new int[limit];
        size = new int[limit];
        this.memoryUsed = 0;
    }

    private void resizeBuffer() {
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() * 2);
        buffer.position(0);
        buffer.limit(memoryUsed);
        newBuffer.put(buffer);
        this.buffer = newBuffer;
    }

    private void resizeArrays() {
        int newLimit = limit * 2;
        int[] newIndex = new int[newLimit];
        int[] newSize = new int[newLimit];
        System.arraycopy(index, 0, newIndex, 0, limit);
        System.arraycopy(size, 0, newSize, 0, limit);
        index = newIndex;
        size = newSize;
        limit = newLimit;
    }

    private boolean memorySufficient(E e) {
        int memorySize = KryoSimple.asByteArray(e).length;
        int memoryRemaining = buffer.capacity() - memoryUsed;
        return memoryRemaining >= memorySize;
    }

    private boolean arraySufficient() {
        return limit > count;
    }

    private void detect(E e) {
        if (!memorySufficient(e)) {
            resizeBuffer();
        }
        if (!arraySufficient()) {
            resizeArrays();
        }
    }

    private E getData(int i) {
        byte[] data = EasyListNative.get(buffer, index[i], size[i]);
        return KryoSimple.asObject(data);
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
        detect(e);
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
            throw new IndexOutOfBoundsException("EasyList: get: failed, Because Index: " + i + ", Size: " + count);
        }
        return getData(i);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        while (count > i) {
            sb.append(getData(i));
            if (count > i + 1) {
                sb.append(", ");
            }
            i ++;
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
        for (int i = 0; i < count; i++) {
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
        for (int i = 0; i < count; i++) {
            E element = get(i);
            hashCode = 31 * hashCode + (element == null ? 0 : element.hashCode());
        }
        return hashCode;
    }
}
