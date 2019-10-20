package nsu.manasyan.treechat.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class FiniteQueue <T>{
    private final int capacity;

    private Deque<T> guids = new ArrayDeque<>();

    private int size = 0;

    public FiniteQueue(int capacity) {
        this.capacity = capacity;
    }

    public void addGUID(T guid){
        guids.addFirst(guid);
        ++size;
        checkSize();
    }

    public void checkSize(){
        if(size >= capacity){
            guids.removeLast();
        }
    }

    public boolean contains(T guid){
        return guids.contains(guid);
    }
}
