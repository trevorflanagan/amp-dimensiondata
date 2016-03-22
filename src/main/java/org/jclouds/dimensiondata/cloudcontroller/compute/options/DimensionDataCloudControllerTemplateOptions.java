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
import java.util.Objects;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;

public class DimensionDataCloudControllerTemplateOptions extends TemplateOptions implements Cloneable {
    protected String networkDomainId;
    protected String vlanId;
    protected String networkDomainName;
    protected String vlanName;
    protected String defaultPrivateIPv4BaseAddress;
    protected Integer defaultPrivateIPv4PrefixSize;

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
            eTo.networkDomainName(networkDomainName);
            eTo.vlanName(vlanName);
            eTo.defaultPrivateIPv4BaseAddress(defaultPrivateIPv4BaseAddress);
            eTo.defaultPrivateIPv4PrefixSize(defaultPrivateIPv4PrefixSize);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DimensionDataCloudControllerTemplateOptions)) return false;
        if (!super.equals(o)) return false;
        DimensionDataCloudControllerTemplateOptions that = (DimensionDataCloudControllerTemplateOptions) o;
        return Objects.equals(networkDomainId, that.networkDomainId) &&
                Objects.equals(vlanId, that.vlanId) &&
                Objects.equals(networkDomainName, that.networkDomainName) &&
                Objects.equals(vlanName, that.vlanName) &&
                Objects.equals(defaultPrivateIPv4BaseAddress, that.defaultPrivateIPv4BaseAddress) &&
                Objects.equals(defaultPrivateIPv4PrefixSize, that.defaultPrivateIPv4PrefixSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), networkDomainId, vlanId, networkDomainName, vlanName, defaultPrivateIPv4BaseAddress, defaultPrivateIPv4PrefixSize);
    }

    @Override
    public String toString() {
        return "DimensionDataCloudControllerTemplateOptions{" +
                "networkDomainId='" + networkDomainId + '\'' +
                ", vlanId='" + vlanId + '\'' +
                ", networkDomainName='" + networkDomainName + '\'' +
                ", vlanName='" + vlanName + '\'' +
                ", defaultPrivateIPv4BaseAddress='" + defaultPrivateIPv4BaseAddress + '\'' +
                ", defaultPrivateIPv4PrefixSize='" + defaultPrivateIPv4PrefixSize + '\'' +
                '}';
    }

    public String getNetworkDomainId() {
        return networkDomainId;
    }

    public String getVlanId() {
        return vlanId;
    }

    public String getNetworkDomainName() {
        return networkDomainName;
    }

    public String getVlanName() {
        return vlanName;
    }

    public String getDefaultPrivateIPv4BaseAddress() {
        return defaultPrivateIPv4BaseAddress;
    }

    public Integer getDefaultPrivateIPv4PrefixSize() {
        return defaultPrivateIPv4PrefixSize;
    }

    public DimensionDataCloudControllerTemplateOptions networkDomainId(@Nullable String networkDomainId) {
        this.networkDomainId = networkDomainId;
        return this;
    }

    public DimensionDataCloudControllerTemplateOptions vlanId(@Nullable String vlanId) {
        this.vlanId = vlanId;
        return this;
    }

    public DimensionDataCloudControllerTemplateOptions networkDomainName(@Nullable String networkDomainName) {
        this.networkDomainName = networkDomainName;
        return this;
    }

    public DimensionDataCloudControllerTemplateOptions vlanName(@Nullable String vlanName) {
        this.vlanName = vlanName;
        return this;
    }

    public DimensionDataCloudControllerTemplateOptions defaultPrivateIPv4BaseAddress(@Nullable String defaultPrivateIPv4BaseAddress) {
        this.defaultPrivateIPv4BaseAddress = defaultPrivateIPv4BaseAddress;
        return this;
    }

    public DimensionDataCloudControllerTemplateOptions defaultPrivateIPv4PrefixSize(@Nullable Integer defaultPrivateIPv4PrefixSize) {
        this.defaultPrivateIPv4PrefixSize = defaultPrivateIPv4PrefixSize;
        return this;
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
         * @see #vlanId
         */
        public static DimensionDataCloudControllerTemplateOptions vlanId(final String vlanId) {
            final DimensionDataCloudControllerTemplateOptions options = new DimensionDataCloudControllerTemplateOptions();
            return options.vlanId(vlanId);
        }
        /**
         * @see #networkDomainName
         */
        public static DimensionDataCloudControllerTemplateOptions networkDomainName(final String networkDomainName) {
            final DimensionDataCloudControllerTemplateOptions options = new DimensionDataCloudControllerTemplateOptions();
            return options.networkDomainName(networkDomainName);
        }
        /**
         * @see #vlanName
         */
        public static DimensionDataCloudControllerTemplateOptions vlanName(final String vlanName) {
            final DimensionDataCloudControllerTemplateOptions options = new DimensionDataCloudControllerTemplateOptions();
            return options.vlanName(vlanName);
        }
        /**
         * @see #defaultPrivateIPv4BaseAddress
         */
        public static DimensionDataCloudControllerTemplateOptions defaultPrivateIPv4BaseAddress(final String defaultPrivateIPv4BaseAddress) {
            final DimensionDataCloudControllerTemplateOptions options = new DimensionDataCloudControllerTemplateOptions();
            return options.defaultPrivateIPv4BaseAddress(defaultPrivateIPv4BaseAddress);
        }
        /**
         * @see #defaultPrivateIPv4PrefixSize
         */
        public static DimensionDataCloudControllerTemplateOptions defaultPrivateIPv4PrefixSize(final Integer defaultPrivateIPv4PrefixSize) {
            final DimensionDataCloudControllerTemplateOptions options = new DimensionDataCloudControllerTemplateOptions();
            return options.defaultPrivateIPv4PrefixSize(defaultPrivateIPv4PrefixSize);
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
