/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.mylutece.service.MyLuteceUserService;
import fr.paris.lutece.plugins.mylutece.service.MyluteceExternalRoleService;
import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.plugins.myluteceaccessrules.business.RuleHome;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.url.UrlItem;




/**
 * This class provides an Access Rules Service
 */
public final class AccessRulesService
{
    
   
	/** Parameter NB REDIRECT */
	public static final String PARAMETER_NB_REDIRECT = "accessrules_nb_redirect";
	
	/** Session attribute that stores the LuteceUser object attached to the session. */
	public static final String I18N_DEFAULT_MESSAGE_UNAUTHORIZED="myluteceaccessrules.message.unauthorized";
	
	/** The Constant I18N_DEFAULT_TITLE_MESSAGE_UNAUTHORIZED. */
	public static final String I18N_DEFAULT_TITLE_MESSAGE_UNAUTHORIZED="myluteceaccessrules.titlemessage.unauthorized";
	
	/** The Constant MARK_PUBLIC_LIST_URL. */
	public static final String MARK_PUBLIC_LIST_URL = "public_list_url";
    
    /** The Constant PUBLIC_URL_PREFIX. */
    public static final String PUBLIC_URL_PREFIX = "mylutece.security.public_url.";
    
    /** The Constant MARK_PORTAL_AUTHENTICATION_REQUIRED. */
    public static final String MARK_PORTAL_AUTHENTICATION_REQUIRED = "portal_authentication_required";
    
    
    /** Parameter NB REDIRECT */
	public static final String PROPERTY_MAX_REDIRECT= "myluteceaccessrules.maxRedirect";
	
    
    /** The Constant MARK_LOCALE. */
    private static final String MARK_LOCALE = "locale";
    
    /** The Constant MARK_WEBAPP_URL. */
    private static final String MARK_WEBAPP_URL = "webapp_url";
    
    /** The Constant CACHE_KEY_LIST_RULES_ENABLED. */
    private static final String CACHE_KEY_LIST_RULES_ENABLED = "list_access_rules_enabled";
    
    /** The Constant URL_INTERROGATIVE. */
    private static final String URL_INTERROGATIVE = "?";
    
    /** The Constant URL_AMPERSAND. */
    private static final String URL_AMPERSAND = "&";
    
    /** The Constant URL_EQUAL. */
    private static final String URL_EQUAL = "=";
    
    /** The Constant URL_STAR. */
    private static final String URL_STAR = "*";
    
    /** The Constant MARKER_BASE_URL. */
    private static final String MARKER_BASE_URL = "$baseurl";
    
    /** The Constant MARKER_BACK_URL. */
    private static final String MARKER_BACK_URL = "$backurl";
    
    
    
    
    
    
    
    /** The singleton. */
    private static AccessRulesService _singleton = null;
    
    /** The cache. */
    private  AccessRulesCacheService _cache;
	    

    /**
     * Private constructor.
     */
    private AccessRulesService(  )
    {
    }

    /**
     * Get the unique instance of the AccessRulesService
     *
     * @return The instance
     */
    public static AccessRulesService getInstance(  )
    {
       
    	if(_singleton==null)
    	{
    		
    		_singleton=new AccessRulesService(  );
    	    _singleton._cache=new AccessRulesCacheService(  );
        	_singleton._cache.enableCache(true);
    	
    	}
    	
    	
    	return _singleton;
    }

    
    
    /**
     * Checks if is access rules enabled.
     *
     * @return true, if is access rules enabled
     */
    public boolean isAccessRulesEnabled()
    {
    	return SecurityService.isAuthenticationEnable(  )  && getRulesEnabled().size()>0;
    	
    }
    
    
    /**
     * Gets the cache.
     *
     * @return the cache
     */
    public AccessRulesCacheService  getCache()
    {
    	
    	return _cache;
    }
    
    
    /**
     * Gets the list of  enabled rules 
     *
     * @return the list of enabled rules
     */
    public  List<Rule>  getRulesEnabled()
    {
    	
    	List<Rule> listRulesEnable = (List<Rule>) getCache().getFromCache( CACHE_KEY_LIST_RULES_ENABLED );
    	
        if ( listRulesEnable == null )
        {
            
        	listRulesEnable=RuleHome.getRulesList().stream().filter(x->x.getEnable()).collect(Collectors.toList());
        	getCache().putInCache( CACHE_KEY_LIST_RULES_ENABLED, listRulesEnable.stream().filter(Rule::getEnable).collect(Collectors.toList()) );

        }
        
        return listRulesEnable;

    }

    
   
    
    
