package org.jboss.hal.flow;

import org.jboss.elemento.Id;
import org.jboss.elemento.IsElement;

import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;

import static org.jboss.elemento.Elements.input;
import static org.jboss.elemento.Elements.label;
import static org.jboss.elemento.Elements.span;
import static org.jboss.elemento.EventType.click;
import static org.jboss.elemento.InputType.checkbox;

class SwitchElement implements IsElement<HTMLLabelElement> {

    private final HTMLLabelElement root;
    private final HTMLInputElement input;

    SwitchElement(boolean value, String label) {
        this(value, label, label);
    }

    SwitchElement(boolean value, String onMessage, String offMessage) {
        String id = Id.unique("switch");
        String inputId = Id.unique("switch-input");
        String onId = Id.unique("switch-on");
        String offId = Id.unique("switch-off");

        this.input = input(checkbox).css("pf-c-switch__input")
                .id(inputId)
                .name(inputId)
                .checked(value)
                .element();
        this.root = label().css("pf-c-switch")
                .attr("for", id)
                .on(click, event -> input.checked = !input.checked)
                .add(input)
                .add(span().css("pf-c-switch__toggle"))
                .add(span().css("pf-c-switch__label", "pf-m-on").id(onId).textContent(onMessage))
                .add(span().css("pf-c-switch__label", "pf-m-off").id(offId).textContent(offMessage))
                .element();
    }

    @Override
    public HTMLLabelElement element() {
        return root;
    }

    boolean value() {
        return input.checked;
    }

    void disable(boolean disabled) {
        input.disabled = disabled;
    }
}
