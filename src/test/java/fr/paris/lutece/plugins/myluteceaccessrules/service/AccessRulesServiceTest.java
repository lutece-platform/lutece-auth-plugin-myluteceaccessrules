package fr.paris.lutece.plugins.myluteceaccessrules.service;

import java.util.ArrayList;

import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.plugins.myluteceaccessrules.business.RuleHome;
import fr.paris.lutece.portal.service.init.LuteceInitException;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.ReflectionTestUtils;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.util.ReferenceList;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class AccessRulesServiceTest extends LuteceTestCase{

	@Inject
	private AccessRulesService _accessRulesService;

	  private static final String NAME = "NAME";
	  private static final String ROLE1 = "ROLE1";
	  private static final String ROLE2 = "ROLE2";
	  private static final String ROLE3 = "ROLE3";
	  
	  private static final String EMAIL = "EMAIL";
	  
	  private static final String ERROR_MESSAGE_INTERNAL_RULE="error internal rule detected";
	
	@Test
	public void testProtectedUrl() throws LuteceInitException
	{
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		LuteceUser user=initTestUserWithAuthMod(request);
		//Add roles 
		ArrayList<String> listRoles = new ArrayList( );
	    listRoles.add( ROLE1 );
	     listRoles.add( ROLE2 );
	     user.setRoles( listRoles );
		
		insertTestRule();
		
		
		
		
		request.setRequestURI("jsp/site/Portal.jsp?page=test");
		request.setServletPath("/jsp/site/Portal.jsp?page=test");
		
	
		Rule rule=_accessRulesService.getFirstRuleTriggered(request);
		
		
	
		assertTrue(rule==null);
		
	}
	
	@Test
	public void testPublicUrl() throws LuteceInitException
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		initTestUserWithAuthMod(request);
		insertTestRule();
		
		
		request.setRequestURI("jsp/site/Portal.jsp?page=test&view=all");
		request.setServletPath("/jsp/site/Portal.jsp?page=test&view=all");
		
	
		Rule rule=_accessRulesService.getFirstRuleTriggered(request);
		assertTrue(rule==null);
		
	}
	
	
	@Test
	public void testUserNotAuthorized() throws LuteceInitException
	{
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		LuteceUser user=initTestUserWithAuthMod(request);
		//Add roles 
		ArrayList<String> listRoles = new ArrayList( );
	    listRoles.add( ROLE1 );

		user.setRoles( listRoles );
		
		insertTestRule();

		request.setRequestURI("jsp/site/Portal.jsp?page=test");
		request.setServletPath("/jsp/site/Portal.jsp?page=test");


		Rule rule=_accessRulesService.getFirstRuleTriggered(request);
		assertTrue(rule!=null);
	}
	
	
	
  void insertTestRule()
  {
	  Rule internalRule=new Rule();
	  internalRule.setTitle("Internal Rule");
	  internalRule.setEnable(true);
	  internalRule.setMessagetodisplay(ERROR_MESSAGE_INTERNAL_RULE);
	  internalRule .setExternal(false);
	  ReferenceList refListProtectedUrls= new ReferenceList();
	  refListProtectedUrls.addItem("test","jsp/site/Portal.jsp?page=test");
	  internalRule.setProtectedUrls(refListProtectedUrls);
	  ReferenceList refListPublicUrls= new ReferenceList();
	  refListPublicUrls.addItem("test","jsp/site/Portal.jsp?page=test&view=all");
	  
	  ReferenceList listRoles = new ReferenceList();
      listRoles.addItem(ROLE1, ROLE1);
      listRoles.addItem(ROLE2, ROLE2);
      
      
      internalRule.setRoles(listRoles);
	  
	  RuleHome.create(internalRule);
	  
  }

  private void enableAuthentication( ) throws LuteceInitException
  {
	  boolean status = SecurityService.isAuthenticationEnable( );
	  if ( !status )
	  {
		  System.setProperty( "mylutece.authentication.enable", "true" );
		  System.setProperty( "mylutece.authentication.class", MokeLuteceAuthentication.class.getName( ) );
		  SecurityService.init( );
	  }
  }
  
  LuteceUser initTestUserWithAuthMod(HttpServletRequest request) throws LuteceInitException
  {
	  enableAuthentication( );

	  SecurityService securityService = SecurityService.getInstance( );
      MokeLuteceAuthentication mockLuteceAuthentication = new MokeLuteceAuthentication( );
      ReflectionTestUtils.setField( securityService, "_authenticationService", mockLuteceAuthentication );
      
      MokeLuteceUser user = new MokeLuteceUser( NAME, mockLuteceAuthentication );
      user.setName( NAME );
    
      user.setUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL, EMAIL );
      
      securityService.getInstance().registerUser(request, user);
	  
      
      return user;
  }

  @AfterEach
  public void tearDown( ) throws Exception
  {
	  super.tearDown();
	  RuleHome.getRulesList().stream().forEach(x->RuleHome.remove(x.getId()));
	  
  }
  
  
  
	
	
	

}
