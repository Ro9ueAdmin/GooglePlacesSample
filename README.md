# GooglePlacesSample
This projects samples the use of Google Places APIs for fetching the nearby places and their photos. 
Most of the Android best practices and coding practices are implemented in this project. Used Google Places Rest API for “nearbysearch” with radius of 1000km

#How to use this project:
Replace your own Places API key in core/constants $API_KEY
public static final String API_KEY = "$TYPE_FOR_PLACES_API_KEY_HERE";

#Created following modules for defining the boundaries:
Rest Module: Contains all rest api related code
Util Module: Contains utility classes like ValidationUtil for validating string empty, list empty, etc.
Places Module: Fetches data from Rest Module. Save Places locally in database. Expose a PlaceDataProvider interface which encapsulates the logic of fetching the places.
App Module: Contains UI of application. Interacts which places module for fetching the data.
Followed MVP architecture.

#Third Party Libraries
Used Room, Retrofit, Fresco, RxPermissions, RxJava libraries

#Junits
Written Junits for core classes PlacesDataProvider, PlacesDataAdapter and PlacesDetailsOps. Used PowerMock for mocking purpose. Used TestObservable for testing with RxJava2. For testing purpose added json response in "resources/.." directory

#Espresso Test Cases
Written espresso test for HomeActivity and PlacesDetailsActivity.
