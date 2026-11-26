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

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.plugins.myluteceaccessrules.business.RuleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.inject.Inject;

/**
 * Removal of an access rule: the security token is required
 */
public class RuleRemoveTokenTest extends LuteceTestCase
{
    private @Inject RuleJspBean _instance;
    private @Inject ISecurityTokenService _securityTokenService;

    private MockHttpServletRequest adminRequest( Rule rule ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "action", "removeRule" );
        request.addParameter( "id", String.valueOf( rule.getId( ) ) );
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );
        AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );
        return request;
    }

    /**
     * A removal request without token is refused and the rule is kept.
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testRemoveRuleWithoutTokenIsRefused( ) throws Exception
    {
        Rule rule = new Rule( );
        rule.setTitle( "remove-no-token" );
        RuleHome.create( rule );

        try
        {
            MockHttpServletRequest request = adminRequest( rule );
            request.setMethod( "GET" );

            assertThrows( AccessDeniedException.class, ( ) -> _instance.processController( request, new MockHttpServletResponse( ) ) );
            assertNotNull( RuleHome.findByPrimaryKey( rule.getId( ) ), "the rule is kept" );
        }
        finally
        {
            RuleHome.remove( rule.getId( ) );
        }
    }

    /**
     * A removal request with the token removes the rule.
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testRemoveRuleWithToken( ) throws Exception
    {
        Rule rule = new Rule( );
        rule.setTitle( "remove-token" );
        RuleHome.create( rule );

        MockHttpServletRequest request = adminRequest( rule );
        request.addParameter( "token", _securityTokenService.getToken( request, "removeRule" ) );
        request.setMethod( "POST" );
        _instance.processController( request, new MockHttpServletResponse( ) );

        assertNull( RuleHome.findByPrimaryKey( rule.getId( ) ), "the rule is removed" );
    }
}
