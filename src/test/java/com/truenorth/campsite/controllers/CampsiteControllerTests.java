package com.truenorth.campsite.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.truenorth.campsite.controllers.CampsiteController;
import com.truenorth.campsite.models.Campsites;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CampsiteControllerTests {

	private static final Logger LOG = LoggerFactory.getLogger(CampsiteControllerTests.class);

	@Autowired
	private CampsiteController campsitesController;

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static Campsites campsite, badCampsite;
	private static ObjectId campsiteId;
	private static String mockName;
	private static Date campsiteOpenDate, campsiteCloseDate;

	private static ResponseEntity<Map<String, Object>> response;

	@BeforeClass
	public static void setUp() {
		try {
			campsiteId = ObjectId.get();
			campsiteOpenDate = formatter.parse("2018-12-01T12:00:00.000+0000");
			campsiteCloseDate = formatter.parse("2018-12-31T12:00:00.000+0000");
			campsite = new Campsites(campsiteId, "Normal Campsite", campsiteOpenDate, campsiteCloseDate);
			badCampsite = new Campsites(ObjectId.get(), "Wrong Campsite", campsiteCloseDate, campsiteOpenDate);
		} catch (ParseException e) {
			LOG.debug(e.toString());
		}
	}

	@Test
	public void addNewCampsiteSucessTest() {
		response = campsitesController.createCampsite(campsite);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void addNewCampsiteDateRangeFailTest() {
		response = campsitesController.createCampsite(badCampsite);
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void getCampsiteByIdSucessTest() {
		response = campsitesController.getAllCampsites();
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		List<Campsites> campsiteList = (List<Campsites>) response.getBody().get("Campsites");
		List<Campsites> resultList = campsiteList.stream()
				.filter(camp -> camp.get_id().toString().equals(campsiteId.toString())).collect(Collectors.toList());
		assertNotNull(resultList);
		assertEquals(1, resultList.size());
		assertEquals(campsite.get_id(), resultList.get(0).get_id());
		assertEquals(campsite.getName(), resultList.get(0).getName());
		assertEquals(campsite.getOpenDate(), resultList.get(0).getOpenDate());
		assertEquals(campsite.getCloseDate(), resultList.get(0).getCloseDate());
	}

	@Test
	public void getCampsiteByIdFailTest() {
		response = campsitesController.getCampsiteById(ObjectId.get());
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void updateCampsiteFailDateTest() {
		try {
			mockName = "Modified Name";
			campsite.setName(mockName);
			campsiteOpenDate = formatter.parse("2018-12-10T12:00:00.000+0000");
			campsite.setOpenDate(campsiteOpenDate);
			campsiteCloseDate = formatter.parse("2012-12-20T12:00:00.000+0000");
			campsite.setCloseDate(campsiteCloseDate);
			response = campsitesController.modifyCampsiteById(campsiteId, campsite);
			assertNotNull(response);
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		} catch (ParseException e) {
			LOG.debug(e.toString());
		}
	}

	@Test
	public void updateCampsiteFailIdTest() {
		try {
			mockName = "Modified Name";
			campsite.setName(mockName);
			campsiteOpenDate = formatter.parse("2018-12-10T12:00:00.000+0000");
			campsite.setOpenDate(campsiteOpenDate);
			campsiteCloseDate = formatter.parse("2019-12-20T12:00:00.000+0000");
			campsite.setCloseDate(campsiteCloseDate);
			response = campsitesController.modifyCampsiteById(ObjectId.get(), campsite);
			assertNotNull(response);
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		} catch (ParseException e) {
			LOG.debug(e.toString());
		}
	}

	@Test
	public void updateCampsiteSucessTest() {
		try {
			mockName = "Modified Name";
			campsite.setName(mockName);
			campsiteOpenDate = formatter.parse("2018-12-10T12:00:00.000+0000");
			campsite.setOpenDate(campsiteOpenDate);
			campsiteCloseDate = formatter.parse("2019-12-20T12:00:00.000+0000");
			campsite.setCloseDate(campsiteCloseDate);
			response = campsitesController.modifyCampsiteById(campsiteId, campsite);
			assertNotNull(response);
			assertEquals(HttpStatus.OK, response.getStatusCode());
			response = campsitesController.getCampsiteById(campsiteId);
			Campsites campsiteModified = (Campsites) response.getBody().get("Campsite");
			assertNotNull(campsiteModified);
			assertEquals(campsiteId, campsiteModified.get_id());
			assertEquals(mockName, campsiteModified.getName());
			assertEquals(campsiteOpenDate, campsiteModified.getOpenDate());
			assertEquals(campsiteCloseDate, campsiteModified.getCloseDate());
		} catch (ParseException e) {
			LOG.debug(e.toString());
		}
	}

	@Test
	public void deleteCampsiteSucessTest() {
		response = campsitesController.deleteCampsite(campsiteId);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		response = campsitesController.getCampsiteById(campsiteId);
		Campsites campsite = (Campsites) response.getBody().get("Campsite");
		assertNull(campsite);
		response = campsitesController.getAllCampsites();
		List<Campsites> campsiteList = (List<Campsites>) response.getBody().get("Campsites");
		List<Campsites> result = campsiteList.stream()
				.filter(camp -> camp.get_id().toString().equals(campsiteId.toString())).collect(Collectors.toList());
		assertNotNull(result);
		assertEquals(0, result.size());
	}

}
