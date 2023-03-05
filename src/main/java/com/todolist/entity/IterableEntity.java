package com.todolist.entity;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IterableEntity<T> implements Iterable<T>{

    public List<T> elements;
    private int start;
    private final int end;

    public IterableEntity(List<T> elements, Integer limit, Integer offset) {
        if (limit == -1) limit = elements.size();
        this.elements = elements;
        this.start = offset == null || offset < 1 ? 0 : offset - 1;
        this.end = limit > elements.size() || limit + start > elements.size() ? elements.size() : start + limit;
    }

    private class IteratorEntity implements Iterator<T> {

        private final IterableEntity<T> iterableEntity;

        public IteratorEntity(IterableEntity<T> iterableEntity) {
            this.iterableEntity = iterableEntity;
        }

        @Override
        public boolean hasNext() {
            return start < end;
        }

        @Override
        public T next() {
            return iterableEntity.elements.get(start++);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorEntity(this);
    }

    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}