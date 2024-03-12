**No longer active**

Flow is now part of [Elemento](https://github.com/hal/elemento?tab=readme-ov-file#flow). 

---

# Flow 

Parallel, sequential, repeated and nested execution of asynchronous tasks in GWT using promises. 

The flow API is currently only used in the [HAL management console](https://hal.github.io) and not available on its own. This repository is mainly used as a playground to evolve and test the API. If there's interest to make this available on its own, leave a note in the [discussions](https://github.com/hpehl/flow/discussions)!  

## API 

The entrypoint to the flow API is the interface `Flow<C extends FlowContext>`. It provides three static methods:

1. `<C extends FlowContext> Sequence<C> parallel(C context, List<Task<C>> tasks)`
2. `<C extends FlowContext> Sequence<C> sequential(C context, List<Task<C>> tasks)`
3. `<C extends FlowContext> Repeat<C> repeat(C context, Task<C> task)`

Tasks need to implement a simple interface: 

```java
@FunctionalInterface
public interface Task<C extends FlowContext> {

    Promise<C> apply(C context);
}
```

All tasks share a common context. The context provides an indicator to signal the progress of the task execution plus a stack and a map for sharing data between tasks. 

The result of the task execution can be processed in different ways:

**Subscription**

The subscription is a terminal operation which supports a callback for the outcome of the execution. Errors in tasks are caught and are tracked in the context. The context provides methods to check if the execution was successful, ran into a timeout or failed with an error.

```java
List<Task<FloContext>> tasks = ...;
Flow.sequential(new FlowContext(), tasks)
        .subscribe(context -> console.log("Finished with status %s", context.status()));
```

**Promise**

The result can be returned as `Promise<C>` to be passed to a calling method, for example,

```java
void Promise<FlowContext> run() {
    List<Task<FloContext>> tasks = ...;
    return Flow.sequential(new FlowContext(), tasks).promise();
}
```

or it can be directly processed using methods like `then()`, `catch_()` or `finally_()`:

```java
List<Task<FloContext>> tasks = ...;
Flow.sequential(new FlowContext(), tasks)
        .then(context -> {
            // get and process data from the context 
        })
        .catch_(error -> {
            // error handling
        });
```

Errors in tasks are propagated to the closest catch handler.

### Parallel Execution

Parallel execution runs all tasks simultaneously and returns when all tasks have finished. For the parallel execution you can specify 

- whether to fail fast or fail last
- a timeout after the execution is canceled. Pleas note that the timeout isn't used at the moment. The reason for that is the all promises are started in parallel and the promise API doesn't allow to cancel running promises at the moment.

To execute a list of tasks in parallel, use something like this 

```java
List<Task<FloContext>> tasks = ...;
Flow.parallel(new FlowContext(), tasks)
        .failFast(true)
        .subscribe(context -> console.log("Done!"));
```

### Sequential Execution

Sequential execution runs the tasks in order. That is the second task starts after the first finished. For the sequential execution you can specify

- whether to fail fast or fail last
- a timeout after the execution is canceled

To execute a list of tasks in order, use something like this

```java
List<Task<FloContext>> tasks = ...;
Flow.sequential(new FlowContext(), tasks)
        .failFast(true)
        .timeout(6_000)
        .subscribe(context -> console.log("Done!"));
```

### Repeated Execution

Repeated execution corresponds to a `while` loop and runs a task as long as certain conditions are met. For the repeated execution you can specify 

- the condition
- whether to fail fast or fail last
- the interval between the iterations
- a timeout after the loop is canceled
- the maximal iterations

To repeatedly execute a task, use something like this

```java
Task<FlowContext> task = context -> context.resolve(new Random().nextInt(10));
Flow.repeat(new FlowContext(), task)
        .while_(context -> context.<Integer>pop() != 3)
        .interval(500)
        .iterations(10)
        .subscribe(context -> {
                if (context.successful()) {
                    console.log("Got a three!");
                } else {
                    console.log("No luck!");
                }
        });
```

### Nested Execution

The flow API makes it easy to nest task executions. You could for instance run five tasks in parallel, then execute three tasks in order and finally execute a task until a condition is met. To do so, the API provides different task implementations:

- `ParallelTasks<C>`
- `SequentialTasks<C>`
- `RepeatTask<C>`

Here's an example that runs multiple asynchronous tasks in sequence three times:

```java
List<Task<FlowContext>> tasks = ...;
Flow.repeat(new FlowContext(), new SequentialTasks<>(tasks))
        .iterations(3)
        .subscribe(context -> console.log("Done!"));
```

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
