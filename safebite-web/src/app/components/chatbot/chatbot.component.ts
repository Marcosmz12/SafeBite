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
          text: '',
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
  const t = this.langService.t();
  // Limpiamos el texto: quitamos tildes y espacios para que la comparación sea perfecta
  const text = userText.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();
  let response: Message = { text: '', type: 'bot' };

  // 1. NAVEGACIÓN: PERFIL (Usamos return para que no siga ejecutando nada más)
  if (text.includes('perfil') || text.includes('profile')) {
    response.text = this.langService.currentLang() === 'es' ? 'Abriendo tu perfil...' : 'Opening your profile...';
    this.messages.push(response);
    setTimeout(() => { 
      this.isOpen = false; 
      this.router.navigate(['/perfil']); 
    }, 1000);
    return; // <--- IMPORTANTE: Detiene la función aquí
  } 

  // 2. NAVEGACIÓN: MENÚ / INICIO
  if (text.includes('menu') || text.includes('principal') || text.includes('inicio') || text.includes('home')) {
    response.text = this.langService.currentLang() === 'es' ? 'Volviendo al inicio...' : 'Going back home...';
    this.messages.push(response);
    setTimeout(() => { 
      this.isOpen = false; 
      this.router.navigate(['/']); 
    }, 1000);
    return; // <--- IMPORTANTE
  }

  // 3. LÓGICA DE ALERGIAS
  if (text.includes('alergia') || text.includes('allergy') || text.includes('comunes')) {
    response.text = t.chat_resp_alergias;
    response.options = [t.chat_opt_gluten, t.chat_opt_lacteos, t.chat_opt_otros];
  } 
  else if (text.includes('gluten')) {
    response.text = t.chat_resp_gluten;
    response.options = [t.chat_btn_perfil, t.chat_btn_menu];
  } 
  else if (text.includes('lacteo') || text.includes('lactosa') || text.includes('dairy')) {
    response.text = t.chat_resp_lacteos;
    response.options = [t.chat_btn_perfil, t.chat_btn_menu];
  } 
  else if (text.includes('otros') || text.includes('others')) {
    response.text = t.chat_resp_otros;
    response.options = [t.chat_btn_perfil, t.chat_btn_menu];
  }
  else if (text.includes('funciona') || text.includes('how')) {
    response.text = t.chat_resp_funciona;
  } 
  else if (text.includes('contacto') || text.includes('contact')) {
    response.text = t.chat_resp_contacto;
    this.messages.push(response);
    setTimeout(() => { 
      this.isOpen = false; 
      this.router.navigate(['/contacto']); 
    }, 2000);
    return; // <--- IMPORTANTE
  } 
  // 4. SI NADA DE LO ANTERIOR COINCIDE
  else {
    response.text = t.chat_resp_error;
    response.options = [t.chat_opt_alergias, t.chat_opt_funciona];
  }

  this.messages.push(response);
}
}