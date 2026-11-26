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
import fr.paris.lutece.portal.business.role.Role;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import fr.paris.lutece.util.ReferenceList;
import jakarta.inject.Inject;

/**
 * Roles of an access rule outside the workgroups of the administrator who modifies it
 */
public class RuleRolesWorkgroupTest extends LuteceTestCase
{
    private static final String ROLE_HIDDEN = "rule_role_hidden_wg";

    private @Inject RuleJspBean _instance;
    private @Inject ISecurityTokenService _securityTokenService;

    /**
     * Modifying a rule keeps its roles that the administrator does not see.
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testModifyRuleKeepsRolesOutsideWorkgroups( ) throws Exception
    {
        Role role = new Role( );
        role.setRole( ROLE_HIDDEN );
        role.setRoleDescription( "hidden role" );
        role.setWorkgroup( "rule_wg_not_member" );
        RoleHome.create( role );
        Rule rule = new Rule( );
        rule.setTitle( "roles-wg" );
        ReferenceList listRoles = new ReferenceList( );
        listRoles.addItem( ROLE_HIDDEN, ROLE_HIDDEN );
        rule.setRoles( listRoles );
        RuleHome.create( rule );

        try
        {
            MockHttpServletRequest request = new MockHttpServletRequest( );
            request.addParameter( "action", "modifyRule" );
            request.addParameter( "token", _securityTokenService.getToken( request, "modifyRule" ) );
            request.addParameter( "id", String.valueOf( rule.getId( ) ) );
            request.addParameter( "title", "roles-wg" );
            request.addParameter( "description", "changed" );
            request.setMethod( "POST" );
            AdminUser adminUser = new AdminUser( );
            adminUser.setAccessCode( "admin" );
            AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );

            _instance.processController( request, new MockHttpServletResponse( ) );

            Rule ruleSaved = RuleHome.findByPrimaryKey( rule.getId( ) );
            assertEquals( "changed", ruleSaved.getDescription( ) );
            assertTrue( ruleSaved.getRoles( ) != null && ruleSaved.getRoles( ).stream( ).anyMatch( x -> ROLE_HIDDEN.equals( x.getName( ) ) ),
                    "the role the administrator does not see is kept" );
        }
        finally
        {
            RuleHome.remove( rule.getId( ) );
            RoleHome.remove( ROLE_HIDDEN );
        }
    }
}
