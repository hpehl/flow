package org.jboss.hal.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * General purpose context to be used inside a {@linkplain Flow flow} and {@linkplain Task tasks}. Provides a {@linkplain Progress progress indicator}, a stack and a map for
 * sharing data between tasks.
 */
public class FlowContext {

    private final Stack<Object> stack;
    private final Map<String, Object> data;
    final Progress progress;

    public FlowContext() {
        this(Progress.NOOP);
    }

    public FlowContext(Progress progress) {
        this.progress = progress;
        this.stack = new Stack<>();
        this.data = new HashMap<>();
    }

    /**
     * Pushes the value om top of the context stack.
     */
    public <T> void push(T value) {
        stack.push(value);
    }

    /**
     * Removes the object at the top of the context stack and returns that object.
     *
     * @return The object at the top of the context stack.
     */
    @SuppressWarnings("unchecked")
    public <T> T pop() {
        return (T) stack.pop();
    }

    /**
     * @return {@code true} if the context stack is empty, {@code false} otherwise.
     */
    public boolean emptyStack() {
        return stack.empty();
    }

    /**
     * Stores the value under the given key in the context map.
     */
    public <T> void set(String key, T value) {
        data.put(key, value);
    }

    /**
     * @return the object for the given key from the context map or {@code null} if no such key was found.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    /**
     * @return the object for the given key from the context map or {@code null} if no such key was found.
     */
    public Set<String> keys() {
        return data.keySet();
    }

    @Override
    public String toString() {
        return "FlowContext {stack: " + stack + ", map: " + data + "}";
    }
}
