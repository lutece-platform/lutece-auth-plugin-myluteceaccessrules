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
 *
 * License 1.0
 */
 	
package fr.paris.lutece.plugins.myluteceaccessrules.web;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.myluteceaccessrules.business.Rule;
import fr.paris.lutece.plugins.myluteceaccessrules.business.RuleHome;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

// TODO: Auto-generated Javadoc
/**
 * This class provides the user interface to manage Rule features ( manage, create, modify, remove ).
 */
@Controller( controllerJsp = "ManageRules.jsp", controllerPath = "jsp/admin/plugins/myluteceaccessrules/", right = "ACCESSRULES_MANAGEMENT" )
public class RuleJspBean extends AbstractManageRulesJspBean
{
    
    /** The Constant TEMPLATE_MANAGE_RULES. */
    // Templates
    private static final String TEMPLATE_MANAGE_RULES = "/admin/plugins/myluteceaccessrules/manage_rules.html";
    
    /** The Constant TEMPLATE_CREATE_RULE. */
    private static final String TEMPLATE_CREATE_RULE = "/admin/plugins/myluteceaccessrules/create_rule.html";
    
    /** The Constant TEMPLATE_MODIFY_RULE. */
    private static final String TEMPLATE_MODIFY_RULE = "/admin/plugins/myluteceaccessrules/modify_rule.html";

    /** The Constant PARAMETER_ID_RULE. */
    // Parameters
    private static final String PARAMETER_ID_RULE = "id";
    
    /** The Constant PARAMETER_PROTECTED_URL. */
    private static final String PARAMETER_PROTECTED_URL = "protected_url";
    
    /** The Constant PARAMETER_PUBLIC_URL. */
    private static final String PARAMETER_PUBLIC_URL = "public_url";
    
    /** The Constant PREFIX_PARAMETER_ROLE. */
    private static final String PREFIX_PARAMETER_ROLE = "role_";
    
    
    /** The Constant PARAMETER_PROTECTED_URL_DELETE. */
    private static final String PARAMETER_PROTECTED_URL_DELETE = "protected_url_delete";
    
    /** The Constant PARAMETER_PUBLIC_URL_DELETE. */
    private static final String PARAMETER_PUBLIC_URL_DELETE = "public_url_delete";
    
    

    
    /** The Constant PROPERTY_PAGE_TITLE_MANAGE_RULES. */
    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_RULES = "myluteceaccessrules.manage_rules.pageTitle";
    
    /** The Constant PROPERTY_PAGE_TITLE_MODIFY_RULE. */
    private static final String PROPERTY_PAGE_TITLE_MODIFY_RULE = "myluteceaccessrules.modify_rule.pageTitle";
    
    /** The Constant PROPERTY_PAGE_TITLE_CREATE_RULE. */
    private static final String PROPERTY_PAGE_TITLE_CREATE_RULE = "myluteceaccessrules.create_rule.pageTitle";

    /** The Constant MARK_RULE_LIST. */
    // Markers
    private static final String MARK_RULE_LIST = "rule_list";
    
    /** The Constant MARK_ROLES_LIST. */
    private static final String MARK_ROLES_LIST = "roles_list";
    
    /** The Constant MARK_RULE. */
    private static final String MARK_RULE = "rule";

    /** The Constant JSP_MANAGE_RULES. */
    private static final String JSP_MANAGE_RULES = "jsp/admin/plugins/myluteceaccessrules/ManageRules.jsp";

    /** The Constant MESSAGE_CONFIRM_REMOVE_RULE. */
    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_RULE = "myluteceaccessrules.message.confirmRemoveRule";

    /** The Constant VALIDATION_ATTRIBUTES_PREFIX. */
    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "myluteceaccessrules.model.entity.rule.attribute.";

    /** The Constant VIEW_MANAGE_RULES. */
    // Views
    private static final String VIEW_MANAGE_RULES = "manageRules";
    
