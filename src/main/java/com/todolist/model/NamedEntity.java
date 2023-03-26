/*
 * Copyright 2002-2013 the original author or authors.
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
package com.todolist.model;

import com.todolist.dtos.autodoc.RoleStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

/**
 * Simple JavaBean domain object adds a name property to <code>BaseEntity</code>. Used as
 * a base class for objects needing these properties.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass
@Getter
@Setter
@ToString(of = "name")
public class NamedEntity extends BaseEntity {

	// Attributes -------------------------------------------------------------
	@Size(min = 3, max = 50)
	@Column(name = "name")
	private String name;

	// Derived attributes -----------------------------------------------------
	@Transient
	public RoleStatus getStatus() {
		return RoleStatus.valueOf(this.name.toUpperCase());
	}

}
