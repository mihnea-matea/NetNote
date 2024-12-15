/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import commons.Note;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ServerUtils {

	private static final String SERVER = "http://localhost:8080/";

	public void getQuotesTheHardWay() throws IOException, URISyntaxException {
		var url = new URI("http://localhost:8080/api/quotes").toURL();
		var is = url.openConnection().getInputStream();
		var br = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	public List<Quote> getQuotes() {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/quotes") //
				.request(APPLICATION_JSON) //
				.get(new GenericType<List<Quote>>() {
				});
	}

	public Quote addQuote(Quote quote) {
		return ClientBuilder.newClient(new ClientConfig()) //
				.target(SERVER).path("api/quotes") //
				.request(APPLICATION_JSON) //
				.post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
	}


//this is from the quote setup but i think we should keep it
	public boolean isServerAvailable() {
		try {
			ClientBuilder.newClient(new ClientConfig()) //
					.target(SERVER) //
					.request(APPLICATION_JSON) //
					.get();
		} catch (ProcessingException e) {
			if (e.getCause() instanceof ConnectException) {
				return false;
			}
		}
		return true;
	}

	// NOTES SECTION -------------------------------------------------------
	/**
	 * Get all notes saved on the server
	 * @return A list of all notes
	 */
	public List<Note> getNotes() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes")
				.request(APPLICATION_JSON)
				.get(new GenericType<List<Note>>() {});
	}

	/**
	 * Get a single note by its id
	 * @param id The id of the note
	 * @return The specific note
	 */
	public Note getNoteById(long id) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + id)
				.request(APPLICATION_JSON)
				.get(Note.class);
	}

	/**
	 * Add a new note to the server
	 * @param note The note object to add.
	 * @return The added note from the server
	 */
	public Note addNote(Note note) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes")
				.request(APPLICATION_JSON)
				.post(Entity.entity(note, APPLICATION_JSON), Note.class);
	}

	/**
	 * Update an existing note on the server
	 * @param note The updated note
	 * @return The updated note from the server
	 */
	public Note updateNote(Note note) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + note.getId())
				.request(APPLICATION_JSON)
				.put(Entity.entity(note, APPLICATION_JSON), Note.class);
	}

	/**
	 * Delete a note by its id
	 * @param id The id of the note to delete.
	 */
	public void deleteNote(long id) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + id)
				.request(APPLICATION_JSON)
				.delete();
	}
	//we can add error messages and try catch blocks for operations, but we should at least have a label that can display the message first

	public List<Note> getFilteredNotes(String filter) {
		//Generative AI used for figuring out encoding
		String encodedFilter = URLEncoder.encode(filter, StandardCharsets.UTF_8);
		return ClientBuilder.newClient(new ClientConfig()).target(SERVER + "api/notes/search?filter=" + encodedFilter)
				.request(APPLICATION_JSON)
				.get(new GenericType<List<Note>>() {
				});
	}

	private RestTemplate restTemplate = new RestTemplate();

	public void deleteNoteById(long id) {
		try {
			if (id < 0) {
				throw new IllegalArgumentException("ID must be a positive number.");
			}
			String url = "http://localhost:8080/" + id;
			restTemplate.delete(url);
			System.out.println("Note with ID " + id + " deleted successfully.");
		} catch (HttpClientErrorException.NotFound e) {
			System.err.println("Error: Note with ID " + id + " not found. " + e.getMessage());
			System.out.println("id is hardcoded to be 10 for now");
			throw e;
		} catch (RestClientException e) {
			throw new RuntimeException(e);
		}
	}

}