    /** The Constant VIEW_CREATE_RULE. */
    private static final String VIEW_CREATE_RULE = "createRule";
    
    /** The Constant VIEW_MODIFY_RULE. */
    private static final String VIEW_MODIFY_RULE = "modifyRule";

    /** The Constant ACTION_CREATE_RULE. */
    // Actions
    private static final String ACTION_CREATE_RULE = "createRule";
    
    /** The Constant ACTION_MODIFY_RULE. */
    private static final String ACTION_MODIFY_RULE = "modifyRule";
    
    /** The Constant ACTION_REMOVE_RULE. */
    private static final String ACTION_REMOVE_RULE = "removeRule";
    
    /** The Constant ACTION_ADD_PROTECTED_URL_CREATE. */
    private static final String ACTION_ADD_PROTECTED_URL_CREATE = "addProtectedUrlCreate";
    
    /** The Constant ACTION_ADD_PROTECTED_URL_MODIFY. */
    private static final String ACTION_ADD_PROTECTED_URL_MODIFY = "addProtectedUrlModify";
    
    /** The Constant ACTION_REMOVE_PROTECTED_URL_CREATE. */
    private static final String ACTION_REMOVE_PROTECTED_URL_CREATE = "removeProtectedUrlCreate";
    
    /** The Constant ACTION_REMOVE_PROTECTED_URL_MODIFY. */
    private static final String ACTION_REMOVE_PROTECTED_URL_MODIFY = "removeProtectedUrlModify";
    
    /** The Constant ACTION_ADD_PUBLIC_URL_CREATE. */
    private static final String ACTION_ADD_PUBLIC_URL_CREATE = "addPublicUrlCreate";
    
    /** The Constant ACTION_ADD_PUBLIC_URL_MODIFY. */
    private static final String ACTION_ADD_PUBLIC_URL_MODIFY = "addPublicUrlModify";
    
    /** The Constant ACTION_REMOVE_PUBLIC_URL_CREATE. */
    private static final String ACTION_REMOVE_PUBLIC_URL_CREATE = "removePublicUrlCreate";
    
    /** The Constant ACTION_REMOVE_PUBLIC_URL_MODIFY. */
    private static final String ACTION_REMOVE_PUBLIC_URL_MODIFY = "removePublicUrlModify";
    
    
    
    /** The Constant ACTION_CONFIRM_REMOVE_RULE. */
    private static final String ACTION_CONFIRM_REMOVE_RULE = "confirmRemoveRule";

    /** The Constant INFO_RULE_CREATED. */
    // Infos
    private static final String INFO_RULE_CREATED = "accessrules.info.rule.created";
    
    /** The Constant INFO_RULE_UPDATED. */
    private static final String INFO_RULE_UPDATED = "accessrules.info.rule.updated";
    
    /** The Constant INFO_RULE_REMOVED. */
    private static final String INFO_RULE_REMOVED = "accessrules.info.rule.removed";
    
    /** The rule. */
    // Session variable to store working values
    private Rule _rule;
    
