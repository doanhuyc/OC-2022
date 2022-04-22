

public class Main {

	private static final Integer NUMBER_OF_LOOP_TO_RUN_TEST = 100;

	public static void main(String[] args) {

		Tests tests = new Tests();

		for (int i = 0; i < NUMBER_OF_LOOP_TO_RUN_TEST; i++) {
			tests.test();
		}

		System.exit(0);
	}
}