     /**
      * Ge the  first rule triggered.
      *
      * @param request the request
      * @return the first Rule triggered 
      */
     public  Rule geFirstRuleTriggered(HttpServletRequest request)
     {
    	 return getRulesEnabled().stream().filter(x-> isRuleMatchingRequest(x, request) && isUserTriggerRule(x, request)).findFirst().orElse(null);
    	 
     }
    
    
    
    /**
     * Checks if this rule  match the request.
     *
     * @param rule the rule
     * @param request the request
     * @return if this rule  match the request
     */
    public  boolean isRuleMatchingRequest(Rule rule,HttpServletRequest request)
    {
       return (rule.getProtectedUrls() ==null || rule.getProtectedUrls().isEmpty()||rule.getProtectedUrls().stream().anyMatch(x->matchUrl(request, x.getName())) )&& (rule.getPublicUrls()==null || rule.getPublicUrls().isEmpty()|| !rule.getPublicUrls().stream().anyMatch(x->matchUrl(request, x.getName())));
    
    }
    
    /**
     * Checks if the authenticated user trigger the rule
     *
     * @param rule the rule
     * @param request the request
     * @return true, if is user trigger rule
     */
    private boolean isUserTriggerRule(Rule rule,HttpServletRequest request)
    {
    	
    	return rule.getRoles()!=null?rule.getRoles().stream().anyMatch(x-> !SecurityService.getInstance().isUserInRole(request, x.getName())):false;
    	
    }
    
    
    
    
    
    
    
    /**
     * method to test if the URL matches the pattern.
     *
     * @param request the request
     * @param strUrlPatern the pattern
     * @return true if the URL matches the pattern
     */
    public boolean matchUrl( HttpServletRequest request, String strUrlPatern )
    {
        boolean bMatch = false;

        if ( strUrlPatern != null )
        {
            UrlItem url = new UrlItem( getResquestedUrl( request ) );

            if ( strUrlPatern.contains( URL_INTERROGATIVE ) )
            {
                for ( String strParamPatternValue : strUrlPatern.substring( strUrlPatern.indexOf( URL_INTERROGATIVE ) +
                        1 ).split( URL_AMPERSAND ) )
                {
                    String[] arrayPatternParamValue = strParamPatternValue.split( URL_EQUAL );

                    if ( ( arrayPatternParamValue != null ) &&
                            ( request.getParameter( arrayPatternParamValue[0] ) != null ) )
                    {
                        url.addParameter( arrayPatternParamValue[0], request.getParameter( arrayPatternParamValue[0] ) );
                    }
                }
            }

            if ( strUrlPatern.contains( URL_STAR ) )
            {
                String strUrlPaternLeftEnd = strUrlPatern.substring( 0, strUrlPatern.indexOf( URL_STAR ) );
                String strAbsoluteUrlPattern = getAbsoluteUrl( request, strUrlPaternLeftEnd );
                bMatch = url.getUrl(  ).startsWith( strAbsoluteUrlPattern );
            }
            else
            {
                String strAbsoluteUrlPattern = getAbsoluteUrl( request, strUrlPatern );
                bMatch = url.getUrl(  ).equals( strAbsoluteUrlPattern );
            }
        }

        return bMatch;
    }

