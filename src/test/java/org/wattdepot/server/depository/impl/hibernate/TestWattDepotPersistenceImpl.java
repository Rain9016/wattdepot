/**
 * TestWattDepotImpl.java This file is part of WattDepot.
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.common.domainmodel.CollectorProcessDefinition;
import org.wattdepot.common.domainmodel.Depository;
import org.wattdepot.common.domainmodel.InstanceFactory;
import org.wattdepot.common.domainmodel.MeasuredValue;
import org.wattdepot.common.domainmodel.Measurement;
import org.wattdepot.common.domainmodel.MeasurementType;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.Sensor;
import org.wattdepot.common.domainmodel.SensorGroup;
import org.wattdepot.common.domainmodel.SensorLocation;
import org.wattdepot.common.domainmodel.SensorModel;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.BadSlugException;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.exception.MeasurementGapException;
import org.wattdepot.common.exception.MeasurementTypeException;
import org.wattdepot.common.exception.MisMatchedOwnerException;
import org.wattdepot.common.exception.NoMeasurementException;
import org.wattdepot.common.exception.UniqueIdException;
import org.wattdepot.server.ServerProperties;

/**
 * TestWattDepotImpl - Test cases for the WattDepotImpl.
 * 
 * @author Cam Moore
 * 
 */
public class TestWattDepotPersistenceImpl {

  private static WattDepotPersistenceImpl impl;
  private static UserInfo testUser;
  private static UserPassword testPassword;
  private static Organization testOrg;

  /**
   * Sets up the WattDepotPersistenceImpl using test properties.
   */
  @BeforeClass
  public static void setupImpl() {
    ServerProperties properties = new ServerProperties();
    properties.setTestProperties();
    impl = new WattDepotPersistenceImpl(properties);
    testUser = InstanceFactory.getUserInfo();
    testPassword = InstanceFactory.getUserPassword();
    testOrg = InstanceFactory.getOrganization();
    // shouldn't have to do this.
    testUser.setOrganizationId(testOrg.getId());
  }

