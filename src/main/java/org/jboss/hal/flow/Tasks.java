package org.jboss.hal.flow;

import java.util.Arrays;
import java.util.List;

import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

import static elemental2.dom.DomGlobal.fetch;
import static elemental2.dom.DomGlobal.setTimeout;

class Tasks {

    private static final double FAILURE_PERCENTAGE = 0.08;

    private final Progress progress;
    private final boolean randomFailure;
    private final boolean failFast;
    private final Logger logger;

    Tasks(final Progress progress, final Logger logger, final boolean randomFailure, final boolean failFast) {
        this.progress = progress;
        this.randomFailure = randomFailure;
        this.failFast = failFast;
        this.logger = logger;
    }

    void parallel() {
        ParallelFlow<FlowContext> flow = Flow.parallel(new FlowContext(progress), tasks());
        if (!failFast) {
            flow = flow.onErrorContinue();
        }
        flow.subscribe(context -> logger.markSuccessful(), failure -> logger.markFailed());
    }

    void sequential() {
        SequentialFlow<FlowContext> flow = Flow.series(new FlowContext(progress), tasks());
        if (!failFast) {
            flow = flow.onErrorResumeNext();
        }
        flow.subscribe(context -> logger.markSuccessful(), failure -> logger.markFailed());
    }

    private List<Task<FlowContext>> tasks() {
        return Arrays.asList(
                currentTime("0"),
                delay("1", 3000),
                currentTime("2"),
                delay("3", 2000),
                currentTime("4"),
                delay("5", 1000),
                currentTime("6")
        );
    }

    private Task<FlowContext> delay(String id, long milliseconds) {
        return context -> new Promise<>(
                (resolve, reject) -> {
                    logger.start(id, "Wait " + milliseconds + " ms...");
                    setTimeout(ignore -> {
                        if (blowUp()) {
                            logger.failure(id, "Failed");
                            reject.onInvoke("Random failure");
                        } else {
                            logger.end(id, "Done");
                            resolve.onInvoke(context);
                        }
                    }, milliseconds);
                });
    }

    private Task<FlowContext> currentTime(String id) {
        return context -> {
            logger.start(id, "Fetch time...");
            return fetchTime().then(time -> {
                if (blowUp()) {
                    logger.failure(id, "Failed");
                    throw new RuntimeException("Random failure");
                } else {
                    logger.end(id, time);
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
