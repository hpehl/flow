package org.jboss.hal.flow;

import org.jboss.elemento.IsElement;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;

import static java.lang.Math.min;
import static java.lang.Math.round;
import static org.jboss.elemento.Elements.div;

class ProgressElement implements Progress, IsElement<HTMLDivElement> {

    private final HTMLDivElement root;
    private final HTMLElement indicator;
    private int value;
    private int total;

    ProgressElement() {
        root = div().css("pf-c-progress", "pf-m-sm")
                .add(div().css("pf-c-progress__bar")
                        .attr("role", "progressbar")
                        .add(indicator = div().css("pf-c-progress__indicator").element()))
                .element();
    }

    @Override
    public HTMLDivElement element() {
        return root;
    }

    @Override
    public void reset() {
        reset(0);
    }

    @Override
    public void reset(final int max, final String label) {
        value = 0;
        total = max;
        div(indicator).style("width:0");
    }

    @Override
    public void tick(final String label) {
        if (value < total) {
            value++;
            double percent = min(round(((double) value / (double) total) * 100.0), 100.0);
            div(indicator).style("width:" + percent + "%");
        }
    }

    @Override
    public void finish() {
        // div(root).css("pf-m-success");
        div(indicator).style("width:100%");
    }
}
