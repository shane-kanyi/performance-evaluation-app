# Performance Evaluation System for Technical Trainers

This is an Android application designed to facilitate the performance evaluation of technical trainers, based on the research and design outlined in the associated academic paper.

## Features

*   **User Authentication:** Secure login for Administrators and Trainers.
*   **Admin Dashboard:**
    *   Manage Trainer accounts.
    *   Submit new performance evaluations for trainers.
    *   View evaluation history for all trainers.
*   **Trainer Dashboard:**
    *   View personal evaluation history and feedback.
    *   Access generated performance reports.

## Tech Stack

*   **IDE:** Android Studio
*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose
*   **Backend:** Firebase
    *   **Authentication:** Firebase Authentication (Email/Password)
    *   **Database:** Firebase Realtime Database
*   **Navigation:** Jetpack Navigation Component
*   **Architecture:** MVVM (Model-View-ViewModel)

## Firebase Setup

1.  Create a Firebase project in the Firebase Console.
2.  Add an Android app to the project with the package name: `com.example.performanceevaluationapp`.
3.  Download the `google-services.json` file and place it in the `app/` directory of the project.
4.  Enable **Email/Password** authentication in the Firebase Console.
5.  Create a **Realtime Database** and start it in test mode for development.

## Project Structure

The project follows a feature-based package structure to promote modularity and scalability.

-   `data/`: Contains data models (e.g., User, Evaluation, Report).
-   `navigation/`: Handles all Jetpack Compose navigation logic.
-   `ui/screens/`: Contains the composable functions for each screen of the app.
-   `ui/viewmodel/`: Contains the ViewModels that provide state and business logic to the UI.
-   `di/`: (Optional) For dependency injection setup.