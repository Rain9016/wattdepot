/**
 * GvizResource.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Yongwen Xu
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

import org.restlet.resource.Get;

/**
 * GvizDepositoryValueResource - HTTP Interface for getting the Google Visualization 
 * Data Source representation for Depository value.
 * (/wattdepot/{org-id}/depository/{depository-id}/value/gviz/) * 
 * @author Yongwen Xu
 * 
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface GvizDepositoryValueResource {

  /**
   * Defines GET
   * /wattdepot/{org-id}/depository/{depository-id}/value/gviz/?
   *    sensor={sensorId}&start={start}&end={end}&gap={gapSeconds}
   * <br/> or GET
   * /wattdepot/{org-id}/depository/{depository-id}/value/gviz/?
   *    sensor={sensorId}&timestamp={timestamp}&gap={gapSeconds}
   * <br/> or GET
   * /wattdepot/{org-id}/depository/{depository-id}/value/gviz/?
   *    sensor={sensorId}&latest=true
   * <br/> or GET
   * /wattdepot/{org-id}/depository/{depository-id}/value/gviz/?
   *    sensor={sensorId}&earliest=true
   *    
   * @return Google Visualization data source.
   */
  @Get("json")
  // Use JSON as transport encoding.
  public String retrieve();
}
