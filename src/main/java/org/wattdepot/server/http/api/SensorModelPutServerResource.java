/**
 * SensorModelServerResource.java This file is part of WattDepot.
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
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.http.api.SensorModelPutResource;

/**
 * SensorModelServerResource - Handles the SensorModel HTTP PUT API
 * ("/wattdepot/public/sensor-model/").
 * 
 * @author Cam Moore
 * 
 */
public class SensorModelPutServerResource extends WattDepotServerResource
    implements SensorModelPutResource {

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.restlet.SensorModelResource#store(org.wattdepot
   * .datamodel.SensorModel)
   */
  @Override
  public void store(SensorModel sensormodel) {
    getLogger().log(Level.INFO,
        "PUT /wattdepot/public/sensor-model/ with " + sensormodel);
    if (!depot.getSensorModelIds().contains(sensormodel.getId())) {
      try {
        depot.defineSensorModel(sensormodel.getId(), sensormodel.getName(),
            sensormodel.getProtocol(), sensormodel.getType(),
            sensormodel.getVersion());
      }
      catch (UniqueIdException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
      catch (BadSlugException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
    }
    else {
      try {
        depot.updateSensorModel(sensormodel);
      }
      catch (IdNotFoundException e) {
        setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
      }
    }
  }
}
