package com.tasks.manager;

import com.flipkart.casclient.client.HttpAuthClient;
import com.flipkart.restbus.client.core.MessageReceiver;
import com.flipkart.restbus.client.core.MessageSender;
import com.flipkart.restbus.client.repository.InboundMessageRepository;
import com.flipkart.restbus.client.repository.OutboundMessageRepository;
import com.fquick.resthibernateplugin.core.annotations.AsyncAnnotation;
import com.fquick.resthibernateplugin.core.annotations.SyncAnnotation;
import com.fquick.resthibernateplugin.core.providers.*;
import com.fquick.resthibernateplugin.db.dao.jpa.restbus.InboundMessageDaoJpaImpl;
import com.fquick.resthibernateplugin.db.dao.jpa.restbus.OutboundMessageDaoJpaImpl;
import com.google.inject.AbstractModule;
import com.google.inject.persist.UnitOfWork;
//import com.restbus.client.plugin.dropwizard.filter.jersey.IdempotencyFilter;
//import com.restbus.lock.client.Lock;
import com.tasks.manager.db.dao.interfaces.*;
import com.tasks.manager.db.dao.jpa.*;
import com.tasks.manager.service.api.TaskManagerService;
import com.tasks.manager.service.impl.TaskManagerServiceImpl;

/**
 * Created by shlok.chaurasia on 07/11/15.
 */
public class BindingClassForTests extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskManagerService.class).to(TaskManagerServiceImpl.class);
        bind(TaskDao.class).to(TaskDaoImpl.class);
        bind(TaskGroupDao.class).to(TaskGroupDaoImpl.class);
        bind(TaskAttributesDao.class).to(TaskAttributesDaoImpl.class);
        bind(SubjectDao.class).to(SubjectDaoImpl.class);
        bind(TaskHistoryDao.class).to(TaskHistoryDaoImpl.class);
        bind(HttpAuthClient.class).toProvider(HttpAuthClientProvider.class);
        bind(OutboundMessageRepository.class).to(OutboundMessageDaoJpaImpl.class);
        bind(InboundMessageRepository.class).to(InboundMessageDaoJpaImpl.class);
        bind(MessageReceiver.class).toProvider(MessageReceiverProvider.class);
        bind(MessageSender.class).annotatedWith(AsyncAnnotation.class).toProvider(AsyncMessageSenderProvider.class);
        bind(MessageSender.class).annotatedWith(SyncAnnotation.class).toProvider(SyncMessageSenderProvider.class);
    }

}
