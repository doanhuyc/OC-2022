


/*
 * Ten Pin Manager class
 * 
 * You must write code here so that this class satisfies the Coursework User Requirements (see CW specification on Can).
 * 
 * You may add private classes and methods to this file 
 * 
 * 
 * 
 ********************* IMPORTANT ******************
 * 
 * 1. You must implement TenPinManager using Java's ReentrantLock class and condition interface (as imported below).
 * 2. Other thread safe classes, e.g. java.util.concurrent MUST not be used by your TenPinManager class.
 * 3. Other thread scheduling classes and methods (e.g. Thread.sleep(timeout), ScheduledThreadPoolExecutor etc.) must not be used by your TenPinManager class..
 * 4. Busy waiting must not be used: specifically, when an instance of your TenPinManager is waiting for an event (i.e. a call to booklane() or playerLogin() ) it must not consume CPU cycles.
 * 5. No other code except that provided by you here (in by your TenPinManager.java file) will be used in the automatic marking.
 * 6. Your code must be reasonably responsive (e.g. no use of sleep methods etc.).
 * 
 * Failure to comply with the above will mean that your code will not be marked and you will receive a mark of 0.
 * 
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TenPinManager implements Manager {
	private class BookerManager {
		private final ArrayList<Booker> listBooker = new ArrayList<>();
		private final ReentrantLock lock = new ReentrantLock();
		private final Condition noBookerFound = lock.newCondition();

		void addBooker(String bookerName, int nPlayers) {
			Booker booker = getBooker(bookerName);
			if (booker == null) {
				addBooker(new Booker(bookerName, nPlayers));
			} else {
				booker.addBooking(nPlayers);
			}
		}

		void playerLogin(String bookerName) throws InterruptedException {
			Booker booker = awaitBooker(bookerName);
			booker.playerLogin();
		}

		private void addBooker(Booker booker) {
			lock.lock();

			listBooker.add(booker);
			noBookerFound.signalAll();

			lock.unlock();
		}

		private Booker awaitBooker(String bookerName) throws InterruptedException {
			lock.lock();
			try  {
				Booker booker = getBooker(bookerName);
				while (booker == null) {
					noBookerFound.await();
					booker = getBooker(bookerName);
				}
				return booker;
			}  finally {
				lock.unlock();
			}
		}

		private Booker getBooker(String bookerName) {
			for (Booker booker : listBooker) {
				if (bookerName.equals(booker.name)) {
					return booker;
				}
			}
			return null;
		}
	}

	private class Booker {
		private final String name;
		private final LinkedList<Booking> bookings;

		private final ReentrantLock lock;
		private final Condition noAvailableSpace;

		Booker(String name, int numberOfPlayer) {
			lock = new ReentrantLock();
			noAvailableSpace = lock.newCondition();

			this.name = name;
			bookings = new LinkedList<>();
			addBooking(numberOfPlayer);
		}

		void addBooking(int numberPlayer) {
			lock.lock();

			Booking booking = new Booking(numberPlayer);
			bookings.add(booking);
			noAvailableSpace.signalAll();

			lock.unlock();
		}

		void playerLogin() throws InterruptedException {
			Booking booking = awaitAvailableBooking();
			booking.processBooking();
		}

		private Booking awaitAvailableBooking() throws InterruptedException {
			lock.lock();
			try {
				Booking booking = getAvailableBooking();

				while (booking == null) {
					noAvailableSpace.await();
					booking = getAvailableBooking();
				}
				booking.playerLogin();
				return booking;
			} finally {
				lock.unlock();
			}
		}

		private Booking getAvailableBooking() {
			for (Booking booking : bookings) {
				if (booking.isBookingAvailable()) {
					return booking;
				}
			}
			return null;
		}
	}

	private class Booking {
		private final int maxNumberPlayer;
		private int numberPlayerLoggedIn;

		private final ReentrantLock lock;
		private final Condition notEnoughPlayerToStart;

		Booking(int maxNumberPlayer) {
			lock = new ReentrantLock();
			notEnoughPlayerToStart = lock.newCondition();

			this.maxNumberPlayer = maxNumberPlayer;
			this.numberPlayerLoggedIn = 0;
		}

		boolean isBookingAvailable() {
			return numberPlayerLoggedIn < maxNumberPlayer;
		}

		void playerLogin() {
			lock.lock();

			numberPlayerLoggedIn++;
			notEnoughPlayerToStart.signalAll();

			lock.unlock();
		}

		void processBooking() throws InterruptedException {
			lock.lock();
			while (numberPlayerLoggedIn != maxNumberPlayer) {
				notEnoughPlayerToStart.await();
			}
			lock.unlock();
		}
	}

	private final BookerManager bookerManager = new BookerManager();

	@Override
	public void bookLane(String bookersName, int nPlayers) {
		bookerManager.addBooker(bookersName, nPlayers);
	}

	@Override
	public void playerLogin(String bookersName) {
		try {
		 	bookerManager.playerLogin(bookersName);
		} catch (InterruptedException e) {
			throw new RuntimeException("Unhandled error");
		}
	}
}
