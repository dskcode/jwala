package com.siemens.cto.aem.persistence.dao.jvm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.siemens.cto.aem.common.exception.BadRequestException;
import com.siemens.cto.aem.common.exception.NotFoundException;
import com.siemens.cto.aem.domain.model.audit.AuditEvent;
import com.siemens.cto.aem.domain.model.event.Event;
import com.siemens.cto.aem.domain.model.group.CreateGroupCommand;
import com.siemens.cto.aem.domain.model.group.Group;
import com.siemens.cto.aem.domain.model.id.Identifier;
import com.siemens.cto.aem.domain.model.jvm.Jvm;
import com.siemens.cto.aem.domain.model.jvm.command.CreateJvmCommand;
import com.siemens.cto.aem.domain.model.jvm.command.UpdateJvmCommand;
import com.siemens.cto.aem.domain.model.temporary.PaginationParameter;
import com.siemens.cto.aem.domain.model.temporary.User;

@Transactional
public abstract class AbstractJvmDaoIntegrationTest {

    @Autowired
    private JvmDao jvmDao;

    private Jvm preCreatedJvm;
    private String userName;

    @Before
    public void setup() {
        userName = "jvmTestUserName";

        preCreatedJvm =
                jvmDao.createJvm(createCreateJvmCommand("Pre-created JVM Name", "Pre-created Host Name",
                                                         5, 4, 3, 2, 1, userName));
    }

    @Test
    public void testCreateNewJvm() {
        final Event<CreateJvmCommand> command = createCreateJvmCommand("New Jvm Name", "New Host Name",
                                                                        5, 4, 3, 2, 1, userName);
        final Jvm createdJvm = jvmDao.createJvm(command);

        assertEquals(command.getCommand().getJvmName(), createdJvm.getJvmName());
    }

