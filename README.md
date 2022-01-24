# DoITnow

ToDo android application that allows the user to create todo tasks related to places/locations (via google maps). For example, if a user needs to buy something from a store, he will enter a task with a title, description and location selected from the maps. Thus, when the user is near that store (within a radius of 150 meters) he will receive a high priority notification for that task to remaind him.


## Before running the application
1. Open https://console.cloud.google.com/google/maps-apis/credentials and generate an API key for your project (firstly you need to create project named DoITnow and enable Maps SDK and Places SDK for that project).
2. Open app/src/main/res/values/google_maps_api.xml and paste the generated API key there.
