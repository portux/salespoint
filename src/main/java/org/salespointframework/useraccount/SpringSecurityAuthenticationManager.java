/*
 * Copyright 2013 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * {@link AuthenticationManager} using the current SpringSecurity {@link Authentication} to lookup a {@link UserAccount}
 * by the identifier of it.
 * 
 * @author Oliver Gierke
 */
@Component
class SpringSecurityAuthenticationManager implements AuthenticationManager {

	private final UserAccountRepository repository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Creates a new {@link SpringSecurityAuthenticationManager} using the given {@link UserAccountRepository}.
	 * 
	 * @param repository must not be {@literal null}.
	 */
	@Autowired
	public SpringSecurityAuthenticationManager(UserAccountRepository repository, PasswordEncoder passwordEncoder) {

		Assert.notNull(repository, "UserAccountRepository must not be null!");
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.salespointframework.useraccount.AuthenticationManager#getCurrentUser()
	 */
	@Override
	public Optional<UserAccount> getCurrentUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return null;
		}

		UserAccountIdentifier userAccountIdentifier = new UserAccountIdentifier(authentication.getName());
		return repository.findOne(userAccountIdentifier);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.salespointframework.useraccount.AuthenticationManager#matches(org.salespointframework.useraccount.Password, org.salespointframework.useraccount.Password)
	 */
	@Override
	public boolean matches(Password candidate, Password existing) {
		
		Assert.notNull(existing);
		
		if (candidate == null) {
			return false;
		}
		
		return passwordEncoder.matches(candidate.toString(), existing.toString());
	}
}
