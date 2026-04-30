Project Name: Quiz – OpenTDB Quiz Tournament System
Android Mobile Application for ISCG7424 – Mobile Software Development
This is the main project for the assignment.
It includes Admin and Player user roles.

👤 Admin Features
Login using admin@gmail.com/111111

Create quiz tournaments with:
Name
Category
Difficulty
Start date
End date
Rating (1–5)

Fetch 10 questions dynamically from OpenTDB API
View all tournaments(include ratings)
Update tournament (name, start date, end date)
Delete tournament (with confirmation)
View profile
Update profile

🧑‍💻 Player Featur
Login using email + password
Register using email + password
View profile
Update profile

View tournaments categorized as:
Ongoing
Upcoming
Past
Participated

Participate only in ongoing tournaments
All players receive the same 10 questions
Questions displayed one per page
Feedback for correct/incorrect answers
Show correct answer when user is wrong
Final score displayed out of 10
Rate the quiz (1–5)

Customise your questions by selecting:
Category
Difficulty
Question count(1-20)


🌐 API Used
OpenTDB – https://opentdb.com/  
Used for fetching quiz questions dynamically.

🛠 Technologies Used
Android Studio (version: Android Studio Panda 1 | 2025.3.1 Patch 1)
Java
Firebase Authentication
Firebase Realtime Database 
Retrofit
RecyclerView + CardView

📥 How to Run the App
Clone the repository:

Code
git clone https://github.com/yourusername/your-repo-name.git
Open the project in Android Studio.

Sync Gradle.

Firebase:
Add your own google-services.json to /app folder.

Run the app on:
Android Emulator
Physical Android device
