/**
 * DepositoryMeasurementResource.java This file is part of WattDepot.
 * <p/>
 * Copyright (C) 2013  Cam Moore
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.common.http.api;

import org.restlet.resource.Put;
import org.wattdepot.common.domainmodel.MeasurementList;

/**
 * DepositoryMeasurementsPutResource - HTTP Interface for storing, deleting, getting
 * a MeasurementList.
 *
 *
 * @author John Smedegaard
 */
@SuppressWarnings("PMD.UnusedModifier")
public interface DepositoryMeasurementsPutResource {

  /** The maximum number of measurements you can put at a single time. */
  public static final Integer MAX_NUM_MEASUREMENTS = 10000;

  /**
   * Defines PUT /wattdepot/{org-id}/depository/{depository_id}/measurements/
   * API call.
   *
   * @param measurementList
   *          The MeasurementList to store.
   */
  @Put
  public void store(MeasurementList measurementList);

}
