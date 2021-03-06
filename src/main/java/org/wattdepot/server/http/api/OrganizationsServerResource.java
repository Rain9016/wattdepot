/**
 * UserGroupsServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.OrganizationList;
import org.wattdepot.common.http.api.OrganizationsResource;

/**
 * UserGroupsServerResource - Handles the UserGroup HTTP API
 * ("/wattdepot/{org-id}/organizations/").
 * 
 * @author Cam Moore
 * 
 */
public class OrganizationsServerResource extends WattDepotServerResource implements
    OrganizationsResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.UserGroupsResource#retrieve()
   */
  @Override
  public OrganizationList retrieve() {
    getLogger().log(Level.INFO, "GET /wattdepot/{" + orgId + "}/organizations/");
    OrganizationList ret = new OrganizationList();
    if (isInRole(Organization.ADMIN_GROUP.getId())) {
      for (Organization o : depot.getOrganizations()) {
        ret.getOrganizations().add(o);
      }
    }
    else {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Non Admins can't view Organizations");
        return null;
    }
    return ret;
  }

}
