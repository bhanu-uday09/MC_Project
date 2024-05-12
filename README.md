# Samachar: The News App!

<img src="https://github.com/bhanu-uday09/MC_Project/assets/140607545/f6564734-409f-41f0-a36d-cc1b21de2e2d" alt="logo_news" width="100" height="100">

## Table of Contents

1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Technologies Used](#technologies-used)
4. [Features](#features)
5. [Implementation Details](#implementation-details)
6. [Activities](#activities)
7. [Testing and Debugging](#testing-and-debugging)
8. [Future Enhancements](#future-enhancements)
9. [Conclusion](#conclusion)
10. [References](#references)
11. [Contributors](#contributors)

## Introduction

In today's fast-paced world, staying up-to-date with the latest news is more important than ever. "Samachar: The News App" is here to make it easier for you to stay informed. Think of it as your one-stop shop for news, where you can quickly and easily access all the information you need, right at your fingertips.

"Samachar: The News App" is an Android application designed to provide users with access to the latest news articles from various sources. The app offers features such as browsing top headlines, saving favorite articles, searching for specific topics, and receiving location-based news updates.

## Project Overview

The project aims to develop a user-friendly news application that aggregates news articles from reliable sources and presents them in a clean and organized manner. By leveraging APIs and integrating location services, the app enhances the user experience by delivering relevant news content based on user preferences and geographic location.

## Technologies Used

- **Android Studio**: Integrated Development Environment (IDE) for Android app development.
- **Kotlin**: Programming language used for developing the Android application.
- **Jetpack Compose**: Used for building the user interface of the app, offering a modern and declarative way to create UI components.
- **Firebase Realtime Database**: Used for storing and retrieving favorite news articles.
- **Retrofit**: HTTP client library for making API requests and handling responses.
- **Jsoup**: Java library for web scraping and parsing HTML content.
- **Google Play Services**: Library for accessing Google's location-based services.
- **Gson**: Library for serializing and deserializing JSON data.
- **XML**: Markup language used for defining layouts and configuring the Android manifest file.
- **JNI (Java Native Interface)**: Facilitates communication between Java (or Kotlin) code and native C++ code, enabling integration of C++ functionality into the Android app.

## Features

- **User-friendly Interface**: Modern and intuitive UI built using Jetpack Compose for a seamless user experience.
- **News Categories**: Explore news articles categorized into various topics such as politics, sports, entertainment, technology, and more.
- **Search Functionality**: Search for specific news articles using keywords.
- **Location-Based News**: Receive news updates relevant to your current location.
- **Text-to-Speech**: Listen to news articles using text-to-speech functionality.
- **Favorites**: Save news articles for later reading or reference.
- **Top Headlines**: Quick access to the most important and trending news stories.
- **Detailed News Display**: Comprehensive access to news content including title, author, description, publication date, and content.
- **Summarization**: Generate concise summaries of news articles.
- **Offline Access**: Access previously viewed news articles even without an internet connection.
- **Real-Time Updates**: Fetch news data from APIs using Retrofit for real-time updates.

## Implementation Details

### Data Retrieval and Display

- Utilized Retrofit to fetch news articles from the News API.
- Implemented RecyclerView and Compose UI for displaying news articles.
- Integrated Jsoup for web scraping to extract article content from webpages.

### Favorite News

- Used Firebase Realtime Database to store and retrieve favorite news items.
- Implemented functionality to add and remove news articles from favorites.

### Location-Based News

- Leveraged Google Play Services for accessing the device's location.
- Integrated Geocoder to convert coordinates into location names.
- Displayed location-specific news articles based on the user's current location.

### Search Functionality

- Implemented search functionality to allow users to search for news articles by keywords.
- Utilized Retrofit to fetch search results from the News API.

### Text Summarization

- Developed a native C++ function for text summarization using CMake and JNI.
- Integrated the text summarization library to generate summaries of news articles.

## Activities

- **Splash Screen**: Animated splash screen displayed on application start.
- **Categories**: Displayed categories using the Lazy Staggered Grid List of Jetpack Compose.
- **News Display**: Card view display of news articles fetched from the News API.
- **News Description**: Detailed display of news article including title, author, description, and content.
- **Search Based News** - Integration of the query-based news endpoint of News API.
- **Location-based News** - Fetched the live location of the device using the FusedLocationProviderClient. It requests necessary permissions for location access, retrieves the latitude and longitude coordinates, and reverse geocoded to obtain human-readable address information, facilitating location-based functionalities.

## Testing and Debugging

- **Functional Testing**: Rigorous testing of each feature to ensure intended performance.
- **Performance Testing**: Assessment of responsiveness and efficiency under various conditions.
- **User Interface Testing**: Evaluation of interface intuitiveness and usability.
- **Debugging**: Thorough debugging procedures to identify and resolve software defects.

## Future Enhancements

While our app offers a robust set of features, there are several avenues for future enhancements:

- **Personalized Recommendations**: Implementing machine learning algorithms for personalized news recommendations.
- **Enhanced Summarization Techniques**: Improvements in text summarization feature using natural language processing.
- **Social Media Integration**: Integrating social media sharing capabilities.
- **Multimedia Content Support**: Incorporating support for videos and podcasts.
- **Accessibility Features**: Further enhancing accessibility features for users with disabilities.

## Conclusion

"Samachar: The News App" represents our commitment to providing users with a seamless and enriching news experience. Through innovative features and continuous improvement, we aim to empower users with knowledge and foster community engagement.

## References

- [Android Developer Documentation](https://developer.android.com/docs)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Jsoup Documentation](https://jsoup.org/documentation)
- [News API Documentation](https://newsapi.org/docs)
- [Google Play Services](https://developers.google.com/android/guides/overview)
- [Animated Splash Screen Tutorial](https://www.youtube.com/watch?v=eFZmMSm1G1c)
- [Grid View for Categories Tutorial](https://www.youtube.com/watch?v=EMJ_Py1mcj4)
- [Card View for News Display Tutorial](https://www.youtube.com/watch?v=b3yC0GeThUA)
- [Summarization of Web Content](https://github.com/Shashankappu/Summarify)
- [Live Location Tracking Tutorial](https://www.youtube.com/watch?v=Jj14sw4Yxk0)
- [Text-to-Speech Tutorial](https://www.youtube.com/watch?v=vo24aZ0UtsA)
- [Native C++ Integration Tutorial](https://youtu.be/bI5_lLovoy4?si=I8Jf4k0iOqbYmZui)

## Contributors

- [M Bhanu Uday - MT23043](https://github.com/bhanu-uday09)
- [Ritik Chhatwani - MT23076](https://github.com/ritik23076)
- [Saurabh Kumar - MT23084](https://github.com/kumarsaurabh614)