    /**
     * Returns the absolute url corresponding to the given one, if the later was
     * found to be relative. An url starting with "http://" is absolute. A
     * relative url should be given relatively to the webapp root.
     *
     * @param request
     *            the http request (provides the base path if needed)
     * @param strUrl
     *            the url to transform
     * @return the corresonding absolute url
     *
     * */
    private String getAbsoluteUrl( HttpServletRequest request, String strUrl )
    {
        if ( ( strUrl != null ) && !strUrl.startsWith( "http://" ) && !strUrl.startsWith( "https://" ) )
        {
            return AppPathService.getBaseUrl( request ) + strUrl;
        }
        else
        {
            return strUrl;
        }
    }

    /**
     * Return the absolute representation of the requested url.
     *
     * @param request            the http request (provides the base path if needed)
     * @return the requested url has a string
     */
    private String getResquestedUrl( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + request.getServletPath(  ).substring( 1 );
    }
    
    
    /**
     * Builds the redirect url depending to the rule specification
     *
     * @param rule the rule
     * @param request the request
     * @param nbRedirect 
     * @return the redirect url
     */
	public String buildRedirectUrl(Rule rule, HttpServletRequest request,int nbRedirect) {

		String strRedirectUrl = getAbsoluteUrl( request,rule.getRedirecturl());
		
		String strBackUrl = rule.getBackUrl();
		if (StringUtils.isEmpty(strBackUrl)) {
			strBackUrl = getDefaultServiceBackUrl(request,nbRedirect);

		} else if (strBackUrl.contains(MARKER_BASE_URL)) {

			String strBaseUrl = AppPathService.getBaseUrl(request);

			if (strBaseUrl.endsWith("/") && strBackUrl.startsWith("/")) {
				strBackUrl = strBackUrl.substring(1);

			}
			if (!strBaseUrl.endsWith("/") && !strBackUrl.startsWith("/")) {
				strBackUrl = strBackUrl + "/";

			}

			strBackUrl = strBackUrl.replaceAll("\\" + MARKER_BASE_URL, strBaseUrl);
		}
		try {
			strBackUrl = URLEncoder.encode(strBackUrl, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {

			AppLogService.error("An error appear during Url encode the back url {}", strBackUrl, e);
		}
		if(rule.isEncodeBackUrl())
		{
			strBackUrl=encodeUrl(strBackUrl);
			
		}
		strRedirectUrl = strRedirectUrl.replaceAll("\\" + MARKER_BACK_URL, strBackUrl);

		return strRedirectUrl;

	}
    
    
    private String getDefaultServiceBackUrl( HttpServletRequest request,int nbRedirect)
    {
    	
    	
          String strDefaultBackUrl=""; 
    	   String strNextUrl = request.getServletPath();
             
            UrlItem url = new UrlItem( strNextUrl );
            Enumeration<String> enumParams = request.getParameterNames( );

            
            try
            {
	            while ( enumParams.hasMoreElements( ) )
	            {
	                String strParamName = enumParams.nextElement( );
	
	               
	                    url.addParameter( strParamName, URLEncoder.encode( request.getParameter( strParamName ), "UTF-8" ) );
	                }
	                url.addParameter(AccessRulesService.PARAMETER_NB_REDIRECT, nbRedirect+1);
	            	strDefaultBackUrl=url.getUrl( ) ;
	            	if(strDefaultBackUrl.startsWith("/"))
	            	{
	            		strDefaultBackUrl=strDefaultBackUrl.substring(1);
	            	}
	            	
	            	strDefaultBackUrl= AppPathService.getAbsoluteUrl(request, strDefaultBackUrl);
	            	
	          }
            
            catch( UnsupportedEncodingException ex )
            {
                AppLogService.error( "Redirection error while encoding URL  in Access Rule Service: {}", ex.getMessage( ), ex );
                strDefaultBackUrl=AppPathService.getBaseUrl(request);
            }

          return strDefaultBackUrl;
             
            
    	
    }
    
    /**
     * encode url in base64
     * @param strUrl the url to encode
     * @return an url encode in base64
     */
    private   String  encodeUrl(String strUrl)
    {
    	return !StringUtils.isEmpty(strUrl) ?  new String(Base64.getUrlEncoder().encode(strUrl.getBytes( StandardCharsets.UTF_8 ))):"";
    	
    }
    
    
    
}
