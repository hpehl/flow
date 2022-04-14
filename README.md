# Flow 

Parallel, sequential, repeated and nested execution of asynchronous tasks based on GWT and Promises. 

The flow API is currently only used in the [HAL management console](https://hal.github.io) and not available on its own. This repository is mainly used as a playground to evolve and test the API. If there's interest to make this available on its own, leave a note in the [discussions](https://github.com/hpehl/flow/discussions)!  

## API 

The entrypoint to the flow API is the interface `Flow<C extends FlowContext>`. It provides three static methods:

1. `<C extends FlowContext> Sequence<C> parallel(C context, List<Task<C>> tasks)`
2. `<C extends FlowContext> Sequence<C> sequential(C context, List<Task<C>> tasks)`
3. `<C extends FlowContext> While<C> while_(C context, Task<C> task, Predicate<C> until)`

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

The subscription is a terminal operation which supports one callback for successful and one for failed execution:

```java
List<Task<FloContext>> tasks = ...;
Flow.sequential(new FlowContext(), tasks)
        .subscribe(context -> console.log("Success!"),
                (context, failure) -> console.error("Failed: " + failure));
```

**Promise**

The result can be returned as `Promise<C>` to be returned to a calling method

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

### Parallel Execution

Parallel execution runs all tasks simultaneously and returns when all tasks have finished. In case of a failure you can specify to 

- fail fast: fail as soon as the first task fails or
- fail last: don't fail if one or more task fail and continue with the execution of the remaining tasks.

To execute a list of tasks in parallel, use something like this 

```java
List<Task<FloContext>> tasks = ...;
Flow.parallel(new FlowContext(), tasks)
        .failFast(true)
        .subscribe(context -> console.log("Success!"),
                (context, failure) -> console.error("Failed: " + failure));
```

### Sequential Execution

Sequential execution runs the tasks in order. That is the second task starts after the first finished. In case of a failure you can specify to

- fail fast: fail as soon as the first task fails or
- fail last: don't fail if one or more task fail and continue with the execution of the remaining tasks.  

To execute a list of tasks in order, use something like this

```java
List<Task<FloContext>> tasks = ...;
Flow.sequential(new FlowContext(), tasks)
        .failFast(true)
        .subscribe(context -> console.log("Success!"),
                (context, failure) -> console.error("Failed: " + failure));
```

### Repeated Execution

Repeated execution corresponds to a `while` loop and runs a task as long as a condition evaluates to `true`. For the repeated execution you can specify 

- whether to fail fast or fail last
- the interval between the iterations
- a timeout after the loop is canceled

To repeatedly execute a task, use something like this

```java
Task<FlowContext> task = context -> Promise.resolve(context.push(new Random().nextInt(10)));
Flow.while_(new FlowContext(), task, context -> context.<Integer>pop() == 3)
        .interval(600)
        .timeout(3_000)
        .subscribe(context -> console.log("Got a three!"),
                (context, failure) -> console.log("No luck!"));
```

### Nested Execution

The flow API makes it easy to nest task executions. You could for instance run five tasks in parallel, then execute three tasks in order and finally execute a task until a condition is met. To do so, the API provides different task implementation:

- `ParallelTasks<C>`
- `SequentialTasks<C>`
- `WhileTask<C>`

Here's an example how to use nested tasks:

```java
List<Task<FlowContext>> parallelTasks = ...;
List<Task<FlowContext>> sequentialTasks = ...;
Task task = ...;
Predicate<FloContext> condition = ...;

Flow.sequential(new FlowContext(), Arrays.asList(
        new ParallelTasks<>(parallelTasks),
        new SequentialTasks<>(sequentialTasks),
        new WhileTask<>(task, condition)))
        .subscribe(context -> console.log("Success!"),
                (context, failure) -> console.error("Failed: " + failure));
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
