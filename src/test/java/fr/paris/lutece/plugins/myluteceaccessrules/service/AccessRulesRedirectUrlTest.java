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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * Back URL given to the external page of an access rule
 */
public class AccessRulesRedirectUrlTest extends LuteceTestCase
{
    private static final String REDIRECT_URL = "https://example.org/login?back=$backurl";

    private @Inject AccessRulesService _accessRulesService;

    private String getBackUrl( Rule rule, MockHttpServletRequest request, int nbRedirect )
    {
        String strUrl = _accessRulesService.buildRedirectUrl( rule, request, nbRedirect );
        return URLDecoder.decode( strUrl.substring( strUrl.indexOf( "back=" ) + "back=".length( ) ), StandardCharsets.UTF_8 );
    }

    /**
     * The default back URL carries the redirect counter once, with its new value.
     */
    @Test
    public void testDefaultBackUrlCarriesTheCounterOnce( )
    {
        Rule rule = new Rule( );
        rule.setRedirecturl( REDIRECT_URL );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setServletPath( "/jsp/site/Portal.jsp" );
        request.addParameter( "page", "counter" );
        request.addParameter( AccessRulesService.PARAMETER_NB_REDIRECT, "1" );

        String strBackUrl = getBackUrl( rule, request, 1 );

        Matcher matcher = Pattern.compile( AccessRulesService.PARAMETER_NB_REDIRECT + "=" ).matcher( strBackUrl );
        int nCount = 0;
        while ( matcher.find( ) )
        {
            nCount++;
        }
        assertEquals( 1, nCount, "one counter in " + strBackUrl );
        assertTrue( strBackUrl.contains( AccessRulesService.PARAMETER_NB_REDIRECT + "=2" ), strBackUrl );
        assertTrue( strBackUrl.contains( "page=counter" ), strBackUrl );
    }
}
