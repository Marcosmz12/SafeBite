import { Component, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
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
  styleUrls: ['./chatbot.component.css']
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

  // Esto hace que el chat baje solo al recibir mensajes
  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) { }
  }

  sendMessage(textOverride?: string) {
    const messageText = textOverride || this.userInput;
    if (!messageText.trim()) return;

    this.messages.push({ text: messageText, type: 'user' });
    this.userInput = '';

    // Respuesta del Bot
    setTimeout(() => {
      this.generateReply(messageText.toLowerCase());
    }, 800);
  }

  generateReply(input: string) {
    let reply: Message = { text: '', type: 'bot' };

    if (input.includes('alergia')) {
      reply.text = 'En SafeBite detectamos alérgenos como gluten, lácteos y frutos secos. ¿Buscas alguno en concreto?';
      reply.options = ['Gluten', 'Lácteos', 'Otros'];
    } else if (input.includes('funciona')) {
      reply.text = 'Es fácil: escaneas el plato o producto y te avisamos si es seguro para ti según tu perfil.';
      reply.options = ['Ver tutorial', 'Empezar ahora'];
    } else if (input.includes('contacto')) {
      reply.text = 'Puedes escribirnos a soporte@safebite.com. ¡Estamos para ayudarte!';
    } else {
      reply.text = 'No estoy seguro de entenderte, pero puedo informarte sobre alérgenos o sobre nuestra app.';
      reply.options = ['Alergias', '¿Cómo funciona?'];
    }

    this.messages.push(reply);
  }
}