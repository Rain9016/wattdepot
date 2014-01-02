/**
 * WattDepotImpl.java This file is part of WattDepot.
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
package org.wattdepot.server.depository.impl.hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.measure.unit.Unit;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.common.util.SensorModelHelper;
import org.wattdepot.common.util.Slug;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.WattDepotPersistence;

/**
 * WattDepotImpl - Hibernate implementation of the WattDepot abstract class.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotPersistenceImpl extends WattDepotPersistence {

  private int sessionOpen = 0;
  private int sessionClose = 0;

  /**
   * Creates a new WattDepotImpl instance with the given ServerProperties.
   * 
   * @param properties
   *          The ServerProperties.
   */
  public WattDepotPersistenceImpl(ServerProperties properties) {
    super();
    setServerProperties(properties);
    UserPasswordImpl adminPassword = (UserPasswordImpl) getUserPassword(
        UserInfo.ROOT.getUid(), UserInfo.ROOT.getOrganizationId());
    // if (getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    if (adminPassword == null) {
      try {
        defineUserPassword(UserPassword.ADMIN.getId(),
            UserPassword.ADMIN.getOrganizationId(),
            UserPassword.ADMIN.getPlainText());
        // if (getSessionClose() != getSessionOpen()) {
        // throw new RuntimeException("opens and closed mismatched.");
        // }
      }
      catch (UniqueIdException e1) {
        // what do we do here?
        e1.printStackTrace();
      }
    }
    else {
      updateUserPassword(adminPassword);
      // if (getSessionClose() != getSessionOpen()) {
      // throw new RuntimeException("opens and closed mismatched.");
      // }
    }
    Organization pub = getOrganization(Organization.PUBLIC_GROUP.getSlug());
    // if (getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    if (pub == null) {
      try {
        defineOrganization(Organization.PUBLIC_GROUP.getSlug(),
            new HashSet<String>());
        // if (getSessionClose() != getSessionOpen()) {
        // throw new RuntimeException("opens and closed mismatched.");
        // }
      }
      catch (UniqueIdException e) {
        // what do we do here?
        e.printStackTrace();
      }
    }
    else {
      updateOrganization(pub);
      // if (getSessionClose() != getSessionOpen()) {
      // throw new RuntimeException("opens and closed mismatched.");
      // }
    }
    Organization admin = getOrganization(Organization.ADMIN_GROUP.getSlug());
    // if (getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    if (admin == null) {
      try {
        defineOrganization(Organization.ADMIN_GROUP.getSlug(),
            Organization.ADMIN_GROUP.getUsers());
        // if (getSessionClose() != getSessionOpen()) {
        // throw new RuntimeException("opens and closed mismatched.");
        // }
      }
      catch (UniqueIdException e) {
        // what do we do here?
        e.printStackTrace();
      }
    }
    else {
      updateOrganization(admin);
      // if (getSessionClose() != getSessionOpen()) {
      // throw new RuntimeException("opens and closed mismatched.");
      // }
    }
    // UserInfo adminUser = getUser(UserInfo.ROOT.getId());
    // if (getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    // TODO: check on this we don't want to have the root user defined in the
    // database, but we need
    // them in the Restlet Application/Component.
    // if (adminUser == null) {
    // try {
    // defineUserInfo(UserInfo.ROOT.getId(), UserInfo.ROOT.getFirstName(),
    // UserInfo.ROOT.getLastName(), UserInfo.ROOT.getEmail(),
    // UserInfo.ROOT.getProperties());
    // if (getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    // }
    // catch (UniqueIdException e) {
    // // what do we do here?
    // e.printStackTrace();
    // }
    // }
    // else {
    // updateUserInfo(UserInfo.ROOT);
    // if (getSessionClose() != getSessionOpen()) {
    // throw new RuntimeException("opens and closed mismatched.");
    // }
    // }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#defineCollectorProcessDefinition(java.lang
   * .String, java.lang.String, java.lang.Long, java.lang.String,
   * java.lang.String)
   */
  @Override
  public CollectorProcessDefinition defineCollectorProcessDefinition(
      String name, String sensorId, Long pollingInterval, String depositoryId,
      String ownerId) throws UniqueIdException, MisMatchedOwnerException,
      IdNotFoundException {
    Sensor sensor = getSensor(sensorId, ownerId);
    if (sensor == null) {
      throw new IdNotFoundException(sensorId + " or " + ownerId
          + " are not defined.");
    }
    if (!ownerId.equals(sensor.getOwnerId())) {
      throw new MisMatchedOwnerException(ownerId + " does not own the sensor "
          + sensorId);
    }
    CollectorProcessDefinition cpd = null;
    String id = Slug.slugify(name);
    cpd = getCollectorProcessDefinition(id, Organization.ADMIN_GROUP_NAME);
    if (cpd != null) {
      throw new UniqueIdException(id
          + " is already a CollectorProcessDefinition id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    cpd = new CollectorProcessDefinition(name, sensorId, pollingInterval,
        depositoryId, ownerId);
    session.save(cpd);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return cpd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineLocation(java.lang.String,
   * java.lang.Double, java.lang.Double, java.lang.Double, java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorLocation defineLocation(String name, Double latitude,
      Double longitude, Double altitude, String description, String ownerId)
      throws UniqueIdException {
    String id = Slug.slugify(name);
    SensorLocation l = null;
    try {
      l = getLocation(id, ownerId);
    }
    catch (MisMatchedOwnerException e) {
      // ok as long as this owner doesn't own the location.
      e.printStackTrace();
    }
    if (l != null) {
      throw new UniqueIdException(id + " is already a Location id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    l = new SensorLocation(name, latitude, longitude, altitude, description,
        ownerId);
    session.save(l);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return l;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineMeasurementType(java.lang.String,
   * java.lang.String)
   */
  @Override
  public MeasurementType defineMeasurementType(String name, String units)
      throws UniqueIdException {
    String slug = Slug.slugify(name);
    MeasurementType mt = null;
    mt = getMeasurementType(slug);
    if (mt != null) {
      throw new UniqueIdException(slug + " is already a MeasurementType id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    Unit<?> unit = Unit.valueOf(units);
    mt = new MeasurementType(name, unit);
    session.save(mt);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return mt;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineOrganization(java.lang.String,
   * java.util.Set<String>)
   */
  @Override
  public Organization defineOrganization(String name, Set<String> users)
      throws UniqueIdException {
    String id = Slug.slugify(name);
    Organization g = getOrganization(id);
    if (g != null) {
      throw new UniqueIdException(id + " is already a Organization id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    // No longer need to update users since they are in the database.
    // for (String uid : users) {
    // UserInfo u = getUser(uid);
    // for (Property p : u.getProperties()) {
    // session.saveOrUpdate(p);
    // }
    // session.saveOrUpdate(u);
    // }
    g = new Organization(name, users);
    session.saveOrUpdate(g);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return g;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensor(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Sensor defineSensor(String name, String uri, String locationId,
      String modelId, String ownerId) throws UniqueIdException,
      MisMatchedOwnerException, IdNotFoundException {
    String slug = Slug.slugify(name);
    SensorLocation loc = getLocation(locationId, ownerId);
    SensorModel model = getSensorModel(modelId);
    if (loc == null) {
      throw new IdNotFoundException(locationId + " is not a SensorLocation id.");
    }
    if (model == null) {
      throw new IdNotFoundException(modelId + " is not a SensorModel id.");
    }
    if (!ownerId.equals(loc.getOwnerId())) {
      throw new MisMatchedOwnerException(ownerId
          + " does not match location's.");
    }
    Sensor s = null;
    s = getSensor(slug, Organization.ADMIN_GROUP_NAME);
    if (s != null) {
      throw new UniqueIdException(name + " is already a Sensor id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    s = new Sensor(name, uri, locationId, modelId, ownerId);
    saveSensor(session, s);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return s;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensorGroup(java.lang.String,
   * java.util.List, org.wattdepot.datamodel.Organization)
   */
  @Override
  public SensorGroup defineSensorGroup(String name, Set<String> sensorIds,
      String ownerId) throws UniqueIdException, MisMatchedOwnerException,
      IdNotFoundException {
    String id = Slug.slugify(name);
    for (String sensorId : sensorIds) {
      Sensor sensor = getSensor(sensorId, ownerId);
      if (sensor != null) {
        if (!ownerId.equals(sensor.getOwnerId())) {
          throw new MisMatchedOwnerException(ownerId
              + " is not the owner of all the sensors.");
        }
      }
      else {
        throw new IdNotFoundException(sensorId + " is not a defined Sensor id.");
      }
    }
    SensorGroup sg = null;
    sg = getSensorGroup(id, Organization.ADMIN_GROUP_NAME);
    if (sg != null) {
      throw new UniqueIdException(id + " is already a SensorGroup id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    sg = new SensorGroup(name, sensorIds, ownerId);
    session.save(sg);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return sg;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineSensorModel(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String,
   * org.wattdepot.datamodel.Organization)
   */
  @Override
  public SensorModel defineSensorModel(String name, String protocol,
      String type, String version) throws UniqueIdException {
    String id = Slug.slugify(name);
    SensorModel sm = null;
    sm = getSensorModel(id);
    if (sm != null) {
      throw new UniqueIdException(id + " is already a SensorModel id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    sm = new SensorModel(name, protocol, type, version);
    session.save(sm);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return sm;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineUserInfo(java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
   * java.lang.Boolean, java.util.Set)
   */
  @Override
  public UserInfo defineUserInfo(String id, String firstName, String lastName,
      String email, String orgId, Set<Property> properties)
      throws UniqueIdException {
    UserInfo u = getUser(id, orgId);
    if (u != null) {
      throw new UniqueIdException(id + " is already a UserInfo id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    u = new UserInfoImpl(id, firstName, lastName, email, orgId, properties);
    session.saveOrUpdate(u);
    for (Property p : properties) {
      session.saveOrUpdate(p);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return u;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineUserPassword(java.lang.String,
   * java.lang.String)
   */
  @Override
  public UserPassword defineUserPassword(String id, String orgId,
      String password) throws UniqueIdException {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    UserPasswordImpl up = new UserPasswordImpl(id, orgId, password);
    session.saveOrUpdate(up);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return up;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#defineWattDepository(java.lang.String,
   * java.lang.String, java.lang.String, org.wattdepot.datamodel.Organization)
   */
  @Override
  public Depository defineWattDepository(String name,
      MeasurementType measurementType, String ownerId) throws UniqueIdException {
    String id = Slug.slugify(name);
    Depository d = null;
    try {
      d = getWattDeposiory(id, ownerId);
    }
    catch (MisMatchedOwnerException e) {
      throw new UniqueIdException(name + " is used by another owner.");
    }
    if (d != null) {
      throw new UniqueIdException(name + " is already a Depository name.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    d = new DepositoryImpl(name, measurementType, ownerId);
    session.saveOrUpdate(d);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return d;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#deleteCollectorProcessDefinition(java.lang
   * .String, java.lang.String)
   */
  @Override
  public void deleteCollectorProcessDefinition(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
    CollectorProcessDefinition s = getCollectorProcessDefinition(id, groupId);
    if (s != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(s);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found for owner " + groupId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteLocation(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteLocation(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
    SensorLocation l = getLocation(id, groupId);
    if (l != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(l);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found for owner " + groupId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteMeasurementType(java.lang.String)
   */
  @Override
  public void deleteMeasurementType(String slug) throws IdNotFoundException {
    MeasurementType mt = getMeasurementType(slug);
    if (mt != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(mt);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(slug + " was not found.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteOrganization(java.lang.String)
   */
  @Override
  public void deleteOrganization(String id) throws IdNotFoundException {
    Organization g = getOrganization(id);
    if (g == null) {
      throw new IdNotFoundException(id + " is not a defined user group id.");
    }
    // Remove Organization owned CollectorProcessDefinitions
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (CollectorProcessDefinition sp : getCollectorProcessDefinitions(
        session, id)) {
      session.delete(sp);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned SensorGroups
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorGroup sg : getSensorGroups(session, id)) {
      session.delete(sg);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned Measurements
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (Depository d : getWattDepositories(session, id)) {
      DepositoryImpl impl = new DepositoryImpl(d);
      for (String sensorId : impl.listSensors(session)) {
        for (Measurement m : impl.getMeasurements(session, sensorId)) {
          session.delete(m);
        }
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned Depositories and Sensors
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<Depository> depositories = getWattDepositories(session, id);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (Depository d : depositories) {
      session.delete(d);
    }
    for (Sensor s : getSensors(session, id)) {
      session.delete(s);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned SensorModels
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorModel sm : getSensorModels(session)) {
      if (!SensorModelHelper.models.containsKey(sm.getName())) {
        session.delete(sm);
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Organization owned SensorLocations
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (SensorLocation l : getLocations(session, id)) {
      session.delete(l);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove Users in the Organization
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (UserInfoImpl u : getUsers(session, id)) {
      session.delete(u);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove UserPasswords in the Organization
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    for (UserPasswordImpl u : getUserPasswords(session, id)) {
      session.delete(u);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    // Remove the organization
    session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(g);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteSensor(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensor(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
    Sensor s = getSensor(id, groupId);
    if (s != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(s);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found for owner " + groupId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteSensorGroup(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensorGroup(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
    SensorGroup s = getSensorGroup(id, groupId);
    if (s != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(s);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found for owner " + groupId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteSensorModel(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteSensorModel(String id) throws IdNotFoundException {
    SensorModel s = getSensorModel(id);
    if (s != null) {
      Session session = Manager.getFactory(getServerProperties()).openSession();
      sessionOpen++;
      session.beginTransaction();
      session.delete(s);
      session.getTransaction().commit();
      session.close();
      sessionClose++;
    }
    else {
      throw new IdNotFoundException(id + " was not found.");
    }
  }

  // /**
  // * Deletes all the objects that have group as their owner.
  // *
  // * @param session
  // * The Session, a transaction must be in progress.
  // * @param group
  // * The Organization to delete.
  // */
  // private void deleteOrganization(Session session, Organization group) {
  // for (CollectorProcessDefinition sp :
  // getCollectorProcessDefinitions(session, group.getId()))
  // {
  // session.delete(sp);
  // }
  // for (SensorGroup sg : getSensorGroups(session, group.getId())) {
  // session.delete(sg);
  // }
  // for (Depository d : getWattDepositories(session, group.getId())) {
  // for (Sensor s : d.listSensors(session)) {
  // for (Measurement m : d.getMeasurements(session, s)) {
  // session.delete(m);
  // }
  // }
  // session.delete(d);
  // }
  // for (Sensor s : getSensors(session, group.getId())) {
  // session.delete(s);
  // }
  // for (SensorModel sm : getSensorModels(session, group.getId())) {
  // session.delete(sm);
  // }
  // for (SensorLocation l : getLocations(session, group.getId())) {
  // session.delete(l);
  // }
  // session.delete(group);
  // }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteUser(java.lang.String)
   */
  @Override
  public void deleteUser(String id, String orgId) throws IdNotFoundException {
    UserInfoImpl u = (UserInfoImpl) getUser(id, orgId);
    if (u == null) {
      throw new IdNotFoundException(id + " is not a defined user id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(u);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteUserPassword(java.lang.String)
   */
  @Override
  public void deleteUserPassword(String userId, String orgId)
      throws IdNotFoundException {
    UserPasswordImpl up = (UserPasswordImpl) getUserPassword(userId, orgId);
    if (up == null) {
      throw new IdNotFoundException(userId
          + " is not a defined user password id.");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(up);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#deleteWattDepository(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void deleteWattDepository(String id, String groupId)
      throws IdNotFoundException, MisMatchedOwnerException {
    Depository d = getWattDeposiory(id, groupId);
    if (d == null) {
      throw new IdNotFoundException(id + " is not a defined depository");
    }
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.delete(d);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#getCollectorProcessDefinition(java.lang.
   * String, java.lang.String)
   */
  @Override
  public CollectorProcessDefinition getCollectorProcessDefinition(String id,
      String groupId) throws MisMatchedOwnerException {
    for (CollectorProcessDefinition s : getCollectorProcessDefinitions(Organization.ADMIN_GROUP_NAME)) {
      if (s.getSlug().equals(id)) {
        if (s.getOwnerId().equals(groupId)
            || groupId.contains(Organization.ADMIN_GROUP_NAME)) {
          return s;
        }
        else {
          throw new MisMatchedOwnerException(id + " is not owned by " + groupId);
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getCollectorProcessDefinitionIds()
   */
  @Override
  public List<String> getCollectorProcessDefinitionIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (CollectorProcessDefinition s : getCollectorProcessDefinitions(groupId)) {
      ret.add(s.getSlug());
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return a List of CollectorProcessDefinitions owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<CollectorProcessDefinition> getCollectorProcessDefinitions(
      Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from CollectorProcessDefinition").list();
    ArrayList<CollectorProcessDefinition> ret = new ArrayList<CollectorProcessDefinition>();
    for (CollectorProcessDefinition d : (List<CollectorProcessDefinition>) result) {
      if (groupId.equals(Organization.ADMIN_GROUP_NAME)
          || groupId.equals(d.getOwnerId())) {
        ret.add(d);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#getCollectorProcessDefinitions(java.lang
   * .String)
   */
  @Override
  public List<CollectorProcessDefinition> getCollectorProcessDefinitions(
      String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<CollectorProcessDefinition> ret = getCollectorProcessDefinitions(
        session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocation(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorLocation getLocation(String id, String groupId)
      throws MisMatchedOwnerException {
    // search through all the known locations
    for (SensorLocation l : getLocations(Organization.ADMIN_GROUP_NAME)) {
      if (l.getSlug().equals(id)) {
        if (l.getOwnerId().equals(groupId)
            || groupId.equals(Organization.ADMIN_GROUP_NAME)) {
          return l;
        }
        else {
          throw new MisMatchedOwnerException(id + " is not owned by " + groupId);
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocationIds()
   */
  @Override
  public List<String> getLocationIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorLocation l : getLocations(groupId)) {
      ret.add(l.getSlug());
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return A List of the Locations owned by the groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorLocation> getLocations(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from SensorLocation").list();
    ArrayList<SensorLocation> ret = new ArrayList<SensorLocation>();
    for (SensorLocation d : (List<SensorLocation>) result) {
      if (groupId.equals(Organization.ADMIN_GROUP_NAME)
          || groupId.equals(d.getOwnerId())) {
        ret.add(d);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getLocations(java.lang.String)
   */
  @Override
  public List<SensorLocation> getLocations(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorLocation> ret = getLocations(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getMeasurementType(java.lang.String)
   */
  @Override
  public MeasurementType getMeasurementType(String slug) {
    for (MeasurementType mt : getMeasurementTypes()) {
      if (mt.getSlug().equals(slug)) {
        return mt;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getMeasurementTypes()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<MeasurementType> getMeasurementTypes() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from MeasurementType").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    ArrayList<MeasurementType> ret = new ArrayList<MeasurementType>();
    for (MeasurementType mt : (List<MeasurementType>) result) {
      ret.add(mt);
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getOrganization(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Organization getOrganization(String id) {
    Organization ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from Organization").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    for (Organization g : (List<Organization>) result) {
      if (id.equals(g.getSlug())) {
        ret = g;
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)m
   * 
   * @see org.wattdepot.server.WattDepot#getOrganizationIds()
   */
  @Override
  public List<String> getOrganizationIds() {
    ArrayList<String> ret = new ArrayList<String>();
    for (Organization u : getOrganizations()) {
      ret.add(u.getSlug());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getOrganizations()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<Organization> getOrganizations() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from Organization").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return (List<Organization>) result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensor(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Sensor getSensor(String id, String groupId)
      throws MisMatchedOwnerException {
    for (Sensor s : getSensors(Organization.ADMIN_GROUP_NAME)) {
      if (s.getSlug().equals(id)) {
        if (s.getOwnerId().equals(groupId)
            || groupId.contains(Organization.ADMIN_GROUP_NAME)) {
          Hibernate.initialize(s.getSensorLocationId());
          Hibernate.initialize(s.getModelId());
          return s;
        }
        else {
          throw new MisMatchedOwnerException(id + " is not owned by " + groupId);
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroup(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorGroup getSensorGroup(String id, String groupId)
      throws MisMatchedOwnerException {
    for (SensorGroup s : getSensorGroups(Organization.ADMIN_GROUP_NAME)) {
      if (s.getSlug().equals(id)) {
        if (s.getOwnerId().equals(groupId)
            || groupId.contains(Organization.ADMIN_GROUP_NAME)) {
          return s;
        }
        else {
          throw new MisMatchedOwnerException(id + " is not owned by " + groupId);
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroupIds()
   */
  @Override
  public List<String> getSensorGroupIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorGroup s : getSensorGroups(groupId)) {
      ret.add(s.getSlug());
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return a List of the SensorGroups owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorGroup> getSensorGroups(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from SensorGroup").list();
    ArrayList<SensorGroup> ret = new ArrayList<SensorGroup>();
    for (SensorGroup d : (List<SensorGroup>) result) {
      if (groupId.equals(Organization.ADMIN_GROUP_NAME)
          || groupId.equals(d.getOwnerId())) {
        ret.add(d);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorGroups(java.lang.String)
   */
  @Override
  public List<SensorGroup> getSensorGroups(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorGroup> ret = getSensorGroups(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorIds()
   */
  @Override
  public List<String> getSensorIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (Sensor s : getSensors(groupId)) {
      ret.add(s.getSlug());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorModel(java.lang.String,
   * java.lang.String)
   */
  @Override
  public SensorModel getSensorModel(String id) {
    for (SensorModel s : getSensorModels()) {
      if (s.getSlug().equals(id)) {
        return s;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorModelIds()
   */
  @Override
  public List<String> getSensorModelIds() {
    ArrayList<String> ret = new ArrayList<String>();
    for (SensorModel s : getSensorModels()) {
      ret.add(s.getSlug());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensorModels(java.lang.String)
   */
  @Override
  public List<SensorModel> getSensorModels() {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<SensorModel> ret = getSensorModels(session);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @return A List of the SensorModels owned by the groupId.
   */
  @SuppressWarnings("unchecked")
  private List<SensorModel> getSensorModels(Session session) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from SensorModel").list();
    ArrayList<SensorModel> ret = new ArrayList<SensorModel>();
    for (SensorModel d : (List<SensorModel>) result) {
      ret.add(d);
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return A List of the Sensors owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<Sensor> getSensors(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from Sensor").list();
    ArrayList<Sensor> ret = new ArrayList<Sensor>();
    for (Sensor d : (List<Sensor>) result) {
      if (groupId.equals(Organization.ADMIN_GROUP_NAME)
          || groupId.equals(d.getOwnerId())) {
        ret.add(d);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getSensors(java.lang.String)
   */
  @Override
  public List<Sensor> getSensors(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<Sensor> ret = getSensors(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /**
   * @return the sessionClose
   */
  public int getSessionClose() {
    return sessionClose;
  }

  /**
   * @return the sessionOpen
   */
  public int getSessionOpen() {
    return sessionOpen;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUser(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  @Override
  public UserInfo getUser(String id, String orgId) {
    UserInfoImpl ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserInfoImpl").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    for (UserInfoImpl u : (List<UserInfoImpl>) result) {
      if (id.equals(u.getUid()) && orgId.equals(u.getOrganizationId())) {
        ret = u;
      }
    }
    return ret;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @param orgId
   *          The organization id.
   * @return a List of the users in the given organization.
   */
  @SuppressWarnings("unchecked")
  private List<UserInfoImpl> getUsers(Session session, String orgId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserInfoImpl").list();
    ArrayList<UserInfoImpl> ret = new ArrayList<UserInfoImpl>();
    for (UserInfoImpl u : (List<UserInfoImpl>) result) {
      if (u.getOrganizationId().equals(orgId)) {
        ret.add(u);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserIds()
   */
  @Override
  public List<String> getUserIds(String orgId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (UserInfo u : getUsers(orgId)) {
      ret.add(u.getUid());
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUserPassword(java.lang.String)
   */
  @Override
  public UserPassword getUserPassword(String id, String orgId) {
    UserPasswordImpl ret = null;
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("unchecked")
    List<UserPasswordImpl> result = (List<UserPasswordImpl>) session
        .createQuery("from UserPasswordImpl").list();
    for (UserPasswordImpl up : result) {
      if (up.getId().equals(id)) {
        ret = up;
      }
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /**
   * @param session
   *          The Session with an open transaction.
   * @param orgId
   *          The organization id.
   * @return a List of the user passwords in the given organization.
   */
  @SuppressWarnings("unchecked")
  private List<UserPasswordImpl> getUserPasswords(Session session, String orgId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserPasswordImpl").list();
    ArrayList<UserPasswordImpl> ret = new ArrayList<UserPasswordImpl>();
    for (UserPasswordImpl u : (List<UserPasswordImpl>) result) {
      if (u.getOrganizationId() != null && u.getOrganizationId().equals(orgId)) {
        ret.add(u);
      }
    }
    return ret;
  }


  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUsers()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<UserInfo> getUsers(String orgId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from UserInfoImpl").list();
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    ArrayList<UserInfo> ret = new ArrayList<UserInfo>();
    for (UserInfoImpl u : (List<UserInfoImpl>) result) {
      if (u.getOrganizationId().equals(orgId)) {
        ret.add(u);
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getUsersGroup(org.wattdepot.datamodel.
   * UserInfo)
   */
  @Override
  public Organization getUsersGroup(UserInfo user) {
    for (Organization group : getOrganizations()) {
      if (group.getUsers().contains(user.getUid())) {
        return group;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDeposiory(java.lang.String,
   * java.lang.String)
   */
  @Override
  public Depository getWattDeposiory(String id, String groupId)
      throws MisMatchedOwnerException {
    List<Depository> all = getWattDepositories(groupId);
    Depository ret = null;
    for (Depository d : all) {
      if (d.getSlug().equals(id)) {
        ret = new DepositoryImpl(d);
      }
    }
    return ret;
  }

  /**
   * @param session
   *          The session with an open transaction.
   * @param groupId
   *          The group id.
   * @return A List of the Depositories owned by groupId.
   */
  @SuppressWarnings("unchecked")
  private List<Depository> getWattDepositories(Session session, String groupId) {
    @SuppressWarnings("rawtypes")
    List result = session.createQuery("from DepositoryImpl").list();
    ArrayList<Depository> ret = new ArrayList<Depository>();
    for (Depository d : (List<Depository>) result) {
      if (groupId.equals(Organization.ADMIN_GROUP_NAME)
          || groupId.equals(d.getOwnerId())) {
        ret.add(new DepositoryImpl(d));
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositories(java.lang.String)
   */
  @Override
  public List<Depository> getWattDepositories(String groupId) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    List<Depository> ret = getWattDepositories(session, groupId);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#getWattDepositoryIds()
   */
  @Override
  public List<String> getWattDepositoryIds(String groupId) {
    ArrayList<String> ret = new ArrayList<String>();
    for (Depository d : getWattDepositories(groupId)) {
      ret.add(d.getName());
    }
    return ret;
  }

  /**
   * Use this method after beginning a transaction.
   * 
   * @param session
   *          The Session, a transaction must be in progress.
   * @param sensor
   *          The Sensor to save.
   */
  private void saveSensor(Session session, Sensor sensor) {
    for (Property p : sensor.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.saveOrUpdate(sensor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateCollectorProcessDefinition(org.wattdepot
   * . datamodel .CollectorProcessDefinition)
   */
  @Override
  public CollectorProcessDefinition updateCollectorProcessDefinition(
      CollectorProcessDefinition process) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(process);
    for (Property p : process.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return process;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#updateLocation(org.wattdepot.datamodel
   * .Location)
   */
  @Override
  public SensorLocation updateLocation(SensorLocation loc) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(loc);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return loc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateMeasurementType(org.wattdepot.datamodel
   * .MeasurementType)
   */
  @Override
  public MeasurementType updateMeasurementType(MeasurementType type) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(type);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateOrganization(org.wattdepot.datamodel
   * .Organization)
   */
  @Override
  public Organization updateOrganization(Organization group) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(group);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensor(org.wattdepot.datamodel.Sensor
   * )
   */
  @Override
  public Sensor updateSensor(Sensor sensor) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(sensor);
    for (Property p : sensor.getProperties()) {
      session.saveOrUpdate(p);
    }
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return sensor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensorGroup(org.wattdepot.datamodel
   * .SensorGroup)
   */
  @Override
  public SensorGroup updateSensorGroup(SensorGroup group) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(group);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateSensorModel(org.wattdepot.datamodel
   * .SensorModel)
   */
  @Override
  public SensorModel updateSensorModel(SensorModel model) {
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(model);
    session.getTransaction().commit();
    session.close();
    sessionClose++;

    return model;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.wattdepot.server.WattDepot#updateUserInfo(org.wattdepot.datamodel
   * .UserInfo)
   */
  @Override
  public UserInfo updateUserInfo(UserInfo user) {
    UserInfoImpl ui = (UserInfoImpl) getUser(user.getUid(),
        user.getOrganizationId());
    if (ui == null) {
      ui = new UserInfoImpl();
    }
    ui.setUid(user.getUid());
    ui.setFirstName(user.getFirstName());
    ui.setLastName(user.getLastName());
    ui.setEmail(user.getEmail());
    ui.setOrganizationId(user.getOrganizationId());
    ui.setProperties(user.getProperties());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(ui);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return ui;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.wattdepot.server.WattDepot#updateUserPassword(org.wattdepot.datamodel
   * .UserPassword)
   */
  @Override
  public UserPassword updateUserPassword(UserPassword password) {
    UserPasswordImpl upi = (UserPasswordImpl) getUserPassword(password.getId(),
        password.getOrganizationId());
    if (upi == null) {
      upi = new UserPasswordImpl();
    }
    upi.setId(password.getId());
    upi.setOrganizationId(password.getOrganizationId());
    upi.setPlainText(password.getPlainText());
    Session session = Manager.getFactory(getServerProperties()).openSession();
    sessionOpen++;
    session.beginTransaction();
    session.saveOrUpdate(upi);
    session.getTransaction().commit();
    session.close();
    sessionClose++;
    return password;
  }
}
