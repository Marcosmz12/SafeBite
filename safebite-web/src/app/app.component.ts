import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { BuscadorSuperComponent } from './components/buscador-super/buscador-super.component';

@Component({
  selector: 'app-root',
  standalone: true,
  // Añadimos BuscadorSuperComponent al array de abajo:
  imports: [
    RouterOutlet, 
    HeaderComponent, 
    FooterComponent, 
    ChatbotComponent, 
    BuscadorSuperComponent // <--- IMPORTANTE AÑADIRLO AQUÍ
  ], 
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent { }