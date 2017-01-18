/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core;

import java.io.Serializable;
import java.util.Date;

import org.eclipse.egit.github.core.util.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * GitHub {@link Issue} and {@link Gist} comment class.
 */
public class Review implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3131732314558153044L;

	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private String id;

	private User user;

	private String body;

	private String commit_id;

	private String state;

	private String html_url;

	private Date createdAt;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the commit_id
	 */
	public String getCommit_id() {
		return commit_id;
	}

	/**
	 * @param commit_id the commit_id to set
	 */
	public void setCommit_id(String commit_id) {
		this.commit_id = commit_id;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the html_url
	 */
	public String getHtml_url() {
		return html_url;
	}

	/**
	 * @param html_url the html_url to set
	 */
	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return DateUtils.clone(createdAt);
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public Review setCreatedAt(Date createdAt) {
		this.createdAt = DateUtils.clone(createdAt);
		return this;
	}

	public String toString() {
		return gson.toJson(this);
	}
}
