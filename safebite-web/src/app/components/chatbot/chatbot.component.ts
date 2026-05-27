import { Component, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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
  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  isOpen = false;
  userInput = '';

  messages: Message[] = [
    {
      text: '¡Hola! 🛡️ Soy el asistente de SafeBite. ¿En qué puedo ayudarte hoy?',
      type: 'bot',
      options: ['Alergias Comunes', '¿Cómo funciona?', 'Contacto']
    }
  ];

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

    // Lógica de respuesta del bot
    setTimeout(() => {
      this.botReply(messageToSend);
    }, 600);
  }

  botReply(userText: string) {
    let response: Message = { text: '', type: 'bot' };
    const text = userText.toLowerCase();

    if (text.includes('alergias')) {
      response.text = 'En SafeBite detectamos alérgenos como gluten, lácteos y frutos secos. ¿Buscas alguno en concreto?';
      response.options = ['Gluten', 'Lácteos', 'Otros'];
    } else if (text.includes('gluten')) {
      response.text = '¡Entendido! Puedes activar el filtro de "Sin Gluten" en tu perfil para que todas las recetas se adapten a ti.';
      response.options = ['Ir al perfil', 'Menú principal'];
    } else if (text.includes('funciona')) {
      response.text = 'Es muy fácil: escanea o busca una receta y te diremos si es segura para tus alergias configuradas.';
    } else {
      response.text = 'No estoy seguro de entenderte, pero puedo informarte sobre alérgenos o sobre nuestra app.';
      response.options = ['Alergias', '¿Cómo funciona?'];
    }

    this.messages.push(response);
  }
}