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
package org.salespointframework.order;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.money.MonetaryAmount;

import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Quantity;
import org.springframework.util.Assert;

/**
 * A CartItem consists of a {@link Product} and a {@link Quantity}.
 *
 * @author Paul Henke
 * @author Oliver Gierke
 */
@ToString
@EqualsAndHashCode
@Getter
public class CartItem implements Priced {

	private final String id;
	private final MonetaryAmount price;
	private final Quantity quantity;
	private final Product product;

	/**
	 * Creates a new {@link CartItem}.
	 *
	 * @param id       must not be {@literal null} or empty.
	 * @param product  must not be {@literal null}.
	 * @param quantity must not be {@literal null}.
	 */
	CartItem(String id, Product product, Quantity quantity) {
		Assert.hasText(id, "Id must not be empty!");
		Assert.notNull(product, "Product must be not null!");
		Assert.notNull(quantity, "Quantity must be not null!");

		product.verify(quantity);

		this.id = id;
		this.product = product;
		this.quantity = quantity;
		this.price = product.getPrice().multiply(quantity.getAmount());
	}

	/**
	 * Returns the name of the {@link Product} associated with the {@link CartItem}.
	 *
	 * @return
	 */
	public final String getProductName() {
		return product.getName();
	}

	/**
	 * Creates an {@link OrderLine} from this CartItem.
	 *
	 * @return
	 */
	final OrderLine toOrderLine() {
		return new OrderLine(product, quantity);
	}
}
