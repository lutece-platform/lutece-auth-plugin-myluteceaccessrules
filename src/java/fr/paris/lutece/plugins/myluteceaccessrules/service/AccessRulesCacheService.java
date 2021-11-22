package fr.paris.lutece.plugins.myluteceaccessrules.service;

import fr.paris.lutece.portal.service.cache.AbstractCacheableService;

/**
 * AccessRulesCacheService
 */
public class AccessRulesCacheService extends AbstractCacheableService
{
    private static final String CACHE_SERVICE_NAME = "Access Rules Cache Service";
    

    /** Constructor */
    public AccessRulesCacheService( )
    {
        initCache( );
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
    
    
   
}
