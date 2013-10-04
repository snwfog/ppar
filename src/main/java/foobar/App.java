package foobar;

import com.googlecode.flyway.core.Flyway;

public class App {
	public static void main(String[] args) {
		// System.out.println( "Hello World!" );

		// Create the Flyway instance
		Flyway flyway = new Flyway();

		// Point it to the database
		// flyway.setDataSource("jdbc:h2:file:target/foobar", "sa", null);
		flyway.setDataSource("jdbc:mysql://localhost:3306/flyway", "root", null);

		// Start the migration
		flyway.migrate();

	}

}
