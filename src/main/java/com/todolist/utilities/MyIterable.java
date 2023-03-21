package com.todolist.utilities;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MyIterable<T> implements Iterable<T> {

    // Attributes -------------------------------------------------------------
    private final int end;
    public final List<T> elements;
    private int start;

    // Constructors -----------------------------------------------------------
    public MyIterable(List<T> elements, Integer limit, Integer offset) {
        if (limit == -1) limit = elements.size();
        this.elements = elements;
        this.start = offset == null || offset < 1 ? 0 : offset - 1;
        this.end = limit > elements.size() || limit + start > elements.size() ? elements.size() : start + limit;
    }

    // Methods ----------------------------------------------------------------

    @Override
    public Iterator<T> iterator() {
        return new MyIterator(this);
    }

    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    // Inner classes ----------------------------------------------------------
    private class MyIterator implements Iterator<T> {

        // Attributes ---------------------------------------------------------
        private final MyIterable<T> myIterable;

        // Constructors -------------------------------------------------------
        public MyIterator(MyIterable<T> myIterable) {
            this.myIterable = myIterable;
        }

        // Methods ------------------------------------------------------------
        @Override
        public boolean hasNext() {
            return start < end;
        }

        @Override
        public T next() {
            return myIterable.elements.get(start++);
        }
    }
}
