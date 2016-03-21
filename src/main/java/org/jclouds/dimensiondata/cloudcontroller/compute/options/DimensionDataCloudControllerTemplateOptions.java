/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.dimensiondata.cloudcontroller.compute.options;

import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;

public class DimensionDataCloudControllerTemplateOptions extends TemplateOptions implements Cloneable {
    protected String networkDomainId;
    protected String vlanId;

    @Override
    public DimensionDataCloudControllerTemplateOptions clone() {
        final DimensionDataCloudControllerTemplateOptions options = new DimensionDataCloudControllerTemplateOptions();
        copyTo(options);
        return options;
    }

    @Override
    public void copyTo(final TemplateOptions to) {
        super.copyTo(to);
        if (to instanceof DimensionDataCloudControllerTemplateOptions) {
            final DimensionDataCloudControllerTemplateOptions eTo = DimensionDataCloudControllerTemplateOptions.class.cast(to);
            eTo.networkDomainId(networkDomainId);
            eTo.vlanId(vlanId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DimensionDataCloudControllerTemplateOptions)) return false;
        if (!super.equals(o)) return false;

        DimensionDataCloudControllerTemplateOptions that = (DimensionDataCloudControllerTemplateOptions) o;

        if (networkDomainId != null ? !networkDomainId.equals(that.networkDomainId) : that.networkDomainId != null) return false;
        if (vlanId != null ? !vlanId.equals(that.vlanId) : that.vlanId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (networkDomainId != null ? networkDomainId.hashCode() : 0);
        result = 31 * result + (vlanId != null ? vlanId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("networkDomainId", networkDomainId)
                .add("vlanId", vlanId)
                .toString();
    }

    public DimensionDataCloudControllerTemplateOptions networkDomainId(@Nullable String networkDomainId) {
        this.networkDomainId = networkDomainId;
        return this;
    }

    public DimensionDataCloudControllerTemplateOptions vlanId(@Nullable String vlanId) {
        this.vlanId = vlanId;
        return this;
    }

    public String getNetworkDomainId() {
        return networkDomainId;
    }

    public String getVlanId() {
        return vlanId;
    }

    public static class Builder {

        /**
         * @see #networkDomainId
         */
        public static DimensionDataCloudControllerTemplateOptions networkDomainId(final String networkDomainName) {
            final DimensionDataCloudControllerTemplateOptions options = new DimensionDataCloudControllerTemplateOptions();
            return options.networkDomainId(networkDomainName);
        }
        /**
         * @see #networkDomainId
         */
        public static DimensionDataCloudControllerTemplateOptions vlanId(final String vlanId) {
            final DimensionDataCloudControllerTemplateOptions options = new DimensionDataCloudControllerTemplateOptions();
            return options.vlanId(vlanId);
        }
    }

    // methods that only facilitate returning the correct object type

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions blockOnPort(int port, int seconds) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.blockOnPort(port, seconds));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions inboundPorts(int... ports) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.inboundPorts(ports));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions authorizePublicKey(String publicKey) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions installPrivateKey(String privateKey) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.installPrivateKey(privateKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions dontAuthorizePublicKey() {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.dontAuthorizePublicKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions nameTask(String name) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.nameTask(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions runAsRoot(boolean runAsRoot) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions runScript(Statement script) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.runScript(script));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions overrideLoginPassword(String password) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.overrideLoginPassword(password));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions overrideLoginPrivateKey(String privateKey) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions overrideLoginUser(String loginUser) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions userMetadata(Map<String, String> userMetadata) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.userMetadata(userMetadata));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions userMetadata(String key, String value) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.userMetadata(key, value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions nodeNames(Iterable<String> nodeNames) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.nodeNames(nodeNames));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DimensionDataCloudControllerTemplateOptions networks(Iterable<String> networks) {
        return DimensionDataCloudControllerTemplateOptions.class.cast(super.networks(networks));
    }
}
