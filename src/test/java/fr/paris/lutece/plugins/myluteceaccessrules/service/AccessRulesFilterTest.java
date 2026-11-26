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
package fr.paris.lutece.plugins.myluteceaccessrules.service;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.myluteceaccessrules.web.AccessRulesFilter;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Access rules filter on a front office request (reuses the user and rule set-up of AccessRulesServiceTest)
 */
public class AccessRulesFilterTest extends AccessRulesServiceTest
{
    /**
     * A redirect counter that is not a number does not break the filter: the rule still refuses the page.
     *
     * @throws Exception
     *             if the filter fails
     */
    @Test
    public void testNonNumericRedirectCounter( ) throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LuteceUser user = initTestUserWithAuthMod( request );
        ArrayList<String> listRoles = new ArrayList<>( );
        listRoles.add( "ROLE1" );
        user.setRoles( listRoles );
        insertTestRule( );
        request.setRequestURI( "jsp/site/Portal.jsp?page=test" );
        request.setServletPath( "/jsp/site/Portal.jsp?page=test" );
        request.addParameter( AccessRulesService.PARAMETER_NB_REDIRECT, "abc" );
        assertNotNull( CDI.current( ).select( AccessRulesService.class ).get( ).getFirstRuleTriggered( request ), "the rule applies" );

        AccessRulesFilter filter = new AccessRulesFilter( );
        filter.init( null );
        boolean [ ] bChained = {
                false
        };
        filter.doFilter( request, new MockHttpServletResponse( ), ( req, resp ) -> bChained [0] = true );

        assertFalse( bChained [0], "the page is refused" );
    }
}
