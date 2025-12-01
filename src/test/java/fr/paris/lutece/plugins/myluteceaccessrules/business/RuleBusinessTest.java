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
 *"
 * License 1.0
 */

package fr.paris.lutece.plugins.myluteceaccessrules.business;

import fr.paris.lutece.test.LuteceTestCase;
import org.junit.jupiter.api.Test;


/**
 * This is the business class test for the object Rule
 */
public class RuleBusinessTest extends LuteceTestCase
{
    private static final String TITLE1 = "Title1";
    private static final String TITLE2 = "Title2";
    private static final String DESCRIPTION1 = "Description1";
    private static final String DESCRIPTION2 = "Description2";
	private static final boolean ENABLE1 = true;
    private static final boolean ENABLE2 = false;
	private static final boolean EXTERNAL1 = true;
    private static final boolean EXTERNAL2 = false;
    private static final String MESSAGETODISPLAY1 = "Messagetodisplay1";
    private static final String MESSAGETODISPLAY2 = "Messagetodisplay2";
    private static final String REDIRECTURL1 = "Redirecturl1";
    private static final String REDIRECTURL2 = "Redirecturl2";

	/**
	* test Rule
	*/
    @Test
    public void testBusiness(  )
    {
        // Initialize an object
        Rule rule = new Rule();
        rule.setTitle( TITLE1 );
        rule.setDescription( DESCRIPTION1 );
        rule.setEnable( ENABLE1 );
        rule.setExternal( EXTERNAL1 );
        rule.setMessagetodisplay( MESSAGETODISPLAY1 );
        rule.setRedirecturl( REDIRECTURL1 );

        // Create test
        RuleHome.create( rule );
        Rule ruleStored = RuleHome.findByPrimaryKey( rule.getId( ) );
        assertEquals( ruleStored.getTitle() , rule.getTitle( ) );
        assertEquals( ruleStored.getDescription() , rule.getDescription( ) );
        assertEquals( ruleStored.getEnable() , rule.getEnable( ) );
        assertEquals( ruleStored.isExternal() , rule.isExternal( ) );
        assertEquals( ruleStored.getMessagetodisplay() , rule.getMessagetodisplay( ) );
        assertEquals( ruleStored.getRedirecturl() , rule.getRedirecturl( ) );

        // Update test
        rule.setTitle( TITLE2 );
        rule.setDescription( DESCRIPTION2 );
        rule.setEnable( ENABLE2 );
        rule.setExternal( EXTERNAL2 );
        rule.setMessagetodisplay( MESSAGETODISPLAY2 );
        rule.setRedirecturl( REDIRECTURL2 );
        RuleHome.update( rule );
        ruleStored = RuleHome.findByPrimaryKey( rule.getId( ) );
        assertEquals( ruleStored.getTitle() , rule.getTitle( ) );
        assertEquals( ruleStored.getDescription() , rule.getDescription( ) );
        assertEquals( ruleStored.getEnable() , rule.getEnable( ) );
        assertEquals( ruleStored.isExternal() , rule.isExternal( ) );
        assertEquals( ruleStored.getMessagetodisplay() , rule.getMessagetodisplay( ) );
        assertEquals( ruleStored.getRedirecturl() , rule.getRedirecturl( ) );

        // List test
        RuleHome.getRulesList( );

        // Delete test
        RuleHome.remove( rule.getId( ) );
        ruleStored = RuleHome.findByPrimaryKey( rule.getId( ) );
        assertNull( ruleStored );
        
    }
    
    
     

}