/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
 package fr.paris.lutece.plugins.myluteceaccessrules.business;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.myluteceaccessrules.service.AccessRulesService;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import jakarta.enterprise.inject.spi.CDI;

/**
 * This class provides instances management methods (create, find, ...) for Rule objects
 */
public final class RuleHome
{

    private static AccessRulesService _accessRulesService = CDI.current( ).select( AccessRulesService.class ).get( );

    // Static variable pointed at the DAO instance
    private static IRuleDAO _dao = CDI.current( ).select( IRuleDAO.class ).get( );

    private static Plugin _plugin = PluginService.getPlugin( "accessrules" );
    public  static final String RULE_PROTECTED_URL_PREFIX="rule_protected_urls_";
    public  static final String RULE_ROLES_PREFIX="rule_roles_";
    public static final String RULE_PUBLIC_URL_PREFIX="rule_public_urls_";
    

    /**
     * Private constructor - this class need not be instantiated
     */
    private RuleHome(  )
    {
    }

    /**
     * Create an instance of the rule class
     * @param rule The instance of the Rule which contains the informations to store
     * @return The  instance of rule which has been created with its primary key.
     */
    public static Rule create( Rule rule )
    {
        _dao.insert( rule, _plugin );
        updateRuleUrls(rule);
        updateRuleRoles(rule);

        
       List<Rule> ruleList=RuleHome.getRulesList();
       ruleList.add(rule);
       RuleHome.updateRulesPriorities(ruleList);

        _accessRulesService.getCache().resetCache();
       
        
        
        return rule;
    }

    /**
     * Update of the rule which is specified in parameter
     * @param rule The instance of the Rule which contains the data to store
     * @return The instance of the  rule which has been updated
     */
    public static Rule update( Rule rule )
    {
    	
    	
        _dao.store( rule, _plugin );
        updateRuleUrls(rule);
        updateRuleRoles(rule);

        _accessRulesService.getCache().resetCache();
        
        
        return rule;
    }

    /**
     * Remove the rule whose identifier is specified in parameter
     * @param nKey The rule Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );

        
        DatastoreService.removeInstanceDataByPrefix(getRuleKeyPrefix(RULE_PROTECTED_URL_PREFIX, nKey));
        DatastoreService.removeInstanceDataByPrefix(getRuleKeyPrefix(RULE_PUBLIC_URL_PREFIX, nKey));
        DatastoreService.removeInstanceDataByPrefix(getRuleKeyPrefix(RULE_ROLES_PREFIX, nKey));

        _accessRulesService.getCache().resetCache();
        
    }

    /**
     * Returns an instance of a rule whose identifier is specified in parameter
     * @param nKey The rule primary key
     * @return an instance of Rule
     */
    public static Rule findByPrimaryKey( int nKey )
    {
        Rule rule=_dao.load( nKey, _plugin );
        if (rule !=null)
        {
            setRuleUrls(rule);
            setRuleRoles(rule);
        }

        return rule;
    }

    /**
     * Load the data of all the rule objects and returns them as a list
     * @return the list which contains the data of all the rule objects
     */
    public static List<Rule> getRulesList( )
    {
        List<Rule>   listRules=  _dao.selectRulesList( _plugin );
        listRules.forEach(x->{setRuleUrls(x);setRuleRoles(x);});
        
        return listRules;
        
    }
    
    /**
     * Load the id of all the rule objects and returns them as a list
     * @return the list which contains the id of all the rule objects
     */
    public static List<Integer> getIdRulesList( )
    {
        return _dao.selectIdRulesList( _plugin );
    }
    
    /**
     * Load the data of all the rule objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the rule objects
     */
    public static ReferenceList getRulesReferenceList( )
    {
        return _dao.selectRulesReferenceList( _plugin );
    }
    
    
    
 	/**
	  * Update rules priorities.
	  *
	  * @param listRules the list rules
	  */
	 public static void updateRulesPriorities(List<Rule> listRules) {
 		if (listRules != null) {
 			
 			List<Rule> listRulesSorted= listRules.stream().sorted(Comparator.comparingInt(Rule::getPriorityOrder).reversed())
 	        .collect(Collectors.toList());
 			
 			int nPriority = listRulesSorted.size()+1;
 			for (Rule rule : listRulesSorted) {

 				rule.setPriorityOrder(nPriority--);
 				// only the priority changes: the URLs and roles of the rule are not rewritten, so that the front office never reads a
 				// rule without its URLs (a rule without protected URL applies to every URL)
 				_dao.store( rule, _plugin );

 			}
 		}
 	   //reset cache after updating rules priorities 
         _accessRulesService.getCache().resetCache();
 	}
     
    
    
