# Practical 7 — SQLite + JSON Person Manager App

A simple Android app showing how to use SQLite database and JSON API together to manage person information.

---

## Features
- Show persons from SQLite + JSON API  
- Add new person (saved in SQLite)  
- Delete person (SQLite → permanent, JSON → temporary)  
- Auto refresh on returning to main screen  

---

## Main Flow

### Main Screen
- Fetches data from SQLite and JSON  
- Displays list in RecyclerView  
- Shows Name, Email, Phone, Address  
- Delete button for each row  
- Register button to add person  

### Register Screen
- User inputs name, email, phone, address  
- Submit → saves to SQLite  
- Toast message shows confirmation  
- Goes back and auto-refreshes list  

---

## OUTPUT

### Main Screen  
<img src="PASTE_ID_HERE" width="300"/>

<br><br>

### Register Screen  
<img src="PASTE_ID_HERE" width="300"/>

---
