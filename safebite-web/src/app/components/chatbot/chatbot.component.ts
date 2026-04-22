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

  // Esta función abre el chat y pone el cursor en el input automáticamente
  abrirChat() {
    this.isOpen = true;
    
    // El "setTimeout" es necesario porque Angular tarda un milisegundo 
    // en dibujar el input después de poner isOpen = true
    setTimeout(() => {
      const inputElement = document.querySelector('.chat-input input') as HTMLInputElement;
      if (inputElement) {
        inputElement.focus();
      }
    }, 100);
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

  private reproducirSonido() {
    const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2358/2358-preview.mp3');
    audio.volume = 0.1; // Volumen bajito
    audio.play().catch(err => console.log("Audio bloqueado hasta que el usuario interactúe"));
  }

  botReply(userText: string) {
  const t = this.langService.t(); // Diccionario actual (ya sea ES o EN)
  
  // Limpiamos el texto que recibimos (quitamos tildes y pasamos a minúsculas)
  const text = userText.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();

  // Función rápida para limpiar y comparar con el diccionario
  const match = (key: string) => {
    if (!key) return false;
    return text === key.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").trim();
  };

  let response: Message = { text: '', type: 'bot' };

  // --- LÓGICA AUTOMÁTICA POR BOTONES ---

  // 1. Si coincide con el botón de ALERGIAS (da igual el idioma)
  if (match(t.chat_opt_alergias) || text.includes('alergia') || text.includes('allergy')) {
    response.text = t.chat_resp_alergias;
    response.options = [t.chat_opt_gluten, t.chat_opt_lacteos, t.chat_opt_otros];
  } 

  // 2. Si coincide con el botón de FUNCIONAMIENTO
  else if (match(t.chat_opt_funciona) || text.includes('funciona') || text.includes('how')) {
    response.text = t.chat_resp_funciona;
  }

  // 3. Si coincide con el botón de CONTACTO
  else if (match(t.chat_opt_contacto) || text.includes('contacto') || text.includes('contact')) {
    response.text = t.chat_resp_contacto;
    this.messages.push(response);
    this.reproducirSonido();
    setTimeout(() => { this.isOpen = false; this.router.navigate(['/contacto']); }, 2000);
    return;
  }

  // 4. Lógica de los alérgenos específicos (Gluten, Lácteos...)
  else if (match(t.chat_opt_gluten) || text.includes('gluten')) {
    response.text = t.chat_resp_gluten;
    response.options = [t.chat_btn_perfil, t.chat_btn_menu];
  }
  else if (match(t.chat_opt_lacteos) || text.includes('dairy') || text.includes('lacteo')) {
    response.text = t.chat_resp_lacteos;
    response.options = [t.chat_btn_perfil, t.chat_btn_menu];
  }

  // 5. RESPUESTA POR DEFECTO
  else {
    response.text = t.chat_resp_error;
    response.options = [t.chat_opt_alergias, t.chat_opt_funciona];
  }

  this.messages.push(response);
  this.reproducirSonido();
}
}