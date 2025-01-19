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

import java.io.*;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import commons.Directory;
import commons.Note;
import jakarta.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.springframework.http.HttpStatus;
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
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/quotes")
				.request(APPLICATION_JSON)
				.get(new GenericType<List<Quote>>() {
				});
	}

	public Quote addQuote(Quote quote) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/quotes")
				.request(APPLICATION_JSON)
				.post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
	}

	public boolean isServerAvailable() {
		try {
			ClientBuilder.newClient(new ClientConfig())
					.target(SERVER)
					.request(APPLICATION_JSON)
					.get();
		} catch (ProcessingException e) {
			if (e.getCause() instanceof ConnectException) {
				return false;
			}
		}
		return true;
	}

	// NOTES SECTION -------------------------------------------------------

	public List<Note> getNotes() {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes")
				.request(APPLICATION_JSON)
				.get(new GenericType<List<Note>>() {
				});
	}

	public Note getNoteById(long id) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + id)
				.request(APPLICATION_JSON)
				.get(Note.class);
	}

	public Note addNote(Note note) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes")
				.request(APPLICATION_JSON)
				.post(Entity.entity(note, APPLICATION_JSON), Note.class);
	}

	public Note updateNote(Note note) {
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + note.getId())
				.request(APPLICATION_JSON)
				.put(Entity.entity(note, APPLICATION_JSON), Note.class);
	}

	public void deleteNote(long id) {
		ClientBuilder.newClient(new ClientConfig())
				.target(SERVER).path("api/notes/" + id)
				.request(APPLICATION_JSON)
				.delete();
	}

	public List<Note> getFilteredNotes(String filter) {
		String encodedFilter = URLEncoder.encode(filter, StandardCharsets.UTF_8);
		return ClientBuilder.newClient(new ClientConfig())
				.target(SERVER + "api/notes/search?filter=" + encodedFilter)
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
			String url = "http://localhost:8080/api/notes/" + id;
			restTemplate.delete(url);
			System.out.println("Note with ID " + id + " deleted successfully.");
		} catch (HttpClientErrorException.NotFound e) {
			System.err.println("Error: Note with ID " + id + " not found. " + e.getMessage());
			throw e;
		} catch (RestClientException e) {
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Note with ID " + id + " not found", null, null, null);
		}
	}

	public List<Directory> getAllDirectories() {
		List<Directory> allDirectories = new ArrayList<>();
		Directory allDirectory = new Directory("All");
		if (allDirectory != null) {
			allDirectory.setNotes(getNotes());

			try {
				String url = SERVER + "api/directories";
				allDirectories = ClientBuilder.newClient(new ClientConfig())
						.target(url)
						.request(APPLICATION_JSON)
						.get(new GenericType<List<Directory>>() {});
				allDirectories.removeIf(directory -> "All".equals(directory.getTitle()));
				allDirectories.add(0, allDirectory);
			} catch (Exception e) {
				e.printStackTrace();
				return List.of();
			}
		}
		return allDirectories;
	}

	public List<Note> getDirectoryNotes(Directory directory) {
		try {
			return ClientBuilder.newClient(new ClientConfig())
					.target(SERVER)
					.path("api/directories/search?filter=" + directory.getId())
					.request(APPLICATION_JSON)
					.get(new GenericType<List<Note>>() {
					});
		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	public Directory addDirectory(Directory directory) {
		try {
			return ClientBuilder.newClient(new ClientConfig())
					.target(SERVER + "api/directories")
					.request(APPLICATION_JSON)
					.post(Entity.entity(directory, APPLICATION_JSON), Directory.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String uploadFile(Long noteId, String fileName, byte[] fileBytes) {
		try {
			String url = "http://localhost:8080/api/files/" + noteId + "/upload";
			Client client = ClientBuilder.newBuilder()
					.register(MultiPartFeature.class)
					.build();
			FormDataMultiPart multiPart = new FormDataMultiPart();
			FormDataContentDisposition contentDisposition = FormDataContentDisposition
					.name("file")
					.fileName(fileName)
					.build();
			FormDataBodyPart filePart = new StreamDataBodyPart(
					"file",
					new ByteArrayInputStream(fileBytes),
					fileName
			);
			filePart.setContentDisposition(contentDisposition);
			multiPart.bodyPart(filePart);
			Response response = client.target(url)
					.request(MediaType.MULTIPART_FORM_DATA)
					.post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));
			if (response.getStatus() == 200) {
				return response.readEntity(String.class);
			} else {
				throw new RuntimeException("Failed to upload file" + response.getStatus());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
