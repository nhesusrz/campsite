package com.truenorth.campsite.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truenorth.campsite.exception.AheadArrivalReservationException;
import com.truenorth.campsite.exception.DateRangeException;
import com.truenorth.campsite.exception.DateRangeReservationException;
import com.truenorth.campsite.exception.DifferenceDayReservationException;
import com.truenorth.campsite.exception.ObjectNotFoundException;
import com.truenorth.campsite.exception.OverlappingException;
import com.truenorth.campsite.models.CampsiteModel;
import com.truenorth.campsite.models.Campsites;
import com.truenorth.campsite.models.Reservations;
import com.truenorth.campsite.repositories.ReservationsRepository;

@Service
public class ReservationAdminServiceImpl implements CampsiteService {

	private static final Logger LOG = LoggerFactory.getLogger(ReservationAdminServiceImpl.class);

	private final int MIN_ADVANCE_RESERVATION = 1;
	private final int MAX_ADVANCE_RESERVATION = 30;
	private final int MAX_DAY_RESERVATION = 3;

	@Autowired
	private ReservationsRepository repository;

	@Autowired
	private CampsiteAdminServiceImpl campsiteService;

	@Override
	public Map<String, Object> getAll() {
		responseMap.clear();
		try {
			responseMap.put(ServiceEnum.RESERVATIONS.toString(), repository.findAll());
		} catch (Exception e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getById(ObjectId id) {
		responseMap.clear();
		try {
			if (!repository.existsById(id.toString()))
				throw new ObjectNotFoundException("id");
			responseMap.put(ServiceEnum.RESERVATION.toString(), repository.findBy_id(id));
		} catch (ObjectNotFoundException e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> save(CampsiteModel object) {
		responseMap.clear();
		Reservations newReservation = (Reservations) object;
		Map<String, Object> campsiteResponse = campsiteService.getById(newReservation.getCampsiteId());
		Campsites campsite = (Campsites) campsiteResponse.get("Campsite");
		LOG.info("ARRIVAL: " + newReservation.getArrivalDate() + " DEPARTURE:  " + newReservation.getDepartureDate());
		try {
			if (campsite == null)
				throw new ObjectNotFoundException("campsiteId");
			if (newReservation.getDepartureDate().before(newReservation.getArrivalDate())
					|| newReservation.getDepartureDate().equals(newReservation.getArrivalDate()))
				throw new DateRangeException();
			if (!isValidMaxDayDifference(newReservation.getArrivalDate(), newReservation.getDepartureDate()))
				throw new DifferenceDayReservationException();
			if (!isValidAdvanceReservation(newReservation.getArrivalDate()))
				throw new AheadArrivalReservationException();
			if (!isValidRangeCampsiteDates(campsite, newReservation))
				throw new DateRangeReservationException();
			if (newReservation.get_id() == null)
				newReservation.set_id(ObjectId.get());
			if (existsOverlappingReservation(campsite, newReservation))
				throw new OverlappingException();
			campsite.getReservationIds().add(newReservation.get_id());
			campsiteService.save(campsite);
			repository.save(newReservation);
			responseMap.put(ServiceEnum.RESERVATION.toString(), newReservation);
		} catch (ObjectNotFoundException | DateRangeException | DifferenceDayReservationException
				| AheadArrivalReservationException | DateRangeReservationException | OverlappingException e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> update(ObjectId id, CampsiteModel object) {
		responseMap.clear();
		Reservations reservation = (Reservations) object;
		Map<String, Object> campsiteResponse = campsiteService.getById(reservation.getCampsiteId());
		Campsites campsite = (Campsites) campsiteResponse.get("Campsite");
		try {
			if (campsite == null)
				throw new ObjectNotFoundException("campsiteId");
			if (reservation.getDepartureDate().before(reservation.getArrivalDate())
					|| reservation.getDepartureDate().equals(reservation.getArrivalDate()))
				throw new DateRangeException();
			if (!isValidMaxDayDifference(reservation.getArrivalDate(), reservation.getDepartureDate()))
				throw new DifferenceDayReservationException();
			if (!isValidAdvanceReservation(reservation.getArrivalDate()))
				throw new AheadArrivalReservationException();
			if (!isValidRangeCampsiteDates(campsite, reservation))
				throw new DateRangeReservationException();
			reservation.set_id(id);
			if (existsOverlappingReservation(campsite, reservation))
				throw new OverlappingException();
			repository.save(reservation);
			responseMap.put(ServiceEnum.RESERVATION.toString(), reservation);
		} catch (ObjectNotFoundException | DateRangeException | DifferenceDayReservationException
				| AheadArrivalReservationException | DateRangeReservationException | OverlappingException e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> delete(ObjectId id) {
		responseMap.clear();
		try {
			if (!repository.existsById(id.toString()))
				throw new ObjectNotFoundException("id");
			Reservations reservation = repository.findBy_id(id);
			Map<String, Object> campsiteResponse = campsiteService.getById(reservation.getCampsiteId());
			Campsites campsite = (Campsites) campsiteResponse.get("Campsite");
			if (campsite == null)
				throw new ObjectNotFoundException("campsiteId");
			List<ObjectId> idList = campsite.getReservationIds().stream()
					.filter(reservationId -> !reservationId.equals(reservation.get_id())).collect(Collectors.toList());
			campsite.setReservationIds(idList);
			campsiteService.update(campsite.get_id(), campsite);
			repository.delete(repository.findBy_id(id));
			responseMap.put(ServiceEnum.MESSAGE.toString(), ServiceEnum.MESSAGE_DELETED);
		} catch (ObjectNotFoundException e) {
			responseMap.put(ServiceEnum.ERROR.toString(), e.getMessage());
		}
		return responseMap;
	}

	private boolean isValidMaxDayDifference(Date initialDate, Date finalDate) {
		long diff = finalDate.getTime() - initialDate.getTime();
		if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <= MAX_DAY_RESERVATION)
			return true;
		return false;
	}

	private boolean isValidAdvanceReservation(Date initialDate) {
		long diff = initialDate.getTime() - new Date().getTime();
		if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) >= MIN_ADVANCE_RESERVATION
				&& TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) <= MAX_ADVANCE_RESERVATION)
			return true;
		return false;
	}

	private boolean isValidRangeCampsiteDates(Campsites campsite, Reservations reservation) {
		if (campsite != null) {
			return (reservation.getArrivalDate().after(campsite.getOpenDate())
					|| reservation.getArrivalDate().equals(campsite.getOpenDate()))
					&& (reservation.getDepartureDate().before(campsite.getCloseDate())
							|| reservation.getDepartureDate().equals(campsite.getCloseDate()));
		}
		return false;
	}

	private boolean existsOverlappingReservation(Campsites campsite, Reservations reservation) {
		boolean overlap = false;
		List<ObjectId> campsiteReservationIds = campsite.getReservationIds();
		int i = 0;
		Reservations campsiteReservation;
		while (!overlap && i < campsiteReservationIds.size()) {
			campsiteReservation = repository.findBy_id(campsiteReservationIds.get(i));
			if ((campsiteReservation != null && !campsiteReservation.get_id().equals(reservation.get_id())
					&& reservation.getArrivalDate().after(campsiteReservation.getArrivalDate())
					&& reservation.getArrivalDate().before(campsiteReservation.getDepartureDate()))
					|| (campsiteReservation != null && !campsiteReservation.get_id().equals(reservation.get_id())
							&& reservation.getDepartureDate().after(campsiteReservation.getArrivalDate())
							&& reservation.getDepartureDate().before(campsiteReservation.getDepartureDate()))
					|| (campsiteReservation != null && !campsiteReservation.get_id().equals(reservation.get_id())
							&& reservation.getArrivalDate().equals(campsiteReservation.getArrivalDate())
							&& reservation.getDepartureDate().equals(campsiteReservation.getDepartureDate())))
				overlap = true;
			else {
				i++;
			}
		}
		return overlap;
	}

}
