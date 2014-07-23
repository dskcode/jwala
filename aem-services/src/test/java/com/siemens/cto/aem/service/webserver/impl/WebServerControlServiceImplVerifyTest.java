package com.siemens.cto.aem.service.webserver.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.siemens.cto.aem.control.webserver.WebServerCommandExecutor;
import com.siemens.cto.aem.domain.model.event.Event;
import com.siemens.cto.aem.domain.model.id.Identifier;
import com.siemens.cto.aem.domain.model.temporary.User;
import com.siemens.cto.aem.domain.model.webserver.WebServer;
import com.siemens.cto.aem.domain.model.webserver.WebServerControlHistory;
import com.siemens.cto.aem.domain.model.webserver.WebServerControlOperation;
import com.siemens.cto.aem.domain.model.webserver.WebServerReachableState;
import com.siemens.cto.aem.domain.model.webserver.command.CompleteControlWebServerCommand;
import com.siemens.cto.aem.domain.model.webserver.command.ControlWebServerCommand;
import com.siemens.cto.aem.persistence.service.webserver.WebServerControlPersistenceService;
import com.siemens.cto.aem.service.VerificationBehaviorSupport;
import com.siemens.cto.aem.service.webserver.WebServerService;
import com.siemens.cto.aem.service.webserver.WebServerStateGateway;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebServerControlServiceImplVerifyTest extends VerificationBehaviorSupport {

    private WebServerControlServiceImpl impl;
    private WebServerControlPersistenceService persistenceService;
    private WebServerService webServerService;
    private WebServerCommandExecutor commandExecutor;
    private WebServerStateGateway webServerStateGateway;
    private User user;

    @Before
    public void setup() {
        persistenceService = mock(WebServerControlPersistenceService.class);
        webServerService = mock(WebServerService.class);
        commandExecutor = mock(WebServerCommandExecutor.class);
        webServerStateGateway = mock(WebServerStateGateway.class);

        impl = new WebServerControlServiceImpl(persistenceService,
                                               webServerService,
                                               commandExecutor,
                                               webServerStateGateway);

        user = new User("unused");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVerificationOfBehaviorForSuccess() throws Exception {
        final ControlWebServerCommand controlCommand = mock(ControlWebServerCommand.class);
        final WebServer webServer = mock(WebServer.class);
        final Identifier<WebServer> webServerId = mock(Identifier.class);
        final Identifier<WebServerControlHistory> historyId = mock(Identifier.class);
        final WebServerControlHistory incompleteHistory = mock(WebServerControlHistory.class);

        when(controlCommand.getWebServerId()).thenReturn(webServerId);
        when(controlCommand.getControlOperation()).thenReturn(WebServerControlOperation.START);
        when(webServerService.getWebServer(eq(webServerId))).thenReturn(webServer);
        when(incompleteHistory.getId()).thenReturn(historyId);

        when(persistenceService.addIncompleteControlHistoryEvent(matchCommandInEvent(controlCommand))).
                thenReturn(incompleteHistory);

        impl.controlWebServer(controlCommand,
                user);

        verify(controlCommand, times(1)).validateCommand();

        verify(persistenceService, times(1)).addIncompleteControlHistoryEvent(matchCommandInEvent(controlCommand));
        verify(persistenceService, times(1)).
                completeControlHistoryEvent(Matchers.<Event<CompleteControlWebServerCommand>>anyObject());

        verify(webServerService, times(1)).getWebServer(eq(webServerId));
        verify(commandExecutor, times(1)).controlWebServer(eq(controlCommand),
                                                     eq(webServer));
        verify(webServerStateGateway, times(1)).setExplicitState(eq(webServerId),
                                                                 eq(WebServerReachableState.START_REQUESTED));
    }
}
