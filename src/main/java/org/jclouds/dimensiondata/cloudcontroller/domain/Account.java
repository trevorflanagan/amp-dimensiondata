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

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue public abstract class Account {
   public abstract String userName();

   @Nullable public abstract String password();

   public abstract String fullName();

   public abstract String firstName();

   public abstract String lastName();

   public abstract String emailAddress();

   @Nullable public abstract String department();

   @Nullable public abstract String customDefined1();

   @Nullable public abstract String customDefined2();

   public abstract String orgId();

   @Nullable public abstract Roles roles();

   Account() {
   }

   public static Account.Builder builder() {
      return new AutoValue_Account.Builder();
   }

   @SerializedNames({ "userName", "password", "fullName", "firstName", "lastName", "emailAddress", "department",
         "customDefined1", "customDefined2", "orgId", "roles" }) public static Account create(String userName,
         String password, String fullName, String firstName, String lastName, String emailAddress, String department,
         String customDefined1, String customDefined2, String orgId, Roles roles) {
      return builder().userName(userName).password(password).fullName(fullName).firstName(firstName).lastName(lastName)
            .emailAddress(emailAddress).department(department).customDefined1(customDefined1)
            .customDefined2(customDefined2).orgId(orgId).roles(roles).build();
   }

   public abstract Account.Builder toBuilder();

   @AutoValue.Builder public abstract static class Builder {

      public abstract Account.Builder userName(String userName);

      public abstract Account.Builder password(String password);

      public abstract Account.Builder fullName(String fullName);

      public abstract Account.Builder firstName(String firstName);

      public abstract Account.Builder lastName(String lastName);

      public abstract Account.Builder emailAddress(String emailAddress);

      public abstract Account.Builder department(String department);

      public abstract Account.Builder customDefined1(String customDefined1);

      public abstract Account.Builder customDefined2(String customDefined2);

      public abstract Account.Builder orgId(String orgId);

      public abstract Account.Builder roles(Roles roles);

      public abstract Account build();
   }

   //@XmlAccessorType(XmlAccessType.FIELD)
   //@XmlType(name = "Account", namespace = "http://oec.api.opsource.net/schemas/directory", propOrder = {
   //        "orgId"
   //
   //})
   //public class Account
   //{
   //
   //
   ///*
   //    "userName",
   //    "password",
   //    "fullName",
   //    "firstName",
   //    "lastName",
   //    "emailAddress",
   //    "department",
   //    "customDefined1",
   //    "customDefined2",*/
   ////,
   ////    "roles"
   //
   //    //    @XmlElement(required = true)
   //    protected String userName;
   //    protected String password;
   //    //    @XmlElement(required = true)
   //    protected String fullName;
   //    //    @XmlElement(required = true)
   //    protected String firstName;
   //    //    @XmlElement(required = true)
   //    protected String lastName;
   //    //    @XmlElement(required = true)
   //    protected String emailAddress;
   //    protected String department;
   //    protected String customDefined1;
   //    protected String customDefined2;
   //    //    @XmlElement(required = true)
   //    protected String orgId;
   //    protected Roles roles;
   //
   //    /**
   //     * Gets the value of the userName property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getUserName()
   //    {
   //        return userName;
   //    }
   //
   //    /**
   //     * Sets the value of the userName property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setUserName(String value)
   //    {
   //        this.userName = value;
   //    }
   //
   //    /**
   //     * Gets the value of the password property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getPassword()
   //    {
   //        return password;
   //    }
   //
   //    /**
   //     * Sets the value of the password property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setPassword(String value)
   //    {
   //        this.password = value;
   //    }
   //
   //    /**
   //     * Gets the value of the fullName property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getFullName()
   //    {
   //        return fullName;
   //    }
   //
   //    /**
   //     * Sets the value of the fullName property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setFullName(String value)
   //    {
   //        this.fullName = value;
   //    }
   //
   //    /**
   //     * Gets the value of the firstName property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getFirstName()
   //    {
   //        return firstName;
   //    }
   //
   //    /**
   //     * Sets the value of the firstName property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setFirstName(String value)
   //    {
   //        this.firstName = value;
   //    }
   //
   //    /**
   //     * Gets the value of the lastName property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getLastName()
   //    {
   //        return lastName;
   //    }
   //
   //    /**
   //     * Sets the value of the lastName property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setLastName(String value)
   //    {
   //        this.lastName = value;
   //    }
   //
   //    /**
   //     * Gets the value of the emailAddress property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getEmailAddress()
   //    {
   //        return emailAddress;
   //    }
   //
   //    /**
   //     * Sets the value of the emailAddress property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setEmailAddress(String value)
   //    {
   //        this.emailAddress = value;
   //    }
   //
   //    /**
   //     * Gets the value of the department property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getDepartment()
   //    {
   //        return department;
   //    }
   //
   //    /**
   //     * Sets the value of the department property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setDepartment(String value)
   //    {
   //        this.department = value;
   //    }
   //
   //    /**
   //     * Gets the value of the customDefined1 property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getCustomDefined1()
   //    {
   //        return customDefined1;
   //    }
   //
   //    /**
   //     * Sets the value of the customDefined1 property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setCustomDefined1(String value)
   //    {
   //        this.customDefined1 = value;
   //    }
   //
   //    /**
   //     * Gets the value of the customDefined2 property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getCustomDefined2()
   //    {
   //        return customDefined2;
   //    }
   //
   //    /**
   //     * Sets the value of the customDefined2 property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setCustomDefined2(String value)
   //    {
   //        this.customDefined2 = value;
   //    }
   //
   //    /**
   //     * Gets the value of the orgId property.
   //     *
   //     * @return possible object is
   //     * {@link String }
   //     */
   //    public String getOrgId()
   //    {
   //        return orgId;
   //    }
   //
   //    /**
   //     * Sets the value of the orgId property.
   //     *
   //     * @param value allowed object is
   //     *              {@link String }
   //     */
   //    public void setOrgId(String value)
   //    {
   //        this.orgId = value;
   //    }
   //
   //    /**
   //     * Gets the value of the roles property.
   //     *
   //     * @return possible object is
   //     * {@link Roles }
   //     */
   //    public Roles getRoles()
   //    {
   //        return roles;
   //    }
   //
   //    /**
   //     * Sets the value of the roles property.
   //     *
   //     * @param value allowed object is
   //     *              {@link Roles }
   //     */
   //    public void setRoles(Roles value)
   //    {
   //        this.roles = value;
   //    }

}
