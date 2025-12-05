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

import java.util.List;

import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.binding.BindingResult;
import fr.paris.lutece.portal.util.mvc.commons.annotations.ModelAttribute;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;
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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Rule features ( manage, create, modify, remove ).
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageRules.jsp", controllerPath = "jsp/admin/plugins/myluteceaccessrules/", right = "ACCESSRULES_MANAGEMENT" )
public class RuleJspBean extends MVCAdminJspBean
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
    private static final String PARAMETER_PROTECTED_URL_CODELIST = "protected_url_codelist";
    private static final String PARAMETER_PROTECTED_URL = "protected_url";
    
    /** The Constant PARAMETER_PUBLIC_URL. */
    private static final String PARAMETER_PUBLIC_URL_CODELIST = "public_url_codelist";
    private static final String PARAMETER_PUBLIC_URL = "public_url";
    
    /** The Constant PREFIX_PARAMETER_ROLE. */
    private static final String PREFIX_PARAMETER_ROLE = "role_";

    /** Marker **/
    private static final String MARKER_PROTECTED_URL_TO_REMOVED_CREATE = "action_removeProtectedUrlCreate";

    private static final String MARKER_PUBLIC_URL_TO_REMOVED_CREATE = "action_removePublicUrlCreate";

    private static final String MARKER_PROTECTED_URL_TO_REMOVED_MODIFY = "action_removeProtectedUrlModify";

    private static final String MARKER_PUBLIC_URL_TO_REMOVED_MODIFY = "action_removePublicUrlModify";


    // Pager
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "accessrules.listItems.itemsPerPage";

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
    
    /** The Constant ACTION_INCREASE_PRIORITY. */
    private static final String ACTION_INCREASE_PRIORITY = "increasePriority";
    /** The Constant ACTION_DECREASE_PRIORITY. */
    private static final String ACTION_DECREASE_PRIORITY = "decreasePriority";
    
    
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
    private static final String INFO_RULE_CREATED = "myluteceaccessrules.info.rule.created";
    
    /** The Constant INFO_RULE_UPDATED. */
    private static final String INFO_RULE_UPDATED = "myluteceaccessrules.info.rule.updated";
    
    /** The Constant INFO_RULE_REMOVED. */
    private static final String INFO_RULE_REMOVED = "myluteceaccessrules.info.rule.removed";
    
    /** The rule. */
    private Rule _rule;

    @Inject
    @Pager( listBookmark = MARK_RULE_LIST, defaultItemsPerPage = PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE )
    private IPager<Rule, Void> _pager;

    @Inject
    Models model;
    
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

        _pager.withBaseUrl(getHomeUrl(request))
                .withListItem(listRules)
                .populateModels(request, model, getLocale());

        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService().getToken( request, ACTION_INCREASE_PRIORITY ) );

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
        return getCreateRule( request, _rule);
    }

    /**
     * Returns the form to create a rule.
     *
     * @param request The Http request
     * @param rule the rule
     * @return the html code of the rule form
     */
    public String getCreateRule( HttpServletRequest request, Rule rule )
    {
        model.put( MARK_RULE, rule );
        ReferenceList refListRoles=RoleHome.getRolesList(getUser());
        refListRoles.forEach(x-> x.setChecked(rule.getRoles()!=null && rule.getRoles().stream().anyMatch(y->y.getName().equals(x.getCode()))));
        model.put( MARK_ROLES_LIST, refListRoles);
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService().getToken( request, ACTION_CREATE_RULE ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_RULE, TEMPLATE_CREATE_RULE, model );
    }

    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param bindingResult the bindingResult
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_CREATE_RULE )
    public String doCreateRule(@Valid @ModelAttribute Rule rule,
                               BindingResult bindingResult,
                               HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

        if ( !getSecurityTokenService( ).validate( request, ACTION_CREATE_RULE ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        if( bindingResult.isFailed() )
        {
            return redirectView( request, VIEW_CREATE_RULE );
        }

        RuleHome.create( rule );
        
        
        addInfo( INFO_RULE_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_RULES );
    }
    
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_ADD_PROTECTED_URL_CREATE )
    public String doAddProtectedUrlCreate( @Valid @ModelAttribute Rule rule,
                                           HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

        addProtectedUrl(request, rule);

        return getCreateRule(request, rule);
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_ADD_PROTECTED_URL_MODIFY)
    public String doAddProtectedUrlModify( @Valid @ModelAttribute Rule rule,
                                           HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

        addProtectedUrl(request, rule);

        return getModifyRule( request, rule );
    }
    
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_ADD_PUBLIC_URL_CREATE )
    public String doAddPublicUrlCreate( @Valid @ModelAttribute Rule rule,
                                        HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

        addPublicUrl(request, rule);

        return getCreateRule(request, rule);
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_ADD_PUBLIC_URL_MODIFY)
    public String doAddPublicUrlModify( @Valid @ModelAttribute Rule rule,
                                        HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

        addPublicUrl(request, rule);

        return getModifyRule(request, rule);
    }

    
    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_REMOVE_PROTECTED_URL_CREATE )
    public String doRemoveProtectedUrlCreate( @Valid @ModelAttribute Rule rule,
                                              HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

    	String strProtectedUrlDelete=request.getParameter(MARKER_PROTECTED_URL_TO_REMOVED_CREATE);
        rule.getProtectedUrls().removeIf(x-> x.getCode().equals(strProtectedUrlDelete));

        return getCreateRule( request, rule );
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_REMOVE_PROTECTED_URL_MODIFY)
    public String doRemoveProtectedUrlModify( @Valid @ModelAttribute Rule rule,
                                              HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

    	String strProtectedUrlDelete=request.getParameter(MARKER_PROTECTED_URL_TO_REMOVED_MODIFY);
        rule.getProtectedUrls().removeIf(x-> x.getCode().equals(strProtectedUrlDelete));
    
    	return getModifyRule(request, rule);
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_REMOVE_PUBLIC_URL_CREATE )
    public String doRemovePublicUrlCreate( @Valid @ModelAttribute Rule rule,
                                           HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

        String strPublicdUrlDelete=request.getParameter(MARKER_PUBLIC_URL_TO_REMOVED_CREATE);
        rule.getPublicUrls().removeIf(x-> x.getCode().equals(strPublicdUrlDelete));
    
        return getCreateRule(request, rule);
    }
    
    /**
     * Process the data capture form of a new rule.
     *
     * @param rule the rule
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_REMOVE_PUBLIC_URL_MODIFY)
    public String doRemovePublicUrlModify( @Valid @ModelAttribute Rule rule,
                                           HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);

    	String strPublicdUrlDelete=request.getParameter(MARKER_PUBLIC_URL_TO_REMOVED_MODIFY);
        rule.getPublicUrls().removeIf(x-> x.getCode().equals(strPublicdUrlDelete));
    
    	return getModifyRule(request, rule);
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
        return getModifyRule( request, _rule);
    }

    /**
     * Returns the form to update info about for a rule.
     *
     * @param request The Http request
     * @param rule the Rule
     * @return The HTML form to update info
     */
    private String getModifyRule( HttpServletRequest request, Rule rule )
    {
        model.put( MARK_RULE, rule );
        ReferenceList refListRoles=RoleHome.getRolesList(getUser());
        refListRoles.forEach(x-> x.setChecked(rule.getRoles()!=null && rule.getRoles().stream().anyMatch(y->y.getName().equals(x.getCode()))));
        model.put( MARK_ROLES_LIST, refListRoles);

        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService().getToken( request, ACTION_MODIFY_RULE ) );

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
    public String doModifyRule( @Valid @ModelAttribute Rule rule,
                                BindingResult bindingResult,
                                HttpServletRequest request ) throws AccessDeniedException
    {
        populateRule(request, rule);
        
        if ( !getSecurityTokenService().validate( request, ACTION_MODIFY_RULE ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( bindingResult.isFailed() )
        {
            return redirect( request, VIEW_MODIFY_RULE, PARAMETER_ID_RULE, _rule.getId( ) );
        }

        RuleHome.update( rule );
        addInfo( INFO_RULE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_RULES );
    }
    
    /**
     * Process the change form of a rule.
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_INCREASE_PRIORITY )
    public String doIncreaseRulePriority( HttpServletRequest request ) throws AccessDeniedException
    {
      
    	int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_RULE ) );
    	 
        if ( !getSecurityTokenService().validate( request, ACTION_INCREASE_PRIORITY ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

     
        List<Rule> listRules=RuleHome.getRulesList();
        Rule rulePrev=null;
        
        for(Rule rule: listRules)
        {
        	if(rule.getId()== nId)
        	{
        	  if(rulePrev!=null)
        	  {
        		  int nPriority=rule.getPriorityOrder();
        		  rule.setPriorityOrder(rulePrev.getPriorityOrder());
        		  rulePrev.setPriorityOrder(nPriority);
        	  }
        	}
        	
        	rulePrev=rule;
        }
    
        RuleHome.updateRulesPriorities(listRules);
        addInfo( INFO_RULE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_RULES );
    }
    
    
    /**
     * Process the change form of a rule.
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException the access denied exception
     */
    @Action( ACTION_DECREASE_PRIORITY )
    public String doDecreaseRulePriority( HttpServletRequest request ) throws AccessDeniedException
    {
      
    	int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_RULE ) );
    	 
        if ( !getSecurityTokenService().validate( request, ACTION_INCREASE_PRIORITY ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

     
        List<Rule> listRules=RuleHome.getRulesList();
        Rule ruleNext=null;
        
        for(Rule rule: listRules)
        {
        	if(rule.getId()== nId)
        	{
        		ruleNext=rule;
        	 
        	}
        	else if(ruleNext!=null)
        	 {
       		  	int nPriority=ruleNext.getPriorityOrder();
       		  	ruleNext.setPriorityOrder(rule.getPriorityOrder());
       		  	rule.setPriorityOrder(nPriority);
        	 }
       	  }
        	
        
    
        RuleHome.updateRulesPriorities(listRules);
        addInfo( INFO_RULE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_RULES );
    }
    
    
    
    
    
    
    
    /**
     * Populate protected urls.
     *
     * @param request the request
     * @param rule the Rule
     */
    private void populateProtectedUrls(HttpServletRequest request, Rule rule)
    {
    	
    	if(rule.getProtectedUrls() ==null)
    	{
            rule.setProtectedUrls(new ReferenceList());
    	}

        String codelist = request.getParameter(PARAMETER_PROTECTED_URL_CODELIST);
        if( StringUtils.isNotEmpty(codelist) )
        {
            String[] protectedCodeList = codelist.split(",");
            for (String code : protectedCodeList) {
                rule.getProtectedUrls().addItem(code, request.getParameter(code));
            }
        }
    }

    /**
     * add one protected Url
     *
     * @param request the request
     * @param rule the Rule
     */
    private void addProtectedUrl(HttpServletRequest request, Rule rule)
    {
        if(StringUtils.isNotEmpty(request.getParameter(PARAMETER_PROTECTED_URL)))
        {
            rule.getProtectedUrls().addItem(RuleHome.RULE_PROTECTED_URL_PREFIX+rule.getId()+"_"+rule.getProtectedUrls().size()+1, request.getParameter(PARAMETER_PROTECTED_URL));
        }
    }

    /**
     * Populate public urls.
     *
     * @param request the request
     * @param rule the Rule
     */
    private void populatePublicUrls(HttpServletRequest request, Rule rule )
    {
    	if(rule.getPublicUrls() ==null)
    	{
            rule.setPublicUrls(new ReferenceList());
    	}

        String codelist = request.getParameter(PARAMETER_PUBLIC_URL_CODELIST);
        if( StringUtils.isNotEmpty(codelist) )
        {
            String[] publicCodeList = codelist.split(",");
            for (String code : publicCodeList) {
                rule.getPublicUrls().addItem(code, request.getParameter(code));
            }
        }
    }

    /**
     * add one public Url
     *
     * @param request the request
     * @param rule the Rule
     */
    private void addPublicUrl(HttpServletRequest request, Rule rule)
    {
        if(StringUtils.isNotEmpty(request.getParameter(PARAMETER_PUBLIC_URL)))
        {
            rule.getPublicUrls().addItem(RuleHome.RULE_PUBLIC_URL_PREFIX+rule.getId()+"_"+rule.getPublicUrls().size()+1, request.getParameter(PARAMETER_PUBLIC_URL));
        }
    }
    
    /**
     * Populate roles.
     *
     * @param request the request
     * @param rule the Rule
     */
    private void populateRoles(HttpServletRequest request, Rule rule )
    {

        rule.setRoles(new ReferenceList());
        RoleHome.getRolesList(getUser()).stream().filter(x->request.getParameter(PREFIX_PARAMETER_ROLE+x.getCode())!=null).forEach(x->rule.getRoles().addItem(x.getCode(), x.getCode()));

    }

    /**
     * Populate roles, Protected and public Urls
     *
     * @param request the request
     * @param rule The Rule
     */
    private void populateRule( HttpServletRequest request, Rule rule )
    {
        populateRoles( request, rule );
        populateProtectedUrls( request, rule );
        populatePublicUrls( request, rule );
    }
}