  /**
   * Sets up the test case by ensuring we have test user, password and
   * organization.
   * 
   * @throws UniqueIdException
   *           if there is a big problem with the database.
   */
  @Before
  public void setUp() throws UniqueIdException {
    try {
      impl.getOrganization(testOrg.getId());
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineOrganization(testOrg.getId(), testOrg.getName(), testOrg.getUsers());
      }
      catch (BadSlugException e1) {
        e1.printStackTrace();
      }
    }
    UserInfo testU = impl.getUser(testUser.getUid(),
        testUser.getOrganizationId());
    if (testU == null) {
      impl.defineUserInfo(testUser.getUid(), testUser.getFirstName(),
          testUser.getLastName(), testUser.getEmail(),
          testUser.getOrganizationId(), testUser.getProperties());
    }
    UserPassword testP = impl.getUserPassword(testPassword.getUid(),
        testPassword.getOrganizationId());
    if (testP == null) {
      impl.defineUserPassword(testPassword.getUid(),
          testPassword.getOrganizationId(), testPassword.getPlainText());
    }
  }

  /**
   * Cleans up the test user, password, and organization.
   * 
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
    UserPassword testP = impl.getUserPassword(testPassword.getUid(),
        testPassword.getOrganizationId());
    if (testP != null) {
      impl.deleteUserPassword(testP.getUid(), testPassword.getOrganizationId());
    }
    UserInfo testU = impl.getUser(testUser.getUid(),
        testUser.getOrganizationId());
    if (testU != null) {
      impl.deleteUser(testU.getUid(), testU.getOrganizationId());
    }
    Organization testO = impl.getOrganization(testOrg.getId());
    if (testO != null) {
      impl.deleteOrganization(testO.getId());
    }
  }

  /**
   * Test methods for dealing with CollectorProcessDefinitions.
   * 
   */
  @Test
  public void testCollectorProcessDefinitions() {
    // get the list of CollectorProcessDefinitions
    List<CollectorProcessDefinition> list = impl
        .getCollectorProcessDefinitions(testOrg.getId());
    int numCollector = list.size();
    assertTrue(numCollector >= 0);
    // Create a new instance
    CollectorProcessDefinition cpd = InstanceFactory
        .getCollectorProcessDefinition();
    try {
      impl.defineCollectorProcessDefinition(cpd.getSensorId(), cpd.getName(),
          cpd.getSensorId(), cpd.getPollingInterval(), cpd.getDepositoryId(),
          cpd.getProperties(), cpd.getOwnerId());
    }
    catch (UniqueIdException e) {
      fail(cpd.getId() + " should not exist in persistence.");
    }
    catch (MisMatchedOwnerException e) {
      fail(cpd.getId() + " should be owned by " + cpd.getOwnerId() + ".");
    }
    catch (BadSlugException e) {
      fail(cpd.getId() + " is valid.");
    }
    catch (IdNotFoundException e) {
      // This is expected.
      try {
        addSensor();
        addMeasurementType();
        addDepository();
      }
      catch (MisMatchedOwnerException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " shouldn't happen");
      }
    }
    try {
      impl.defineCollectorProcessDefinition(cpd.getId(), cpd.getName(),
          cpd.getSensorId(), cpd.getPollingInterval(), cpd.getDepositoryId(),
          cpd.getProperties(), cpd.getOwnerId());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    // try to define it again.
    try {
      impl.defineCollectorProcessDefinition(cpd.getId(), cpd.getName(),
          cpd.getSensorId(), cpd.getPollingInterval(), cpd.getDepositoryId(),
          cpd.getProperties(), cpd.getOwnerId());
    }
    catch (UniqueIdException e2) {
      // expected result
    }
    catch (MisMatchedOwnerException e2) {
      e2.printStackTrace();
      fail(e2.getMessage() + " shouldn't happen");
    }
    catch (IdNotFoundException e2) {
      e2.printStackTrace();
      fail(e2.getMessage() + " shouldn't happen");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }

    list = impl.getCollectorProcessDefinitions(testOrg.getId());
    assertTrue(numCollector + 1 == list.size());
    List<String> ids = impl.getCollectorProcessDefinitionIds(testOrg.getId());
    assertTrue(list.size() == ids.size());
    try {
      CollectorProcessDefinition defined = impl.getCollectorProcessDefinition(
          cpd.getId(), cpd.getOwnerId());
      assertNotNull(defined);
      assertTrue(defined.equals(cpd));
      assertTrue(defined.toString().equals(cpd.toString()));
      defined.setName("New Name");
      // Update the instance
      impl.updateCollectorProcessDefinition(defined);
      CollectorProcessDefinition updated = impl.getCollectorProcessDefinition(
          cpd.getId(), cpd.getOwnerId());
      assertNotNull(updated);
      assertTrue(defined.equals(updated));
      assertFalse(updated.equals(cpd));
      assertTrue(defined.hashCode() == updated.hashCode());
      assertFalse(updated.hashCode() == cpd.hashCode());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    // Delete the instance
    try {
      impl.deleteCollectorProcessDefinition("bogus-collector-3921",
          cpd.getOwnerId());
      fail("should be able to delete bogus CollectorProcessDefinition.");
    }
    catch (IdNotFoundException e1) {
      // expected.
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail("should not happen");
    }
    try {
      impl.deleteCollectorProcessDefinition(cpd.getId(), cpd.getOwnerId());
      list = impl.getCollectorProcessDefinitions(testOrg.getId());
      assertTrue(numCollector == list.size());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    try {
      deleteSensor();
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Test methods for dealing with Depositories.
   */
  @Test
  public void testDepositories() {
    // get the depositories.
    List<Depository> list = impl.getDepositories(testOrg.getId());
    int numDepository = list.size();
    assertTrue(numDepository >= 0);
    // Create a new instance
    Depository dep = InstanceFactory.getDepository();
    try {
      impl.defineDepository(dep.getId(), dep.getName(),
          dep.getMeasurementType(), dep.getOwnerId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (ConstraintViolationException e) {
      // This is expected since we haven't defined the measurement type.
      try {
        addMeasurementType();
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail("should not happen");
      }
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.defineDepository(dep.getId(), dep.getName(),
          dep.getMeasurementType(), dep.getOwnerId());
      list = impl.getDepositories(testOrg.getId());
      assertTrue(numDepository + 1 == list.size());
      List<String> ids = impl.getDepositoryIds(testOrg.getId());
      assertTrue(list.size() == ids.size());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    // try to define it again.
    try {
      impl.defineDepository(dep.getId(), dep.getName(),
          dep.getMeasurementType(), dep.getOwnerId());
      fail("Should not be able to define second depository with same name.");
    }
    catch (UniqueIdException e2) {
      // expected result
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail(e.getMessage() + " shouldn't happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      Depository defined = impl.getDepository(dep.getId(), dep.getOwnerId());
      assertNotNull(defined);
      assertTrue(defined.equals(dep));
      assertTrue(defined.toString().equals(dep.toString()));
      Depository copy = impl.getDepository(dep.getId(), dep.getOwnerId());
      assertTrue(copy.equals(defined));
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    try {
      impl.deleteDepository("bogus-depository-3258", dep.getOwnerId());
      fail("Shouldn't be able to delete bogus-depository-3258");
    }
    catch (IdNotFoundException e1) {
      // this is expected
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail("should not happen");
    }
    try {
      impl.deleteDepository(dep.getId(), dep.getOwnerId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    list = impl.getDepositories(testOrg.getId());
    assertTrue(numDepository == list.size());
    try {
      deleteMeasurementType();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Tests for dealing with Measurements.
   */
  @Test
  public void testMeasurements() {
    try {
      addMeasurementType();
    }
    catch (UniqueIdException e1) {
      e1.printStackTrace();
      fail("should not happen");
    }
    // create a depository to store the measurements.
    Depository dep = InstanceFactory.getDepository();
    String sensorId = null;
    try {
      impl.defineDepository(dep.getId(), dep.getName(),
          dep.getMeasurementType(), dep.getOwnerId());
      Depository d = impl.getDepository(dep.getId(), dep.getOwnerId());
      MeasurementType type = d.getMeasurementType();
      assertTrue(type.equals(InstanceFactory.getMeasurementType()));
      Measurement m1 = InstanceFactory.getMeasurementOne();
      Measurement m2 = InstanceFactory.getMeasurementTwo();
      Measurement m3 = InstanceFactory.getMeasurementThree();
      sensorId = m1.getSensorId();
      d.putMeasurement(m1);
      d.putMeasurement(m2);
      d.putMeasurement(m3);
      Measurement defined = d.getMeasurement(m1.getSlug());
      assertNotNull(defined);
      assertTrue(defined.equals(m1));
      assertTrue(defined.toString().equals(m1.toString()));
      // assertTrue(defined.hashCode() == m1.hashCode());
      List<Measurement> list = d.getMeasurements(sensorId);
      assertTrue(list.size() == 3);
      list = d.getMeasurements(sensorId,
          InstanceFactory.getTimeBetweenM1andM2(),
          InstanceFactory.getTimeBetweenM1andM3());
      assertTrue(list.size() == 1);
      try {
        Double val = d.getValue(sensorId,
            InstanceFactory.getTimeBetweenM1andM2());
        assertTrue(Math.abs(val - m1.getValue()) < 0.001);
        try {
          Double val2 = d.getValue(sensorId,
              InstanceFactory.getTimeBetweenM1andM2(), 700L);
          assertTrue(Math.abs(val - val2) < 0.001);
        }
        catch (MeasurementGapException e) {
          e.printStackTrace();
          fail(e.getMessage() + " should not happen");
        }
        try {
          Double val2 = d.getValue(sensorId, m2.getDate(), 700L);
          assertTrue(Math.abs(val - val2) < 0.001);
        }
        catch (MeasurementGapException e) {
          e.printStackTrace();
          fail(e.getMessage() + " should not happen");
        }
        try {
          d.getValue(sensorId, InstanceFactory.getTimeBetweenM1andM2(), 70L);
          fail("Should detect gap.");
        }
        catch (MeasurementGapException e) {
          // expected
        }
        val = d.getValue(sensorId, m1.getDate());
        assertTrue(Math.abs(val - m1.getValue()) < 0.0001);
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      try {
        d.getValue(sensorId, InstanceFactory.getTimeBeforeM1());
        fail("Should throw NoMeasurementException.");
      }
      catch (NoMeasurementException e) {
        // expected
      }
      try {
        d.getValue(sensorId, InstanceFactory.getTimeAfterM3());
        fail("Should throw NoMeasurementException.");
      }
      catch (NoMeasurementException e) {
        // expected
      }
      try {
        d.getValue(sensorId, InstanceFactory.getTimeBeforeM1(), 700L);
        fail("Should throw NoMeasurementException.");
      }
      catch (NoMeasurementException e) {
        // expected
      }
      catch (MeasurementGapException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      try {
        d.getValue(sensorId, InstanceFactory.getTimeAfterM3(), 700L);
        fail("Should throw NoMeasurementException.");
      }
      catch (NoMeasurementException e) {
        // expected
      }
      catch (MeasurementGapException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      try {
        Double val = d.getValue(sensorId,
            InstanceFactory.getTimeBetweenM1andM2(),
            InstanceFactory.getTimeBetweenM1andM3());
        assertTrue(Math.abs(val - 0.0) < 0.001);
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " shouldn't happen");
      }
      try {
        Double val = d.getValue(sensorId,
            InstanceFactory.getTimeBetweenM1andM2(),
            InstanceFactory.getTimeBetweenM1andM3(), 700L);
        assertTrue(Math.abs(val - 0.0) < 0.001);
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " shouldn't happen");
      }
      catch (MeasurementGapException e) {
        e.printStackTrace();
        fail(e.getMessage() + " shouldn't happen");
      }
      try {
        MeasuredValue earliest = d.getEarliestMeasuredValue(sensorId);
        assertNotNull(earliest);
        assertTrue(earliest.equivalent(m1));
        assertTrue(earliest.getSensorId().equals(m1.getSensorId()));
        assertTrue(earliest.getValue().equals(m1.getValue()));
        assertTrue(earliest.getMeasurementType().getUnits()
            .equals(m1.getMeasurementType()));
        assertTrue(earliest.getDate().equals(m1.getDate()));
        MeasuredValue latest = d.getLatestMeasuredValue(sensorId);
        assertNotNull(latest);
        assertTrue(latest.equivalent(m3));
        assertTrue(latest.getSensorId().equals(m3.getSensorId()));
      }
      catch (NoMeasurementException e) {
        e.printStackTrace();
        fail(e.getMessage() + " should not happen");
      }
      List<String> sensors = d.listSensors();
      assertNotNull(sensors);
      assertTrue(sensors.contains(sensorId));
      d.deleteMeasurement(m1);
      d.deleteMeasurement(m2);
      d.deleteMeasurement(m3);
      impl.deleteDepository(dep.getId(), dep.getOwnerId());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
    }
    catch (MeasurementTypeException e) {
      e.printStackTrace();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
    }
    catch (BadSlugException e1) {
      e1.printStackTrace();
    }

    // Clean up

    try {
      deleteMeasurementType();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }

  }

  /**
   * Test methods for dealing with SensorLocations.
   */
  @Test
  public void testSensorLocations() {
    // get the list of locations.
    List<SensorLocation> list = impl.getSensorLocations(testOrg.getId());
    int numLocations = list.size();
    assertTrue(numLocations >= 0);
    // create a new SensorLocation.
    SensorLocation loc = InstanceFactory.getLocation();
    try {
      impl.defineSensorLocation(loc.getId(), loc.getName(),
          loc.getLatitude(), loc.getLongitude(), loc.getAltitude(),
          loc.getDescription(), loc.getOwnerId());
      list = impl.getSensorLocations(testOrg.getId());
      assertTrue(numLocations + 1 == list.size());
      List<String> ids = impl.getSensorLocationIds(testOrg.getId());
      assertTrue(list.size() == ids.size());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    try {
      SensorLocation defined = impl.getSensorLocation(loc.getId(),
          loc.getOwnerId());
      assertTrue(defined.equals(loc));
      assertTrue(loc.equals(defined));
      assertTrue(defined.toString().equals(loc.toString()));
      defined.setName("New Name");
      impl.updateSensorLocation(defined);
      SensorLocation updated = impl.getSensorLocation(loc.getId(),
          loc.getOwnerId());
      assertFalse(loc.equals(updated));
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    // delete
    try {
      impl.deleteSensorLocation("bogus-location-5432", loc.getOwnerId());
    }
    catch (IdNotFoundException e1) {
      // expected result.
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail("should not happen");
    }
    try {
      impl.deleteSensorLocation(loc.getId(), loc.getOwnerId());
      list = impl.getSensorLocations(testOrg.getId());
      assertTrue(numLocations == list.size());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Test methods for dealing with MeasurementTypes.
   */
  @Test
  public void testMeasurementTypes() {
    List<MeasurementType> list = impl.getMeasurementTypes();
    int numMeasurementTypes = list.size();
    assertTrue(numMeasurementTypes >= 0);
    MeasurementType type = InstanceFactory.getMeasurementType2();
    // create a MeasurementType
    try {
      impl.defineMeasurementType(type.getId(), type.getName(),
          type.getUnits());
      list = impl.getMeasurementTypes();
      assertTrue(numMeasurementTypes + 1 == list.size());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail("should not happen");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    MeasurementType defined = impl.getMeasurementType(type.getId());
    assertNotNull(defined);
    assertTrue(type.equals(defined));
    assertTrue(defined.equals(type));
    assertTrue(defined.toString().equals(type.toString()));
    assertTrue(defined.hashCode() == type.hashCode());
    defined.setName("New Name");
    impl.updateMeasurementType(defined);
    list = impl.getMeasurementTypes();
    assertTrue(numMeasurementTypes + 1 == list.size());
    MeasurementType update = impl.getMeasurementType(type.getId());
    assertFalse(type.equals(update));
    // delete
    try {
      impl.deleteMeasurementType("bogus-measurementtype-9520");
      fail("shouldn't be able to delete bogus MeasurementType");
    }
    catch (IdNotFoundException e) {
      // expected result
    }
    try {
      impl.deleteMeasurementType(type.getId());
      list = impl.getMeasurementTypes();
      assertTrue(numMeasurementTypes == list.size());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Test methods for dealing with Organizations.
   */
  @Test
  public void testOrganizations() {
    // get the defined organizations
    List<Organization> list = impl.getOrganizations();
    int numOrgs = list.size();
    assertTrue(numOrgs >= 0);
    Organization org = InstanceFactory.getOrganization2();
    try {
      impl.defineOrganization(org.getId(), org.getName(), org.getUsers());
      list = impl.getOrganizations();
      assertTrue(numOrgs + 1 == list.size());
      List<String> ids = impl.getOrganizationIds();
      assertTrue(list.size() == ids.size());
      Organization defined = impl.getOrganization(org.getId());
      assertNotNull(defined);
      assertTrue(defined.equals(org));
      assertTrue(defined.toString().equals(org.toString()));
      // can't check hash codes since we are dealing with Organizations and
      // OrganizationImpls.
      // assertTrue(defined.hashCode() == org.hashCode());
      defined.setName("New Name");
      impl.updateOrganization(defined);
      list = impl.getOrganizations();
      assertTrue(numOrgs + 1 == list.size());
      Organization updated = impl.getOrganization(org.getId());
      assertFalse(org.equals(updated));
      assertFalse(org.toString().equals(updated.toString()));
      assertFalse(org.hashCode() == updated.hashCode());
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen.");
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    try {
      impl.deleteOrganization("bogus-organization-9491");
    }
    catch (IdNotFoundException e) {
      // expected
    }
    try {
      impl.deleteOrganization(org.getId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail("should not happen");
    }
  }

  /**
   * Test methods for dealing with Sensors.
   */
  @Test
  public void testSensors() {
    List<Sensor> list = impl.getSensors(testOrg.getId());
    int numSensors = list.size();
    assertTrue(numSensors >= 0);
    Sensor sens = InstanceFactory.getSensor();
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(),
          sens.getSensorLocationId(), sens.getModelId(), sens.getProperties(),
          sens.getOwnerId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      // expected result.
      try {
        addSensorLocation();
        addSensorModel();
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
    }
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(),
          sens.getSensorLocationId(), sens.getModelId(), sens.getProperties(),
          sens.getOwnerId());
      list = impl.getSensors(testOrg.getId());
      assertTrue(numSensors + 1 == list.size());
      List<String> ids = impl.getSensorIds(testOrg.getId());
      assertTrue(list.size() == ids.size());
      Sensor defined = impl.getSensor(sens.getId(), sens.getOwnerId());
      assertNotNull(defined);
      assertTrue(defined.equals(sens));
      assertTrue(defined.toString().equals(sens.toString()));
      defined.setName("New Name");
      impl.updateSensor(defined);
      Sensor updated = impl.getSensor(sens.getId(), sens.getOwnerId());
      assertFalse(updated.equals(sens));
      assertFalse(updated.toString().equals(sens.toString()));
      list = impl.getSensors(testOrg.getId());
      assertTrue(numSensors + 1 == list.size());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    // try to define the same sensor a second time
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(),
          sens.getSensorLocationId(), sens.getModelId(), sens.getProperties(),
          sens.getOwnerId());
    }
    catch (UniqueIdException e1) {
      // expected.
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(),
          "bogus-sensor-location-id-9921", sens.getModelId(),
          sens.getProperties(), sens.getOwnerId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e1) {
      // expected.
    }
    try {
      impl.defineSensor(sens.getId(), sens.getName(), sens.getUri(),
          sens.getSensorLocationId(), "bogus-sensor-model-id-4921",
          sens.getProperties(), sens.getOwnerId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e1) {
      e1.printStackTrace();
      fail(e1.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e1) {
      // expected.
    }

    try {
      impl.deleteSensor(sens.getId(), sens.getOwnerId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      deleteSensorLocation();
      deleteSensorModel();
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * Test methods for dealing with SensorGroups.
   */
  @Test
  public void testSensorGroups() {
    List<SensorGroup> list;
    int numGroups = 0;
    list = impl.getSensorGroups(testOrg.getId());
    numGroups = list.size();
    assertTrue(numGroups >= 0);
    SensorGroup group = InstanceFactory.getSensorGroup();
    try {
      impl.defineSensorGroup(group.getId(), group.getName(),
          group.getSensors(), group.getOwnerId());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      // expected.
      try {
        addSensor();
      }
      catch (MisMatchedOwnerException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
      catch (UniqueIdException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
      catch (IdNotFoundException e1) {
        e1.printStackTrace();
        fail(e1.getMessage() + " should not happen");
      }
    }
    try {
      impl.defineSensorGroup(group.getId(), group.getName(),
          group.getSensors(), group.getOwnerId());
      list = impl.getSensorGroups(testOrg.getId());
      assertTrue(numGroups + 1 == list.size());
      List<String> ids = impl.getSensorGroupIds(testOrg.getId());
      assertTrue(list.size() == ids.size());
      SensorGroup defined = impl.getSensorGroup(group.getId(),
          group.getOwnerId());
      assertNotNull(defined);
      assertTrue(defined.equals(group));
      assertTrue(defined.toString().equals(group.toString()));
      defined.setName("New Name");
      impl.updateSensorGroup(defined);
      list = impl.getSensorGroups(testOrg.getId());
      assertTrue(numGroups + 1 == list.size());
      SensorGroup updated = impl.getSensorGroup(group.getId(),
          group.getOwnerId());
      assertFalse(updated.equals(group));
      assertFalse(updated.toString().equals(group.toString()));
      assertFalse(updated.hashCode() == group.hashCode());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.deleteSensorGroup("bogus-sensor-group-id-8440", group.getOwnerId());
    }
    catch (IdNotFoundException e) {
      // expected
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.deleteSensorGroup(group.getId(), group.getOwnerId());
      list = impl.getSensorGroups(testOrg.getId());
      assertTrue(numGroups == list.size());
      deleteSensor();
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    catch (MisMatchedOwnerException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }

  }

  /**
   * Test methods for dealing with SensorModels.
   */
  @Test
  public void testSensorModels() {
    List<SensorModel> list = impl.getSensorModels();
    int numModels = list.size();
    assertTrue(numModels >= 0);
    SensorModel model = InstanceFactory.getSensorModel();
    try {
      impl.defineSensorModel(model.getId(), model.getName(),
          model.getProtocol(), model.getType(), model.getVersion());
      list = impl.getSensorModels();
      assertTrue(numModels + 1 == list.size());
      List<String> ids = impl.getSensorModelIds();
      assertTrue(list.size() == ids.size());
      SensorModel defined = impl.getSensorModel(model.getId());
      assertNotNull(defined);
      assertTrue(defined.equals(model));
      assertTrue(defined.toString().equals(model.toString()));
      defined.setName("New Name");
      impl.updateSensorModel(defined);
      list = impl.getSensorModels();
      assertTrue(numModels + 1 == list.size());
      SensorModel updated = impl.getSensorModel(model.getId());
      assertNotNull(updated);
      assertFalse(updated.equals(model));
      assertFalse(updated.toString().equals(model.toString()));
      assertFalse(updated.hashCode() == model.hashCode());
    }
    catch (BadSlugException e) {
      e.printStackTrace();
      fail("should not happen.");
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.deleteSensorModel("bogus-sensor-model-id-4926");
      fail("should not be able to delete bogus sensor model");
    }
    catch (IdNotFoundException e) {
      // expected
    }
    try {
      impl.deleteSensorModel(model.getId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }

  }

  /**
   * Test method for dealing with UserInfos.
   */
  @Test
  public void testUsers() {
    UserInfo user = InstanceFactory.getUserInfo2();
    List<UserInfo> list = impl.getUsers(user.getOrganizationId());
    int numUsers = list.size();
    assertTrue(numUsers >= 0);
    try {
      impl.defineUserInfo(user.getUid(), user.getFirstName(),
          user.getLastName(), user.getEmail(), user.getOrganizationId(),
          user.getProperties());
      list = impl.getUsers(user.getOrganizationId());
      assertTrue(numUsers + 1 == list.size());
      List<String> ids = impl.getUserIds(user.getOrganizationId());
      assertTrue(list.size() == ids.size());
      UserInfo defined = impl.getUser(user.getUid(), user.getOrganizationId());
      assertNotNull(defined);
      assertTrue(defined.equals(user));
      assertTrue(defined.toString().equals(user.toString()));
      defined.setFirstName("New Name");
      impl.updateUserInfo(defined);
      list = impl.getUsers(user.getOrganizationId());
      assertTrue(numUsers + 1 == list.size());
      UserInfo updated = impl.getUser(user.getUid(), user.getOrganizationId());
      assertNotNull(updated);
      assertFalse(updated.equals(user));
      assertFalse(updated.toString().equals(user.toString()));
      assertFalse(updated.hashCode() == user.hashCode());
      Organization group = impl.getUsersGroup(user);
      assertNull(group);
      group = impl.getUsersGroup(InstanceFactory.getUserInfo());
      assertNotNull(group);
      assertTrue(group.getId().equals(user.getOrganizationId()));
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.deleteUser("bogus-user-id-4502", "bogus-organization-9520");
      fail("should be able to delete bogus user.");
    }
    catch (IdNotFoundException e) {
      // expected.
    }
    try {
      impl.deleteUser(user.getUid(), user.getOrganizationId());
      list = impl.getUsers(user.getOrganizationId());
      assertTrue(numUsers == list.size());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * Test methods for dealing with UserPasswords.
   */
  @Test
  public void testUserPasswords() {
    UserPassword password = InstanceFactory.getUserPassword2();
    try {
      impl.defineUserPassword(password.getUid(), password.getOrganizationId(),
          password.getPlainText());
      UserPassword defined = impl.getUserPassword(password.getUid(),
          password.getOrganizationId());
      assertNotNull(defined);
      assertTrue(defined.equals(password));
      defined.setPlainText("New plainText");
      impl.updateUserPassword(defined);
      UserPassword updated = impl.getUserPassword(password.getUid(),
          password.getOrganizationId());
      assertNotNull(updated);
      assertFalse(updated.equals(password));
      assertFalse(updated.toString().equals(password.toString()));
    }
    catch (UniqueIdException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    try {
      impl.deleteUserPassword("bogus-user-password-id-9685",
          "bogus-organization-5928");
      fail("Should not be able to delete bogus password.");
    }
    catch (IdNotFoundException e) {
      // expected.
    }
    try {
      impl.deleteUserPassword(password.getUid(), password.getOrganizationId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
  }

  /**
   * @throws IdNotFoundException
   *           if there is a mismatch in the ownership.
   * @throws UniqueIdException
   *           if the Location is already defined.
   */
  private void addSensorLocation() throws UniqueIdException,
      IdNotFoundException {
    SensorLocation loc = InstanceFactory.getLocation();
    try {
      impl.getSensorLocation(loc.getId(), loc.getOwnerId());
    }
    catch (IdNotFoundException e1) {
      try {
        impl.defineSensorLocation(loc.getId(), loc.getId(), loc.getLatitude(),
            loc.getLongitude(), loc.getAltitude(), loc.getDescription(),
            loc.getOwnerId());
      }
      catch (BadSlugException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @throws MisMatchedOwnerException
   *           if there is a mismatch in ownership.
   * @throws IdNotFoundException
   *           if the SensorLocation is not defined.
   */
  private void deleteSensorLocation() throws MisMatchedOwnerException,
      IdNotFoundException {
    SensorLocation loc = InstanceFactory.getLocation();
    SensorLocation defined = impl.getSensorLocation(loc.getId(),
        loc.getOwnerId());
    if (defined != null) {
      impl.deleteSensorLocation(loc.getId(), loc.getOwnerId());
    }
  }

  /**
   * @throws UniqueIdException
   *           if the Model is already defined.
   */
  private void addSensorModel() throws UniqueIdException {
    SensorModel model = InstanceFactory.getSensorModel();
    SensorModel defined = impl.getSensorModel(model.getId());
    if (defined == null) {
      try {
        impl.defineSensorModel(model.getId(), model.getName(), model.getProtocol(),
            model.getType(), model.getVersion());
      }
      catch (BadSlugException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @throws IdNotFoundException
   *           if the SensorModel is not defined.
   */
  private void deleteSensorModel() throws IdNotFoundException {
    SensorModel model = InstanceFactory.getSensorModel();
    SensorModel defined = impl.getSensorModel(model.getId());
    if (defined != null) {
      impl.deleteSensorModel(model.getId());
    }
  }

  /**
   * @throws MisMatchedOwnerException
   *           if there is a mismatch in the ownership.
   * @throws UniqueIdException
   *           if the Sensor is already defined.
   * @throws IdNotFoundException
   *           if the SensorLocation or SensorModel aren't defined.
   */
  private void addSensor() throws MisMatchedOwnerException, UniqueIdException,
      IdNotFoundException {
    addSensorLocation();
    addSensorModel();
    Sensor sensor = InstanceFactory.getSensor();
    try {
      impl.getSensor(sensor.getId(), sensor.getOwnerId());
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineSensor(sensor.getId(), sensor.getName(), sensor.getUri(),
            sensor.getSensorLocationId(), sensor.getModelId(), sensor.getProperties(),
            sensor.getOwnerId());
      }
      catch (BadSlugException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * @throws MisMatchedOwnerException
   *           if there is a mismatch in ownership.
   * @throws IdNotFoundException
   *           if the Sensor is not defined.
   */
  private void deleteSensor() throws MisMatchedOwnerException,
      IdNotFoundException {
    deleteSensorLocation();
    deleteSensorModel();
    Sensor sensor = InstanceFactory.getSensor();
    Sensor defined = impl.getSensor(sensor.getId(), sensor.getOwnerId());
    if (defined != null) {
      impl.deleteSensor(sensor.getId(), sensor.getOwnerId());
    }
  }

  /**
   * @throws UniqueIdException
   *           if there is already a Depository defined.
   * @throws IdNotFoundException
   *           if there are problems with the ids.
   */
  private void addDepository() throws UniqueIdException, IdNotFoundException {
    Depository depot = InstanceFactory.getDepository();
    try {
      impl.getDepository(depot.getId(), depot.getOwnerId());
    }
    catch (IdNotFoundException e) {
      try {
        impl.defineDepository(depot.getId(), depot.getName(), depot.getMeasurementType(),
            depot.getOwnerId());
      }
      catch (BadSlugException e1) {
        e1.printStackTrace();
      }
    }
  }

  /**
   * @throws MisMatchedOwnerException
   *           if there is a mismatch in ownership.
   * @throws UniqueIdException
   *           if the CollectorProcessDefinition is already defined.
   * @throws IdNotFoundException
   *           if the Sensor is not defined.
   */
  private void addCollectorProcessDefinition() throws MisMatchedOwnerException,
      UniqueIdException, IdNotFoundException {
    addSensor();
    CollectorProcessDefinition cpd = InstanceFactory
        .getCollectorProcessDefinition();
    CollectorProcessDefinition defined = impl.getCollectorProcessDefinition(
        cpd.getId(), cpd.getOwnerId());
    if (defined == null) {
      try {
        impl.defineCollectorProcessDefinition(cpd.getId(), cpd.getName(), cpd.getSensorId(),
            cpd.getPollingInterval(), cpd.getDepositoryId(), cpd.getProperties(), cpd.getOwnerId());
      }
      catch (BadSlugException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @throws UniqueIdException
   *           if the MeasurementType is already defined.
   */
  private void addMeasurementType() throws UniqueIdException {
    MeasurementType type = InstanceFactory.getMeasurementType();
    MeasurementType defined = impl.getMeasurementType(type.getId());
    if (defined == null) {
      try {
        impl.defineMeasurementType(type.getId(), type.getName(), type.getUnits());
      }
      catch (BadSlugException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @throws IdNotFoundException
   *           if the MeasurementType isn't defined.
   */
  private void deleteMeasurementType() throws IdNotFoundException {
    MeasurementType type = InstanceFactory.getMeasurementType();
    MeasurementType defined = impl.getMeasurementType(type.getId());
    if (defined != null) {
      impl.deleteMeasurementType(type.getId());
    }
  }
}
