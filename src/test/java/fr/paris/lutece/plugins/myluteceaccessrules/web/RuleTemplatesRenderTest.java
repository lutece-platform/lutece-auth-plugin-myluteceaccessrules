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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.plugins.myluteceaccessrules.business.RuleHome;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.util.ReferenceList;
import jakarta.inject.Inject;

/**
 * Renders the rule templates with the core macros and the HTML auto-escaping (LUT-33153): the forms render with the
 * params map of the external switch, and the values of a rule are written as text.
 */
public class RuleTemplatesRenderTest extends LuteceTestCase
{
    private static final String MARKUP = "<b>rule</b> \"quoted\"";
    private static final String MARKUP_ESCAPED = "&lt;b&gt;rule&lt;/b&gt; &quot;quoted&quot;";
    private static final String URL_MARKUP = "/jsp/site/Portal.jsp?page=<i>x</i>";
    private static final String URL_ESCAPED = "/jsp/site/Portal.jsp?page=&lt;i&gt;x&lt;/i&gt;";

    private @Inject RuleJspBean _instance;
    private Rule _rule;

    /**
     * Removes the rule created by a test.
     */
    @AfterEach
    public void removeRule( )
    {
        if ( _rule != null && _rule.getId( ) > 0 )
        {
            RuleHome.remove( _rule.getId( ) );
        }
    }

    /**
     * Builds a rule whose values hold markup.
     *
     * @return the rule
     */
    private static Rule ruleWithMarkup( )
    {
        Rule rule = new Rule( );
        rule.setTitle( MARKUP );
        rule.setDescription( MARKUP );
        rule.setMessagetodisplay( MARKUP );
        rule.setRedirecturl( URL_MARKUP );
        rule.setBackUrl( URL_MARKUP );
        rule.setPriorityOrder( 1 );
        ReferenceList listProtectedUrls = new ReferenceList( );
        listProtectedUrls.addItem( "protected_url_0", URL_MARKUP );
        rule.setProtectedUrls( listProtectedUrls );
        ReferenceList listPublicUrls = new ReferenceList( );
        listPublicUrls.addItem( "public_url_0", URL_MARKUP );
        rule.setPublicUrls( listPublicUrls );
        rule.setRoles( new ReferenceList( ) );
        return rule;
    }

    /**
     * Asserts that the values of the rule are written as text.
     *
     * @param strHtml
     *            the rendered page
     */
    private static void assertWrittenAsText( String strHtml )
    {
        assertNotNull( strHtml );
        assertFalse( strHtml.contains( "<b>rule</b>" ), strHtml );
        assertFalse( strHtml.contains( "<i>x</i>" ), strHtml );
        assertTrue( strHtml.contains( MARKUP_ESCAPED ), strHtml );
        assertTrue( strHtml.contains( URL_ESCAPED ), strHtml );
    }

    /**
     * The creation form renders, with the params map of the external switch, and writes the values as text.
     */
    @Test
    public void testCreateRuleForm( )
    {
        String strHtml = _instance.getCreateRule( new MockHttpServletRequest( ), ruleWithMarkup( ) );

        assertWrittenAsText( strHtml );
        assertTrue( strHtml.contains( "data-toggle=\"collapse\"" ), strHtml );
    }

    /**
     * The buttons that add or remove an URL of the creation form are not blocked by the validation of the required
     * fields.
     */
    @Test
    public void testCreateRuleUrlButtonsSkipValidation( )
    {
        String strHtml = _instance.getCreateRule( new MockHttpServletRequest( ), ruleWithMarkup( ) );

        for ( String strAction : new String [ ] {
                "action_addProtectedUrlCreate", "action_removeProtectedUrlCreate", "action_addPublicUrlCreate", "action_removePublicUrlCreate"
        } )
        {
            Matcher matcher = Pattern.compile( "<button[^>]*name=\"" + strAction + "\"[^>]*>" ).matcher( strHtml );
            assertTrue( matcher.find( ), strAction );
            assertTrue( matcher.group( ).contains( "formnovalidate" ), matcher.group( ) );
        }
    }

    /**
     * The modification form renders, with the params map of the external switch, and writes the values as text.
     */
    @Test
    public void testModifyRuleForm( )
    {
        _rule = RuleHome.create( ruleWithMarkup( ) );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id", String.valueOf( _rule.getId( ) ) );

        String strHtml = _instance.getModifyRule( request );

        assertWrittenAsText( strHtml );
        assertTrue( strHtml.contains( "data-toggle=\"collapse\"" ), strHtml );
    }

    /**
     * The list of the rules writes the title and the description as text.
     */
    @Test
    public void testManageRules( )
    {
        _rule = RuleHome.create( ruleWithMarkup( ) );

        String strHtml = _instance.getManageRules( new MockHttpServletRequest( ) );

        assertNotNull( strHtml );
        assertFalse( strHtml.contains( "<b>rule</b>" ), strHtml );
        assertTrue( strHtml.contains( MARKUP_ESCAPED ), strHtml );
    }
}
