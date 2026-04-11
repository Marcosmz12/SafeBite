import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http'; //he quitado HttpClientModule porque no es necesario importarlo aquí, solo en el appConfig
import { CommonModule } from '@angular/common';
import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-contacto',
  standalone: true, // Lo hacemos standalone para Angular 19
  imports: [CommonModule], // Importamos lo necesario
  templateUrl: './contacto.component.html',
  styleUrl: './contacto.component.css'
})
export class ContactoComponent {
  private http = inject(HttpClient); 
  langService = inject(LanguageService);
  
  enviarMensaje(event: any) {
    event.preventDefault();
    
    // Capturamos los datos del formulario
    const formulario = event.target;
    const formData = {
      nombre: formulario.nombre.value,
      email: formulario.email.value,
      mensaje: formulario.mensaje.value
    };

    this.http.post('http://localhost:3000/api/contacto', formData).subscribe({
      next: (res) => {
        alert('¡Mensaje enviado con éxito a SafeBite! 🚀');
        formulario.reset(); // Limpia el formulario al terminar
      },
      error: (err) => alert('Error al enviar el mensaje')
    });
  }
}