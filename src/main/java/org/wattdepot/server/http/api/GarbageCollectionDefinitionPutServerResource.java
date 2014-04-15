/**
 * GarbageCollectionDefinitionPutServerResource.java This file is part of WattDepot.
 *
 * Copyright (C) 2014  Cam Moore
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
package org.wattdepot.server.http.api;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.wattdepot.common.domainmodel.GarbageCollectionDefinition;
import org.wattdepot.common.domainmodel.Labels;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.http.api.API;
import org.wattdepot.common.http.api.GarbageCollectionDefinitionPutResource;

/**
 * GarbageCollectionDefinitionPutServerResource - Handles the
 * GarbageCollectionDefinition HTTP API
 * ("/wattdepot/{org-id}/garbage-collection-definition/") PUT requests.
 * 
 * 
 * @author Cam Moore
 * 
 */
public class GarbageCollectionDefinitionPutServerResource extends WattDepotServerResource implements
    GarbageCollectionDefinitionPutResource {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.common.http.api.GarbageCollectionDefinitionPutResource#store
   * (org.wattdepot.common.domainmodel.GarbageCollectionDefinition)
   */
  @Override
  public void store(GarbageCollectionDefinition definition) {
    getLogger().log(
        Level.INFO,
        "PUT " + API.BASE_URI + "{" + orgId + "}/" + Labels.GARBAGE_COLLECTION_DEFINITION
            + "/ with " + definition);
    if (isInRole(orgId)) {
      try {
        depot.getOrganization(orgId, true);
      }
      catch (IdNotFoundException e1) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " is not a defined Organization.");
      }

    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Bad credentials");
    }
  }

}
