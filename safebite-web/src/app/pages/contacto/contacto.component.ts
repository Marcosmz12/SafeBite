import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-contacto',
  templateUrl: './contacto.component.html'
})
export class ContactoComponent {
  constructor(private http: HttpClient) {}

  enviarMensaje(event: any) {
    event.preventDefault();
    const formData = {
      nombre: event.target.nombre.value,
      email: event.target.email.value,
      mensaje: event.target.mensaje.value
    };

    this.http.post('http://localhost:3000/api/contacto', formData).subscribe({
      next: (res) => alert('¡Mensaje enviado con éxito!'),
      error: (err) => alert('Error al enviar')
    });
  }
}