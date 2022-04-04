# Flow 

Parallel and sequential execution of asynchronous tasks based on GWT and Promises. 

This repository contains a small API to execute asynchronous tasks in parallel or one after the other. The API is used in the [HAL management console](https://hal.github.io). This repository is mainly used as a playground to evolve and test the API.  

## API 

The API basically consists of these classes:

### Flow

```java
public interface Flow {

    static <C extends FlowContext> Promise<C> parallel(C context, List<Task<C>> tasks) {
        return parallel(context, tasks, true);
    }

    static <C extends FlowContext> Promise<C> parallel(C context, List<Task<C>> tasks, boolean failFast) {
        // ...
    }

    static <C extends FlowContext> Promise<C> series(C context, List<Task<C>> tasks) {
        return series(context, tasks, true);
    }

    static <C extends FlowContext> Promise<C> series(C context, List<Task<C>> tasks, boolean failFast) {
        // ...
    }
}
```

### Task

```java
@JsFunction
@FunctionalInterface
public interface Task<C extends FlowContext> {

    @JsAsync
    Promise<C> apply(C context);
}
```

### FlowContext

The `FlowContext` acts as a common data structure which is shared between the tasks. It can be used to store and retrieve arbitrary data. 

## Build

To build locally use

```shell
mvn verify
```

## Run

To run locally use 

```shell
mvn gwt:devmode
```

## Demo

There's a small demo available at https://hpehl.github.io/flow which uses the API to execute tasks in parallel and sequential order. 
