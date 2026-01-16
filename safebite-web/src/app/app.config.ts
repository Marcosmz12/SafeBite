import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { initializeApp, provideFirebaseApp } from '@angular/fire/app';
import { getAuth, provideAuth } from '@angular/fire/auth';
import { getFirestore, provideFirestore } from '@angular/fire/firestore';
import { getStorage, provideStorage } from '@angular/fire/storage';

const firebaseConfig = {
  apiKey: "AIzaSyD_BWf7nYPgN5JbHs2cFvQ-4tPvjSWR2DA",
  authDomain: "safebite-d26ff.firebaseapp.com",
  projectId: "safebite-d26ff",
  storageBucket: "safebite-d26ff.firebasestorage.app",
  messagingSenderId: "528336228386",
  appId: "1:528336228386:web:f6889453db1137624c3a7b"
};

// app.config.ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideFirebaseApp(() => initializeApp(firebaseConfig)),
    provideFirestore(() => getFirestore()),
    provideAuth(() => getAuth()),
    // Asegúrate de que NO tienes nada raro que desactive NgZone
  ]
};
