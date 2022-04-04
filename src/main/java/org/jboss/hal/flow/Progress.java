package org.jboss.hal.flow;

/** Interface to reflect progress of a flow. */
public interface Progress {

    void reset();

    default void reset(int max) {
        reset(max, null);
    }

    void reset(int max, String label);

    default void tick() {
        tick(null);
    }

    void tick(String label);

    void finish();

    Progress NOOP = new Progress() {

        @Override
        public void reset() {
        }

        @Override
        public void reset(int max, String label) {
        }

        @Override
        public void tick(String label) {
        }

        @Override
        public void finish() {
        }
    };
}
