/**
 * SensorGroupResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.SensorGroup;

/**
 * SensorGroupResource - HTTP Interface for data model SensorGroup.
 * 
 * @author Cam Moore
 * 
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface SensorGroupResource {

  /**
   * Defines GET /wattdepot/sensor-group/{sensor-group-id} API call.
   * 
   * @return The SensorGroup with the given id. The id is sent in the request.
   */
  @Get("json") // Use JSON as transport encoding.
  public SensorGroup retrieve();

  /**
   * Defines the PUT /wattdepot/sensor-group/ API call.
   * 
   * @param sensorgroup
   *          The SensorGroup to store.
   */
  @Post
  public void update(SensorGroup sensorgroup);

  /**
   * Defined the DEL /wattdepot/sensor-group/{sensor-group-id} API call. The id is
   * sent in the request.
   */
  @Delete
  public void remove();

}
