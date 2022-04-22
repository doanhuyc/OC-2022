
/*
 * Test Java classes module
 * 
 * Contains one example test
 * 
 * You must write your other tests in this file
 * 
 * Note that you can use any libraries here for your tests that are available in the standard  CS Java version
 * 
 * For instance, this example uses a 'thread safe' AtomicInteger.
 * 
 *  NOTE: you are NOT allowed to use any thread safe libraries in TenPinManager.java
 *  
 */

public class Tests {
	private int threadsReturned;
	private int nPlayerCreated;
	private Manager tenPinManager;

	private class PlayerThread extends Thread {
		Manager manager;
		String bookersName;
		PlayerThread (Manager manager, String bookersName) {
			this.manager = manager;
			this.bookersName = bookersName;
			nPlayerCreated++;
		}
		public void run(){
			manager.playerLogin(bookersName);
			threadsReturned++;
		}
	}

	// To call at the start of every test
	private void setUp() {
		tenPinManager = new TenPinManager();
		threadsReturned = 0;
		nPlayerCreated = 0;
	}

	void test() {
		test_booker_name_case_sensitive();

		test_book_lane_invalid_number_1();
		test_book_lane_invalid_number_2();

		test_not_available_space_1();
		test_not_available_space_2();
		test_not_available_space_3();

		test_not_enough_player_in_queue1();
		test_not_enough_player_in_queue2();
		test_not_enough_player_in_queue3();

		test_basic_3();
		test_basic_4();

		test_basic_3_change_order_1();
		test_basic_3_change_order_2();

		test_basic_4_change_order_1();
		test_basic_4_change_order_2();
		test_basic_4_change_order_3();
	}

	private void test_book_lane_invalid_number_1() {
		System.out.println("This test books lane with invalid number and create 5 players to login.");
		System.out.println("Expected behaviour: 5 players will wait");
		setUp();

		tenPinManager.bookLane("Jane", -8);
		playerLogin("Jane", 5);

		assertResult(0);
	}

	private void test_book_lane_invalid_number_2() {
		System.out.println("This test books 1 lane with invalid number and 1 lane with 5 players then create 5 players to login.");
		System.out.println("Expected behaviour: 5 players will return, 0 will wait");
		setUp();

		tenPinManager.bookLane("Jane", -8);
		playerLogin("Jane", 5);
		tenPinManager.bookLane("Jane", 5);

		assertResult(5);
	}

	private void test_booker_name_case_sensitive() {
		System.out.println("This test books 1 lane with invalid number and 1 lane with 5 players then create 5 players to login, name case sensitive.");
		System.out.println("Expected behaviour: 0 players will return, 5 will wait");
		setUp();

		tenPinManager.bookLane("Jane", 5);
		playerLogin("JANe", 5);

		assertResult(0);
	}

	private void test_not_available_space_1(){
		System.out.println("This test books 1 lane for 6 players in the name of 'Jane' and then creates 7 player threads that try to login.");
		System.out.println("Expected behaviour: 6 players return from tenPinManager.playerLogin, 1 player indefinitely waits");
		setUp();

		tenPinManager.bookLane("Jane", 6);
		playerLogin("Jane", 7);

		assertResult(6);
	}

	private void test_not_available_space_2(){
		System.out.println("This test books 1 lane for 6 players in the name of 'Jane' and then creates 9 player threads that try to login, then books 1 lane for 3 players in the name of 'Jane'" );
		System.out.println("Expected behaviour: 9 players return from tenPinManager.playerLogin");
		setUp();

		tenPinManager.bookLane("Jane", 6);
		playerLogin("Jane", 9);
		tenPinManager.bookLane("Jane", 3);

		assertResult(9);
	}

	private void test_not_available_space_3(){
		System.out.println("This test books 1 lane for 6 players in the name of 'Jane' and then creates 9 player threads that try to login, then books 1 lane for 3 players in the name of 'Jane'"
				+ "and then create 3 players login in the name of 'Jane");
		System.out.println("Expected behaviour: 9 players return from tenPinManager.playerLogin, 3 players waiting");
		setUp();

		tenPinManager.bookLane("Jane", 6);
		playerLogin("Jane", 9);
		tenPinManager.bookLane("Jane", 3);
		playerLogin("Jane", 3);

		assertResult(9);
	}

	private void test_not_enough_player_in_queue1() {
		System.out.println("This test books 2 lane for 6 and 3 player in the name of 'Jane, and then creates 5 players trying to login");
		System.out.println("Expected behaviour: then all 5 player threads should be blocked as the first booking requires 6 players, 0 return");
		setUp();

		tenPinManager.bookLane("Jane", 6);
		tenPinManager.bookLane("Jane", 3);
		playerLogin("Jane", 5);

		assertResult(0);
	}

	private void test_not_enough_player_in_queue2() {
		System.out.println("This test books 2 lane for 6 and 3 player in the name of 'Jane, and then creates 8 players trying to login");
		System.out.println("Expected behaviour: 6 player threads should be returned 2 will wait");
		setUp();

		tenPinManager.bookLane("Jane", 6);
		tenPinManager.bookLane("Jane", 3);
		playerLogin("Jane", 8);

		assertResult(6);
	}

