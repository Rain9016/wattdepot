/**
 * CollectorProcessDefinitionResource.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.common.http.api;

import org.restlet.resource.Put;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;

/**
 * CollectorProcessDefinitionResource - HTTP Interface for
 * CollectorProcessDefinition.
 * 
 * @author Cam Moore
 * 
 */
public interface CollectorProcessDefinitionPutResource {

  /**
   * Defines the PUT /wattdepot/collector-metadata/ API call.
   * 
   * @param metadata
   *          The CollectorProcessDefinition to store.
   */
  @Put
  public void store(CollectorProcessDefinition metadata);

}
