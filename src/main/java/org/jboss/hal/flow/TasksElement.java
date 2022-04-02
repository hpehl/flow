package org.jboss.hal.flow;

import java.util.HashMap;
import java.util.Map;

import org.jboss.elemento.IsElement;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLPreElement;

import static org.jboss.elemento.Elements.article;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.pre;

class TasksElement implements IsElement<HTMLElement>, Logger {

    private final boolean randomFailure;
    private final boolean failFast;
    private final Map<String, HTMLElement> lines;
    private final HTMLElement root;
    private final ProgressElement progressElement;
    private final HTMLElement body;

    TasksElement(final String mode, final boolean randomFailure, final boolean failFast) {
        this.randomFailure = randomFailure;
        this.failFast = failFast;
        this.lines = new HashMap<>();
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

    // ------------------------------------------------------ logging

    @Override
    public void start(final String id, final String message) {
        HTMLPreElement line;
        div(body).add(line = pre()
                .css("pf-u-font-size-sm", "pf-u-color-300", "pf-u-text-truncate")
                .textContent(message)
                .element());
        lines.put(id, line);
    }

    @Override
    public void end(final String id, final String message) {
        HTMLElement line = lines.get(id);
        if (line != null) {
            pre(line).textContent(line.textContent + message);
        }
    }

    @Override
    public void failure(final String id, final String message) {
        HTMLElement line = lines.get(id);
        if (line != null) {
            line.classList.remove("pf-u-color-300");
            pre(line).css("pf-u-danger-color-200").textContent(line.textContent + message);
        }
    }

    @Override
    public void markSuccessful() {
        article(root).css("fl-status__done");
    }

    @Override
    public void markFailed() {
        article(root).css("fl-status__error");
    }
}
