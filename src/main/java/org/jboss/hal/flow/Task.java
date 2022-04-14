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

import elemental2.promise.Promise;

/**
 * Interface for the execution of an asynchronous task.
 *
 * @param <C> the type of the {@linkplain FlowContext context} shared between tasks
 */
@FunctionalInterface
public interface Task<C extends FlowContext> {

    /**
     * Executes the task.
     *
     * @param context the context shared between tasks
     * @return a promise containing the shared context
     */
    Promise<C> apply(C context);
}
