package com.yitiaojiayu.easylist;

import com.yitiaojiayu.kryo.KryoSimple;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author yitiaojiayu
 * @date 2025/3/27
 */
@SuppressWarnings({"unused", "unchecked", "PatternVariableCanBeUsed", "java:S127", "java:S2160"})
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
        int lastRet = -1;
        int modCount;

        public EasyIterator() {
            this.modCount = EasyListNative.mod_count(id);
        }

        @Override
        public boolean hasNext() {
            return cursor < size();
        }

        @Override
        public E next() {
            if (modCount != EasyListNative.mod_count(id)) {
                throw new ConcurrentModificationException("EasyList: iterator: next: The list was also modified");
            }
            if (!hasNext()) {
                throw new NoSuchElementException("EasyList: iterator: next: No more elements");
            }
            E nextElement = EasyList.this.get(cursor);
            lastRet = cursor;
            cursor++;
            return nextElement;
        }

        @Override
        public void remove() {
            if (modCount != EasyListNative.mod_count(id)) {
                throw new ConcurrentModificationException("EasyList: iterator: remove: The list was also modified");
            }
            if (lastRet == -1) {
                throw new IllegalStateException("EasyList: iterator: remove: can only be called once after next()");
            }
            EasyList.this.remove(lastRet);
            cursor--;
            lastRet = -1;
            modCount = EasyListNative.mod_count(id);
        }
    }

    @Override
    public Object[] toArray() {
        int count = size();
        Object[] arr = new Object[count];
        for (int i = 0; i < count; i++) {
            arr[i] = get(i);
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int count = size();
        if (a == null) {
            throw new NullPointerException("EasyList: toArray(T[] a): Input array cannot be null");
        }
        if (a.length < count) {
            Class<?> componentType = a.getClass().getComponentType();
            T[] newArray = (T[]) Array.newInstance(componentType, count);
            for (int i = 0; i < count; i++) {
                try {
                    newArray[i] = (T) get(i);
                } catch (ClassCastException e) {
                    throw new ArrayStoreException("EasyList: toArray(T[] a): Element type mismatch during array copy");
                }
            }
            return newArray;
        }
        for (int i = 0; i < count; i++) {
            try {
                a[i] = (T) get(i);
            } catch (ClassCastException e) {
                throw new ArrayStoreException("EasyList: toArray(T[] a): Element type mismatch during array copy");
            }
        }
        if (a.length > count) {
            a[count] = null;
        }
        return a;
    }

    @Override
    public boolean add(E e) {
        return EasyListNative.add(id, size(), KryoSimple.asByteArray(e));
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(get(i), o)) {
                remove(i);
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
        if (c == null) {
            throw new NullPointerException("EasyList: addAll(Collection<? extends E> c): list is null");
        }
        Object[] a = c.toArray();
        int len = a.length;
        if (len == 0) {
            return false;
        }
        for (Object o : a) {
            E element = (E) o;
            add(element);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("EasyList: addAll(Collection<? extends E> c): list is null");
        }
        Object[] a = c.toArray();
        int len = a.length;
        if (len == 0) {
            return false;
        }
        for (Object o : a) {
            E element = (E) o;
            add(index++, element);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size(); i++) {
            if (c.contains(get(i))) {
                remove(i);
                i--;
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (int i = 0; i < size(); i++) {
            if (!c.contains(get(i))) {
                remove(i);
                i--;
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size(); i++) {
            remove(i);
            i--;
        }
    }

    @Override
    public E get(int index) {
        return KryoSimple.asObject(EasyListNative.get(id, index));
    }

    @Override
    public E set(int index, E element) {
        E oldElement = get(index);
        EasyListNative.set(id, index, KryoSimple.asByteArray(element));
        return oldElement;
    }

    @Override
    public void add(int index, E e) {
        EasyListNative.add(id, index, KryoSimple.asByteArray(e));
    }

    @Override
    public E remove(int index) {
        E e = get(index);
        EasyListNative.remove(id, index);
        return e;
    }

    @Override
    public int indexOf(Object o) {
        int count = size();
        if (o == null) {
            for (int i = 0; i < count; i++) {
                if (get(i) == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < count; i++) {
                if (o.equals(get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int count = size();
        if (o == null) {
            for (int i = count - 1; i >= 0; i--) {
                if (get(i) == null) {
                    return i;
                }
            }
        } else {
            for (int i = count - 1; i >= 0; i--) {
                if (o.equals(get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new EasyListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new EasyListIterator(index);
    }

    private class EasyListIterator implements ListIterator<E> {
        int cursor;
        int lastRet = -1;
        int modCount;

        public EasyListIterator() {
            this.modCount = EasyListNative.mod_count(id);
        }

        public EasyListIterator(int index) {
            if (index < 0 || index > size()) {
                throw new IndexOutOfBoundsException("EasyList: listIterator: Index Error");
            }
            this.cursor = index;
            this.modCount = EasyListNative.mod_count(id);
        }

        @Override
        public boolean hasNext() {
            return cursor < size();
        }

        @Override
        public E next() {
            if (modCount != EasyListNative.mod_count(id)) {
                throw new ConcurrentModificationException("EasyList: iterator: next: The list was also modified");
            }
            if (!hasNext()) {
                throw new NoSuchElementException("EasyList: listIterator: next: No next");
            }
            lastRet = cursor;
            return get(cursor++);
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        public E previous() {
            if (modCount != EasyListNative.mod_count(id)) {
                throw new ConcurrentModificationException("EasyList: iterator: next: The list was also modified");
            }
            if (!hasPrevious()) {
                throw new NoSuchElementException("EasyList: listIterator: previous: no previous");
            }
            lastRet = --cursor;
            return get(cursor);
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            if (modCount != EasyListNative.mod_count(id)) {
                throw new ConcurrentModificationException("EasyList: iterator: next: The list was also modified");
            }
            if (lastRet == -1) {
                throw new IllegalStateException("EasyList: listIterator: remove: can only be called once after next()");
            }
            EasyList.this.remove(lastRet);
            if (lastRet < cursor) {
                cursor--;
            }
            lastRet = -1;
            modCount = EasyListNative.mod_count(id);
        }

        @Override
        public void set(E e) {
            if (modCount != EasyListNative.mod_count(id)) {
                throw new ConcurrentModificationException("EasyList: iterator: next: The list was also modified");
            }
            if (lastRet == -1) {
                throw new IllegalStateException("EasyList: listIterator: remove: can only be called once after next()");
            }
            EasyList.this.set(lastRet, e);
        }

        @Override
        public void add(E e) {
            if (modCount != EasyListNative.mod_count(id)) {
                throw new ConcurrentModificationException("EasyList: iterator: next: The list was also modified");
            }
            EasyList.this.add(cursor, e);
            cursor++;
            lastRet = -1;
            modCount = EasyListNative.mod_count(id);
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new EasySubList(fromIndex, toIndex);
    }

    private class EasySubList extends AbstractList<E> {
        private final int offset;
        private int size;

        EasySubList(int fromIndex, int toIndex) {
            if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex) {
                throw new IndexOutOfBoundsException("EasyList: subList: Index Error");
            }
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public E get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("EasyList: subList: get: Index Error");
            }
            return EasyList.this.get(offset + index);
        }

        @Override
        public E set(int index, E element) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("EasyList: subList: set: Index Error");
            }
            return EasyList.this.set(offset + index, element);
        }

        @Override
        public void add(int index, E element) {
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException("EasyList: subList: add: Index Error");
            }
            EasyList.this.add(offset + index, element);
            size++;
        }

        @Override
        public E remove(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("EasyList: subList: remove: Index Error");
            }
            E removed = EasyList.this.remove(offset + index);
            size--;
            return removed;
        }
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
