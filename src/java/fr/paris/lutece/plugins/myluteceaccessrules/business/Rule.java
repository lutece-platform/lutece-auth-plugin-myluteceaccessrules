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
package fr.paris.lutece.plugins.myluteceaccessrules.business;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;


import org.hibernate.validator.constraints.NotEmpty;

import fr.paris.lutece.util.ReferenceList;

/**
 * This is the business class for the object Rule
 */ 
public class Rule implements Serializable
{
    private static final long serialVersionUID = 1L;
    
  
    // Variables declarations 
    private int _nId;
    
    @NotEmpty( message = "#i18n{myluteceaccessrules.validation.rule.Title.notEmpty}" )
    @Size( max = 50 , message = "#i18n{myluteceaccessrules.validation.rule.Title.size}" ) 
    private String _strTitle;
    
    @Size( max = 255 , message = "#i18n{myluteceaccessrules.validation.rule.Description.size}" ) 
    private String _strDescription;
    
    
    @Nullable
    private boolean _bEnable;
    @Nullable
    private boolean _bExternal;
    
    @Nullable
    private boolean _bLogoutUser;
    
    
    private String _strMessagetodisplay;
    
    @Size( max = 255 , message = "#i18n{myluteceaccessrules.validation.rule.Redirecturl.size}" ) 
    private String _strRedirecturl;
    
    private String _strBackUrl;
    
    
    


	@Nullable
    private ReferenceList  _listProtectedUrls;
    @Nullable
    private ReferenceList  _listPublicUrls;
	private ReferenceList _listRoles;
     
    
    
    

    /**
     * Returns the Id
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * @param nId The Id
     */ 
    public void setId( int nId )
    {
        _nId = nId;
    }
    
    /**
     * Returns the Title
     * @return The Title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Sets the Title
     * @param strTitle The Title
     */ 
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }
    
    /**
     * Returns the Description
     * @return The Description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     * @param strDescription The Description
     */ 
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }
    
    /**
     * Returns the Enable
     * @return The Enable
     */
    public boolean getEnable( )
    {
        return _bEnable;
    }

    /**
     * Sets the Enable
     * @param bEnable The Enable
     */ 
    public void setEnable( boolean bEnable )
    {
        _bEnable = bEnable;
    }
    
    /**
     * Returns the External
     * @return The External
     */
    public boolean isExternal( )
    {
        return _bExternal;
    }

    /**
     * Sets the External
     * @param bExternal The External
     */ 
    public void setExternal( boolean bExternal )
    {
        _bExternal = bExternal;
    }
    
    /**
     * Returns the Messagetodisplay
     * @return The Messagetodisplay
     */
    public String getMessagetodisplay( )
    {
        return _strMessagetodisplay;
    }

    /**
     * Sets the Messagetodisplay
     * @param strMessagetodisplay The Messagetodisplay
     */ 
    public void setMessagetodisplay( String strMessagetodisplay )
    {
        _strMessagetodisplay = strMessagetodisplay;
    }
    
    /**
     * Returns the Redirecturl
     * @return The Redirecturl
     */
    public String getRedirecturl( )
    {
        return _strRedirecturl;
    }

    /**
     * Sets the Redirecturl
     * @param strRedirecturl The Redirecturl
     */ 
    public void setRedirecturl( String strRedirecturl )
    {
        _strRedirecturl = strRedirecturl;
    }

	public ReferenceList getProtectedUrls() {
		return _listProtectedUrls;
	}

	public void setProtectedUrls(ReferenceList listProtectedUrls) {
		this._listProtectedUrls = listProtectedUrls;
	}
	
	
	public ReferenceList getPublicUrls() {
		return _listPublicUrls;
	}

	public void setPublicUrls(ReferenceList listPublicUrls) {
		this._listPublicUrls = listPublicUrls;
	}

	public ReferenceList  getRoles() {
		return _listRoles;
	}

	public void setRoles(ReferenceList listRoles) {
		this._listRoles = listRoles;
	}
	
	
    public String getBackUrl() {
		return _strBackUrl;
	}

	public void setBackUrl(String _strBackUrlParameter) {
		this._strBackUrl = _strBackUrlParameter;
	}
	
	/**
     * Sets  Logout User
     * @param bLogoutUser logout user
     */ 
    public void setLogoutUser( boolean bLogoutUser )
    {
    	_bLogoutUser = bLogoutUser;
    }
    
    /**
     * Returns the true if the user muset be logout
     * @return true if the user muset be logout
     */
    public boolean isLogoutUser( )
    {
        return _bLogoutUser;
    }
}
