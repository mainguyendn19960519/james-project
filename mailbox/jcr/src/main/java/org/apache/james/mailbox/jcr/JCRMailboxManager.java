/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.apache.james.mailbox.jcr;

import java.util.EnumSet;

import org.apache.james.mailbox.MailboxPathLocker;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.acl.GroupMembershipResolver;
import org.apache.james.mailbox.acl.MailboxACLResolver;
import org.apache.james.mailbox.exception.MailboxException;
import org.apache.james.mailbox.jcr.mail.model.JCRMailbox;
import org.apache.james.mailbox.model.MailboxPath;
import org.apache.james.mailbox.model.MessageId;
import org.apache.james.mailbox.store.Authenticator;
import org.apache.james.mailbox.store.Authorizator;
import org.apache.james.mailbox.store.JVMMailboxPathLocker;
import org.apache.james.mailbox.store.StoreMailboxManager;
import org.apache.james.mailbox.store.StoreMessageManager;
import org.apache.james.mailbox.store.mail.model.Mailbox;
import org.apache.james.mailbox.store.mail.model.impl.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JCR implementation of a MailboxManager
 * 
 */
public class JCRMailboxManager extends StoreMailboxManager implements JCRImapConstants {

    private final Logger logger = LoggerFactory.getLogger(JCRMailboxManager.class);
    
    public JCRMailboxManager(JCRMailboxSessionMapperFactory mapperFactory, Authenticator authenticator, Authorizator authorizator,
            MailboxACLResolver aclResolver, GroupMembershipResolver groupMembershipResolver, 
            MessageParser messageParser, MessageId.Factory messageIdFactory) {
	    this(mapperFactory, authenticator, authorizator, new JVMMailboxPathLocker(), aclResolver, groupMembershipResolver, messageParser, messageIdFactory);
    }

    public JCRMailboxManager(JCRMailboxSessionMapperFactory mapperFactory, Authenticator authenticator, Authorizator authorizator,
            MailboxPathLocker locker, MailboxACLResolver aclResolver, GroupMembershipResolver groupMembershipResolver, 
            MessageParser messageParser, MessageId.Factory messageIdFactory) {
        super(mapperFactory, authenticator, authorizator, locker, aclResolver, groupMembershipResolver, messageParser, messageIdFactory);
    }

    @Override
    public EnumSet<MailboxCapabilities> getSupportedMailboxCapabilities() {
        return EnumSet.of(MailboxCapabilities.Namespace);
    }
    
    @Override
    protected StoreMessageManager createMessageManager(Mailbox mailboxEntity, MailboxSession session) throws MailboxException{
        return new JCRMessageManager(getMapperFactory(),
            getMessageSearchIndex(),
            getEventDispatcher(),
            getLocker(),
            (JCRMailbox) mailboxEntity,
            getAclResolver(),
            getGroupMembershipResolver(),
            logger,
            getQuotaManager(),
            getQuotaRootResolver(),
            getMessageParser(),
            getMessageIdFactory());
    }

    @Override
    protected Mailbox doCreateMailbox(MailboxPath path, MailboxSession session) throws MailboxException {
        return new org.apache.james.mailbox.jcr.mail.model.JCRMailbox(path, randomUidValidity(), logger);
    }

}
