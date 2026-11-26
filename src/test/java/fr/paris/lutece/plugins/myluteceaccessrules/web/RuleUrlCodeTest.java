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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.inject.Inject;

/**
 * Codes of the rows of the protected and public URLs of a rule in the creation form
 */
public class RuleUrlCodeTest extends LuteceTestCase
{
    private static final Pattern PATTERN_CODELIST = Pattern
            .compile( "name=\"protected_url_codelist\"[^>]*value=\"([^\"]*)\"|value=\"([^\"]*)\"[^>]*name=\"protected_url_codelist\"" );

    private @Inject RuleJspBean _instance;

    /**
     * A protected URL added after the removal of another row gets a code that no other row uses.
     *
     * @throws Exception
     *             if the controller fails
     */
    @Test
    public void testAddedRowAfterRemovalHasUniqueCode( ) throws Exception
    {
        // rows a (code _0_01) and b (code _0_11) added, then a removed: only b is left
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "action", "addProtectedUrlCreate" );
        request.addParameter( "title", "codes" );
        request.addParameter( "protected_url_codelist", "rule_protected_urls_0_11" );
        request.addParameter( "rule_protected_urls_0_11", "jsp/site/Portal.jsp?page=b" );
        request.addParameter( "protected_url", "jsp/site/Portal.jsp?page=c" );
        request.setMethod( "POST" );
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );
        AdminAuthenticationService.getInstance( ).registerUser( request, adminUser );

        String strHtml = _instance.processController( request, new MockHttpServletResponse( ) );

        Matcher matcher = PATTERN_CODELIST.matcher( strHtml );
        assertTrue( matcher.find( ), "the list of the codes is in the form" );
        String strCodes = matcher.group( 1 ) != null ? matcher.group( 1 ) : matcher.group( 2 );
        List<String> listCodes = Arrays.asList( strCodes.split( "," ) );
        assertEquals( 2, listCodes.size( ) );
        assertEquals( 2, new HashSet<>( listCodes ).size( ), "the codes are unique: " + strCodes );
        assertTrue( strHtml.contains( "jsp/site/Portal.jsp?page=b" ) && strHtml.contains( "jsp/site/Portal.jsp?page=c" ), "both URLs are kept" );
    }
}
