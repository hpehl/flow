package org.jboss.hal.flow;

import com.google.gwt.core.client.EntryPoint;

import elemental2.dom.HTMLElement;

import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.button;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.h;
import static org.jboss.elemento.Elements.li;
import static org.jboss.elemento.Elements.main;
import static org.jboss.elemento.Elements.p;
import static org.jboss.elemento.Elements.removeChildrenFrom;
import static org.jboss.elemento.Elements.section;
import static org.jboss.elemento.Elements.span;
import static org.jboss.elemento.Elements.ul;
import static org.jboss.elemento.EventType.bind;
import static org.jboss.elemento.EventType.click;

public class Main implements EntryPoint {


    private static final String DESCRIPTION = "This page tests the parallel and sequential execution of asynchronous " +
            "tasks. Depending on which button you press, seven tasks are executed in parallel, or one after the other:";
    private static final String FETCH_TIME = "Four tasks fetch the current time from worldtimeapi.org.";
    private static final String WAIT = "Three tasks wait for a given time (3, 2 and 1 seconds).";

    HTMLElement tasksContainer;
    SwitchElement randomFailure;
    SwitchElement failFast;

    @Override
    public void onModuleLoad() {
        randomFailure = new SwitchElement(true, "Produce random failures");
        failFast = new SwitchElement(true, "Fail fast");
        bind(randomFailure, click, event -> failFast.disable(!randomFailure.value()));

        body()
                .add(div().css("pf-c-page")
                        .add(main().css("pf-c-page__main")
                                .add(section().css("pf-c-page__main-section", "pf-m-limit-width", "pf-m-light")
                                        .add(div().css("pf-c-page__main-body")
                                                .add(div().css("pf-c-content")
                                                        .add(h(1, "Flow"))
                                                        .add(p().textContent(DESCRIPTION))
                                                        .add(ul()
                                                                .add(li().textContent(FETCH_TIME))
                                                                .add(li().textContent(WAIT))))))
                                .add(section().css("pf-c-page__main-section", "pf-m-limit-width", "pf-m-light")
                                        .add(div().css("pf-c-page__main-body")
                                                .add(span().css("pf-l-flex", "pf-m-column", "pf-u-mb-md")
                                                        .add(randomFailure)
                                                        .add(failFast))
                                                .add(button().css("pf-c-button", "pf-m-primary", "pf-u-mr-sm")
                                                        .textContent("Parallel")
                                                        .on(click, e -> parallel()))
                                                .add(button().css("pf-c-button", "pf-m-primary", "pf-u-mr-sm")
                                                        .textContent("Sequential")
                                                        .on(click, e -> sequential()))
                                                .add(button().css("pf-c-button", "pf-m-secondary")
                                                        .textContent("Clear")
                                                        .on(click, e -> clear()))))
                                .add(section().css("pf-c-page__main-section", "pf-m-limit-width")
                                        .add(div().css("pf-c-page__main-body")
                                                .add(tasksContainer = div().css("pf-l-gallery", "pf-m-gutter")
                                                        .element())))));
    }

    void parallel() {
        String title = "Parallel" + (failFast.value() ? " (fail fast)" : " (fail last)");
        TasksElement tasksElement = new TasksElement(title, randomFailure.value(), failFast.value());
        div(tasksContainer).add(tasksElement);
        tasksElement.parallel();
    }

    void sequential() {
        String title = "Sequential" + (failFast.value() ? " (fail fast)" : " (fail last)");
        TasksElement tasksElement = new TasksElement(title, randomFailure.value(), failFast.value());
        div(tasksContainer).add(tasksElement);
        tasksElement.sequential();
    }

    void clear() {
        removeChildrenFrom(tasksContainer);
    }
}
