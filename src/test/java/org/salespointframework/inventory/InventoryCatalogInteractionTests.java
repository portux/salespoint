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
package org.salespointframework.inventory;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.salespointframework.AbstractIntegrationTests;
import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Cookie;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.Currencies;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;

public class InventoryCatalogInteractionTests extends AbstractIntegrationTests {

	@Autowired Inventory<InventoryItem> inventory;
	@Autowired Catalog<Product> catalog;

	private Cookie cookie;
	private InventoryItem item;

	private static int counter = 0;

	@Before
	public void before() {

		cookie = new Cookie("Superkeks " + (counter++), Currencies.ZERO_EURO);
		item = new InventoryItem(cookie, Quantity.of(10));
	}

	@Test
	public void addInCatalogThenInInventoryThrowsNoException() {

		catalog.save(cookie);
		inventory.save(item);
	}

	@Test
	public void addInInventoryAddsProductInCatalog() {

		inventory.save(item);
		assertThat(catalog.exists(cookie.getId()), is(true));
	}

	@Test
	public void removeInInventory() {

		inventory.save(item);
		inventory.delete(item.getId());
	}

	@Test
	public void removeInInventoryThenRemoveInCatalog() {

		catalog.save(cookie);
		inventory.save(item);
		inventory.delete(item.getId());

		catalog.delete(cookie.getId());
	}

	@Test
	public void removeInInventoryDoesNotRemoveInCatalog() {

		catalog.save(cookie);
		inventory.save(item);
		inventory.delete(item.getId());

		assertThat(catalog.exists(cookie.getId()), is(true));
	}
}
