/*
 * Copyright (c) 2008-2012, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.collection.operations;

import com.hazelcast.collection.CollectionProxyType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;

import java.io.IOException;
import java.util.List;

/**
 * @ali 1/17/13
 */
public class SetOperation extends CollectionBackupAwareOperation {

    int index;

    Data value;

    transient boolean shouldBackup;

    public SetOperation() {
    }

    public SetOperation(String name, CollectionProxyType proxyType, Data dataKey, int threadId, int index, Data value) {
        super(name, proxyType, dataKey, threadId);
        this.index = index;
        this.value = value;
    }

    public void run() throws Exception {
        Object obj = isBinary() ? value : toObject(value);
        List list = getCollection();
        if (list != null) {
            try {
                response = list.set(index, obj);
                shouldBackup = true;

            } catch (IndexOutOfBoundsException e) {
                response = e;
            }
        }
    }

    public Operation getBackupOperation() {
        return new SetBackupOperation(name, proxyType, dataKey, index, value);
    }

    public boolean shouldBackup() {
        return shouldBackup;
    }

    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(index);
        value.writeData(out);
    }

    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        index = in.readInt();
        value = new Data();
        value.readData(in);
    }
}
