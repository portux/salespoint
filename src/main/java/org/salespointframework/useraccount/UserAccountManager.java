/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.salespointframework.useraccount;

import java.util.Optional;

import org.salespointframework.core.Streamable;

/**
 * Central service to manager {@link UserAccount} instances.
 *
 * @author Paul Henke
 * @author Oliver Gierke
 */
public interface UserAccountManager {

	/**
	 * Creates a new {@link UserAccount} and persists it right away.
	 * 
	 * @param userName the unique name of the user, the name is also used as login name.
	 * @param password the password
	 * @param roles zero or more roles
	 * @return a {@link UserAccount}, will never be {@literal null}.
	 * @throws NullPointerException if userName, password or roles is null
	 */
	UserAccount create(String userName, String password, Role... roles);

	/**
	 * Returns an {@link UserAccount} for a given identifier.
	 * 
	 * @param userAccountIdentifier
	 * @return will never be {@literal null}.
	 */
	Optional<UserAccount> get(UserAccountIdentifier userAccountIdentifier);

	/**
	 * Saves the {@link UserAccount}
	 * 
	 * @param userAccount
	 * @return will never be {@literal null}.
	 */
	UserAccount save(UserAccount userAccount);

	/**
	 * Enables the {@link UserAccount}.
	 * 
	 * @param userAccountIdentifier
	 */
	void enable(UserAccountIdentifier userAccountIdentifier);

	/**
	 * Disables the {@link UserAccount}.
	 * 
	 * @param userAccountIdentifier
	 */
	void disable(UserAccountIdentifier userAccountIdentifier);

	/**
	 * Changes the password of the {@link UserAccount}.
	 * 
	 * @param userAccount
	 * @param password
	 */
	void changePassword(UserAccount userAccount, String password);

	/**
	 * Checks if an {@link UserAccount} exists.
	 * 
	 * @param userAccountIdentifier
	 * @return
	 */
	boolean contains(UserAccountIdentifier userAccountIdentifier);

	/**
	 * Finds all {@link UserAccount}s.
	 * 
	 * @return
	 */
	Streamable<UserAccount> findAll();

	/**
	 * Finds only enabled {@link UserAccount}s.
	 * 
	 * @return
	 */
	Streamable<UserAccount> findEnabled();

	/**
	 * Finds only disabled {@link UserAccount}s.
	 * 
	 * @return
	 */
	Streamable<UserAccount> findDisabled();

	/**
	 * Returns the user with the given user name.
	 * 
	 * @param username must not be {@literal null} or empty.
	 * @return
	 */
	Optional<UserAccount> findByUsername(String username);
}
