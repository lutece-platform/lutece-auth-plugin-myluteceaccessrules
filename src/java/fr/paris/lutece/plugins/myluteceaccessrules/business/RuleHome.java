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

import java.util.List;

import fr.paris.lutece.plugins.myluteceaccessrules.service.AccessRulesService;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for Rule objects
 */
public final class RuleHome
{
    // Static variable pointed at the DAO instance
    private static IRuleDAO _dao = SpringContextService.getBean( "myluteceaccessrules.ruleDAO" );
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
        
        AccessRulesService.getInstance().getCache().resetCache();
        
        
        
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
        
        AccessRulesService.getInstance().getCache().resetCache();
        
        
        return rule;
    }

    /**
     * Remove the rule whose identifier is specified in parameter
     * @param nKey The rule Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );

        
        DatastoreService.removeDataByPrefix(RULE_PROTECTED_URL_PREFIX+nKey);
        DatastoreService.removeDataByPrefix(RULE_PUBLIC_URL_PREFIX+nKey);
        DatastoreService.removeDataByPrefix(RULE_ROLES_PREFIX+nKey);
        
        AccessRulesService.getInstance().getCache().resetCache();           
        
    }

    /**
     * Returns an instance of a rule whose identifier is specified in parameter
     * @param nKey The rule primary key
     * @return an instance of Rule
     */
    public static Rule findByPrimaryKey( int nKey )
    {
        Rule rule=_dao.load( nKey, _plugin );
        setRuleUrls(rule);
        setRuleRoles(rule);
        
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
    
    
    private static void updateRuleUrls(Rule rule)
    {
    	  int[] idx = { 0 };
    	 DatastoreService.removeDataByPrefix(RULE_PROTECTED_URL_PREFIX+rule.getId());
         DatastoreService.removeDataByPrefix(RULE_PUBLIC_URL_PREFIX+rule.getId());
         
         if( rule.getProtectedUrls()!=null )
         {
        	
         	rule.getProtectedUrls().stream().forEach(x -> DatastoreService.setInstanceDataValue(RULE_PROTECTED_URL_PREFIX+rule.getId()+"_"+idx[0]++, x.getName()));
         	
         }
         
         if( rule.getPublicUrls()!=null )
         {
        	idx[0]=0; 
         	rule.getPublicUrls().stream().forEach(x -> DatastoreService.setInstanceDataValue(RULE_PUBLIC_URL_PREFIX+rule.getId()+"_"+idx[0]++, x.getName()));
         	
         }
           	
    }
    
    private static void updateRuleRoles(Rule rule)
    {
    	  int[] idx = { 0 };
    	 DatastoreService.removeDataByPrefix(RULE_ROLES_PREFIX+rule.getId());
     
         
         if( rule.getRoles() !=null )
         {
        	
         	rule.getRoles().stream().forEach(x -> DatastoreService.setInstanceDataValue(RULE_ROLES_PREFIX+rule.getId()+"_"+idx[0]++, x.getName()));
         	
         }
         
           	
    }
    
    
    private static void setRuleUrls(Rule rule)
    {

        rule.setProtectedUrls(DatastoreService.getDataByPrefix( RULE_PROTECTED_URL_PREFIX+rule.getId() ));
        rule.setPublicUrls(DatastoreService.getDataByPrefix( RULE_PUBLIC_URL_PREFIX+rule.getId() ));	
    	
    
    }
    
    private static void setRuleRoles(Rule rule)
    {

          rule.setRoles(DatastoreService.getDataByPrefix( RULE_ROLES_PREFIX+rule.getId() ));	
    	
    
    }
}

