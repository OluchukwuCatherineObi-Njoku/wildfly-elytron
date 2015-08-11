/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.security.sasl.util;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;

/**
 * @author Kabir Khan
 */
public class PropertiesSaslClientFactory extends AbstractDelegatingSaslClientFactory {

    private final Map<String, ?> properties;

    /**
     * Constructor
     * @param delegate the underlying SaslServerFactory
     * @param properties the properties
     */
    public PropertiesSaslClientFactory(SaslClientFactory delegate, Map<String, ?> properties) {
        super(delegate);
        this.properties = new HashMap<>(properties);
    }

    @Override
    public String[] getMechanismNames(Map<String, ?> props) {
        return delegate.getMechanismNames(combine(props, properties));
    }

    @Override
    public SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName, Map<String, ?> props, CallbackHandler cbh) throws SaslException {
        return super.createSaslClient(mechanisms, authorizationId, protocol, serverName, combine(props, properties), cbh);
    }

    private static Map<String, ?> combine(Map<String, ?> provided, Map<String, ?> configured) {
        Map<String, Object> combined = new HashMap<>(provided);
        combined.putAll( configured);

        return combined;
    }
}
