/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
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

package org.wildfly.security.sasl;

import java.util.Map;

import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

import org.wildfly.security.auth.provider.ServerAuthenticationContext;

/**
 * A SASL server factory which is compatible with an {@link ServerAuthenticationContext}.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public interface SaslServerFactory {
    SaslServer createSaslServer(String mechanism, String protocol, String serverName, Map<String, ?> properties, ServerAuthenticationContext authenticationContext) throws SaslException;

    String[] getMechanismNames(Map<String, ?> properties, ServerAuthenticationContext authenticationContext);
}
