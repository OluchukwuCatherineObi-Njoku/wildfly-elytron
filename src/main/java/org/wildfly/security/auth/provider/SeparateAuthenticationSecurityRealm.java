/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
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

package org.wildfly.security.auth.provider;

import java.security.Principal;

import org.wildfly.security.auth.spi.AuthorizationIdentity;
import org.wildfly.security.auth.spi.CredentialSupport;
import org.wildfly.security.auth.spi.RealmIdentity;
import org.wildfly.security.auth.spi.RealmUnavailableException;
import org.wildfly.security.auth.spi.SecurityRealm;

/**
 * A realm which directs authentication to one realm and authorization to another.  The authentication realm need
 * not provide any authorization information.  Likewise the authorization realm need not provide any authentication
 * credential acquisition or verification capabilities.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class SeparateAuthenticationSecurityRealm implements SecurityRealm {
    private final SecurityRealm authenticationRealm;
    private final SecurityRealm authorizationRealm;

    /**
     * Construct a new instance.
     *
     * @param authenticationRealm the realm to use for authentication
     * @param authorizationRealm the realm to use for authorization
     */
    public SeparateAuthenticationSecurityRealm(final SecurityRealm authenticationRealm, final SecurityRealm authorizationRealm) {
        this.authenticationRealm = authenticationRealm;
        this.authorizationRealm = authorizationRealm;
    }

    public RealmIdentity createRealmIdentity(final Principal principal) throws RealmUnavailableException {
        boolean ok = false;
        final RealmIdentity authenticationIdentity = authenticationRealm.createRealmIdentity(principal);
        try {
            final RealmIdentity authorizationIdentity = authorizationRealm.createRealmIdentity(principal);
            try {
                final Identity identity = new Identity(authenticationIdentity, authorizationIdentity);
                ok = true;
                return identity;
            } finally {
                if (! ok) authorizationIdentity.dispose();
            }
        } finally {
            if (! ok) authenticationIdentity.dispose();
        }
    }

    public CredentialSupport getCredentialSupport(final Class<?> credentialType) throws RealmUnavailableException {
        return authenticationRealm.getCredentialSupport(credentialType);
    }

    static final class Identity implements RealmIdentity {

        private final RealmIdentity authenticationIdentity;
        private final RealmIdentity authorizationIdentity;

        Identity(final RealmIdentity authenticationIdentity, final RealmIdentity authorizationIdentity) {
            this.authenticationIdentity = authenticationIdentity;
            this.authorizationIdentity = authorizationIdentity;
        }

        public Principal getPrincipal() throws RealmUnavailableException {
            return authenticationIdentity.getPrincipal();
        }

        public CredentialSupport getCredentialSupport(final Class<?> credentialType) throws RealmUnavailableException {
            return authenticationIdentity.getCredentialSupport(credentialType);
        }

        public <C> C getCredential(final Class<C> credentialType) throws RealmUnavailableException {
            return authenticationIdentity.getCredential(credentialType);
        }

        public boolean verifyCredential(final Object credential) throws RealmUnavailableException {
            return authenticationIdentity.verifyCredential(credential);
        }

        public boolean exists() throws RealmUnavailableException {
            return authenticationIdentity.exists();
        }

        public AuthorizationIdentity getAuthorizationIdentity() throws RealmUnavailableException {
            return authorizationIdentity.exists() ? authorizationIdentity.getAuthorizationIdentity() : AuthorizationIdentity.emptyIdentity(getPrincipal());
        }

        public void dispose() {
            authenticationIdentity.dispose();
            authorizationIdentity.dispose();
        }
    }
}
