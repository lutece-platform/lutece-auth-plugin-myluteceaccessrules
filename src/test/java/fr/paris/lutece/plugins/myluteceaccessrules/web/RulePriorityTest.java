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
package fr.paris.lutece.plugins.myluteceaccessrules.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.plugins.myluteceaccessrules.business.RuleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.inject.Inject;

/**
 * Priorities of the access rules changed from the back office
 */
public class RulePriorityTest extends LuteceTestCase
{
    private @Inject RuleJspBean _instance;
    private @Inject ISecurityTokenService _securityTokenService;
    private final List<Integer> _listCreated = new ArrayList<>( );

    @AfterEach
    public void cleanUp( )
    {
        _listCreated.forEach( RuleHome::remove );
    }

    private Rule createRule( String strTitle )
    {
        Rule rule = new Rule( );
        rule.setTitle( strTitle );
        RuleHome.create( rule );
        _listCreated.add( rule.getId( ) );
        return rule;
    }

    private MockHttpServletRequest adminRequest( String strAction, String strTokenAction ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "action", strAction );
        request.addParameter( "token", _securityTokenService.getToken( request, strTokenAction ) );
        request.setMethod( "POST" );
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );
        AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
        return request;
    }

    private List<String> order( List<String> listTitles )
    {
        List<String> list = new ArrayList<>( );
        RuleHome.getRulesList( ).stream( ).filter( r -> listTitles.contains( r.getTitle( ) ) ).forEach( r -> list.add( r.getTitle( ) ) );
        return list;
    }

    /**
     * Decreasing the priority of a rule moves it one step down only.
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testDecreasePriorityMovesOneStepDown( ) throws Exception
    {
        List<String> listTitles = Arrays.asList( "prio-A", "prio-B", "prio-C" );
        Rule ruleA = createRule( "prio-A" );
        createRule( "prio-B" );
        createRule( "prio-C" );
        assertEquals( listTitles, order( listTitles ) );

        MockHttpServletRequest request = adminRequest( "decreasePriority", "increasePriority" );
        request.addParameter( "id", String.valueOf( ruleA.getId( ) ) );
        _instance.processController( request, new MockHttpServletResponse( ) );

        assertEquals( Arrays.asList( "prio-B", "prio-A", "prio-C" ), order( listTitles ) );
    }
}
