package org.jboss.hal.flow;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import org.jboss.elemento.Id;

import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.setTimeout;
import static java.util.Arrays.asList;

class Tasks {

    private static final double FAILURE_PERCENTAGE = 0.08;
    static final String GOOD_TIME = "0";
    static final int INTERVAL = 1000;
    static final int TIMEOUT = 8000;

    private final Progress progress;
    private final Logger logger;
    private final boolean randomFailure;
    private final boolean failFast;

    Tasks(final Progress progress, final Logger logger, final boolean randomFailure, final boolean failFast) {
        this.progress = progress;
        this.logger = logger;
        this.randomFailure = randomFailure;
        this.failFast = failFast;
    }

    // ------------------------------------------------------ task executions

    void parallel() {
        Sequence<FlowContext> sequence = Flow.parallel(context(), tasks())
                .failFast(failFast);
        if (new Random().nextBoolean()) {
            sequence.subscribe(__ -> logger.markSuccessful(), (__, ___) -> logger.markFailed());
        } else {
            sequence.then(__ -> logger.markSuccessful()).catch_(__ -> logger.markFailed());

        }
    }

    void sequential() {
        Sequence<FlowContext> sequence = Flow.series(context(), tasks())
                .failFast(failFast);
        if (new Random().nextBoolean()) {
            sequence.subscribe(__ -> logger.markSuccessful(), (__, ___) -> logger.markFailed());
        } else {
            sequence.then(__ -> logger.markSuccessful()).catch_(__ -> logger.markFailed());
        }
    }

    void while_() {
        While<FlowContext> while_ = Flow.while_(context(), currentTime(), wrongTime())
                .failFast(failFast)
                .interval(INTERVAL)
                .timeout(TIMEOUT);
        if (new Random().nextBoolean()) {
            while_.subscribe(__ -> logger.markSuccessful(), (__, ___) -> logger.markFailed());
        } else {
            while_.then(__ -> logger.markSuccessful()).catch_(__ -> logger.markFailed());
        }
    }

    void nested() {
        Sequence<FlowContext> sequence = Flow.series(context(), nestedTasks())
                .failFast(failFast);
        if (new Random().nextBoolean()) {
            sequence.subscribe(__ -> logger.markSuccessful(), (__, ___) -> logger.markFailed());
        } else {
            sequence.then(__ -> logger.markSuccessful()).catch_(__ -> logger.markFailed());
        }
    }

    // ------------------------------------------------------ factory methods

    private List<Task<FlowContext>> nestedTasks() {
        return asList(
                new ParallelTasks<>(tasks(), failFast),
                new SequentialTasks<>(tasks(), failFast),
                new WhileTask<>(currentTime(), wrongTime(), failFast, INTERVAL, TIMEOUT)
        );
    }

    private List<Task<FlowContext>> tasks() {
        return asList(
                currentTime(),
                delay(3000),
                currentTime(),
                delay(2000),
                currentTime(),
                delay(1000),
                currentTime()
        );
    }

    private FlowContext context() {
        return new FlowContext(progress);
    }

    private Predicate<FlowContext> wrongTime() {
        return context -> !context.pop("").endsWith(GOOD_TIME);
    }

    // ------------------------------------------------------ task implementations

    private Task<FlowContext> delay(long milliseconds) {
        return context -> new Promise<>(
                (resolve, reject) -> {
                    String uniqueId = Id.unique();
                    logger.start(uniqueId, "Wait " + milliseconds + " ms...");
                    setTimeout(ignore -> {
                        if (blowUp()) {
                            logger.failure(uniqueId, "Failed");
                            reject.onInvoke("Random failure");
                        } else {
                            logger.end(uniqueId, "Done");
                            resolve.onInvoke(context);
                        }
                    }, milliseconds);
                });
    }

    private Task<FlowContext> currentTime() {
        return context -> {
            String uniqueId = Id.unique();
            logger.start(uniqueId, "Fetch time...");
            return fetchTime().then(time -> {
                context.push(time);
                if (blowUp()) {
                    logger.failure(uniqueId, "Failed");
                    throw new RuntimeException("Random failure");
                } else {
                    logger.end(uniqueId, time);
                    return Promise.resolve(context);
                }
            });
        };
    }

    private boolean blowUp() {
        return randomFailure && Math.random() < FAILURE_PERCENTAGE;
    }

    private Promise<String> fetchTime() {
        return fetch("http://worldtimeapi.org/api/timezone/Europe/Berlin")
                .then(Response::json)
                .then(json -> Promise.resolve(Js.<Now>cast(json).time()));
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static class Now {

        String datetime;

        @JsOverlay
        final String time() {
            // datetime format is "2022-03-31T11:03:39.348365+02:00"
            return datetime.substring(11, 23);
        }
    }
}
