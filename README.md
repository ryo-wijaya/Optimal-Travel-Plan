# Optimal Travel Plan
Full-stack software engineering project in a project team of size 4

### Description
A complete multi-tiered enterprise solution with 2 front-end components, one website and one hybrid mobile web application that businesses and customers alike can use to manage travel itineraries, services and bookings. 

#### Optimal Travel Plan Website
**Admin submodule**
Used for administrators to manage accounts, services, tags and countries and view a dashboard of statistics related to the business functions of the application. Admins can also partake in a chat based system to resolve support requests started by customers.

**Business submodule**
The core functions of this application is centred around the ability to manually build a personalised travel itinerary based on services that businesses have created, as well as (after selecting start and end dates) generate an editable travel itinerary. Email notifications will be sent during registration, forgetting of password, and payment. Customers are able to manage their account details, view available services, tags and countries, manage bookings, make reviews and receive replies, create support requests and partake in a chat-based system with the admin (on the JSF side), and view past transactions.

**Business Use cases**
* Create/View/View a list of/Update/Delete Country
* Create/View/View a list of/Update/Delete Tag
* Create/View/View a list of/Update/Disable Account
* View/View a list of/Resolve Support Requests
* Add comment to Support Requests
* Create/View/View a list of/Disable Services
* Business Interface submodule
* View Ratings, Income and Services Charts
* Create/View/Update/Delete Account details (contact info/address)
* Create/View/View a list of/Update/Disable Service
* View/View a list of Booking Details
* View/View a list of/Reply to review
* Generate Booking Report
* Create/View/View a list of/Disable Service Rate


#### Optimal Travel Plan Mobile Web Application
The hybrid mobile application will be designed for customers to use our platform to access our services. Customers are able to handle basic profile management tasks such as account details updating, choosing a list of favourite tags, managing payment methods and viewing and filtering for past transactions and bookings. The main business function of this application is provided in the travel itinerary details page. A non-logged in customer is able to play around with an itinerary, viewing services (and their reviews) and adding them to it. This itinerary will not be saved unless the customer does a login. Afterwhich, the customer may also choose to have a travel itinerary recommended to him/her according to the dates that they determine. Payment can then be made on all predefined bookings. Email notifications will be sent to the Customer’s email address upon registration, sending of recovery email for a forgotten password, payment of bookings in the travel itinerary, and finally the resolving of a support request on the JSF side. 

**Business Use cases / Application functionalities**
* Index Page
* Register as Customer
* Login
* Side Menu
* Home Page
* My Account
* My Travel Itineraries
* Create Support Request
* View All my Support Requests
* View a support request’s details
* Manage Payment
* Adding New Payment Method
* View a List of Services
* View a Service’s Details
* View Travel Itinerary Details (Before Login)
* Generate a Travel Itinerary
* View Booking Details in a Travel Itinerary
* Making Payment for a Travel Itinerary
* View FAQ page


### Technologies used
* Jakarta EE Platform
* GlassFish application server (Oracle)
* Jakarta Server Faces (Website)
* Ionic/Angular (Hybrid mobile web application)
* RESTful web services for Ionic front-end application to call back-end methods made with Enterprise Java Beans components

### How to run
Java EE backend component will has to be hosted, either locally (localhost) or remotely, when deployed, REST services will be deployed together. Once deployed, either front-end applications can be run. If locally, the JSF website can be run on port 8080 http://localhost:8080/ while the Ionic application can be run on port 8100 http://localhost:8100/.

Additional Instructions for demo-ing applications:
For local demo, requires MySQL database and Jakarta EE

For Ionic install: 
npm install primeng --save
npm install primeicons --save
npm install @fullcalendar/angular --save
npm install @fullcalendar/daygrid --save
npm install @fullcalendar/interaction --save
npm install @fullcalendar/timegrid --save
npm install @angular/animations --save
npm install @angular/router --save
npm install @angular/cdk --save

For JSF Install:
Use redmond-1.0.10.jar for PrimeFaces themes:
    <context-param>
        <param-name>primefaces.THEME</param-name>
        <param-value>redmond</param-value>
    </context-param>

Preloaded data: System Administrator with username: "sysadmin" and password: "password"


### Folder Tree Explanation
bin - .apk file (Requires the REST services to be hosted on localhost:8080 to be able to login)
data - Contains database name
docs - Contain project report (Writeup, prototype explanations, use case diagrams, Class diagram, Component diagrams)
source - Source code for backend JEE + frontend JSF ("OptimalTravelPlan"), Source code for frontend Ionic ("DemoIonic")
