Project Setup With Pre‑Populated Database
This project is designed to run entirely from a pre‑populated MySQL database. All member and legislation data has already been synced from the Congress API and exported into a SQL file included with the submission. No external API calls or sync operations are required to run or grade the project.

1. Overview
   The application uses Spring Boot, MySQL, and JPA.
   To ensure consistent results during grading, the database is provided as a SQL dump file containing:
   • 	All members
   • 	All sponsored and cosponsored legislation
   • 	All schema definitions
   The professor only needs to import the SQL file and update the database credentials in the configuration file.

2. Files Included in the Submission
   The project folder contains:
   • 	 — full Spring Boot source code
   • 	 — Maven dependencies
   • 	 — configured for read‑only database usage
   • 	 — the complete MySQL database dump
   • 	 — this setup guide
   The SQL file is the key component that allows the project to run without syncing from the Congress API.

3. How the Database Was Prepared (for reference)
   The team populated the database before submission using two internal endpoints:
   • 	 — loads all members
   • 	 — loads all sponsored and cosponsored legislation
   Once the database was fully populated, it was exported using:

This SQL file is included in the submission so the professor does not need to run these sync operations.

4. Importing the Database (Professor Instructions)
1. 	Create a new MySQL database (example name: ).
2. 	Import the provided SQL file using the following command:

3. 	Update the database credentials in  to match your local MySQL setup.
4. 	Start the Spring Boot application.
      Once imported, the application will run entirely from the stored data.

5. Spring Boot Configuration for Submission
   To prevent Hibernate from modifying or recreating tables, the project is configured with:

This ensures the database schema and data remain unchanged during grading.

6. Running the Application
   After importing the SQL file and updating the database credentials:
1. 	Run the Spring Boot application (via IDE or ).
2. 	Access the API endpoints normally.
3. 	All data will load from the database — no API calls or sync operations are required.

7. Notes for the Professor
   • 	The application does not require internet access.
   • 	All data is already included in the SQL file.
   • 	Sync endpoints are not needed for grading and can be ignored.
   • 	The project will behave consistently on any machine with MySQL installed.