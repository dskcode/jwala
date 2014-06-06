package com.siemens.cto.toc.files.configuration;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.siemens.cto.toc.files.FilesConfiguration;
import com.siemens.cto.toc.files.NameSynthesizer;
import com.siemens.cto.toc.files.Repository;
import com.siemens.cto.toc.files.WebArchiveManager;
import com.siemens.cto.toc.files.impl.DefaultNameSynthesizer;
import com.siemens.cto.toc.files.impl.LocalFileSystemRepositoryImpl;
import com.siemens.cto.toc.files.impl.PropertyFilesConfigurationImpl;
import com.siemens.cto.toc.files.impl.WebArchiveManagerImpl;
import com.siemens.med.hs.soarian.config.PropertiesStore;

@Configuration
public class TocFileManagerConfiguration {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(TocFileManagerConfiguration.class); 

    private static final String TOC_FILEMANAGER_PROPERTY_SET = "TocFiles";

    @Bean
    public FilesConfiguration getFilesConfiguration() {
        Properties fmProperties;
        
        try {
            fmProperties = PropertiesStore.getProperties(TOC_FILEMANAGER_PROPERTY_SET);
            PropertiesStore.stopPropertiesMonitor();            // BETTER APPROACH?
        } catch(Exception e) {
            LOGGER.trace(TOC_FILEMANAGER_PROPERTY_SET.toString()+".properties is missing: ", e);
            fmProperties = new Properties();
        }
        return new PropertyFilesConfigurationImpl(fmProperties);
    }
    

    @Bean FileSystem getPlatformFileSystem() {
        return FileSystems.getDefault();
    }

    @Bean NameSynthesizer getNameSynthesizer() {
        return new DefaultNameSynthesizer();
    }
    
    @Bean WebArchiveManager getWebArchiveManager() {
        return new WebArchiveManagerImpl();
    }
    
    @Bean Repository getFileSystemStorage() throws IOException {
        return new LocalFileSystemRepositoryImpl();
    }
    
}
