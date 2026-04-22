Smart Campus Sensor \& Room Management API

A RESTful JAX-RS API built with Jersey 2.41 and an embedded Grizzly HTTP server.  

Module: \*\*5COSC022W – Client-Server Architectures (2025/26)\*\*

API Overview

This API manages campus *Room* and the *Sensors* deployed within them, along with a historical log of *SensorReadings*. It is built entirely with JAX-RS (no Spring Boot, no database) using in-memory `ConcurrentHashMap` structures as the data store.


Base URL  `http://localhost:8080/api/v1` 

Format    JSON (`application/json`)     

Resource Hierarchy
api/v1

GET    → list all rooms
POST   → create a room


GET    → get one room
DELETE → delete room (blocked if sensors present)


GET    → list sensors (optional ?type= filter)

POST   → register a sensor (validates roomId)

{sensorId}

GET    → get one sensor

readings
GET  → reading history

POST → append new reading (updates sensor currentValue)

Build \& Run Instructions


Prerequisites

Java 11 or higher
Maven 3.6+
NetBeans IDE

Step 1 – Open the project

Open NetBeans → File → Open Project → select the smart-campus-api folder.

Step 2 – Build

Right-click the project → Clean and Build (Shift+F11)


Step 3 – Run

Right-click SmartCampusApi.java → Run File


You should see:

Smart Campus API running at: http://localhost:8080/api/v1/

Step 4 – Stop

Press ENTER in the NetBeans console.


Sample curl Commands


1. Discovery endpoint

curl -X GET http://localhost:8080/api/v1/


2. List all rooms

curl -X GET http://localhost:8080/api/v1/rooms


 3. Create a new room

curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d "{"id":"ENG-205","name":"Engineering Lab","capacity":40}"



4. Register a new sensor

curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{"id":"CO2-002","type":"CO2","status":"ACTIVE","currentValue":400.0,"roomId":"ENG-205"}"


 5. Filter sensors by type

curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"


6. Add a sensor reading

curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{"value":23.7}"


 7. View reading history

curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings

8. Attempt reading on MAINTENANCE sensor (expects 403)

curl -X POST http://localhost:8080/api/v1/sensors/OCC-001/readings -H "Content-Type: application/json" -d "{"value":15.0}"


9. Attempt to delete room with sensors (expects 409)

curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301


10. Attempt sensor with fake roomId (expects 422)

curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{"id":"FAKE-001","type":"CO2","roomId":"DOES-NOT-EXIST"}"


Report – Answers to Coursework Questions


Part 1.1 – JAX-RS Resource Lifecycle


By default, JAX-RS creates a new instance of each resource class for every incoming HTTP request (per-request lifecycle). This means every call creates a fresh resource object which is discarded once the response is sent.


This design guarantees thread safety at the instance level. However, any data stored as an instance variable inside a resource class is lost the moment the request ends. To work around this, all persistent data lives in a shared DataStore singleton. ConcurrentHashMap is used instead of plain HashMap because multiple request threads can read and write simultaneously, preventing data corruption and race conditions without requiring synchronized blocks everywhere.


Part 1.2 – HATEOAS and Hypermedia


HATEOAS (Hypermedia As The Engine Of Application State) is the principle that API responses should include hyperlinks pointing to related resources and available actions, allowing clients to navigate the API dynamically rather than relying on hardcoded URLs.


This is considered a hallmark of advanced RESTful design because it makes the API self-documenting and discoverable. Compared to static documentation, HATEOAS is always in sync with the live API, reduces client coupling to specific URL structures, and makes it easy for new developers to explore the system by simply following the embedded links.


Part 2.1 – Returning IDs vs Full Objects

Returning only IDs means the client must fire one GET request per room to retrieve actual data — the classic N+1 request problem. For a campus with 500 rooms this means 501 round-trips, which is inefficient and slow.


Returning full objects means all room data arrives in a single request. The payload is larger but a single network round-trip is almost always faster than many small ones. Returning full objects is the right choice here because the Room model is small and the primary use case needs all fields to render the list.


Part 2.2 – Is DELETE Idempotent?


In this implementation DELETE is functionally idempotent in terms of end state but not strictly in terms of response code. The first DELETE on an existing room returns 204 No Content. A second DELETE on the same room returns 404 Not Found. The server state after both calls is identical (the room is absent), so DELETE is technically idempotent from a state perspective. The different status codes do not violate idempotency — they report the state more accurately.

Part 3.1 – @Consumes and Media Type Mismatch


The @Consumes(MediaType.APPLICATION\_JSON) annotation declares that the endpoint only accepts requests with Content-Type: application/json. If a client sends text/plain or application/xml, JAX-RS inspects the Content-Type header before the request reaches the resource method and rejects it immediately with HTTP 415 Unsupported Media Type. This prevents the Jackson deserialiser from attempting to parse non-JSON payloads and gives clients a clear, standard signal that they sent the wrong format.


Part 3.2 – @QueryParam vs Path Segment for Filtering


Query parameters are semantically designed for filtering, sorting and searching within a collection. They are optional by nature — omitting ?type=CO2 simply returns all sensors. The base resource URI still clearly identifies the sensors collection and the query string modifies the view of it.


Path segments are designed to identify specific distinct resources. Using /sensors/type/CO2 implies that type is a sub-resource, which is architecturally misleading. Query parameters can also be combined trivially (?type=CO2\&status=ACTIVE) while combining path segments quickly becomes unwieldy.


Part 4.1 – Benefits of the Sub-Resource Locator Pattern


The sub-resource locator pattern delegates a URL path segment to a dedicated resource class at runtime rather than defining all methods in one monolithic controller.


The primary benefit is separation of concerns: SensorResource manages sensor CRUD while SensorReadingResource focuses exclusively on reading history. Each class is smaller, easier to read and easier to test in isolation. In a large API, defining every method in one class would produce a file with hundreds of methods — unmaintainable and impossible to navigate. New nested resources can be added without touching existing classes, conforming to the Open/Closed principle.


Part 5.2 – Why HTTP 422 Over 404 for Missing References


HTTP 404 communicates that the requested URL does not exist. In this scenario the URL POST /api/v1/sensors is perfectly valid and the server found it successfully.


HTTP 422 Unprocessable Entity communicates that the request was syntactically correct but semantically invalid — the server understood the request but cannot process it because the data contains a logical error. The roomId field points to a room that does not exist. Using 422 is more semantically precise: it tells the client the problem is a business-logic validation failure on a specific field, not a routing problem (404) or a format problem (400).


Part 5.4 – Security Risks of Exposing Stack Traces


Exposing raw Java stack traces to external API consumers is a significant security vulnerability because:

1. It reveals internal package and class names, making it easier to craft targeted exploits

2. It discloses library names and versions, letting attackers look up known CVEs for those exact versions

3. It exposes server file paths, aiding directory traversal attacks

4. It leaks business logic flow, helping attackers understand where to inject malicious input


The GlobalExceptionMapper mitigates all these risks by logging the full trace server-side only while returning a generic information-free 500 message to the client.


Part 5.5 – Filters vs Manual Logging

Using a JAX-RS filter for logging is superior to inserting Logger.info() calls into every resource method because:

1. Single point of maintenance — a change to the log format requires editing one class not dozens

2. Guaranteed coverage — the filter runs for every request including those that hit exception mappers before reaching a resource method

3. DRY principle — avoids code duplication across every endpoint

4. Separation of concerns — resource methods contain only business logic

5. Extensibility — the same filter can be extended to add request ID tracking or performance timing without touching resource code







