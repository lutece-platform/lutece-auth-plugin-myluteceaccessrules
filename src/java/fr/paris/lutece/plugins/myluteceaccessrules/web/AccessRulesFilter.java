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
package fr.paris.lutece.plugins.myluteceaccessrules.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.mylutece.service.security.AuthenticationFilterService;
import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.plugins.myluteceaccessrules.service.AccessRulesService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * Filter to prevent unauthenticated access to site if site authentication is enabled
 */
public class AccessRulesFilter implements Filter
{
   
   
    /**
     * {@inheritDoc}
     */
    @Override
    public void init( FilterConfig config ) throws ServletException
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy(  )
    {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        		
        if ( AccessRulesService.getInstance().isAccessRulesEnabled()&&  SecurityService.getInstance(  ).getRegisteredUser( req ) !=null && isPrivateUrl( req ) && AccessRulesService.getInstance().geFirstRuleTriggered(req)!=null )
        {
           
        	Rule rule=AccessRulesService.getInstance().geFirstRuleTriggered(req);
        	//Logout User if the rule is not verified
        	SecurityService.getInstance().logoutUser(req);
        	 int nbRedirect=!StringUtils.isEmpty(request.getParameter( AccessRulesService.PARAMETER_NB_REDIRECT)) ? Integer.parseInt(req.getParameter( AccessRulesService.PARAMETER_NB_REDIRECT)):0;      
        	      
        	if(nbRedirect < AppPropertiesService.getPropertyInt(AccessRulesService.PROPERTY_MAX_REDIRECT, 10)  && rule.isExternal() && StringUtils.isNotEmpty(rule.getRedirecturl()))
        	{
        		resp.sendRedirect(  AccessRulesService.getInstance().buildRedirectUrl(rule, req,nbRedirect));
        	}
        	else
        	{
                    try
                    {
                    	if(StringUtils.isEmpty(rule.getMessagetodisplay()))
                    	{
                    		SiteMessageService.setMessage( req, AccessRulesService.I18N_DEFAULT_MESSAGE_UNAUTHORIZED, null,
                    				AccessRulesService.I18N_DEFAULT_TITLE_MESSAGE_UNAUTHORIZED, null, "", SiteMessage.TYPE_STOP );
                    	}
                    	else
                    	{
                    		SiteMessageService.setCustomMessage(req, I18nService.getLocalizedString(AccessRulesService.I18N_DEFAULT_TITLE_MESSAGE_UNAUTHORIZED, request.getLocale()),rule.getMessagetodisplay() , SiteMessage.TYPE_STOP );
                    		
                    	}
                    }
                    catch ( SiteMessageException lme )
                    {
                        resp.sendRedirect( AppPathService.getSiteMessageUrl( req ) );
                    }
                }
               

                return;
            
        }

        chain.doFilter( request, response );
    }

    /**
     * Check wether a given url is to be considered as private (ie that needs a
     * successful authentication to be accessed) or public (ie that can be
     * access without being authenticated)
     *
     * @param request
     *            the http request
     * @return true if the url needs to be authenticated, false otherwise
     *
     * */
    private boolean isPrivateUrl( HttpServletRequest request )
    {
        return !( ( isInSiteMessageUrl( request ) || ( isInPublicUrlList( request ) ) ) );
    }

   
    /**
     * Checks if the requested is the url of site message
     * @param request The HTTP request
     * @return true if the requested is the url of site message
     */
    private boolean isInSiteMessageUrl( HttpServletRequest request )
    {
        return AccessRulesService.getInstance().matchUrl( request, AppPathService.getSiteMessageUrl( request ) );
    }

    /**
     * Checks if the requested is in the list of urls defined in  Security service
     * that shouldn't be protected
     *
     * @param request
     *            the http request
    
     * @return true if the url is in the list, false otherwise
     *
     * */
    private boolean isInPublicUrlList( HttpServletRequest request )
    {
        
    	if( SecurityService.getInstance(  ).isPortalAuthenticationRequired(  ))
    	{
	    	for ( String strPubliUrl : AuthenticationFilterService.getInstance(  ).getPublicUrls(  ) )
	        {
	            if (AccessRulesService.getInstance(). matchUrl( request, strPubliUrl ) )
	            {
	                return true;
	            }
	        }
    	}

        return false;
    }

  
}
