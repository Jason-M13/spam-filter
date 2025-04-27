# Spam Detection 

## Project Information
This project uses the **Bayesian spam filter** to classify emails as **spam** or **ham (non-spam)** by counting word frequencies. The system analyzes emails and traces words in training emails to compute the probabilities of whether an email is a scam.

## Improvements to The System
### 1. Aesthetic Changes 
- A change  made to the interface was adding bolding labels and font
- Added a question prompt to select files

### 2. System Changes
- Removed all special characters and turned all letters into lowercases
- Separate each sentence into individual words for easy tracking and system training

## How To Run 
### Cloning Repo 
- git clone https://github.com/Jason-M13/spam-filter.git
- cd \spam-detection\spam-filter\src\main\java\csci2020u\spamprogram
- Open the java file, spamDetectorGUI.java

### Running The File
- Run the file, and an interface should appear; you'll see folders located in **spam-filter**.
- Double-click on **src** then **main** then **resources** and stop.
- Here, you'll see the folder data, but **don't** double-click on it. On the bottom right, a button says **open**.
- Click on **open**, and the program will begin training the system. The system scans emails from the 'train/spam' and 'train/ham' folders.

### User Interface and Results
- The interface will display each email's classification results, including the filenames, actual class, spam probability, accuracy, and precision.
- The accuracy is the measure of correct classifications, and the precision is the classification of how many emails were actual scam emails

## Other Sources 
[1] [The math behind the Bayesian filtering](https://www.youtube.com/watch?v=lFJbZ6LVxN8&ab_channel=NormalizedNerd)

[2][Understanding Maps](https://www.w3schools.com/java/ref_hashmap_getordefault.asp#:~:text=Definition%20and%20Usage,the%20second%20parameter%20is%20returned.)

### This project was originally submitted as coursework for CSCI2020U Software Systems Development & Integration at Ontario Tech University, Winter 2025.
