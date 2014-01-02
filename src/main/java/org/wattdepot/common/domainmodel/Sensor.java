/**
 * Sensor.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

import java.util.HashSet;
import java.util.Set;

import org.wattdepot.common.util.Slug;

/**
 * Sensor - Represents the device making measurements.
 * 
 * @author Cam Moore
 * 
 */
public class Sensor implements IDomainModel {
  /** A unique slug for the Sensor usable in URLs. */
  private String slug;
  /** The name of the Sensor. */
  private String name;
  /** The URI to the sensor. */
  private String uri;
  /** The location of the sensor. */
  private String locationId;
  /** The id of the model of the sensor. */
  private String modelId;
  /** Additional properties of the sensor. */
  private Set<Property> properties;
  /** The owner of this sensor. */
  private String ownerId;

  /**
   * Default constructor.
   */
  public Sensor() {
    this.properties = new HashSet<Property>();
  }

  /**
   * @param name
   *          The name.
   * @param uri
   *          The URI to the meter.
   * @param locationId
   *          The id of the meter's location.
   * @param modelId
   *          The id of the meter's model.
   * @param ownerId
   *          the id of the owner of the sensor.
   */
  public Sensor(String name, String uri, String locationId, String modelId,
      String ownerId) {
    this.slug = Slug.slugify(name);
    this.name = name;
    this.uri = uri;
    this.locationId = locationId;
    this.modelId = modelId;
    this.properties = new HashSet<Property>();
    this.ownerId = ownerId;
  }

  /**
   * @param e
   *          The Property to add.
   * @return true if added.
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean addProperty(Property e) {
    return properties.add(e);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Sensor other = (Sensor) obj;
    if (slug == null) {
      if (other.slug != null) {
        return false;
      }
    }
    else if (!slug.equals(other.slug)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (locationId == null) {
      if (other.locationId != null) {
        return false;
      }
    }
    else if (!locationId.equals(other.locationId)) {
      return false;
    }
    if (modelId == null) {
      if (other.modelId != null) {
        return false;
      }
    }
    else if (!modelId.equals(other.modelId)) {
      return false;
    }
    if (ownerId == null) {
      if (other.ownerId != null) {
        return false;
      }
    }
    else if (!ownerId.equals(other.ownerId)) {
      return false;
    }
    if (properties == null) {
      if (other.properties != null) {
        return false;
      }
    }
    else if (!properties.equals(other.properties)) {
      return false;
    }
    if (uri == null) {
      if (other.uri != null) {
        return false;
      }
    }
    else if (!uri.equals(other.uri)) {
      return false;
    }
    return true;
  }

  /**
   * @return the id
   */
  public String getSlug() {
    return slug;
  }

  /**
   * @return the modelId
   */
  public String getModelId() {
    return modelId;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the id of the owner
   */
  public String getOwnerId() {
    return ownerId;
  }

  /**
   * @return the properties
   */
  public Set<Property> getProperties() {
    return properties;
  }

  /**
   * @param key
   *          The key.
   * @return The value of associated with the key.
   */
  public Property getProperty(String key) {
    for (Property p : properties) {
      if (p.getKey().equals(key)) {
        return p;
      }
    }
    return null;
  }

  /**
   * @return the sensorLocation
   */
  public String getSensorLocationId() {
    return locationId;
  }

  /**
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((slug == null) ? 0 : slug.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
    result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
    result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
    result = prime * result + ((properties == null) ? 0 : properties.hashCode());
    result = prime * result + ((uri == null) ? 0 : uri.hashCode());
    return result;
  }

  /**
   * Determines if the given group is the owner of this location.
   * 
   * @param group
   *          the UserGroup to check.
   * @return True if the group owns the Location or the group is the
   *         ADMIN_GROUP.
   */
  public boolean isOwner(Organization group) {
    if (ownerId != null && (ownerId.equals(group.getSlug()) || group.equals(Organization.ADMIN_GROUP))) {
      return true;
    }
    return false;
  }

  /**
   * @param o
   *          The Property to remove.
   * @return true if removed.
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean removeProperty(Object o) {
    return properties.remove(o);
  }

  /**
   * @param id
   *          the id to set
   */
  public void setSlug(String id) {
    this.slug = id;
  }

  /**
   * @param model
   *          the id of the model to set
   */
  public void setModelId(String model) {
    this.modelId = model;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
    if (this.slug == null) {
      this.slug = Slug.slugify(name);
    }
  }

  /**
   * @param ownerId
   *          the id of the owner to set
   */
  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * @param properties
   *          the properties to set
   */
  public void setProperties(Set<Property> properties) {
    this.properties = properties;
  }

  /**
   * @param sensorLocation
   *          the sensorLocation to set
   */
  public void setSensorLocationId(String sensorLocation) {
    this.locationId = sensorLocation;
  }

  /**
   * @param uri
   *          the uri to set
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Sensor [slug=" + slug + ", name=" + name + ", uri=" + uri + ", locationId="
        + locationId + ", modelId=" + modelId + ", properties=" + properties + "]";
  }

}
