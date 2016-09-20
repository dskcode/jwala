package com.siemens.cto.aem.service.webserver;

import com.siemens.cto.aem.common.domain.model.group.Group;
import com.siemens.cto.aem.common.domain.model.id.Identifier;
import com.siemens.cto.aem.common.domain.model.resource.ResourceGroup;
import com.siemens.cto.aem.common.domain.model.user.User;
import com.siemens.cto.aem.common.domain.model.webserver.WebServer;
import com.siemens.cto.aem.common.domain.model.webserver.WebServerReachableState;
import com.siemens.cto.aem.common.request.webserver.CreateWebServerRequest;
import com.siemens.cto.aem.common.request.webserver.UpdateWebServerRequest;
import com.siemens.cto.aem.common.request.webserver.UploadWebServerTemplateRequest;
import com.siemens.cto.aem.persistence.jpa.domain.resource.config.template.JpaWebServerConfigTemplate;

import java.util.List;

public interface WebServerService {

    WebServer createWebServer(final CreateWebServerRequest aCreateWebServerCommand, final User aCreatingUser);

    WebServer getWebServer(final Identifier<WebServer> aWebServerId);

    WebServer getWebServer(final String aWebServerName);

    List<WebServer> getWebServers();

    List<WebServer> findWebServers(final Identifier<Group> aGroupId);

    WebServer updateWebServer(final UpdateWebServerRequest anUpdateWebServerCommand, final User anUpdatingUser);

    void removeWebServer(final Identifier<WebServer> aWebServerId);

    String generateInvokeWSBat(WebServer webServer);

    String generateHttpdConfig(final String aWebServerName, ResourceGroup resourceGroup);

    List<String> getResourceTemplateNames(final String webServerName);

    String getResourceTemplate(final String webServerName, final String resourceTemplateName, final boolean tokensReplaced, ResourceGroup resourceGroup);

    JpaWebServerConfigTemplate uploadWebServerConfig(UploadWebServerTemplateRequest uploadWebServerTemplateCommand, User user);

    String updateResourceTemplate(final String wsName, final String resourceTemplateName, final String template);

    String previewResourceTemplate(String webServerName, String groupName, String template);

    boolean isStarted(WebServer webServer);

    void updateErrorStatus(Identifier<WebServer> id, String errorStatus);

    void updateState(Identifier<WebServer> id, WebServerReachableState state, String errorStatus);

    Long getWebServerStartedCount(String groupName);

    Long getWebServerCount(String groupName);

    List<WebServer> getWebServersPropagationNew();

    Long getWebServerStoppedCount(String groupName);

    String getResourceTemplateMetaData(String aWebServerName, String resourceTemplateName);
}