/**
 * CollectorProcessDefinitionServerResource.java This file is part of WattDepot.
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
package org.wattdepot.server.http.api;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.CollectorProcessDefinitionPutResource;

/**
 * CollectorProcessDefinitionServerResource - Handles the
 * CollectorProcessDefinition HTTP API
 * (("/wattdepot/{org-id}/collector-metadata/",
 * "/wattdepot/{org-id}/collector-metadata/{collector-metadata-id}").
 * 
 * @author Cam Moore
 * 
 */
public class CollectorProcessDefinitionPutServerResource extends WattDepotServerResource implements
    CollectorProcessDefinitionPutResource {
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.restlet.CollectorProcessDefinitionResource#store(org.wattdepot
   * .datamodel.CollectorProcessDefinition)
   */
  @Override
  public void store(CollectorProcessDefinition metadata) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/{" + orgId + "}/collector-metadata/ with " + metadata);
    Organization owner = depot.getOrganization(orgId);
    if (owner != null) {
      if (!depot.getCollectorProcessDefinitionIds(orgId).contains(metadata.getSlug())) {
        try {
          Sensor s = depot.getSensor(metadata.getSensorId(), orgId);
          if (s != null) {
            depot.defineCollectorProcessDefinition(metadata.getName(), s.getSlug(),
                metadata.getPollingInterval(), metadata.getDepositoryId(), owner.getSlug());
          }
          else {
            setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, "Sensor " + metadata.getSensorId()
                + " is not defined.");
          }
        }
        catch (UniqueIdException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (MisMatchedOwnerException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
        catch (IdNotFoundException e) {
          setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY, e.getMessage());
        }
      }
      else {
        setStatus(Status.CLIENT_ERROR_CONFLICT, "CollectorProcessDefinition " + metadata.getName()
            + " is already defined.");
      }
    }
    else {
      setStatus(Status.CLIENT_ERROR_BAD_REQUEST, orgId + " does not exist.");
    }
  }
}