    private static void updateRuleUrls(Rule rule)
    {
        writeRuleData( RULE_PROTECTED_URL_PREFIX, rule.getId( ), rule.getProtectedUrls( ) );
        writeRuleData( RULE_PUBLIC_URL_PREFIX, rule.getId( ), rule.getPublicUrls( ) );
    }
    
    private static void updateRuleRoles(Rule rule)
    {
        writeRuleData( RULE_ROLES_PREFIX, rule.getId( ), rule.getRoles( ) );
    }

    /**
     * Writes the values of a rule data type (URLs or roles): the values are written first, then the keys left over from a longer former list are
     * removed. A concurrent reader of the rules therefore never sees the list emptied while it is rewritten.
     *
     * @param strPrefix
     *            the prefix of the data type
     * @param nIdRule
     *            the rule id
     * @param listValues
     *            the values to store, null for none
     */
    private static void writeRuleData( String strPrefix, int nIdRule, ReferenceList listValues )
    {
        String strKeyPrefix = getRuleKeyPrefix( strPrefix, nIdRule );
        int nCount = 0;
        if ( listValues != null )
        {
            for ( ReferenceItem item : listValues )
            {
                DatastoreService.setInstanceDataValue( strKeyPrefix + nCount++, item.getName( ) );
            }
        }

        String strInstanceKeyPrefix = getInstanceKeyPrefix( strKeyPrefix );
        for ( ReferenceItem item : getRuleData( strPrefix, nIdRule ) )
        {
            String strIndex = item.getCode( ).substring( strInstanceKeyPrefix.length( ) );
            if ( !StringUtils.isNumeric( strIndex ) || Integer.parseInt( strIndex ) >= nCount )
            {
                DatastoreService.removeInstanceData( strKeyPrefix + strIndex );
            }
        }
    }

    
    private static void setRuleUrls(Rule rule)
    {

        rule.setProtectedUrls(getRuleData( RULE_PROTECTED_URL_PREFIX, rule.getId() ));
        rule.setPublicUrls(getRuleData( RULE_PUBLIC_URL_PREFIX, rule.getId() ));	
    	
    
    }
    
    private static void setRuleRoles(Rule rule)
    {

          rule.setRoles(getRuleData( RULE_ROLES_PREFIX, rule.getId() ));	
    	
    
    }

    /**
     * Returns the datastore key prefix of the data of a rule, ended by a separator so that rule 1 does not match rule 10
     *
     * @param strPrefix
     *            the prefix of the data type
     * @param nIdRule
     *            the rule id
     * @return the key prefix
     */
    private static String getRuleKeyPrefix( String strPrefix, int nIdRule )
    {
        return strPrefix + nIdRule + "_";
    }

    /**
     * Returns the key prefix as stored by DatastoreService.setInstanceDataValue in the current webapp instance
     *
     * @param strKeyPrefix
     *            the key prefix
     * @return the stored key prefix
     */
    private static String getInstanceKeyPrefix( String strKeyPrefix )
    {
        return AppPathService.isDefaultWebappInstance( ) ? strKeyPrefix : AppPathService.getWebappInstance( ) + "." + strKeyPrefix;
    }

    /**
     * Returns the data of a rule in the current webapp instance whose key starts exactly with its key prefix
     * (the datastore lookup by prefix also matches other characters in place of the underscores)
     *
     * @param strPrefix
     *            the prefix of the data type
     * @param nIdRule
     *            the rule id
     * @return the data of the rule
     */
    private static ReferenceList getRuleData( String strPrefix, int nIdRule )
    {
        String strKeyPrefix = getRuleKeyPrefix( strPrefix, nIdRule );
        String strInstanceKeyPrefix = getInstanceKeyPrefix( strKeyPrefix );
        ReferenceList list = new ReferenceList( );
        DatastoreService.getInstanceDataByPrefix( strKeyPrefix ).stream( ).filter( item -> item.getCode( ).startsWith( strInstanceKeyPrefix ) )
                .forEach( list::add );

        return list;
    }
}

