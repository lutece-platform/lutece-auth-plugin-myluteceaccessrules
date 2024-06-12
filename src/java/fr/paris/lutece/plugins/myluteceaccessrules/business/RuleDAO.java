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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Rule objects
 */
public final class RuleDAO implements IRuleDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_rule, title, description, enable, external, messagetodisplay, redirecturl, backurl,priority_order,encodebackurl FROM mylutece_accessrules_rule WHERE id_rule = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO mylutece_accessrules_rule ( title, description, enable, external, messagetodisplay, redirecturl, backurl,priority_order, encodebackurl ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM mylutece_accessrules_rule WHERE id_rule = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE mylutece_accessrules_rule SET id_rule = ?, title = ?, description = ?, enable = ?, external = ?, messagetodisplay = ?, redirecturl = ?, backurl= ? ,priority_order=?,encodebackurl= ? WHERE id_rule = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_rule, title, description, enable, external, messagetodisplay, redirecturl, backurl, priority_order, encodebackurl FROM mylutece_accessrules_rule ORDER BY priority_order DESC ";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_rule FROM mylutece_accessrules_rule";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Rule rule, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++ , rule.getTitle( ) );
            daoUtil.setString( nIndex++ , rule.getDescription( ) );
            daoUtil.setBoolean( nIndex++ , rule.getEnable( ) );
            daoUtil.setBoolean( nIndex++ , rule.isExternal( ) );
            daoUtil.setString( nIndex++ , rule.getMessagetodisplay( ) );
            daoUtil.setString( nIndex++ , rule.getRedirecturl( ) );
            daoUtil.setString( nIndex++ , rule.getBackUrl() );
            daoUtil.setInt( nIndex++ , rule.getPriorityOrder() );
            daoUtil.setBoolean( nIndex++ , rule.isEncodeBackUrl());
            
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                rule.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rule load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        Rule rule = null;
	
	        if ( daoUtil.next( ) )
	        {
	            rule = new Rule();
	            int nIndex = 1;
	            
	            rule.setId( daoUtil.getInt( nIndex++ ) );
	            rule.setTitle( daoUtil.getString( nIndex++ ) );            
	            rule.setDescription( daoUtil.getString( nIndex++ ) );            
	            rule.setEnable( daoUtil.getBoolean( nIndex++ ) );            
	            rule.setExternal( daoUtil.getBoolean( nIndex++ ) );            
	            rule.setMessagetodisplay( daoUtil.getString( nIndex++ ) );            
	            rule.setRedirecturl( daoUtil.getString( nIndex++ ) );
	            rule.setBackUrl( daoUtil.getString( nIndex++ ) );
	            rule.setPriorityOrder(daoUtil.getInt(nIndex++));
	            rule.setEncodeBackUrl(daoUtil.getBoolean( nIndex ));
	            
	        }
	
	        daoUtil.free( );
	        return rule;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeUpdate( );
	        daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Rule rule, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
	        daoUtil.setInt( nIndex++ , rule.getId( ) );
	        daoUtil.setString( nIndex++ , rule.getTitle( ) );
	        daoUtil.setString( nIndex++ , rule.getDescription( ) );
	        daoUtil.setBoolean( nIndex++ , rule.getEnable( ) );
	        daoUtil.setBoolean( nIndex++ , rule.isExternal( ) );
	        daoUtil.setString( nIndex++ , rule.getMessagetodisplay( ) );
	        daoUtil.setString( nIndex++ , rule.getRedirecturl( ) );
	        daoUtil.setString( nIndex++ , rule.getBackUrl() );
	        daoUtil.setInt( nIndex++ , rule.getPriorityOrder() );
	        daoUtil.setBoolean( nIndex++ , rule.isEncodeBackUrl());
	        
	        daoUtil.setInt( nIndex , rule.getId( ) );
	
	        daoUtil.executeUpdate( );
	        daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Rule> selectRulesList( Plugin plugin )
    {
        List<Rule> ruleList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            Rule rule = new Rule(  );
	            int nIndex = 1;
	            
	            rule.setId( daoUtil.getInt( nIndex++ ) );
	            rule.setTitle( daoUtil.getString( nIndex++ ) );
	            rule.setDescription( daoUtil.getString( nIndex++ ) );
	            rule.setEnable( daoUtil.getBoolean( nIndex++ ) );
	            rule.setExternal( daoUtil.getBoolean( nIndex++ ) );
	            rule.setMessagetodisplay( daoUtil.getString( nIndex++ ) );
	            rule.setRedirecturl( daoUtil.getString( nIndex++ ) );       
	            rule.setBackUrl( daoUtil.getString( nIndex++ ) ); 
	            rule.setPriorityOrder(daoUtil.getInt( nIndex++ ));
	            rule.setEncodeBackUrl(daoUtil.getBoolean( nIndex ));
	            ruleList.add( rule );
	        }
	
	        daoUtil.free( );
	        return ruleList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdRulesList( Plugin plugin )
    {
        List<Integer> ruleList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            ruleList.add( daoUtil.getInt( 1 ) );
	        }
	
	        daoUtil.free( );
	        return ruleList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectRulesReferenceList( Plugin plugin )
    {
        ReferenceList ruleList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            ruleList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        daoUtil.free( );
	        return ruleList;
    	}
    }
    
    
    
}
