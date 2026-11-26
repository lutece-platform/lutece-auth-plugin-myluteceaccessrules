/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.myluteceaccessrules.service.AccessRulesService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import jakarta.inject.Inject;

/**
 * Readers of the access rules never see a rule without its URLs while the rules are saved
 */
public class RuleConcurrentUpdateTest extends LuteceTestCase
{
    private static final String PROTECTED_URL = "jsp/site/Portal.jsp?e2e_ar=concurrent";
    private static final int WRITES = 300;

    private @Inject AccessRulesService _accessRulesService;
    private final List<Integer> _listCreated = new ArrayList<>( );

    @AfterEach
    public void cleanUp( )
    {
        _listCreated.forEach( RuleHome::remove );
    }

    private static ReferenceList listOf( String strName )
    {
        ReferenceList list = new ReferenceList( );
        ReferenceItem item = new ReferenceItem( );
        item.setCode( strName );
        item.setName( strName );
        list.add( item );
        return list;
    }

    private Rule createRule( String strTitle )
    {
        Rule rule = new Rule( );
        rule.setTitle( strTitle );
        rule.setEnable( true );
        rule.setProtectedUrls( listOf( PROTECTED_URL ) );
        rule.setRoles( listOf( "concurrent_role" ) );
        RuleHome.create( rule );
        _listCreated.add( rule.getId( ) );
        return rule;
    }

    private static boolean hasNoProtectedUrl( List<Rule> listRules, int nIdRule )
    {
        return listRules.stream( ).filter( r -> r.getId( ) == nIdRule )
                .anyMatch( r -> r.getProtectedUrls( ) == null || r.getProtectedUrls( ).isEmpty( ) );
    }

    /**
     * Saving the priorities of the rules (what « increase / decrease priority » and the creation of a rule do) while front office requests read
     * the rules: no reader sees the rule without its protected URLs (a rule without protected URL applies to every URL), and the cached list
     * is right once the writes are over.
     *
     * @throws Exception
     *             if a thread fails
     */
    @Test
    public void testReadersNeverSeeARuleWithoutItsUrls( ) throws Exception
    {
        Rule rule = createRule( "concurrent-A" );
        createRule( "concurrent-B" );

        AtomicBoolean bWriting = new AtomicBoolean( true );
        AtomicInteger nEmptyReads = new AtomicInteger( );
        AtomicInteger nReads = new AtomicInteger( );

        Thread reader = new Thread( ( ) -> {
            while ( bWriting.get( ) )
            {
                nReads.incrementAndGet( );
                if ( hasNoProtectedUrl( RuleHome.getRulesList( ), rule.getId( ) )
                        || hasNoProtectedUrl( _accessRulesService.getRulesEnabled( ), rule.getId( ) ) )
                {
                    nEmptyReads.incrementAndGet( );
                }
            }
        } );
        reader.start( );
        try
        {
            for ( int i = 0; i < WRITES; i++ )
            {
                RuleHome.updateRulesPriorities( RuleHome.getRulesList( ) );
            }
        }
        finally
        {
            bWriting.set( false );
            reader.join( );
        }

        assertTrue( nReads.get( ) > 0, "the reader ran" );
        assertEquals( 0, nEmptyReads.get( ), "reads that saw the rule without its protected URL, out of " + nReads.get( ) );
        assertFalse( hasNoProtectedUrl( _accessRulesService.getRulesEnabled( ), rule.getId( ) ), "cached rules after the writes" );
        assertEquals( PROTECTED_URL, RuleHome.findByPrimaryKey( rule.getId( ) ).getProtectedUrls( ).get( 0 ).getName( ) );
    }
}
