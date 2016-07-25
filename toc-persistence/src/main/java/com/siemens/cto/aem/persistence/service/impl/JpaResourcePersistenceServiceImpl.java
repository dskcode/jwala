package com.siemens.cto.aem.persistence.service.impl;

import com.siemens.cto.aem.persistence.jpa.domain.resource.config.template.JpaGroupAppConfigTemplate;
import com.siemens.cto.aem.persistence.service.ResourcePersistenceService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by z003e5zv on 3/25/2015.
 */
public class JpaResourcePersistenceServiceImpl implements ResourcePersistenceService {

    @PersistenceContext(unitName = "aem-unit")
    private EntityManager em;

    @Override
    // NOTE: We're going to use the entity manager here since we are phasing out the CRUD layer soon.
    public List<String> getApplicationResourceNames(final String groupName, final String appName) {
        final Query q = em.createNamedQuery(JpaGroupAppConfigTemplate.QUERY_APP_RESOURCE_NAMES);
        q.setParameter("grpName", groupName);
        q.setParameter("appName", appName);
        return q.getResultList();
    }

    @Override
    public String getAppTemplate(final String groupName, final String appName, final String templateName) {
        final Query q = em.createNamedQuery(JpaGroupAppConfigTemplate.GET_GROUP_APP_TEMPLATE_CONTENT);
        q.setParameter(JpaGroupAppConfigTemplate.QUERY_PARAM_GRP_NAME, groupName);
        q.setParameter(JpaGroupAppConfigTemplate.QUERY_PARAM_APP_NAME, appName);
        q.setParameter(JpaGroupAppConfigTemplate.QUERY_PARAM_TEMPLATE_NAME, templateName);
        return (String) q.getSingleResult();
    }
}
