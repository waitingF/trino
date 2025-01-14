/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.plugin.password.ldap;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;
import io.airlift.configuration.ConfigSecuritySensitive;
import io.airlift.configuration.DefunctConfig;
import io.airlift.configuration.validation.FileExists;
import io.airlift.units.Duration;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Objects.requireNonNull;

@DefunctConfig("ldap.ssl-trust-certificate")
public class LdapConfig
{
    private String ldapUrl;
    private boolean allowInsecure;
    private File keystorePath;
    private String keystorePassword;
    private File trustStorePath;
    private String truststorePassword;
    private List<String> userBindSearchPatterns = ImmutableList.of();
    private String groupAuthorizationSearchPattern;
    private String userBaseDistinguishedName;
    private String bindDistinguishedName;
    private String bindPassword;
    private boolean ignoreReferrals;
    private Duration ldapCacheTtl = new Duration(1, TimeUnit.HOURS);
    private Optional<Duration> ldapConnectionTimeout = Optional.empty();
    private Optional<Duration> ldapReadTimeout = Optional.empty();

    @NotNull
    @Pattern(regexp = "^ldaps?://.*", message = "Invalid LDAP server URL. Expected ldap:// or ldaps://")
    public String getLdapUrl()
    {
        return ldapUrl;
    }

    @Config("ldap.url")
    @ConfigDescription("URL of the LDAP server")
    public LdapConfig setLdapUrl(String url)
    {
        this.ldapUrl = url;
        return this;
    }

    public boolean isAllowInsecure()
    {
        return allowInsecure;
    }

    @Config("ldap.allow-insecure")
    @ConfigDescription("Allow insecure connection to the LDAP server")
    public LdapConfig setAllowInsecure(boolean allowInsecure)
    {
        this.allowInsecure = allowInsecure;
        return this;
    }

    @AssertTrue(message = "Connecting to the LDAP server without SSL enabled requires `ldap.allow-insecure=true`")
    public boolean isUrlConfigurationValid()
    {
        return nullToEmpty(ldapUrl).startsWith("ldaps://") || allowInsecure;
    }

    public Optional<@FileExists File> getKeystorePath()
    {
        return Optional.ofNullable(keystorePath);
    }

    @Config("ldap.ssl.keystore.path")
    @ConfigDescription("Path to the PEM or JKS key store")
    public LdapConfig setKeystorePath(File path)
    {
        this.keystorePath = path;
        return this;
    }

    public Optional<String> getKeystorePassword()
    {
        return Optional.ofNullable(keystorePassword);
    }

    @Config("ldap.ssl.keystore.password")
    @ConfigSecuritySensitive
    @ConfigDescription("Password for the key store")
    public LdapConfig setKeystorePassword(String password)
    {
        this.keystorePassword = password;
        return this;
    }

    public Optional<@FileExists File> getTrustStorePath()
    {
        return Optional.ofNullable(trustStorePath);
    }

    @Config("ldap.ssl.truststore.path")
    @ConfigDescription("Path to the PEM or JKS trust store")
    public LdapConfig setTrustStorePath(File path)
    {
        this.trustStorePath = path;
        return this;
    }

    public Optional<String> getTruststorePassword()
    {
        return Optional.ofNullable(truststorePassword);
    }

    @Config("ldap.ssl.truststore.password")
    @ConfigSecuritySensitive
    @ConfigDescription("Password for the trust store")
    public LdapConfig setTruststorePassword(String password)
    {
        this.truststorePassword = password;
        return this;
    }

    @NotNull
    public List<String> getUserBindSearchPatterns()
    {
        return userBindSearchPatterns;
    }

    public LdapConfig setUserBindSearchPatterns(List<String> userBindSearchPatterns)
    {
        this.userBindSearchPatterns = requireNonNull(userBindSearchPatterns, "userBindSearchPatterns is null");
        return this;
    }

    @Config("ldap.user-bind-pattern")
    @ConfigDescription("Custom user bind pattern. Example: ${USER}@example.com")
    public LdapConfig setUserBindSearchPatterns(String userBindSearchPatterns)
    {
        this.userBindSearchPatterns = Splitter.on(":")
                .trimResults()
                .omitEmptyStrings()
                .splitToList(userBindSearchPatterns);
        return this;
    }

    public String getGroupAuthorizationSearchPattern()
    {
        return groupAuthorizationSearchPattern;
    }

    @Config("ldap.group-auth-pattern")
    @ConfigDescription("Custom group authorization check query. Example: &(objectClass=user)(memberOf=cn=group)(user=username)")
    public LdapConfig setGroupAuthorizationSearchPattern(String groupAuthorizationSearchPattern)
    {
        this.groupAuthorizationSearchPattern = groupAuthorizationSearchPattern;
        return this;
    }

    public String getUserBaseDistinguishedName()
    {
        return userBaseDistinguishedName;
    }

    @Config("ldap.user-base-dn")
    @ConfigDescription("Base distinguished name of the user. Example: dc=example,dc=com")
    public LdapConfig setUserBaseDistinguishedName(String userBaseDistinguishedName)
    {
        this.userBaseDistinguishedName = userBaseDistinguishedName;
        return this;
    }

    public String getBindDistingushedName()
    {
        return bindDistinguishedName;
    }

    @Config("ldap.bind-dn")
    @ConfigDescription("Bind distinguished name. Example: CN=User Name,OU=CITY_OU,OU=STATE_OU,DC=domain,DC=domain_root")
    public LdapConfig setBindDistingushedName(String bindDistingushedName)
    {
        this.bindDistinguishedName = bindDistingushedName;
        return this;
    }

    public String getBindPassword()
    {
        return bindPassword;
    }

    @Config("ldap.bind-password")
    @ConfigDescription("Bind password used. Example: password1234")
    @ConfigSecuritySensitive
    public LdapConfig setBindPassword(String bindPassword)
    {
        this.bindPassword = bindPassword;
        return this;
    }

    public boolean isIgnoreReferrals()
    {
        return ignoreReferrals;
    }

    @Config("ldap.ignore-referrals")
    @ConfigDescription("Referrals allow finding entries across multiple LDAP servers. Ignore them to only search within 1 LDAP server")
    public LdapConfig setIgnoreReferrals(boolean ignoreReferrals)
    {
        this.ignoreReferrals = ignoreReferrals;
        return this;
    }

    @NotNull
    public Duration getLdapCacheTtl()
    {
        return ldapCacheTtl;
    }

    @Config("ldap.cache-ttl")
    public LdapConfig setLdapCacheTtl(Duration ldapCacheTtl)
    {
        this.ldapCacheTtl = ldapCacheTtl;
        return this;
    }

    public Optional<Duration> getLdapConnectionTimeout()
    {
        return ldapConnectionTimeout;
    }

    @Config("ldap.timeout.connect")
    @ConfigDescription("Timeout for establishing a connection")
    public LdapConfig setLdapConnectionTimeout(Duration ldapConnectionTimeout)
    {
        this.ldapConnectionTimeout = Optional.ofNullable(ldapConnectionTimeout);
        return this;
    }

    public Optional<Duration> getLdapReadTimeout()
    {
        return ldapReadTimeout;
    }

    @Config("ldap.timeout.read")
    @ConfigDescription("Timeout for reading data from LDAP")
    public LdapConfig setLdapReadTimeout(Duration ldapReadTimeout)
    {
        this.ldapReadTimeout = Optional.ofNullable(ldapReadTimeout);
        return this;
    }
}
