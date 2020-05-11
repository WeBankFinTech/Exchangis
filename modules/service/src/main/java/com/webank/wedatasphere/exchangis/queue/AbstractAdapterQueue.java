/*
 *
 *  Copyright 2020 WeBank
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.exchangis.queue;

import com.webank.wedatasphere.exchangis.queue.domain.QueueElement;

import java.util.*;

/**
 * @author davidhua
 * 2019/1/7
 */
public abstract class AbstractAdapterQueue implements Queue<QueueElement> {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<QueueElement> iterator() {
        return Collections.singletonList(new QueueElement()).iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[]{};
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException("Don't supported this method");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends QueueElement> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }
    //TODO lock queue
}
