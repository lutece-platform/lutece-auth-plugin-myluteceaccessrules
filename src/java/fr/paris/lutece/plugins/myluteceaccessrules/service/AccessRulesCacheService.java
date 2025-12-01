package fr.paris.lutece.plugins.myluteceaccessrules.service;

import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.servlet.ServletContext;

import java.util.List;

/**
 * AccessRulesCacheService
 */
@ApplicationScoped
public class AccessRulesCacheService extends AbstractCacheableService<String, List<Rule>>
{
    private static final String CACHE_SERVICE_NAME = "Access Rules Cache Service";

    public void init( )
    {
        initCache( );
        enableCache( true );
    }

    /**
     * Gets the cache service name
     * 
     * @return The service name
     */
    @Override
    public String getName( )
    {
        return CACHE_SERVICE_NAME;
    }

    /**
     * This method observes the initialization of the {@link ApplicationScoped} context.
     * It ensures that this CDI beans are instantiated at the application startup.
     *
     * <p>This method is triggered automatically by CDI when the {@link ApplicationScoped} context is initialized,
     * which typically occurs during the startup of the application server.</p>
     *
     * @param context the {@link ServletContext} that is initialized. This parameter is observed
     *                and injected automatically by CDI when the {@link ApplicationScoped} context is initialized.
     */
    public void initializedService(@Observes @Initialized(ApplicationScoped.class) ServletContext context) {
        // This method is intentionally left empty to trigger CDI bean instantiation
    }
   
}
