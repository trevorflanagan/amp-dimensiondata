/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.dimensiondata.cloudcontroller.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FirewallRuleTarget {

    @AutoValue
    public abstract static class Port {

        Port() {} // For AutoValue only!

        public abstract Integer begin();
        @Nullable public abstract Integer end();

        @SerializedNames({ "begin", "end" })
        public static Port create(Integer begin, Integer end) {
            return new AutoValue_FirewallRuleTarget_Port(begin, end);
        }
    }

    public static Builder builder() {
        return new AutoValue_FirewallRuleTarget.Builder();
    }

    FirewallRuleTarget() {} // For AutoValue only!

    public abstract IpRange ip();
    @Nullable public abstract Port port();

    @SerializedNames({ "ip", "port" })
    public static FirewallRuleTarget create(IpRange ip, Port port) {
        return builder().ip(ip).port(port).build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder ip(IpRange ip);
        public abstract Builder port(Port port);

        public abstract FirewallRuleTarget build();
    }

}