	private void test_not_enough_player_in_queue3() {
		System.out.println("This test books lanes and create player alternately");
		System.out.println("Expected behaviour: 9 player threads should be returned 3 will wait");
		setUp();

		tenPinManager.bookLane("Jane", 2);
		playerLogin("Jane", 5);
		tenPinManager.bookLane("Jane", 3);
		playerLogin("Jane", 1);
		tenPinManager.bookLane("Jane", 4);
		playerLogin("Jane", 6);

		assertResult(9);
	}

	private void test_basic_3() {
		System.out.println("This test books 2 lane for 6 and 3 player in the name of 'Jane and 1 lane for 3 players in the name of Fred,"
				+ " and then creates 5 players trying to login to Jane and 4 players login to Fred");
		System.out.println("Expected behaviour: 3 players login to Fred should be allow to return and rest of player");
		setUp();

		tenPinManager.bookLane("Jane", 6);
		tenPinManager.bookLane("Jane", 3);
		tenPinManager.bookLane("Fred", 3);
		playerLogin("Jane", 5);
		playerLogin("Fred", 4);

		assertResult(3);
	}

	private void test_basic_4() {
		System.out.println("This test creates 5 players trying to login to Jane and 4 players login to Fred "
				+ "and then books 1 lane for 6 player in the name of 'Jane and 1 lane for 3 players in the name of Fred");
		System.out.println("Expected behaviour: 3 players login to Fred should be allow to return and rest of player waiting");
		setUp();

		playerLogin("Jane", 5);
		playerLogin("Fred", 4);
		tenPinManager.bookLane("Jane", 6);
		tenPinManager.bookLane("Fred", 3);

		assertResult(3);
	}

	private void test_basic_3_change_order_1() {
		System.out.println("test_basic_3_change_order_1");
		System.out.println("Expected behaviour: 3 players login to Fred should be allow to return and rest of player");
		setUp();

		playerLogin("Jane", 5);
		tenPinManager.bookLane("Jane", 6);
		playerLogin("Fred", 4);
		tenPinManager.bookLane("Fred", 3);
		tenPinManager.bookLane("Jane", 3);

		assertResult(3);
	}

	private void test_basic_3_change_order_2() {
		System.out.println("test_basic_3_change_order_2");
		System.out.println("Expected behaviour: 3 players login to Fred should be allow to return and rest of player");
		setUp();


		tenPinManager.bookLane("Fred", 3);
		playerLogin("Fred", 4);
		tenPinManager.bookLane("Jane", 6);
		playerLogin("Jane", 5);
		tenPinManager.bookLane("Jane", 3);

		assertResult(3);
	}

	private void test_basic_4_change_order_1() {
		System.out.println("Test_basic_4_change_order_1");
		System.out.println("Expected behaviour: 3 players login to Fred should be allow to return and rest of player waiting");
		setUp();

		playerLogin("Jane", 5);
		tenPinManager.bookLane("Jane", 6);
		playerLogin("Fred", 4);
		tenPinManager.bookLane("Fred", 3);

		assertResult(3);
	}

	private void test_basic_4_change_order_2() {
		System.out.println("Test_basic_4_change_order_2");
		System.out.println("Expected behaviour: 3 players login to Fred should be allow to return and rest of player waiting");
		setUp();

		tenPinManager.bookLane("Jane", 6);
		playerLogin("Fred", 4);
		tenPinManager.bookLane("Fred", 3);
		playerLogin("Jane", 5);

		assertResult(3);
	}

	private void test_basic_4_change_order_3() {
		System.out.println("Test_basic_4_change_order_3");
		System.out.println("Expected behaviour: 3 players login to Fred should be allow to return and rest of player waiting");
		setUp();

		playerLogin("Fred", 4);
		playerLogin("Jane", 5);
		tenPinManager.bookLane("Fred", 3);
		tenPinManager.bookLane("Jane", 6);

		assertResult(3);
	}
	
	private void playerLogin(String bookerName, int numberPlayer) {
		for (int i=0; i < numberPlayer; i++) {
			PlayerThread player = new PlayerThread(tenPinManager, bookerName);
			player.start();
		}
	}

	private void assertResult(int expectedValue) {
		threadSleepUntilAllThreadReturnOrExceedTimeOut(expectedValue);

		//Test result
		if (threadsReturned == expectedValue) {
			System.out.println ("Test = SUCCESS " + printDetail());
		} else {
			System.out.println("Test = FAIL " + printDetail());
		}
	}

	private String printDetail() {
		return nPlayerCreated + " created. " + threadsReturned + " returned. " + (nPlayerCreated - threadsReturned) + " waiting";
	}

	private void threadSleepUntilAllThreadReturnOrExceedTimeOut(int expectedValue) {
		int testTimeout = 10; //mS
		int maximumTestTimeOut = 10000; //ms

		// Now wait for player threads to do their thing:
		int timeSpent = 0;
		do {
			try {
				Thread.sleep(testTimeout);
				timeSpent = timeSpent + testTimeout;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (threadsReturned != expectedValue && timeSpent < maximumTestTimeOut);
	}
}
