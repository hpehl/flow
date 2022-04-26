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
import static org.jboss.hal.flow.Repeat.DEFAULT_ITERATIONS;

class Tasks {

    private static final double FAILURE_PERCENTAGE = 0.08;
    static final String GOOD_TIME = "0";
    static final int INTERVAL = 1_000;
    static final int SEQUENCE_TIMEOUT = 4_000;
    static final int REPEAT_TIMEOUT = 8_000;

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
        Flow.parallel(context(), tasks())
                .failFast(failFast)
                .timeout(SEQUENCE_TIMEOUT)
                .subscribe(context -> logger.finish(context.status()));
    }

    void sequential() {
        Flow.sequential(context(), tasks())
                .failFast(failFast)
                .timeout(SEQUENCE_TIMEOUT)
                .subscribe(context -> logger.finish(context.status()));
    }

    void repeat() {
        Flow.repeat(context(), currentTime())
                .while_(wrongTime())
                .failFast(failFast)
                .interval(INTERVAL)
                .timeout(REPEAT_TIMEOUT)
                .subscribe(context -> logger.finish(context.status()));
    }

    void nested() {
        Flow.sequential(context(), nestedTasks())
                .failFast(failFast)
                .timeout(SEQUENCE_TIMEOUT + REPEAT_TIMEOUT)
                .subscribe(context -> logger.finish(context.status()));
    }

    // ------------------------------------------------------ factory methods

    private List<Task<FlowContext>> nestedTasks() {
        return asList(
                new ParallelTasks<>(tasks(), failFast),
                new SequentialTasks<>(tasks(), failFast),
                new RepeatTask<>(currentTime(), wrongTime(), failFast, INTERVAL, REPEAT_TIMEOUT, DEFAULT_ITERATIONS)
        );
    }

    private List<Task<FlowContext>> tasks() {
        return asList(
                currentTime(),
                delay(),
                currentTime(),
                delay(),
                currentTime(),
                delay(),
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

    private Task<FlowContext> delay() {
        long random = 500L + new Random().nextInt(2_001);
        final long milliseconds = random - random % 500L;
        return context -> new Promise<>(
                (resolve, reject) -> {
                    String uniqueId = Id.unique();
                    logger.logStart(uniqueId, "Wait " + milliseconds + " ms...");
                    setTimeout(__ -> {
                        if (blowUp()) {
                            logger.logFailure(uniqueId, "Failed");
                            reject.onInvoke("Random failure");
                        } else {
                            logger.logEnd(uniqueId, "Done");
                            resolve.onInvoke(context);
                        }
                    }, milliseconds);
                });
    }

    private Task<FlowContext> currentTime() {
        return context -> {
            String uniqueId = Id.unique();
            logger.logStart(uniqueId, "Fetch time...");
            return fetchTime().then(time -> {
                context.push(time);
                if (blowUp()) {
                    logger.logFailure(uniqueId, "Failed");
                    return context.reject("Random failure");
                } else {
                    logger.logEnd(uniqueId, time);
                    return context.resolve();
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