    /**
     * Build the Manage View.
     *
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_RULES, defaultView = true )
    public String getManageRules( HttpServletRequest request )
    {
        _rule = null;
        List<Rule> listRules = RuleHome.getRulesList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_RULE_LIST, listRules, JSP_MANAGE_RULES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_RULES, TEMPLATE_MANAGE_RULES, model );
    }

    /**
     * Returns the form to create a rule.
     *
     * @param request The Http request
     * @return the html code of the rule form
     */
    @View( VIEW_CREATE_RULE )
    public String getCreateRule( HttpServletRequest request )
    {
        _rule = ( _rule != null ) ? _rule : new Rule(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_RULE, _rule );
        
        

        
        
        ReferenceList refListRoles=RoleHome.getRolesList(getUser());
        refListRoles.forEach(x-> x.setChecked(_rule.getRoles()!=null && _rule.getRoles().stream().anyMatch(y->y.getName().equals(x.getCode()))));
        model.put( MARK_ROLES_LIST, refListRoles);
        
        
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_RULE ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_RULE, TEMPLATE_CREATE_RULE, model );
    }

    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_CREATE_RULE )
    public String doCreateRule( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _rule, request, getLocale( ) );
        populateRoles(request);

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_RULE ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _rule, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_RULE );
        }

        RuleHome.create( _rule );
        addInfo( INFO_RULE_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_RULES );
    }
    
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_ADD_PROTECTED_URL_CREATE )
    public String doAddProtectedUrlCreate( HttpServletRequest request ) throws AccessDeniedException
    {
     
    	populate( _rule, request, getLocale( ) );
        populateRoles(request);
        
    	populateProtectedUrls(request);
    	
    
    	 return redirectView( request, VIEW_CREATE_RULE );
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_ADD_PROTECTED_URL_MODIFY)
    public String doAddProtectedUrlModify( HttpServletRequest request ) throws AccessDeniedException
    {
     
    	populate( _rule, request, getLocale( ) );
        populateRoles(request);
        
    	populateProtectedUrls(request);
    	
    
    	return redirect( request, VIEW_MODIFY_RULE, PARAMETER_ID_RULE, _rule.getId( ) );
    }
    
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_ADD_PUBLIC_URL_CREATE )
    public String doAddPublicUrlCreate( HttpServletRequest request ) throws AccessDeniedException
    {
     
    	populate( _rule, request, getLocale( ) );
        populateRoles(request);
        
    	populatePublicUrls(request);
    	
    
    	 return redirectView( request, VIEW_CREATE_RULE );
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_ADD_PUBLIC_URL_MODIFY)
    public String doAddPublicUrlModify( HttpServletRequest request ) throws AccessDeniedException
    {
     
    	populate( _rule, request, getLocale( ) );
        populateRoles(request);
        
    	populatePublicUrls(request);
    	
    
    	return redirect( request, VIEW_MODIFY_RULE, PARAMETER_ID_RULE, _rule.getId( ) );
    }

    
    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_REMOVE_PROTECTED_URL_CREATE )
    public String doRemoveProtectedUrlCreate( HttpServletRequest request ) throws AccessDeniedException
    {
     
    	String strProtectedUrlDelete=request.getParameter(PARAMETER_PROTECTED_URL_DELETE);
    	
    	_rule.getProtectedUrls().removeIf(x-> x.getCode().equals(strProtectedUrlDelete));
    
    	 return redirectView( request, VIEW_CREATE_RULE );
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_REMOVE_PROTECTED_URL_MODIFY)
    public String doRemoveProtectedUrlModify( HttpServletRequest request ) throws AccessDeniedException
    {
     
    	String strProtectedUrlDelete=request.getParameter(PARAMETER_PROTECTED_URL_DELETE);
    	
    	_rule.getProtectedUrls().removeIf(x-> x.getCode().equals(strProtectedUrlDelete));
    
    	return redirect( request, VIEW_MODIFY_RULE, PARAMETER_ID_RULE, _rule.getId( ) );
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_REMOVE_PUBLIC_URL_CREATE )
    public String doRemovePublicUrlCreate( HttpServletRequest request ) throws AccessDeniedException
    {
     
    	String strPublicdUrlDelete=request.getParameter(PARAMETER_PUBLIC_URL_DELETE);
    	
    	_rule.getPublicUrls().removeIf(x-> x.getCode().equals(strPublicdUrlDelete));
    
    	 return redirectView( request, VIEW_CREATE_RULE );
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_REMOVE_PUBLIC_URL_MODIFY)
    public String doRemovePublicUrlModify( HttpServletRequest request ) throws AccessDeniedException
    {
     
    	String strPublicdUrlDelete=request.getParameter(PARAMETER_PUBLIC_URL_DELETE);
    	
    	_rule.getPublicUrls().removeIf(x-> x.getCode().equals(strPublicdUrlDelete));
    
    	return redirect( request, VIEW_MODIFY_RULE, PARAMETER_ID_RULE, _rule.getId( ) );
    }




    /**
     * Manages the removal form of a rule whose identifier is in the http
     * request.
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_RULE )
    public String getConfirmRemoveRule( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_RULE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_RULE ) );
        url.addParameter( PARAMETER_ID_RULE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_RULE, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a rule.
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage rules
     */
    @Action( ACTION_REMOVE_RULE )
    public String doRemoveRule( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_RULE ) );
        RuleHome.remove( nId );
        addInfo( INFO_RULE_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_RULES );
    }

    /**
     * Returns the form to update info about a rule.
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_RULE )
    public String getModifyRule( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_RULE ) );

        if ( _rule == null || ( _rule.getId(  ) != nId ) )
        {
            _rule = RuleHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_RULE, _rule );
        ReferenceList refListRoles=RoleHome.getRolesList(getUser());
        refListRoles.forEach(x-> x.setChecked(_rule.getRoles()!=null && _rule.getRoles().stream().anyMatch(y->y.getName().equals(x.getCode()))));
        model.put( MARK_ROLES_LIST, refListRoles);
        
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_RULE ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_RULE, TEMPLATE_MODIFY_RULE, model );
    }

    /**
     * Process the change form of a rule.
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_MODIFY_RULE )
    public String doModifyRule( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _rule, request, getLocale( ) );
        populateRoles(request);
        
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_RULE ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _rule, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_RULE, PARAMETER_ID_RULE, _rule.getId( ) );
        }

        RuleHome.update( _rule );
        addInfo( INFO_RULE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_RULES );
    }
    
    
    /**
     * Populate protected urls.
     *
     * @param request the request
     */
    private void populateProtectedUrls(HttpServletRequest request )
    {
    	
    	if(_rule.getProtectedUrls() ==null)
    	{
    		_rule.setProtectedUrls(new ReferenceList());	
    	}
    	
    	_rule.getProtectedUrls().forEach(x-> x.setName( request.getParameter(x.getCode())));
    	if(StringUtils.isNotEmpty(request.getParameter(PARAMETER_PROTECTED_URL)))
    	{
    		_rule.getProtectedUrls().addItem(RuleHome.RULE_PROTECTED_URL_PREFIX+_rule.getId()+"_"+_rule.getProtectedUrls().size()+1, request.getParameter(PARAMETER_PROTECTED_URL));
    	}
    	
    }
    
    
    
    /**
     * Populate public urls.
     *
     * @param request the request
     */
    private void populatePublicUrls(HttpServletRequest request )
    {
    	
    	if(_rule.getPublicUrls() ==null)
    	{
    		_rule.setPublicUrls(new ReferenceList());	
    	}
    	
    	_rule.getPublicUrls().forEach(x-> x.setName( request.getParameter(x.getCode())));
    	if(StringUtils.isNotEmpty(request.getParameter(PARAMETER_PUBLIC_URL)))
    	{
    		_rule.getPublicUrls().addItem(RuleHome.RULE_PUBLIC_URL_PREFIX+_rule.getId()+"_"+_rule.getPublicUrls().size()+1, request.getParameter(PARAMETER_PUBLIC_URL));
    	}
    	
    }
    
    /**
     * Populate roles.
     *
     * @param request the request
     */
    private void populateRoles(HttpServletRequest request )
    {
    	
    	_rule.setRoles(new ReferenceList());	
     	RoleHome.getRolesList(getUser()).stream().filter(x->request.getParameter(PREFIX_PARAMETER_ROLE+x.getCode())!=null).forEach(x->_rule.getRoles().addItem(x.getCode(), x.getCode()));
    	
    	
    }
}
