/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.flow;

import java.util.List;
import java.util.function.Predicate;

import static org.jboss.hal.flow.FlowSequence.Mode.PARALLEL;
import static org.jboss.hal.flow.FlowSequence.Mode.SEQUENTIAL;

public interface Flow<C extends FlowContext> {

    // ------------------------------------------------------ factory methods

    static <C extends FlowContext> Sequence<C> parallel(C context, List<Task<C>> tasks) {
        return new FlowSequence<>(PARALLEL, context, tasks);
    }

    static <C extends FlowContext> Sequence<C> series(C context, List<Task<C>> tasks) {
        return new FlowSequence<>(SEQUENTIAL, context, tasks);
    }

    static <C extends FlowContext> Repeat<C> repeat(C context, Task<C> task, Predicate<C> until) {
        return new FlowRepeat<>(context, task, until);
    }
}
