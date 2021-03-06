/**
 * UserInfoResource.java This file is part of WattDepot.
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

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.wattdepot.common.domainmodel.UserInfo;

/**
 * UserResource - HTTP /wattdepot/{org-id}/user/{user-id} Interface for data
 * model UserInfo.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface UserInfoResource {

  /**
   * Defines GET /wattdepot/{org-id}/user/{user-id} API call.
   * 
   * @return The User with the given id. The id is sent in the request.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public UserInfo retrieve();

  /**
   * Defines the POST /wattdepot/{org-id}/user/{user-id} API call.
   * 
   * @param user
   *          The UserInfo to update.
   */
  @Post("json")
  public void update(UserInfo user);

  /**
   * Defined the DEL /wattdepot/user/{user_id} API call. The id is sent in the
   * request.
   */
  @Delete
  public void remove();

}