    @Test(expected = BadRequestException.class)
    public void testCreateDuplicateJvm() {

        final Event<CreateJvmCommand> commandEvent =
                createCreateJvmCommand(preCreatedJvm.getJvmName(),
                                       preCreatedJvm.getHostName(),
                                       preCreatedJvm.getHttpPort(),
                                       preCreatedJvm.getHttpsPort(),
                                       preCreatedJvm.getRedirectPort(),
                                       preCreatedJvm.getShutdownPort(),
                                       preCreatedJvm.getAjpPort(),
                                       userName);
        jvmDao.createJvm(commandEvent);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveJvm() {
        jvmDao.removeJvm(preCreatedJvm.getId());

        jvmDao.getJvm(preCreatedJvm.getId());
        fail("JVM should not exist");
    }

    @Test
    public void testUpdateJvm() {
        final Event<UpdateJvmCommand> update =
                createUpdateJvmCommand(preCreatedJvm.getId(), "New Jvm Name", "New Host Name", 5, 4, 3, 2, 1, userName);
        final Jvm actualJvm = jvmDao.updateJvm(update);

        assertEquals(update.getCommand().getNewJvmName(), actualJvm.getJvmName());
        assertEquals(update.getCommand().getNewHostName(), actualJvm.getHostName());
        assertEquals(update.getCommand().getNewHttpPort(), actualJvm.getHttpPort());
        assertEquals(update.getCommand().getNewHttpsPort(), actualJvm.getHttpsPort());
        assertEquals(update.getCommand().getNewRedirectPort(), actualJvm.getRedirectPort());
        assertEquals(update.getCommand().getNewShutdownPort(), actualJvm.getShutdownPort());
        assertEquals(update.getCommand().getNewAjpPort(), actualJvm.getAjpPort());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateDuplicateJvm() {
        final Jvm newJvm =
                jvmDao.createJvm(createCreateJvmCommand("Eventually duplicate JVM name", "Unused", 5, 4, 3, 2, 1, userName));

        jvmDao.updateJvm(createUpdateJvmCommand(newJvm.getId(),
                                                preCreatedJvm.getJvmName(),
                                                preCreatedJvm.getHostName(),
                                                preCreatedJvm.getHttpPort(),
                                                preCreatedJvm.getHttpsPort(),
                                                preCreatedJvm.getRedirectPort(),
                                                preCreatedJvm.getShutdownPort(),
                                                preCreatedJvm.getAjpPort(),
                                                userName));
    }

    @Test
    public void testFindJvmsByName() {
        final int numberToCreate = 10;
        final int numberActive = 4;

        final String activeSuffix = "Active";
        final String passiveSuffix = "Passive";

        createMultipleJvms(numberActive, activeSuffix, activeSuffix);
        createMultipleJvms(numberToCreate - numberActive, passiveSuffix, passiveSuffix);

        final List<Jvm> activeJvms = jvmDao.findJvms(activeSuffix, new PaginationParameter(0, numberToCreate));
        final List<Jvm> passiveJvms = jvmDao.findJvms(passiveSuffix, new PaginationParameter(0, numberToCreate));

        verifyBulkJvmAssertions(activeJvms, numberActive, activeSuffix, activeSuffix, 5, 4, 3, 2, 1);
        verifyBulkJvmAssertions(passiveJvms, numberToCreate - numberActive, passiveSuffix, passiveSuffix, 5, 4, 3, 2, 1);
    }

    @Test
    public void testGetJvms() {
        final int numberToCreate = 7;
        final String suffix = "GET_JVM_SUFFIX";

        createMultipleJvms(numberToCreate, suffix, suffix);
        final List<Jvm> jvms = jvmDao.getJvms(new PaginationParameter(0, numberToCreate));

        assertEquals(numberToCreate, jvms.size());
    }

    protected Event<CreateGroupCommand> createGroupCommandEvent(final String aGroupName, final String aUserName) {
        return new Event<>(new CreateGroupCommand(aGroupName), AuditEvent.now(new User(aUserName)));
    }

    protected Event<CreateJvmCommand> createCreateJvmCommand(final String aJvmName,
                                                             final String aHostName,
                                                             final Integer httpPort,
                                                             final Integer httpsPort,
                                                             final Integer redirectPort,
                                                             final Integer shutdownPort,
                                                             final Integer ajpPort,
                                                             final String aUserName) {
        return new Event<>(new CreateJvmCommand(aJvmName, aHostName, httpPort, httpsPort, redirectPort, shutdownPort, ajpPort), AuditEvent.now(new User(aUserName)));
    }

    protected Event<UpdateJvmCommand> createUpdateJvmCommand(final Identifier<Jvm> aJvmId, final String aNewJvmName,
                                                             final String aNewHostName,
                                                             final Integer aNewHttpPort,
                                                             final Integer aNewHttpsPort,
                                                             final Integer aNewRedirectPort,
                                                             final Integer aNewShutdownPort,
                                                             final Integer aNewAjpPort,
                                                             final String aUserName) {
        return new Event<>(new UpdateJvmCommand(aJvmId,
                                                aNewJvmName,
                                                aNewHostName,
                                                Collections.<Identifier<Group>> emptySet(),
                                                aNewHttpPort,
                                                aNewHttpsPort,
                                                aNewRedirectPort,
                                                aNewShutdownPort,
                                                aNewAjpPort), AuditEvent.now(new User(aUserName)));
    }

    protected void verifyBulkJvmAssertions(final List<Jvm> someJvms,
                                           final int anExpectedSize,
                                           final String anExpectedJvmSuffix,
                                           final String anExpectedHostNameSuffix,
                                           final Integer httpPort,
                                           final Integer httpsPort,
                                           final Integer redirectPort,
                                           final Integer shutdownPort,
                                           final Integer ajpPort) {

        assertEquals(anExpectedSize, someJvms.size());
        for (final Jvm jvm : someJvms) {
            assertTrue(jvm.getJvmName().contains(anExpectedJvmSuffix));
            assertTrue(jvm.getHostName().contains(anExpectedHostNameSuffix));

            assertTrue(jvm.getHttpPort().equals(httpPort));
            assertTrue(jvm.getHttpsPort().equals(httpsPort));
            assertTrue(jvm.getRedirectPort().equals(redirectPort));
            assertTrue(jvm.getShutdownPort().equals(shutdownPort));
            assertTrue(jvm.getAjpPort().equals(ajpPort));
        }
    }

    protected void createMultipleJvms(final int aNumberToCreate, final String aJvmNameSuffix,
            final String aHostNameSuffix) {
        for (int i = 1; i <= aNumberToCreate; i++) {
            jvmDao.createJvm(createCreateJvmCommand("JVM" + i + aJvmNameSuffix, "HostName" + i + aHostNameSuffix,
                                                    5, 4, 3, 2, 1, userName));
        }
    }
}
