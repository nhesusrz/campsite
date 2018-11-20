package com.truenorth.campsite.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.truenorth.campsite.models.Reservations;
import com.truenorth.campsite.services.ReservationAdminServiceImpl;

@RestController
public class ReservationController {

	private static final Logger LOG = LoggerFactory.getLogger(ReservationController.class);

	@Autowired
	private ReservationAdminServiceImpl reservationService;

	@RequestMapping(value = { "/reservations" }, method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getAllReservations() {
		Map<String, Object> responseMap = reservationService.getAll();
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = { "/reservations/{id}" }, method = RequestMethod.GET)
	// @Cacheable(value = "reservations", key = "#id", sync = true)
	public ResponseEntity<Map<String, Object>> getReservationById(
			@PathVariable(value = "id", required = true) ObjectId id) {
		Map<String, Object> responseMap = reservationService.getById(id);
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = { "/reservations" }, method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createReservation(
			@Valid @RequestBody(required = true) Reservations reservation) {
		Map<String, Object> responseMap = reservationService.save(reservation);
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = { "/reservations/{id}" }, method = RequestMethod.PUT)
	// @CachePut(value = "reservations", key = "#id")
	// The above annotation is commented because annotation causes errors with the
	// unit tests.
	public ResponseEntity<Map<String, Object>> modifyReservationById(
			@PathVariable(value = "id", required = true) ObjectId id,
			@Valid @RequestBody(required = true) Reservations reservation) {
		Map<String, Object> responseMap = reservationService.update(id, reservation);
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

	@RequestMapping(value = { "/reservations/{id}" }, method = RequestMethod.DELETE)
	@CacheEvict(value = "reservations", allEntries = true)
	public ResponseEntity<Map<String, Object>> deleteReservation(
			@PathVariable(value = "id", required = true) ObjectId id) {
		Map<String, Object> responseMap = reservationService.delete(id);
		if (responseMap.containsKey("Error"))
			return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Map<String, Object>>(responseMap, HttpStatus.OK);
	}

}
