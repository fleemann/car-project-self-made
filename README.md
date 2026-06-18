# Autovermietung – Fullstack-Projekt

Dieses Projekt ist eine selbst gebaute Fullstack-Webanwendung für eine einfache Autovermietung.

Das Projekt besteht aus zwei Teilen:

- Backend: Java 21 mit Spring Boot
- Frontend: React mit Vite

## Funktionen

- Autos anzeigen
- Autos nach Marke, Modell, Preis, Getriebe, Motor, PS, Baujahr, Sitzen und Zeitraum filtern
- Autos erstellen
- Autos bearbeiten
- Autos löschen
- Autos für einen Zeitraum mieten
- Buchungskonflikte erkennen
- Fehler als JSON zurückgeben
- API über Swagger UI testen
- Backend-Tests ausführen

## Projektstruktur

```text
car-project-self-made/
├── backend/
│   ├── pom.xml
│   ├── data/
│   │   └── cars.json
│   └── src/
│       ├── main/
│       │   ├── java/com/example/carrental/
│       │   │   ├── CarRentalApplication.java
│       │   │   ├── WebConfig.java
│       │   │   ├── controller/
│       │   │   ├── exception/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/com/example/carrental/
│               ├── repository/
│               └── service/
│
└── frontend/
    ├── package.json
    ├── index.html
    ├── vite.config.js
    └── src/
        ├── main.jsx
        ├── App.jsx
        ├── App.css
        ├── api/
        └── components/