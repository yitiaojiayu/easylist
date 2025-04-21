package com.yitiaojiayu.easylist;

import com.yitiaojiayu.kryo.KryoSimple;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author yitiaojiayu
 * @date 2025/3/27
 */
@SuppressWarnings({"AlibabaConstantFieldShouldBeUpperCase", "PatternVariableCanBeUsed", "unchecked", "unused", "java:S106"})
public class EasyList<E> implements List<E> {

    private static final int DEFAULT_BUFFER_CAPACITY = 1024;
    private static final int DEFAULT_LIMIT_SIZE = 10;
    private static final int MULTIPLE = 2;

    private ByteBuffer buffer;
    private int useStart;
    private int useEnd;
    private int[] index;
    private int[] size;
    private int limit;
    private int count;

    public EasyList() {
        this.buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_CAPACITY);
        this.useStart = 0;
        this.useEnd = 0;
        this.index = new int[DEFAULT_LIMIT_SIZE];
        this.size = new int[DEFAULT_LIMIT_SIZE];
        this.limit = DEFAULT_LIMIT_SIZE;
        this.count = 0;
    }

    private void resizeBufferBase(int startPosition) {
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() * MULTIPLE);
        buffer.position(useStart);
        buffer.limit(useEnd);
        newBuffer.position(startPosition);
        newBuffer.put(buffer);
        this.buffer = newBuffer;
    }

    private void resizeBufferEnd() {
        resizeBufferBase(useStart);
    }

    private void resizeBufferStart() {
        int bufferSize = buffer.capacity();
        useStart += bufferSize;
        useEnd += bufferSize;
        for (int i = 0; i < count; i++) {
            index[i] += bufferSize;
        }
        resizeBufferBase(useStart);
    }

    private boolean memoryEndNotFull(E e) {
        int memorySize = KryoSimple.asByteArray(e).length;
        int memoryRemaining = buffer.capacity() - useEnd;
        return memoryRemaining >= memorySize;
    }

    private boolean memoryStartNotFull(E e) {
        int memorySize = KryoSimple.asByteArray(e).length;
        return useStart >= memorySize;
    }

    private void resizeArrays() {
        int newLimit = limit * MULTIPLE;
        int[] newIndex = new int[newLimit];
        int[] newSize = new int[newLimit];
        System.arraycopy(index, 0, newIndex, 0, limit);
        System.arraycopy(size, 0, newSize, 0, limit);
        index = newIndex;
        size = newSize;
        limit = newLimit;
    }

    private void arraysExt() {
        if (limit <= count) {
            resizeArrays();
        }
    }

    private void detectEnd(E e) {
        while (!memoryEndNotFull(e)) {
            resizeBufferEnd();
        }
        arraysExt();
    }

    private void detectStart(E e) {
        while (!memoryStartNotFull(e)) {
            resizeBufferStart();
        }
        arraysExt();
    }

    private E getData(int i) {
        byte[] data = EasyListNative.get(buffer, index[i], size[i]);
        return KryoSimple.asObject(data);
    }

    private boolean deleteDate(int i) {
        return EasyListNative.delete(buffer, index[i], size[i], useStart, useEnd);
    }

    private void delete(int i) {
        int deletedSize = size[i];
        if (deleteDate(i)) {
            useEnd -= deletedSize;
        } else {
            useStart += deletedSize;
        }
        int numMoved = count - i - 1;
        if (numMoved > 0) {
            System.arraycopy(index, i + 1, index, i, numMoved);
            System.arraycopy(size, i + 1, size, i, numMoved);
        }
        count--;
    }

    private void addData(int index, E element, boolean rightExt) {

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
        for (int i = 0; i < count; i++) {
            E element = get(i);
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
            return cursor < count;
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
        Object[] arr = new Object[count];
        for (int i = 0; i < count; i++) {
            arr[i] = get(i);
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] arr) {
        if (arr == null) {
            throw new NullPointerException("EasyList: toArray(T[] arr): Input array cannot be null");
        }
        if (arr.length < count) {
            Class<?> componentType = arr.getClass().getComponentType();
            T[] newArray = (T[]) Array.newInstance(componentType, count);
            for (int i = 0; i < count; i++) {
                try {
                    newArray[i] = (T) get(i);
                } catch (ClassCastException e) {
                    throw new ArrayStoreException("EasyList: toArray(T[] arr): Element type mismatch during array copy");
                }
            }
            return newArray;
        }
        for (int i = 0; i < count; i++) {
            try {
                arr[i] = (T) get(i);
            } catch (ClassCastException e) {
                throw new ArrayStoreException("EasyList: toArray(T[] arr): Element type mismatch during array copy");
            }
        }
        if (arr.length > count) {
            arr[count] = null;
        }
        return arr;
    }

    @Override
    public boolean add(E e) {
        detectEnd(e);
        byte[] data = KryoSimple.asByteArray(e);
        int dataLength = data.length;
        buffer.put(data);
        index[count] = useEnd;
        size[count] = dataLength;
        useEnd += dataLength;
        count++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < count; i++) {
            if (get(i).equals(o)) {
                delete(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
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
    public E get(int i) {
        if (i < 0 || i >= count) {
            throw new IndexOutOfBoundsException("EasyList: get(int index): failed, Because Index: " + i + ", Size: " + count);
        }
        return getData(i);
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("EasyList: add(int index, E element): Index out of bounds. Index: " + index + ", Size: " + count);
        }
        byte[] data = KryoSimple.asByteArray(element);
        int dataLength = data.length;
        int endSize = this.index[index];
        int startSize = this.index[count] - endSize - dataLength;
        boolean endExt = endSize > startSize;
        if (endExt) {
            if (memoryEndNotFull(element)) {
                useEnd += dataLength;
            }

        } else {
            useStart -= dataLength;
        }
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("EasyList: remove(int index): Index out of bounds. Index: " + index + ", Size: " + count);
        }
        E elementToRemove = getData(index);
        deleteDate(index);
        return elementToRemove;
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
            i++;
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
