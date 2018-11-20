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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.truenorth.campsite.controllers.CampsiteController;
import com.truenorth.campsite.controllers.ReservationController;
import com.truenorth.campsite.models.Campsites;
import com.truenorth.campsite.models.Reservations;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReservationControllerTests {

	private static final Logger LOG = LoggerFactory.getLogger(ReservationControllerTests.class);

	@Autowired
	private ReservationController reservationsController;
	@Autowired
	private CampsiteController campsitesController;

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static Campsites campsite, wrong_campsite;
	private static ObjectId mockCampsiteId;
	private static String mockCampsiteName;
	private static Date mockOpenDate, mockCloseDate;

	private static Reservations reservation;
	private static ObjectId mockReservationId;
	private static String mockEmail, mockReservationName, mockReservationSurname;
	private static Date mockArrivalDate, mockDepartureDate, mockArrivalDate1, mockDepartureDate1,
			mockArrivalDateOverCase1, mockDepartureDateOverCase1, mockArrivalDateOverCase2, mockDepartureDateOverCase2,
			mockArrivalDateOverCase3, mockDepartureDateOverCase3, mockWrongArrivalDate;

	private static ResponseEntity<Map<String, Object>> response;

	@BeforeClass
	public static void setUp() {
		try {
			mockCampsiteId = ObjectId.get();
			mockCampsiteName = "Holly Campsite";

			mockOpenDate = formatter.parse("2018-11-29T12:00:00.000+0000");
			mockCloseDate = formatter.parse("2018-12-31T12:00:00.000+0000");

			campsite = new Campsites(mockCampsiteId, mockCampsiteName, mockOpenDate, mockCloseDate);

			mockReservationId = ObjectId.get();
			mockEmail = "test@mail.com";
			mockReservationName = "Martin";
			mockReservationSurname = "Pacheco";

			mockArrivalDate = formatter.parse("2018-12-01T12:00:00.000+0000");
			mockDepartureDate = formatter.parse("2018-12-03T12:00:00.000+0000");

			mockArrivalDate1 = formatter.parse("2018-12-09T12:00:00.000+0000");
			mockDepartureDate1 = formatter.parse("2018-12-11T12:00:00.000+0000");
			// Overlapping - Case 1
			mockArrivalDateOverCase1 = formatter.parse("2018-12-01T12:00:00.000+0000");
			mockDepartureDateOverCase1 = formatter.parse("2018-12-03T12:00:00.000+0000");
			// Overlapping - Case 2
			mockArrivalDateOverCase2 = formatter.parse("2018-12-02T12:00:00.000+0000");
			mockDepartureDateOverCase2 = formatter.parse("2018-12-05T12:00:00.000+0000");
			// Overlapping - Case 3
			mockArrivalDateOverCase3 = formatter.parse("2018-11-30T12:00:00.000+0000");
			mockDepartureDateOverCase3 = formatter.parse("2018-12-02T12:00:00.000+0000");

			mockWrongArrivalDate = formatter.parse("2017-12-03T12:00:00.000+0000");
			reservation = new Reservations(mockReservationId, mockEmail, mockCampsiteId, mockReservationName,
					mockReservationSurname, mockArrivalDate, mockDepartureDate);
		} catch (ParseException e) {
			LOG.debug(e.toString());
		}
	}

	@Test
	public void addNewReservationCampsiteValidationFailTest() {
		response = reservationsController.createReservation(reservation);
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		LOG.info("addNewReservationCampsiteValidationFailTest - Response Message: "
				+ response.getBody().get("Error").toString());
	}

	@Test
	public void addNewReservationDatesValidationFailTest() {
		response = campsitesController.createCampsite(campsite);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		reservation.setDepartureDate(mockArrivalDate);
		response = reservationsController.createReservation(reservation);
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		LOG.info("addNewReservationDatesValidationFailTest - Response Message: "
				+ response.getBody().get("Error").toString());
	}

	@Test
	public void addNewReservationThreeDaysValidationFailTest() {
		reservation.setArrivalDate(mockWrongArrivalDate);
		reservation.setDepartureDate(mockDepartureDate);
		response = reservationsController.createReservation(reservation);
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		LOG.info("addNewReservationThreeDaysValidationFailTest - Response Message: "
				+ response.getBody().get("Error").toString());
	}

	/*
	 * @Test // TODO: generate dates public void
	 * addNewReservationAdvanceValidationFailTest() {
	 * reservation.setArrivalDate(mockWrongArrivalDate);
	 * reservation.setDepartureDate(mockWrongDepartureDate2); response =
	 * reservationsController.createReservation(reservation);
	 * assertNotNull(response); assertEquals(HttpStatus.BAD_REQUEST,
	 * response.getStatusCode()); LOG.
	 * info("addNewReservationOutofCampsiteDatesValidationFailTest - Response Message: "
	 * + response.getBody().get("message").toString()); }
	 */

	@Test
	public void addNewReservationOverlappingCase1ValidationFailTest() {
		reservation.setArrivalDate(mockArrivalDate);
		reservation.setDepartureDate(mockDepartureDate);
		response = reservationsController.createReservation(reservation);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		reservation.set_id(ObjectId.get());
		reservation.setArrivalDate(mockArrivalDateOverCase1);
		reservation.setDepartureDate(mockDepartureDateOverCase1);
		response = reservationsController.createReservation(reservation);
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		LOG.info("addNewReservationOverlappingCase1ValidationFailTest - Response Message: "
				+ response.getBody().get("Error").toString());
	}

	@Test
	public void addNewReservationOverlappingCase2ValidationFailTest() {
		reservation.set_id(ObjectId.get());
		reservation.setArrivalDate(mockArrivalDateOverCase2);
		reservation.setDepartureDate(mockDepartureDateOverCase2);
		response = reservationsController.createReservation(reservation);
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		LOG.info("addNewReservationOverlappingCase2ValidationFailTest - Response Message: "
				+ response.getBody().get("Error").toString());
	}

	@Test
	public void addNewReservationOverlappingCase3ValidationFailTest() {
		reservation.set_id(ObjectId.get());
		reservation.setArrivalDate(mockArrivalDateOverCase3);
		reservation.setDepartureDate(mockDepartureDateOverCase3);
		response = reservationsController.createReservation(reservation);
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		LOG.info("addNewReservationOverlappingCase3ValidationFailTest - Response Message: "
				+ response.getBody().get("Error").toString());
	}

	@Test
	public void findByIdReservationSucessTest() {
		response = reservationsController.getReservationById(mockReservationId);
		Reservations reserv = (Reservations) response.getBody().get("Reservation");
		assertNotNull(reserv);
		assertEquals(mockReservationId, reserv.get_id());
		assertEquals(mockEmail, reserv.getEmail());
		assertEquals(mockReservationName, reserv.getName());
		assertEquals(mockReservationSurname, reserv.getSurname());
		assertEquals(mockArrivalDate, reserv.getArrivalDate());
		assertEquals(mockDepartureDate, reserv.getDepartureDate());
		LOG.info("findByIdReservationSucessTest - Response Message: "
				+ response.getBody().get("Reservation").toString());
	}

	@Test
	public void findByIdReservationFailTest() {
		response = reservationsController.getReservationById(ObjectId.get());
		Reservations reserv = (Reservations) response.getBody().get("Reservation");
		assertNull(reserv);
		LOG.info("findByIdReservationFailTest - Response Message: " + response.getBody().get("Error").toString());
	}

	@Test
	public void newValuesReservationSucessTest() {
		mockEmail = "newtest@mail.com";
		mockReservationName = "Juan";
		mockReservationSurname = "Pedro";
		reservation.setEmail(mockEmail);
		reservation.setName(mockReservationName);
		reservation.setSurname(mockReservationSurname);
		reservation.setArrivalDate(mockArrivalDate1);
		reservation.setDepartureDate(mockDepartureDate1);
		response = reservationsController.modifyReservationById(mockReservationId, reservation);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		LOG.info("newValuesReservationSucessTest - Response Message: "
				+ response.getBody().get("Reservation").toString());
		List<Reservations> reservationList = (List<Reservations>) reservationsController.getAllReservations().getBody()
				.get("Reservations");
		List<Reservations> result = reservationList.stream()
				.filter(reserv -> reserv.get_id().toString().equals(mockReservationId.toString()))
				.collect(Collectors.toList());
		assertEquals(1, result.size());
		assertEquals(mockEmail, result.get(0).getEmail());
		assertEquals(mockReservationName, result.get(0).getName());
		assertEquals(mockReservationSurname, result.get(0).getSurname());
		assertEquals(mockArrivalDate1, result.get(0).getArrivalDate());
		assertEquals(mockDepartureDate1, result.get(0).getDepartureDate());
	}

	@Test
	public void removeReservationSucessTest() {
		response = reservationsController.deleteReservation(mockReservationId);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		// LOG.info("addNewReservationValidationSucessTest - Response Message: "
		// + response.getBody().get("message").toString());
		Reservations reserv = (Reservations) reservationsController.getReservationById(mockReservationId).getBody()
				.get("Reservation");
		assertNull(reserv);
		response = campsitesController.deleteCampsite(mockCampsiteId);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}
