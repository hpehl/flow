/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.flow;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;

import elemental2.promise.Promise;

/**
 * General purpose context to be used as a common data structure when executing a list of {@linkplain Task asynchronous tasks} in {@linkplain Flow#parallel(FlowContext, List) parallel}, in {@linkplain Flow#sequential(FlowContext, List) sequence} or when executing a task {@linkplain Flow#repeat(FlowContext, Task) repeatedly} while a {@linkplain Predicate condition} evaluates to {@code true}.
 * <p>
 * The context provides a {@linkplain Progress progress indicator} to signal the progress of the task execution and a stack and a map for sharing data between {@linkplain Task asynchronous tasks}.
 */
public class FlowContext {

    private final Stack<Object> stack;
    private final Map<String, Object> data;
    final Progress progress;

    /**
     * Creates a new instance with a {@linkplain Progress#NOOP noop progress implementation}.
     */
    public FlowContext() {
        this(Progress.NOOP);
    }

    /**
     * Creates a new instance using the given progress indicator.
     *
     * @param progress the progress indicator to signal progress when executing the tasks
     */
    public FlowContext(Progress progress) {
        this.progress = progress;
        this.stack = new Stack<>();
        this.data = new HashMap<>();
    }

    /**
     * Resolves this context as a promise.
     */
    public Promise<FlowContext> resolve() {
        return Promise.resolve(this);
    }

    /**
     * Pushes the value on top of the stack and resolves this context as a promise.
     */
    public <T> Promise<FlowContext> resolve(T value) {
        push(value);
        return resolve();
    }

    /**
     * Stores the value under the given key in the map and resolves this context as a promise.
     */
    public <T> Promise<FlowContext> resolve(String key, T value) {
        set(key, value);
        return resolve();
    }

    /**
     * Rejects this context with an error.
     */
    public Promise<FlowContext> reject(String failure) {
        return Promise.reject(failure);
    }

    /**
     * Pushes the value om top of the stack.
     */
    public <T> void push(T value) {
        stack.push(value);
    }

    /**
     * Removes the object at the top of the stack and returns that object.
     *
     * @return The object at the top of the stack.
     * @throws EmptyStackException if this stack is empty.
     */
    @SuppressWarnings("unchecked")
    public <T> T pop() {
        return (T) stack.pop();
    }

    /**
     * Removes the object at the top of the stack and returns that object or returns the default value, if the stack is empty.
     *
     * @return The object at the top of the stack or the default value if the stack is empty.
     */
    @SuppressWarnings("unchecked")
    public <T> T pop(T defaultValue) {
        return emptyStack() ? defaultValue : (T) stack.pop();
    }

    /**
     * @return {@code true} if the stack is empty, {@code false} otherwise.
     */
    public boolean emptyStack() {
        return stack.empty();
    }

    /**
     * Stores the value under the given key in the map.
     */
    public <T> void set(String key, T value) {
        data.put(key, value);
    }

    /**
     * @return the object for the given key from the map or {@code null} if no such key was found.
     */
    public <T> T get(String key) {
        return get(key, null);
    }

    /**
     * @return the object for the given key from the context map or {@code defaultValue} if no such key was found.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) data.getOrDefault(key, defaultValue);
    }

    /**
     * @return the set of keys stored in the map.
     */
    public Set<String> keys() {
        return data.keySet();
    }

    @Override
    public String toString() {
        return "FlowContext {stack: " + stack + ", map: " + data + "}";
    }
}
