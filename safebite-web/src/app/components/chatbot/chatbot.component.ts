import { Component, inject, ElementRef, ViewChild, AfterViewChecked, OnInit, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LanguageService } from '../../services/language.service';
import { Router } from '@angular/router';

interface Message {
  text: string;
  type: 'bot' | 'user';
  options?: string[];
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.component.html',
  styleUrl: './chatbot.component.css'
})
export class ChatbotComponent implements AfterViewChecked {

  public langService = inject(LanguageService);
  private router = inject(Router); // <--- Inyectamos el router

  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  isOpen = false;
  userInput = '';
  messages: Message[] = []; // vacío al empezar
  isTyping = signal(false); // <--- Signal para el efecto "escribiendo"

  constructor() {
    // Esta función se ejecuta CADA VEZ que el idioma cambia
    effect(() => {
      const t = this.langService.t();
      
      // Reiniciamos el chat con el saludo en el idioma nuevo
      //Esto borrará la conversación actual para que empiece de cero en el nuevo idioma
      this.messages = [
        {
          text: t.chat_saludo,
          type: 'bot',
          options: [
            t.chat_opt_alergias,
            t.chat_opt_funciona,
            t.chat_opt_contacto
          ]
        }
      ];
    });
    
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) { }
  }

  sendMessage(text?: string) {
    const messageToSend = text || this.userInput;
    if (!messageToSend.trim()) return;

    // Mensaje del usuario
    this.messages.push({ text: messageToSend, type: 'user' });
    this.userInput = '';

    // Efecto "Escribiendo..."
    this.isTyping.set(true);

    // Lógica de respuesta del bot
    setTimeout(() => {
      this.isTyping.set(false);
      this.botReply(messageToSend);
    }, 600);
  }

  botReply(userText: string) {
    let response: Message = { text: '', type: 'bot' };
    const text = userText.toLowerCase();
    const t = this.langService.t(); // Acceso rápido a las traducciones

    // Lógica inteligente: detecta palabras en ambos idiomas
    if (text.includes('contacto') || text.includes('contact')) {
    response.text = t.chat_resp_contacto; // <--- Ahora sí tiene texto y no dará error
    // Esperamos un poco más para que el usuario lea el mensaje antes de que la página cambie
    setTimeout(() => {
      this.isOpen = false; // Cerramos el chat
      this.router.navigate(['/contacto']);
    }, 2000);
  } 
    else if (text.includes('recetas') || text.includes('recipes')) {
      response.text = this.langService.currentLang() === 'es'
        ? 'Entendido. Vamos a ver el catálogo de recetas.'
        : 'Understood. Let\'s see the recipe catalog.';
      setTimeout(() => this.router.navigate(['/recetas']), 2000);
    }
    if (text.includes('alergia') || text.includes('allergy') || text.includes('allergies')) {
      response.text = t.chat_resp_alergias;
      response.options = ['Gluten', 'Lácteos', 'Otros'];
    } 
    else if (text.includes('gluten')) {
      response.text = t.chat_resp_gluten;
      response.options = [t.nav_perfil || 'Perfil', 'Menu'];
    } 
    else if (text.includes('funciona') || text.includes('works') || text.includes('how')) {
      response.text = t.chat_resp_funciona;
    } 
    // RESPUESTA POR DEFECTO (ERROR)
    else {
      response.text = t.chat_resp_error;
      response.options = [t.chat_opt_alergias, t.chat_opt_funciona];
    }

    this.messages.push(response);
  }
}