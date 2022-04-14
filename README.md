# Flow 

Parallel, sequential, repeated and nested execution of asynchronous tasks based on GWT and Promises. 

The flow API is currently only used in the [HAL management console](https://hal.github.io) and not available on its own. This repository is mainly used as a playground to evolve and test the API. If there's interest to make this available on its own, leave a note in the [discussions](https://github.com/hpehl/flow/discussions)!  

## API 

The entrypoint to the flow API is the interface `Flow<C extends FlowContext>`. It provides three methods:

```java
static <C extends FlowContext> Sequence<C> parallel(C context, List<Task<C>> tasks)

static <C extends FlowContext> Sequence<C> sequential(C context, List<Task<C>> tasks)

static <C extends FlowContext> While<C> while_(C context, Task<C> task, Predicate<C> until)
```

Tasks are implemented using a simple interface: 

```java
@FunctionalInterface
public interface Task<C extends FlowContext> {

    Promise<C> apply(C context);
}
```

All tasks share a common context. The context provides a progress indicator to signal the progress of the task execution and a stack and a map for sharing data between asynchronous tasks.

### Parallel Execution

To execute a list of asynchronous tasks in parallel, use something like this 

```java
List<Task<FloContext>> task = ...;
Flow.parallel(new FlowContext(), tasks)
        .failFast(true)
        .subscribe(context -> console.log("Success!"),
                (context, failure) -> console.error("Failed: " + failure));
```

### Sequential Execution

To execute a list of asynchronous tasks in order, use something like this

```java
List<Task<FloContext>> task = ...;
Flow.sequential(new FlowContext(), tasks)
        .failFast(true)
        .subscribe(context -> console.log("Success!"),
                (context, failure) -> console.error("Failed: " + failure));
```

### Repeated Execution

Pending...

### Nested Execution

Pending...

## Build & Run

To build locally use

```shell
mvn verify
```

To run locally use

```shell
mvn gwt:devmode
```

## Try Online

There's a demo available at https://hpehl.github.io/flow which uses the flow API to execute tasks in parallel, in order, in a while loop and nested inside each other. 
