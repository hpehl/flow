package org.jboss.hal.flow;

import org.jboss.elemento.IsElement;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;

import static elemental2.dom.DomGlobal.document;
import static org.jboss.elemento.Elements.article;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.pre;

class TasksElement implements IsElement<HTMLElement>, Logger {

    private final boolean randomFailure;
    private final boolean failFast;
    private final HTMLElement root;
    private final ProgressElement progressElement;
    private final HTMLElement body;

    TasksElement(final String mode, final boolean randomFailure, final boolean failFast) {
        this.randomFailure = randomFailure;
        this.failFast = failFast;
        this.root = article()
                .css("pf-c-card", "pf-u-min-width", "pf-u-min-height")
                .style("--pf-u-min-width--MinWidth: 250px;--pf-u-min-height--MinHeight: 285px")
                .add(div().css("pf-c-card__title").textContent(mode))
                .add(div().css("pf-c-card__body", "pf-m-no-fill")
                        .add(progressElement = new ProgressElement()))
                .add(body = div().css("pf-c-card__body").element())
                .element();
    }

    @Override
    public HTMLElement element() {
        return root;
    }

    void parallel() {
        new Tasks(progressElement, this, randomFailure, failFast).parallel();
    }

    void sequential() {
        new Tasks(progressElement, this, randomFailure, failFast).sequential();
    }

    void while_() {
        new Tasks(progressElement, this, randomFailure, failFast).while_();
    }

    void nested() {
        new Tasks(progressElement, this, randomFailure, failFast).nested();
    }

    // ------------------------------------------------------ logging

    @Override
    public void start(final String id, final String message) {
        div(body).add(pre().id(id)
                .css("pf-u-font-size-sm", "pf-u-color-300", "pf-u-text-truncate")
                .textContent(message));
    }

    @Override
    public void end(final String id, final String message) {
        Element line = document.getElementById(id);
        if (line != null) {
            pre(line).textContent(line.textContent + message);
        }
    }

    @Override
    public void failure(final String id, final String message) {
        Element line = document.getElementById(id);
        if (line != null) {
            line.classList.remove("pf-u-color-300");
            pre(line).css("pf-u-danger-color-200").textContent(line.textContent + message);
        }
    }

    @Override
    public Promise<Void> markSuccessful() {
        article(root).css("fl-status__done");
        return Promise.resolve((Void) null);
    }

    @Override
    public Promise<Void> markFailed() {
        article(root).css("fl-status__error");
        return Promise.resolve((Void) null);
    }
}
